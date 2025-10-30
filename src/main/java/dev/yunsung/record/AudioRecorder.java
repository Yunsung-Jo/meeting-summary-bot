package dev.yunsung.record;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.channel.Channel;

public class AudioRecorder implements AudioReceiveHandler {

	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
	static final int RECORD_DELAY = 1000; // 1초 딜레이

	private Map<Long, ByteArrayOutputStream> audioMap; // 음성 데이터
	private Map<Long, UserTimer> timerMap; // 유저별로 문장을 끊어 저장하기 위한 타이머
	private Channel channel; // 음성 채널
	private LocalDateTime dateTime; // 회의 시작 시간
	private boolean recording = false; // 녹음 여부

	public Channel getChannel() {
		return channel;
	}

	public String getFolderName() {
		String dt = dateTime.format(formatter);
		return "audio/" + dt + "-" + channel.getName();
	}

	@Override
	public void handleUserAudio(@NotNull UserAudio userAudio) {
		try {
			// 말을 하고 있는 유저의 id를 가져옴
			long userId = userAudio.getUser().getIdLong();
			byte[] audioData = userAudio.getAudioData(1.0);

			// 저장된 음성 데이터가 없다면 새로운 ByteArrayOutputStream을 생성
			audioMap.computeIfAbsent(userId, k -> new ByteArrayOutputStream());

			// 디스코드에서 받아온 음성 데이터 추가
			audioMap.get(userId).write(audioData);

			// 이미 사용자의 타이머가 작동 중이라면 멈추기
			if (timerMap.get(userId) != null) {
				timerMap.get(userId).timer().cancel();
			}

			// 타이머 추가
			String name = userAudio.getUser().getEffectiveName();
			Timer timer = getTimer(userId, name);
			timerMap.put(userId, new UserTimer(name, timer));
		} catch (Exception ignored) {
		}
	}

	private Timer getTimer(Long userId, String name) {
		TimerTask task = new TimerTask() {
			public void run() {
				// RECORD_DELAY 만큼 말을 하지 않았다면 음성 데이터를 파일로 저장
				saveVoice(userId, name);
			}
		};
		// RECORD_DELAY 후에 타이머 실행
		Timer timer = new Timer();
		timer.schedule(task, RECORD_DELAY);
		return timer;
	}

	private void saveVoice(Long userId, String name) {
		// 해당하는 사용자의 음성 데이터를 가져옴
		ByteArrayOutputStream outputStream = this.audioMap.get(userId);
		if (outputStream == null) {
			return;
		}

		AudioFormat audioFormat = AudioReceiveHandler.OUTPUT_FORMAT;
		try {
			byte[] audioData = outputStream.toByteArray();
			AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(audioData), audioFormat,
				audioData.length / audioFormat.getFrameSize());

			// 음성 데이터의 길이를 구함
			float durationInMillis = (float)(1000L * ais.getFrameLength()) / audioFormat.getFrameRate();
			if (durationInMillis < 1000.0F) { // 길이가 1초 이상이라면 파일로 저장
				return;
			}

			// audio/{회의 시작 시간}-{음성 채널 이름}/{사용자명}/{현재시간.wav} 구조로 파일을 저장
			File root = new File(getFolderName() + "/" + name);
			File file = new File(root.getPath() + "/" + System.currentTimeMillis() + ".wav");

			// 폴더가 없다면 생성
			root.mkdirs();

			// 파일에 음성 데이터를 저장
			AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
			ais.close();
		} catch (IOException ignored) {
		}

		// 파일로 저장한 음성 데이터를 삭제
		audioMap.remove(userId);
		timerMap.remove(userId);
	}

	public void startRecording(Channel channel) {
		audioMap = new HashMap<>();
		timerMap = new HashMap<>();
		this.channel = channel;
		this.dateTime = LocalDateTime.now();
		recording = true;
	}

	public void stopRecording() {
		this.channel = null;
		recording = false;
		// 아직 저장되지 않은 음성 데이터가 있다면 저장
		for (Long key : timerMap.keySet()) {
			this.saveVoice(key, timerMap.get(key).name());
		}
	}

	@Override
	public boolean canReceiveUser() {
		// true인 경우 디스코드로부터 음성 데이터를 받을 수 있고 false인 경우 받지 않음
		return recording;
	}
}

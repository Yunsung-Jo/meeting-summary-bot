package dev.yunsung.record;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import dev.yunsung.util.EnvUtil;
import dev.yunsung.util.LogUtil;
import net.dv8tion.jda.api.entities.User;

public class AudioData {
	static final int RECORD_DELAY = EnvUtil.getenv("RECORD_DELAY", 1000);

	private final long id;
	private final String speaker;
	private String sentence;
	private final File audioFile;
	private final FileOutputStream audioStream;
	private final LocalDateTime startTime;
	private LocalDateTime endTime;
	private final Consumer<AudioData> callback;
	private Timer timer;

	private AudioData(long id, String speaker, Consumer<AudioData> callback) throws IOException {
		this.id = id;
		this.speaker = speaker;
		this.audioFile = Files.createTempFile("audio", ".tmp").toFile();
		this.audioStream = new FileOutputStream(audioFile);
		this.startTime = LocalDateTime.now();
		this.callback = callback;
		startTimer();
	}

	public static AudioData discord(User user, Consumer<AudioData> callback) throws IOException {
		return new AudioData(
			user.getIdLong(),
			user.getEffectiveName(),
			callback
		);
	}

	public long getId() {
		return id;
	}

	public String getSpeaker() {
		return speaker;
	}

	public String getSentence() {
		return sentence;
	}

	public File getAudioFile() {
		return audioFile;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public void write(byte[] audio) throws IOException {
		this.audioStream.write(audio);
		resetTimer();
	}

	private void resetTimer() {
		// 이미 사용자의 타이머가 작동 중이라면 멈추기
		if (timer != null) {
			timer.cancel();
		}
		startTimer();
	}

	private void startTimer() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				stopTimer();
			}
		}, RECORD_DELAY);
	}

	public void stopTimer() {
		if (timer != null) {
			timer.cancel();
		}
		try {
			audioStream.close();
		} catch (IOException e) {
			LogUtil.error("임시 오디오 파일 스트림을 닫는 중 오류가 발생했습니다.", e);
		}
		AudioData.this.endTime = LocalDateTime.now();
		callback.accept(AudioData.this);
	}

	@Override
	public String toString() {
		return "[" + speaker + "]: " + sentence;
	}

	public void deleteTempFile() {
		audioFile.delete();
	}
}

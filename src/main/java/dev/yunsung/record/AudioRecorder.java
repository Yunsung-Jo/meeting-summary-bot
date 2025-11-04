package dev.yunsung.record;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import dev.yunsung.stt.STT;

public abstract class AudioRecorder {

	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

	protected final Map<Long, AudioData> bufferAudios = new ConcurrentHashMap<>();
	protected final TreeMap<LocalDateTime, AudioData> archiveAudios = new TreeMap<>();

	private String startTime;
	private boolean recording = false;

	private final STT stt;
	private AtomicInteger activeSttJobs;
	private final Object sttJobLock = new Object();

	protected AudioRecorder(STT stt) {
		this.stt = stt;
	}

	public TreeMap<LocalDateTime, AudioData> getArchiveAudios() {
		return archiveAudios;
	}

	protected String getStartTime() {
		return startTime;
	}

	protected abstract AudioFormat getAudioFormat();

	public abstract String getFolderName();

	protected void saveVoice(AudioData audioData) {
		// 버퍼에 있는 데이터 삭제
		bufferAudios.remove(audioData.getId());

		try {
			// wav 파일로 저장
			saveWavFile(audioData);

			// 음성을 텍스트로 변환
			transcribe(audioData);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void saveWavFile(AudioData audioData) throws IOException {
		String speaker = audioData.getSpeaker();
		String time = audioData.getStartTime().format(formatter);
		ByteArrayOutputStream outputStream = audioData.getAudio();

		AudioFormat audioFormat = getAudioFormat();
		byte[] bytes = outputStream.toByteArray();

		AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(bytes), audioFormat,
			bytes.length / audioFormat.getFrameSize());

		// 음성 데이터의 길이를 구함
		float durationInMillis = (float)(1000L * ais.getFrameLength()) / audioFormat.getFrameRate();
		if (durationInMillis < 1000.0F) { // 길이가 1초 미만이면 저장하지 않음
			return;
		}

		// audio/{폴더 이름}/{사용자명}/{시작 시간.wav} 구조로 파일을 저장
		String fileName = time + ".wav";
		File root = new File("audio/" + getFolderName() + "/" + speaker);
		File file = new File(root.getPath() + "/" + fileName);

		// 폴더가 없다면 생성
		root.mkdirs();

		// 파일에 음성 데이터를 저장
		AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
		ais.close();
	}

	private void transcribe(AudioData audioData) throws IOException, InterruptedException {
		try {
			activeSttJobs.incrementAndGet();
			String speaker = audioData.getSpeaker();
			String time = audioData.getStartTime().format(formatter);
			String sentence = stt.transcribe(getFolderName() + "/" + speaker + "/" + time + ".wav");
			audioData.setSentence(sentence);
			archiveAudios.put(audioData.getStartTime(), audioData);
		} finally {
			// 마지막으로 실행 중인 작업이라면 sttJobLock을 깨움
			if (activeSttJobs.decrementAndGet() == 0) {
				synchronized (sttJobLock) {
					sttJobLock.notifyAll();
				}
			}
		}
	}

	protected void startRecording() {
		if (recording) {
			return;
		}
		bufferAudios.clear();
		archiveAudios.clear();
		activeSttJobs = new AtomicInteger(0);
		startTime = LocalDateTime.now().format(formatter);
		recording = true;
	}

	protected void stopRecording() throws InterruptedException {
		if (!recording) {
			return;
		}

		// 아직 저장되지 않은 음성 데이터가 있다면 저장
		for (Long key : bufferAudios.keySet()) {
			AudioData audioData = bufferAudios.get(key);
			audioData.stopTimer();
		}

		// 모든 STT 작업이 완료될 때까지 대기
		synchronized (sttJobLock) {
			while (activeSttJobs.get() > 0) {
				sttJobLock.wait();
			}
		}

		recording = false;
	}

	public boolean isRecording() {
		return recording;
	}
}

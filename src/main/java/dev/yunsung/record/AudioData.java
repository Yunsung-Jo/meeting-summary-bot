package dev.yunsung.record;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import net.dv8tion.jda.api.entities.User;

public class AudioData {
	static final int RECORD_DELAY = Integer.parseInt(System.getenv("RECORD_DELAY"));

	private final long id;
	private final String speaker;
	private String sentence;
	private final ByteArrayOutputStream audio;
	private final LocalDateTime startTime;
	private LocalDateTime endTime;
	private final Consumer<AudioData> callback;
	private Timer timer;

	private AudioData(long id, String speaker, Consumer<AudioData> callback) {
		this.id = id;
		this.speaker = speaker;
		this.audio = new ByteArrayOutputStream();
		this.startTime = LocalDateTime.now();
		this.callback = callback;
		startTimer();
	}

	public static AudioData discord(User user, Consumer<AudioData> callback) {
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

	public ByteArrayOutputStream getAudio() {
		return audio;
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
		this.audio.write(audio);
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
		AudioData.this.endTime = LocalDateTime.now();
		callback.accept(AudioData.this);
	}

	@Override
	public String toString() {
		return "[" + speaker + "]: " + sentence;
	}
}

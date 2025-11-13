package dev.yunsung.record;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;

import dev.yunsung.stt.STT;
import dev.yunsung.util.LogUtil;

public final class DiscordAudioRecorder extends AudioRecorder implements AudioReceiveHandler {

	private final String name;
	private Channel channel;

	public DiscordAudioRecorder(STT stt, String name) {
		super(stt);
		this.name = name;
	}

	public Channel getChannel() {
		return channel;
	}

	@Override
	protected AudioFormat getAudioFormat() {
		return AudioReceiveHandler.OUTPUT_FORMAT;
	}

	@Override
	public String getFolderName() {
		if (getChannel() == null) {
			return null;
		}
		return name + "-" + getChannel().getName() + "-" + getStartTime();
	}

	@Override
	public void handleUserAudio(@NotNull UserAudio userAudio) {
		User user = userAudio.getUser();
		long userId = user.getIdLong();
		byte[] bytes = userAudio.getAudioData(1.0);

		bufferAudios.computeIfAbsent(userId, k -> AudioData.discord(user, this::saveVoice));

		try {
			// 디스코드에서 받아온 음성 데이터 추가
			bufferAudios.get(userId).write(bytes);
		} catch (IOException e) {
			LogUtil.error("음성 데이터를 기록하지 못했습니다", e);
		}
	}

	public void startRecording(Channel channel) {
		super.startRecording();
		this.channel = channel;
	}

	@Override
	public void stopRecording() throws InterruptedException {
		super.stopRecording();
		this.channel = null;
	}

	@Override
	public boolean canReceiveUser() {
		// true인 경우 디스코드로부터 음성 데이터를 받을 수 있고 false인 경우 받지 않음
		return isRecording();
	}
}

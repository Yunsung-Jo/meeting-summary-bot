package dev.yunsung.record;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.dv8tion.jda.api.entities.Guild;

import dev.yunsung.stt.STT;
import dev.yunsung.stt.Whisper;

public class RecorderService {

	private final STT stt = new Whisper();
	private final Map<Long, DiscordAudioRecorder> recorders = new ConcurrentHashMap<>();

	public DiscordAudioRecorder getRecorder(Guild guild) {
		return recorders.computeIfAbsent(guild.getIdLong(), id -> new DiscordAudioRecorder(stt, guild.getName()));
	}

	public void removeRecorder(long guildId) {
		recorders.remove(guildId);
	}
}

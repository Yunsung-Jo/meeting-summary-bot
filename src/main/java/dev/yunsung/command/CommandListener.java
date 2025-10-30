package dev.yunsung.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import dev.yunsung.record.AudioRecorder;
import dev.yunsung.stt.STT;
import dev.yunsung.stt.Whisper;

public class CommandListener extends ListenerAdapter {

	private final Map<String, Command> commands = new HashMap<>();
	private final STT stt = new Whisper();
	private final AudioRecorder audioRecorder = new AudioRecorder(stt);

	public void registerCommands(JDA jda) {
		List<Command> cmdList = List.of(
			new MeetingStartCommand(audioRecorder),
			new MeetingStopCommand(audioRecorder),
			new MeetingSummaryCommand()
		);

		for (Command cmd : cmdList) {
			commands.put(cmd.getName(), cmd);
		}

		jda.updateCommands().addCommands(
			cmdList.stream()
				.map(Command::slash)
				.toList()
		).queue();
	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		if (event.getGuild() == null) {
			return;
		}

		Command cmd = commands.get(event.getName());
		if (cmd != null) {
			cmd.execute(event);
			return;
		}
		event.reply("알 수 없는 명령어입니다.").queue();
	}
}

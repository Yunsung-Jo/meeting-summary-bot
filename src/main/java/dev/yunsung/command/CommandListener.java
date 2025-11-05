package dev.yunsung.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import dev.yunsung.record.RecorderService;
import dev.yunsung.summary.SummaryService;

public class CommandListener extends ListenerAdapter {

	private final Map<String, Command> commands = new HashMap<>();
	private final RecorderService recorderService = new RecorderService();
	private final SummaryService summaryService = new SummaryService();

	public void registerCommands(JDA jda) {
		List<Command> cmdList = List.of(
			new MeetingStartCommand(recorderService),
			new MeetingStopCommand(recorderService, summaryService),
			new MeetingSummaryCommand(recorderService, summaryService)
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

		try {
			Command cmd = commands.get(event.getName());
			if (cmd != null) {
				cmd.execute(event);
				return;
			}
			event.reply("알 수 없는 명령어입니다.").queue();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

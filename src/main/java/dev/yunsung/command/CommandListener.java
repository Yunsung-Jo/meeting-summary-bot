package dev.yunsung.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import dev.yunsung.command.backup.BackupCommand;
import dev.yunsung.command.meeting.MeetingCommand;

public class CommandListener extends ListenerAdapter {

	private final Map<String, Command> commands = new HashMap<>();

	public void registerCommands(JDA jda) {
		List<Command> cmdList = List.of(
			new MeetingCommand(),
			new BackupCommand()
		);

		for (Command cmd : cmdList) {
			commands.put(cmd.getName(), cmd);
		}

		jda.updateCommands().addCommands(
			cmdList.stream()
				.map(Command::getData)
				.toList()
		).queue();
	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		Command cmd = commands.get(event.getName());
		cmd.execute(event);
	}

	@Override
	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
		Command cmd = commands.get(event.getName());
		cmd.autoComplete(event);
	}
}

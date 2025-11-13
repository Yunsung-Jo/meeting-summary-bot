package dev.yunsung.command;

import java.util.List;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import dev.yunsung.util.LogUtil;

public abstract class Command {

	public abstract String getName();

	public String getDescription() {
		return "Description";
	}

	public SlashCommandData getData() {
		SlashCommandData base = Commands.slash(getName(), getDescription());
		List<SubCommand> sub = getSubCommands();

		if (!sub.isEmpty()) {
			base.addSubcommands(getSubCommands().stream()
				.map(SubCommand::getData)
				.toList());
		}

		return base;
	}

	protected List<SubCommand> getSubCommands() {
		return List.of();
	}

	public void execute(SlashCommandInteractionEvent event) {
		String subCmd = event.getSubcommandName();
		if (subCmd == null) {
			return;
		}

		getSubCommands().stream()
			.filter(cmd -> cmd.getName().equals(subCmd))
			.findFirst()
			.ifPresent(cmd -> {
				try {
					cmd.execute(event);
				} catch (Exception e) {
					LogUtil.error(cmd.getName() + " 명령을 실패했습니다", e);
				}
			});
	}

	public void autoComplete(CommandAutoCompleteInteractionEvent event) {

	}
}

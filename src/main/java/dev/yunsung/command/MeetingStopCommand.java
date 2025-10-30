package dev.yunsung.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class MeetingStopCommand implements Command {

	@Override
	public String getName() {
		return "종료";
	}

	@Override
	public SlashCommandData slash() {
		return Commands.slash(getName(), "진행 중인 회의를 종료하고 요약합니다.");
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {

	}
}

package dev.yunsung.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface Command {

	String getName();

	SlashCommandData slash();

	void execute(SlashCommandInteractionEvent event);
}

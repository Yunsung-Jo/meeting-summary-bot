package dev.yunsung.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public interface SubCommand {

	String getName();

	SubcommandData getData();

	void execute(SlashCommandInteractionEvent event) throws Exception;
}

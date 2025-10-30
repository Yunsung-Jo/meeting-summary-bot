package dev.yunsung.command;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class MeetingStartCommand implements Command {

	@Override
	public String getName() {
		return "회의";
	}

	@Override
	public SlashCommandData slash() {
		OptionData channelOption = new OptionData(OptionType.CHANNEL, "채널", "녹화할 음성 채널을 선택하세요.", true)
			.setChannelTypes(ChannelType.VOICE);

		return Commands.slash(getName(), "지정한 음성 채널을 녹화합니다.")
			.addOptions(channelOption);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {

	}
}

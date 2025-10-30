package dev.yunsung.command;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import dev.yunsung.record.AudioRecorder;

public record MeetingStartCommand(AudioRecorder audioRecorder) implements Command {

	@Override
	public String getName() {
		return "회의";
	}

	@Override
	public SlashCommandData slash() {
		OptionData channelOption = new OptionData(OptionType.CHANNEL, "채널", "녹음할 음성 채널을 선택하세요.", true)
			.setChannelTypes(ChannelType.VOICE);

		return Commands.slash(getName(), "지정한 음성 채널을 녹음합니다.")
			.addOptions(channelOption);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		if (audioRecorder.canReceiveUser()) {
			event.reply(audioRecorder.getChannel().getAsMention() + "에서 회의가 진행 중입니다.").queue();
			return;
		}

		OptionMapping channelOption = event.getOption("채널");
		assert channelOption != null;

		Guild guild = event.getGuild();
		assert guild != null;

		// 음성 채널에 봇을 연결
		VoiceChannel voiceChannel = channelOption.getAsChannel().asVoiceChannel();
		guild.getAudioManager().openAudioConnection(voiceChannel);
		guild.getAudioManager().setReceivingHandler(audioRecorder);
		audioRecorder.startRecording(voiceChannel);

		// 봇 상태 변경
		event.getJDA().getPresence().setActivity(Activity.listening(voiceChannel.getName() + "에서 회의 중"));

		// 채널에 메시지 전송
		event.reply(voiceChannel.getAsMention() + "에서 회의를 시작합니다.").queue();
	}
}

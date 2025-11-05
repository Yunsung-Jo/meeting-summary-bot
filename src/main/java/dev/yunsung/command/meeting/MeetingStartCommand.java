package dev.yunsung.command.meeting;

import java.util.Objects;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import dev.yunsung.command.SubCommand;
import dev.yunsung.record.RecorderService;

public record MeetingStartCommand(RecorderService recorderService) implements SubCommand {

	@Override
	public String getName() {
		return System.getenv("MEETING_START_COMMAND");
	}

	@Override
	public SubcommandData getData() {
		OptionData channelOption = new OptionData(OptionType.CHANNEL, "채널", "녹음할 음성 채널을 선택하세요.", true)
			.setChannelTypes(ChannelType.VOICE);

		return new SubcommandData(getName(), "지정한 음성 채널을 녹음합니다.")
			.addOptions(channelOption);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		Guild guild = Objects.requireNonNull(event.getGuild());
		var audioRecorder = recorderService.getRecorder(guild);

		if (audioRecorder.isRecording()) {
			event.reply(audioRecorder.getChannel().getAsMention() + "에서 회의가 진행 중입니다.").queue();
			return;
		}

		OptionMapping channelOption = event.getOption("채널");
		assert channelOption != null;

		// 음성 채널에 봇을 연결
		VoiceChannel voiceChannel = channelOption.getAsChannel().asVoiceChannel();
		guild.getAudioManager().openAudioConnection(voiceChannel);
		guild.getAudioManager().setReceivingHandler(audioRecorder);
		audioRecorder.startRecording(voiceChannel);

		// 채널에 메시지 전송
		event.reply(voiceChannel.getAsMention() + "에서 회의를 시작합니다.").queue();
	}
}

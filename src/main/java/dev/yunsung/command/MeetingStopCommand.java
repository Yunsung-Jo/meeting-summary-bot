package dev.yunsung.command;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import dev.yunsung.record.AudioRecorder;

public record MeetingStopCommand(AudioRecorder audioRecorder) implements Command {

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
		if (!audioRecorder.canReceiveUser()) {
			event.reply("진행 중인 회의가 없습니다.").queue();
			return;
		}

		Guild guild = event.getGuild();
		assert guild != null;

		// 회의 종료
		String folderName = audioRecorder.getFolderName();
		audioRecorder.stopRecording();
		event.deferReply().queue();

		// 봇과 음성 채널의 연결을 해제
		guild.getAudioManager().closeAudioConnection();

		// 봇 상태 변경
		event.getJDA().getPresence().setActivity(Activity.playing("대기 중"));

		// TODO: wav → text 기능 구현

		// TODO: 회의 요약 기능 구현
	}
}

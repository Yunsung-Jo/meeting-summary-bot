package dev.yunsung.command;

import java.io.File;
import java.util.TreeMap;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;

import dev.yunsung.record.AudioRecorder;
import dev.yunsung.record.AudioText;
import dev.yunsung.record.CsvExporter;
import dev.yunsung.summary.Summarizer;

public record MeetingStopCommand(AudioRecorder audioRecorder, Summarizer summarizer) implements Command {

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
		event.deferReply().queue();
		String folderName = audioRecorder.getFolderName();
		audioRecorder.stopRecording();

		// 봇과 음성 채널의 연결을 해제
		guild.getAudioManager().closeAudioConnection();

		// 봇 상태 변경
		event.getJDA().getPresence().setActivity(Activity.playing("대기 중"));

		try {
			// 회의 내용 저장
			TreeMap<Long, AudioText> audioTexts = audioRecorder.getAudioTexts();
			File file = CsvExporter.saveAsCsv(audioTexts, folderName);

			// 회의 요약
			String summary = summarizer.summarize(audioTexts);
			event.getHook()
				.sendMessage(summary)
				.addFiles(FileUpload.fromData(file))
				.queue();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

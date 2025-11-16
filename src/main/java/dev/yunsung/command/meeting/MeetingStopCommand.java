package dev.yunsung.command.meeting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.FileUpload;

import dev.yunsung.command.SubCommand;
import dev.yunsung.record.AudioData;
import dev.yunsung.record.CsvExporter;
import dev.yunsung.record.DiscordAudioRecorder;
import dev.yunsung.record.RecorderService;
import dev.yunsung.summary.SummaryService;
import dev.yunsung.util.LogUtil;

public record MeetingStopCommand(RecorderService recorderService, SummaryService summaryService) implements SubCommand {

	@Override
	public String getName() {
		return System.getenv("MEETING_STOP_COMMAND");
	}

	public SubcommandData getData() {
		return new SubcommandData(getName(), "진행 중인 회의를 종료하고 요약합니다.");
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		Guild guild = Objects.requireNonNull(event.getGuild());
		var audioRecorder = recorderService.getRecorder(guild);

		if (!audioRecorder.canReceiveUser()) {
			event.reply("진행 중인 회의가 없습니다.").queue();
			return;
		}

		LogUtil log = new LogUtil();
		log.start("회의 종료를 시작합니다.");

		// 봇과 음성 채널의 연결을 해제
		guild.getAudioManager().closeAudioConnection();
		log.record("봇과 음성 채널의 연결을 해제했습니다.");
		event.deferReply().queue();

		// JDA 하트비트가 끊기지 않도록 별도의 스레드에서 비동기로 희의 종료
		CompletableFuture.runAsync(() -> {
			try {
				stop(audioRecorder, event.getHook(), log);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).thenAccept(o -> {
			recorderService.removeRecorder(guild.getIdLong());
			log.record("회의를 성공적으로 종료했습니다.");
		}).exceptionally(e -> {
			recorderService.removeRecorder(guild.getIdLong());
			event.getHook().sendMessage("회의 종료에 실패했습니다.").queue();
			LogUtil.error("회의 종료에 실패했습니다.", e);
			return null;
		});
	}

	private void stop(
		DiscordAudioRecorder audioRecorder,
		InteractionHook hook,
		LogUtil log
	) throws IOException, InterruptedException {

		String folderName = audioRecorder.getFolderName();
		audioRecorder.stopRecording();
		log.record("음성 녹음을 종료했습니다.");

		// 회의 내용 저장
		TreeMap<LocalDateTime, AudioData> archiveAudios = audioRecorder.getArchiveAudios();
		File file = CsvExporter.saveAsCsv(archiveAudios, folderName);
		log.record("회의 내용을 csv로 저장했습니다.");

		// 회의 요약
		String summary = summaryService.summarize(archiveAudios);
		if (archiveAudios.isEmpty()) {
			hook.sendMessage(summary).queue();
		} else {
			hook.sendMessage(summary)
				.addFiles(FileUpload.fromData(file))
				.queue();
		}
		log.record("회의를 요약했습니다.");

		// 요약 내용 저장
		Path resultPath = Paths.get("audio/" + folderName + "/result.txt");
		Files.writeString(resultPath, summary);
		log.record("요약 내용을 파일로 저장했습니다.");
	}
}

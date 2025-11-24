package dev.yunsung.command.meeting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.FileUpload;

import dev.yunsung.command.SubCommand;
import dev.yunsung.record.RecorderService;
import dev.yunsung.summary.SummaryService;
import dev.yunsung.util.EnvUtil;

public record MeetingSummaryCommand(RecorderService recorderService, SummaryService summaryService)
	implements SubCommand {

	@Override
	public String getName() {
		return EnvUtil.getenv("MEETING_SUMMARY_COMMAND", "요약");
	}

	@Override
	public SubcommandData getData() {
		return new SubcommandData(getName(), "진행 중인 회의를 요약합니다.");
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) throws IOException {
		Guild guild = Objects.requireNonNull(event.getGuild());
		var audioRecorder = recorderService.getRecorder(guild);

		if (!audioRecorder.canReceiveUser()) {
			event.reply("진행 중인 회의가 없습니다.").queue();
			return;
		}

		event.deferReply().queue();

		// 회의 요약
		String summary = summaryService.summarize(audioRecorder.getArchiveAudios());

		Path resultPath = Paths.get("audio/" + audioRecorder.getFolderName() + "/result.txt");
		Files.writeString(resultPath, summary);

		event.getHook()
			.sendMessage("회의를 요약했습니다.")
			.addFiles(FileUpload.fromData(resultPath))
			.queue();
	}
}

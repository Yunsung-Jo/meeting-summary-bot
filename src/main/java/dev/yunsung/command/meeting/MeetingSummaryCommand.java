package dev.yunsung.command.meeting;

import java.util.Objects;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import dev.yunsung.command.SubCommand;
import dev.yunsung.record.RecorderService;
import dev.yunsung.summary.SummaryService;

public record MeetingSummaryCommand(RecorderService recorderService, SummaryService summaryService)
	implements SubCommand {

	@Override
	public String getName() {
		return System.getenv("MEETING_SUMMARY_COMMAND");
	}

	@Override
	public SubcommandData getData() {
		return new SubcommandData(getName(), "진행 중인 회의를 요약합니다.");
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		Guild guild = Objects.requireNonNull(event.getGuild());
		var audioRecorder = recorderService.getRecorder(guild);

		if (!audioRecorder.canReceiveUser()) {
			event.reply("진행 중인 회의가 없습니다.").queue();
			return;
		}

		event.deferReply().queue();

		// 회의 요약
		String summary = summaryService.summarize(audioRecorder.getArchiveAudios());
		event.getHook().sendMessage(summary).queue();
	}
}

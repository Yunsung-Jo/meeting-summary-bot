package dev.yunsung.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import dev.yunsung.record.AudioRecorder;
import dev.yunsung.summary.Summarizer;

public record MeetingSummaryCommand(AudioRecorder audioRecorder, Summarizer summarizer) implements Command {

	@Override
	public String getName() {
		return "요약";
	}

	@Override
	public SlashCommandData slash() {
		return Commands.slash(getName(), "진행 중인 회의를 요약합니다.");
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		if (!audioRecorder.canReceiveUser()) {
			event.reply("진행 중인 회의가 없습니다.").queue();
			return;
		}

		event.deferReply().queue();

		// 회의 요약
		String summary = summarizer.summarize(audioRecorder.getAudioTexts());
		event.getHook().sendMessage(summary).queue();
	}
}

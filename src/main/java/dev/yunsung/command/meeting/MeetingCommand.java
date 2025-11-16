package dev.yunsung.command.meeting;

import java.util.List;

import dev.yunsung.command.Command;
import dev.yunsung.command.SubCommand;
import dev.yunsung.record.RecorderService;
import dev.yunsung.summary.SummaryService;
import dev.yunsung.util.EnvUtil;

public class MeetingCommand extends Command {

	private final RecorderService recorderService = new RecorderService();
	private final SummaryService summaryService = new SummaryService();

	@Override
	public String getName() {
		return EnvUtil.getenv("MEETING_COMMAND", "회의");
	}

	@Override
	protected List<SubCommand> getSubCommands() {
		return List.of(
			new MeetingStartCommand(recorderService),
			new MeetingStopCommand(recorderService, summaryService),
			new MeetingSummaryCommand(recorderService, summaryService)
		);
	}
}

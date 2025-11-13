package dev.yunsung.command.backup;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import dev.yunsung.backup.BackupService;
import dev.yunsung.command.Command;
import dev.yunsung.util.LogUtil;

public class BackupCommand extends Command {

	private static final int FILE_LIST_LIMIT = Integer.parseInt(System.getenv("FILE_LIST_LIMIT"));
	private static final Pattern FOLDER_PATTERN = Pattern.compile("^([^/\\\\:]+)-([^/\\\\:]+)-(\\d{8}-\\d{6})$");

	private final BackupService backupService = new BackupService();

	@Override
	public String getName() {
		return System.getenv("BACKUP_COMMAND");
	}

	@Override
	public String getDescription() {
		return "요약한 내용을 다른 저장소에 백업합니다.";
	}

	@Override
	public SlashCommandData getData() {
		OptionData pathOption = new OptionData(OptionType.STRING, "경로", "백업할 경로", true);
		String backupPath = System.getenv("BACKUP_PATH");
		if (backupPath != null && !backupPath.isEmpty()) {
			Arrays.stream(backupPath.split(","))
				.map(String::trim)
				.map(entry -> entry.split(":", 2))
				.filter(parts -> parts.length == 2)
				.forEach(parts -> pathOption.addChoice(parts[0].trim(), parts[1].trim()));
		}
		return super.getData()
			.addOptions(pathOption)
			.addOption(OptionType.STRING, "파일", "선택할 파일", true, true);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		if (!backupService.isUseBackup()) {
			event.reply("백업 설정이 필요합니다.").queue();
			return;
		}

		OptionMapping pathOption = event.getOption("경로");
		OptionMapping fileOption = event.getOption("파일");
		assert pathOption != null;
		assert fileOption != null;

		String folderName = fileOption.getAsString();
		Matcher matcher = FOLDER_PATTERN.matcher(folderName);
		if (!matcher.matches()) {
			event.reply("유효하지 않은 폴더명입니다. 자동 완성에서 선택해주세요.").queue();
			return;
		}

		File file = new File("audio/" + folderName + "/" + "result.txt");
		if (!file.exists()) {
			event.reply("요약 내용을 찾을 수 없습니다.").queue();
			return;
		}

		try {
			event.deferReply().queue();
			String title = matcher.group(3) + "-" + matcher.group(2);
			String summary = Files.readString(file.toPath(), StandardCharsets.UTF_8);
			String backup = backupService.backup(pathOption.getAsString(), title, summary);
			event.getHook().sendMessage("백업에 성공했습니다.\n" + backup).queue();
			LogUtil.info("백업에 성공했습니다: " + backup);
		} catch (Exception e) {
			event.getHook().sendMessage("백업에 실패했습니다.").queue();
			LogUtil.error("백업에 실패했습니다", e);
		}
	}

	@Override
	public void autoComplete(CommandAutoCompleteInteractionEvent event) {
		File root = new File("audio");

		assert event.getGuild() != null;

		event.replyChoices(
			Arrays.stream(Objects.requireNonNull(root.listFiles(File::isDirectory)))
				.filter(f -> f.getName().startsWith(event.getGuild().getName()))
				.sorted(Comparator.comparingLong(File::lastModified).reversed())
				.limit(FILE_LIST_LIMIT)
				.map(dir -> new Choice(dir.getName(), dir.getName()))
				.toList()
		).queue();
	}
}

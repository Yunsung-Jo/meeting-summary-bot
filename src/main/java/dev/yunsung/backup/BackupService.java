package dev.yunsung.backup;

import java.util.Map;

import dev.yunsung.util.EnvUtil;

public class BackupService {

	private static final Map<String, BackupStrategy> strategies = Map.of(
		"confluence", new Confluence()
	);

	private final boolean useBackup;
	private BackupStrategy backupStrategy;

	public BackupService() {
		useBackup = EnvUtil.getenv("USE_BACKUP", false);
		if (!useBackup) {
			return;
		}

		String backupStrategy = EnvUtil.getenv("BACKUP_STRATEGY");
		this.backupStrategy = strategies.entrySet().stream()
			.filter(entry -> backupStrategy.equals(entry.getKey()))
			.map(Map.Entry::getValue)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("지원하지 않는 백업 전략입니다: " + backupStrategy));
	}

	public boolean isUseBackup() {
		return useBackup;
	}

	public String backup(String path, String title, String content) throws Exception {
		return backupStrategy.backup(path, title, content);
	}
}

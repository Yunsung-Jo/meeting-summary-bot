package dev.yunsung.backup;

import java.io.IOException;

public interface BackupStrategy {

	String backup(String path, String title, String content) throws IOException, InterruptedException;
}

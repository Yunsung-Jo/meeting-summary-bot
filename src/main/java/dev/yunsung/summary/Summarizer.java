package dev.yunsung.summary;

import java.time.LocalDateTime;
import java.util.TreeMap;

import dev.yunsung.record.AudioData;

public interface Summarizer {

	String summarize(TreeMap<LocalDateTime, AudioData> archiveAudios);
}

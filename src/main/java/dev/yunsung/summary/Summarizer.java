package dev.yunsung.summary;

import java.util.TreeMap;

import dev.yunsung.record.AudioText;

public interface Summarizer {

	String summarize(TreeMap<Long, AudioText> audioTexts);
}

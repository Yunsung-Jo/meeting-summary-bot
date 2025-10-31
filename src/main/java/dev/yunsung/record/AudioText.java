package dev.yunsung.record;

import org.jetbrains.annotations.NotNull;

public record AudioText(String name, String sentence) {

	@NotNull
	@Override
	public String toString() {
		return "[" + name + "]: " + sentence;
	}
}

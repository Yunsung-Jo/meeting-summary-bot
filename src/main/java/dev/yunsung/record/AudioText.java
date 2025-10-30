package dev.yunsung.record;

import org.jetbrains.annotations.NotNull;

public record AudioText(String name, String text) {

	@NotNull
	@Override
	public String toString() {
		return "[" + name + "]: " + text;
	}
}

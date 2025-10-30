package dev.yunsung.stt;

import java.io.IOException;

public interface STT {

	String transcribe(String path) throws IOException, InterruptedException;
}

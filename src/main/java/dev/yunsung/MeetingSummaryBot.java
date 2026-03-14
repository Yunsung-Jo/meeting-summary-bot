package dev.yunsung;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
import net.dv8tion.jda.api.requests.GatewayIntent;

import club.minnced.discord.jdave.interop.JDaveSessionFactory;
import dev.yunsung.command.CommandListener;
import dev.yunsung.util.EnvUtil;
import dev.yunsung.util.LogUtil;

public class MeetingSummaryBot {

	public static CommandListener commandListener = new CommandListener();

	public static void main(String[] args) {
		String token = EnvUtil.getenv("DISCORD_TOKEN");

		if (token.isBlank()) {
			LogUtil.error("'DISCORD_TOKEN'을 찾을 수 없습니다.");
		}

		// 디스코드 봇 연결
		JDA jda = JDABuilder
			.createDefault(token)
			.setAudioModuleConfig(new AudioModuleConfig()
				.withDaveSessionFactory(new JDaveSessionFactory()))
			.enableIntents(GatewayIntent.MESSAGE_CONTENT)
			.addEventListeners(commandListener)
			.build();

		// 슬래시 명령어 등록
		commandListener.registerCommands(jda);
	}
}

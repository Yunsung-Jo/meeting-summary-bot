package dev.yunsung;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import dev.yunsung.command.CommandListener;

public class MeetingSummaryBot {

	public static CommandListener commandListener = new CommandListener();

	public static void main(String[] args) {
		String token = System.getenv("DISCORD_TOKEN");

		if (token.isBlank()) {
			System.out.println("'DISCORD_TOKEN'을 찾을 수 없습니다.");
		}

		// 디스코드 봇 연결
		JDA jda = JDABuilder
			.createDefault(token)
			.enableIntents(GatewayIntent.MESSAGE_CONTENT)
			.addEventListeners(commandListener)
			.setActivity(Activity.customStatus("대기 중"))
			.build();

		// 슬래시 명령어 등록
		commandListener.registerCommands(jda);
	}
}

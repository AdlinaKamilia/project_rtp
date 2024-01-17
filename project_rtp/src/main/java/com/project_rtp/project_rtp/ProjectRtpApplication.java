package com.project_rtp.project_rtp;

import com.project_rtp.project_rtp.telegramBot.newBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication

public class ProjectRtpApplication {

	public static void main(String[] args) throws TelegramApiException {

		SpringApplication.run(ProjectRtpApplication.class, args);
		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		newBot bot = new newBot();
		botsApi.registerBot(bot);
	}

}

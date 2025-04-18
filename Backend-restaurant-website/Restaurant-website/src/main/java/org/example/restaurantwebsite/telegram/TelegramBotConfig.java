package org.example.restaurantwebsite.telegram;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    private final RestaurantBot restaurantBot;

    public TelegramBotConfig(RestaurantBot restaurantBot) {
        this.restaurantBot = restaurantBot;
    }

    @PostConstruct
    public void start() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(restaurantBot);
            System.out.println("✅ Telegram-бот запущен");
        } catch (TelegramApiException e) {
            System.err.println("❌ Ошибка запуска Telegram-бота: " + e.getMessage());
        }
    }
}

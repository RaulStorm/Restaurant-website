package org.example.restaurantwebsite.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component
public class RestaurantBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    private ReviewApiClient reviewApiClient;

    private final Map<Long, Integer> userReviewIndexes = new HashMap<>();
    private final Map<Long, List<ReviewBot>> userReviews = new HashMap<>();

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                String callbackData = update.getCallbackQuery().getData();
                if (callbackData.equals("prev")) {
                    editReview(update, -1);
                } else if (callbackData.equals("next")) {
                    editReview(update, 1);
                }
                return;
            }

            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                Long chatId = update.getMessage().getChatId();

                switch (messageText) {
                    case "/start":
                        sendMessageWithKeyboard(chatId, "👋 Привет! Я бот ресторана. Выберите, что вас интересует:");
                        break;
                    case "О ресторане":
                        sendRestaurantInfo(chatId);
                        break;
                    case "Меню":
                        sendSimpleMessage(chatId, "📋 Меню скоро будет доступно.");
                        break;
                    case "Бронь":
                        sendSimpleMessage(chatId, "📅 Чтобы забронировать столик, напишите дату и время.");
                        break;
                    case "Оставить отзыв":
                        sendSimpleMessage(chatId, "✍️ Напишите ваш отзыв, и мы обязательно его учтём.");
                        break;
                    case "Контакты":
                        sendSimpleMessage(chatId, "📞 Телефон: +7 (900) 123-45-67\n📍 Адрес: ул. Вкусная, 7");
                        break;
                    case "Профиль":
                        sendSimpleMessage(chatId, "👤 Профиль в разработке. В будущем здесь будут ваши заказы и бонусы.");
                        break;
                    default:
                        sendSimpleMessage(chatId, "❓ Неизвестная команда. Пожалуйста, используйте кнопки ниже.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendSimpleMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageWithKeyboard(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("О ресторане"));
        row1.add(new KeyboardButton("Меню"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Бронь"));
        row2.add(new KeyboardButton("Оставить отзыв"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("Контакты"));
        row3.add(new KeyboardButton("Профиль"));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendRestaurantInfo(Long chatId) {
        String info = """
                Мы готовы предложить вам уникальный гастрономический опыт с блюдами, которые мы готовим с любовью и вниманием к деталям. Наши повара используют только свежие и качественные ингредиенты.

                В "Деливия" вас ждет уютная атмосфера, где каждый гость чувствует себя по-особенному. Мы уверены, что каждый визит станет для вас запоминающимся событием.

                Мы предлагаем как классические блюда, так и авторские творения, чтобы удовлетворить любой вкус. Обязательно посетите наше меню и узнайте о специальных предложениях.
                """;

        sendSimpleMessage(chatId, info);

        List<ReviewBot> reviews = reviewApiClient.fetchLatestPositiveReviews();
        if (!reviews.isEmpty()) {
            userReviews.put(chatId, reviews);
            userReviewIndexes.put(chatId, 0);
            sendReviewMessage(chatId, reviews.get(0));
        } else {
            sendSimpleMessage(chatId, "Пока нет отзывов с рейтингом выше 4 ⭐");
        }
    }

    private void sendReviewMessage(Long chatId, ReviewBot review) {
        String text = formatReviewText(review);
        SendMessage message = new SendMessage(chatId.toString(), text);
        message.setParseMode("Markdown");
        message.setReplyMarkup(getReviewKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void editReview(Update update, int direction) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        List<ReviewBot> reviews = userReviews.get(chatId);
        if (reviews == null || reviews.isEmpty()) return;

        int index = (userReviewIndexes.getOrDefault(chatId, 0) + direction + reviews.size()) % reviews.size();
        userReviewIndexes.put(chatId, index);
        ReviewBot review = reviews.get(index);

        String text = formatReviewText(review);

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setText(text);
        editMessage.setParseMode("Markdown");
        editMessage.setReplyMarkup(getReviewKeyboard());

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String formatReviewText(ReviewBot review) {
        String stars = "⭐".repeat(review.getRating());
        return String.format("""
                ❤️ *Вот отзывы от любимых клиентов:*

                👤 *%s*
                📅 %s
                %s
                _%s_
                """,
                review.getUserName(),
                review.getFormattedDate(),
                stars,
                review.getReviewText()
        );
    }

    private InlineKeyboardMarkup getReviewKeyboard() {
        InlineKeyboardButton prev = new InlineKeyboardButton();
        prev.setText("<<");
        prev.setCallbackData("prev");

        InlineKeyboardButton next = new InlineKeyboardButton();
        next.setText(">>");
        next.setCallbackData("next");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(prev);
        row.add(next);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(Collections.singletonList(row));
        return markup;
    }
}

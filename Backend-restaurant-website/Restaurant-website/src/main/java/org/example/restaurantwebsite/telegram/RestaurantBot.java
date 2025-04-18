package org.example.restaurantwebsite.telegram;

import org.example.restaurantwebsite.model.MenuItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
    @Autowired
    private MenuApiClient menuApiClient;

    private final Map<Long, Integer> userReviewIndexes = new HashMap<>();
    private final Map<Long, List<ReviewBot>> userReviews = new HashMap<>();
    private final Map<Long, List<MenuItemDto>> userMenuItems = new HashMap<>();
    private final Map<Long, Integer> userMenuIndexes = new HashMap<>();

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
                Long chatId = update.getCallbackQuery().getMessage().getChatId();

                // Проверяем нажатие на категорию меню
                if (callbackData.startsWith("menu_")) {
                    String category = callbackData.substring("menu_".length());
                    List<MenuItemDto> items = menuApiClient.fetchMenuByCategory(category);
                    if (!items.isEmpty()) {
                        userMenuItems.put(chatId, items);
                        userMenuIndexes.put(chatId, 0);
                        sendMenuItem(chatId, items.get(0));
                    }
                }
                // Обработка перехода к предыдущему блюду
                else if (callbackData.equals("menu_prev")) {
                    changeMenuItem(update, -1);
                }
                // Обработка перехода к следующему блюду
                else if (callbackData.equals("menu_next")) {
                    changeMenuItem(update, 1);
                }
                // Обработка отзывов
                else if (callbackData.equals("prev")) {
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
                        sendCategorySelection(chatId);
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
        SendMessage message = new SendMessage(chatId.toString(), text);
        try {
            execute(message);
        } catch (org.telegram.telegrambots.meta.exceptions.TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessageWithKeyboard(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("О ресторане");
        button1.setCallbackData("О ресторане");

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Меню");
        button2.setCallbackData("Меню");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);
        row1.add(button2);

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Бронь");
        button3.setCallbackData("Бронь");

        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText("Оставить отзыв");
        button4.setCallbackData("Оставить отзыв");

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(button3);
        row2.add(button4);

        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button5.setText("Контакты");
        button5.setCallbackData("Контакты");

        InlineKeyboardButton button6 = new InlineKeyboardButton();
        button6.setText("Профиль");
        button6.setCallbackData("Профиль");

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(button5);
        row3.add(button6);

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch ( org.telegram.telegrambots.meta.exceptions.TelegramApiException e) {
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
        } catch (org.telegram.telegrambots.meta.exceptions.TelegramApiException e) {
            throw new RuntimeException(e);
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
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setText(formatReviewText(review));
        editMessage.setParseMode("Markdown");
        editMessage.setReplyMarkup(getReviewKeyboard());
        try {
            execute(editMessage);
        } catch (org.telegram.telegrambots.meta.exceptions.TelegramApiException e) {
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
                review.getUserName(), review.getFormattedDate(), stars, review.getReviewText());
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

    private void sendCategorySelection(Long chatId) {
        List<String> categories = menuApiClient.fetchCategories();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (String category : categories) {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(category);
            btn.setCallbackData("menu_" + category);
            rows.add(Collections.singletonList(btn));
        }
        markup.setKeyboard(rows);
        SendMessage message = new SendMessage(chatId.toString(), "🍽 Выберите категорию меню:");
        message.setReplyMarkup(markup);
        try {
            execute(message);
        } catch (org.telegram.telegrambots.meta.exceptions.TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMenuItem(Long chatId, MenuItemDto item) {
        String text = String.format("""
                🍽 *%s*
                💬 _%s_
                💵 Цена: %.2f₽
                """, item.getName(), item.getDescription(), item.getPrice());

        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId.toString());
        photo.setCaption(text);
        photo.setParseMode("Markdown");
        photo.setReplyMarkup(getMenuKeyboard());

        if (item.getImages() != null && !item.getImages().isEmpty()) {
            photo.setPhoto(new InputFile(item.getImages().get(0)));
        } else {
            photo.setPhoto(new InputFile("https://via.placeholder.com/300x200.png?text=Нет+фото"));
        }

        try {
            execute(photo);
        } catch (org.telegram.telegrambots.meta.exceptions.TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void changeMenuItem(Update update, int direction) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        List<MenuItemDto> items = userMenuItems.get(chatId);
        if (items == null || items.isEmpty()) return;
        int index = (userMenuIndexes.getOrDefault(chatId, 0) + direction + items.size()) % items.size();
        userMenuIndexes.put(chatId, index);
        sendMenuItem(chatId, items.get(index));
    }

    private InlineKeyboardMarkup getMenuKeyboard() {
        InlineKeyboardButton prev = new InlineKeyboardButton();
        prev.setText("<<");
        prev.setCallbackData("menu_prev");
        InlineKeyboardButton next = new InlineKeyboardButton();
        next.setText(">>");
        next.setCallbackData("menu_next");
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(prev);
        row.add(next);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(Collections.singletonList(row));
        return markup;
    }
}

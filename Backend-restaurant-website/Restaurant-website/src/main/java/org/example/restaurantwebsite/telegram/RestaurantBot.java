package org.example.restaurantwebsite.telegram;

import org.example.restaurantwebsite.model.MenuItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
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
    @Autowired
    private MenuApiClient menuApiClient;

    private final Map<Long, String> userSelectedCategory = new HashMap<>();
    private final Map<Long, List<MenuItemDto>> categoryDishesCache = new HashMap<>();
    private final Map<Long, Integer> categoryDishIndexes = new HashMap<>();
    private final Map<Long, Integer> lastMenuMessageIds = new HashMap<>();
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
                Long chatId = update.getCallbackQuery().getMessage().getChatId();
                Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

                if (callbackData.startsWith("menuCategory_")) {
                    String category = callbackData.substring("menuCategory_".length());
                    List<MenuItemDto> filtered = menuApiClient.fetchMenuByCategory(category);
                    if (!filtered.isEmpty()) {
                        userSelectedCategory.put(chatId, category);
                        categoryDishesCache.put(chatId, filtered);
                        categoryDishIndexes.put(chatId, 0);
                        sendMenuItem(chatId, filtered.get(0));
                    } else {
                        sendSimpleMessage(chatId, "❌ Нет блюд в категории " + category);
                    }
                } else if (callbackData.equals("menu_prev")) {
                    updateMenuItem(chatId, -1);
                } else if (callbackData.equals("menu_next")) {
                    updateMenuItem(chatId, 1);
                } else if (callbackData.equals("prev")) {
                    editReview(update, -1);
                } else if (callbackData.equals("next")) {
                    editReview(update, 1);
                }
                return;
            }

            if (update.hasMessage() && update.getMessage().hasText()) {
                Long chatId = update.getMessage().getChatId();
                String text = update.getMessage().getText();

                switch (text) {
                    case "/start" -> sendMainMenu(chatId);
                    case "О ресторане" -> sendRestaurantInfo(chatId);
                    case "Меню" -> sendCategorySelection(chatId);
                    case "Бронь" -> sendSimpleMessage(chatId, "📅 Чтобы забронировать столик, напишите дату и время.");
                    case "Оставить отзыв" -> sendSimpleMessage(chatId, "✍️ Напишите ваш отзыв, и мы обязательно его учтём.");
                    case "Контакты" -> sendSimpleMessage(chatId, "📞 Телефон: +7 (900) 123-45-67\n📍 Адрес: ул. Вкусная, 7");
                    case "Профиль" -> sendSimpleMessage(chatId, "👤 Профиль в разработке.");
                    default -> sendSimpleMessage(chatId, "❓ Неизвестная команда.");
                }
            }
        } catch (Exception e) {
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
    private void sendMainMenu(Long chatId) {
        SendMessage message = new SendMessage(chatId.toString(), "👋 Привет! Я бот ресторана. Выберите, что вас интересует:");

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> rows = new ArrayList<>();

        rows.add(new KeyboardRow(List.of(new KeyboardButton("О ресторане"), new KeyboardButton("Меню"))));
        rows.add(new KeyboardRow(List.of(new KeyboardButton("Бронь"), new KeyboardButton("Оставить отзыв"))));
        rows.add(new KeyboardRow(List.of(new KeyboardButton("Контакты"), new KeyboardButton("Профиль"))));

        keyboard.setKeyboard(rows);
        message.setReplyMarkup(keyboard);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendSimpleMessage(Long chatId, String text) {
        try {
            execute(new SendMessage(chatId.toString(), text));
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
        InlineKeyboardButton prev = new InlineKeyboardButton("<<");
        prev.setCallbackData("prev");
        InlineKeyboardButton next = new InlineKeyboardButton(">>");
        next.setCallbackData("next");
        List<InlineKeyboardButton> row = List.of(prev, next);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(row));
        return markup;
    }

    private void sendCategorySelection(Long chatId) {
        List<String> categories = menuApiClient.fetchCategories();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (String category : categories) {
            InlineKeyboardButton btn = new InlineKeyboardButton(category);
            btn.setCallbackData("menuCategory_" + category);
            rows.add(Collections.singletonList(btn));
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);
        SendMessage message = new SendMessage(chatId.toString(), "🍽 Выберите категорию меню:");
        message.setReplyMarkup(markup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMenuItem(Long chatId, MenuItemDto item) {
        // Удалить предыдущее сообщение, если было
        if (lastMenuMessageIds.containsKey(chatId)) {
            Integer lastMessageId = lastMenuMessageIds.get(chatId);
            try {
                execute(new org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage(chatId.toString(), lastMessageId));
            } catch (TelegramApiException e) {
                e.printStackTrace(); // если не удалось удалить — просто идём дальше
            }
        }

        String text = String.format("""
            🍽 *%s*
            💬 _%s_
            💵 Цена: %.2f₽
            """, item.getName(), item.getDescription(), item.getPrice());

        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId.toString());
        photo.setParseMode("Markdown");
        photo.setCaption(text);
        photo.setReplyMarkup(getMenuKeyboard());

        String imageUrl = (item.getImages() != null && !item.getImages().isEmpty())
                ? item.getImages().get(0)
                : "https://via.placeholder.com/300x200.png?text=Нет+фото";

        photo.setPhoto(new InputFile(imageUrl));

        try {
            org.telegram.telegrambots.meta.api.objects.Message sentMessage = execute(photo);
            lastMenuMessageIds.put(chatId, sentMessage.getMessageId()); // сохраняем ID нового фото
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
    private void updateMenuItem(Long chatId, int direction) {
        List<MenuItemDto> dishes = categoryDishesCache.get(chatId);
        if (dishes == null || dishes.isEmpty()) return;

        int currentIndex = categoryDishIndexes.getOrDefault(chatId, 0);
        int newIndex = (currentIndex + direction + dishes.size()) % dishes.size();
        categoryDishIndexes.put(chatId, newIndex);

        sendMenuItem(chatId, dishes.get(newIndex));
    }
}
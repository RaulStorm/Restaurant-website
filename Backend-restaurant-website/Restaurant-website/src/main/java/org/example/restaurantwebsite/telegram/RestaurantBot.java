package org.example.restaurantwebsite.telegram;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.Date;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.example.restaurantwebsite.dto.*;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.example.restaurantwebsite.model.*;
import org.example.restaurantwebsite.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    @Autowired
    private AuthService authService;
    @Autowired
    private RestaurantTableRepository restaurantTableRepository;
    private static final String CANCEL_COMMAND = "❌ Отменить";
    private final Map<Long, String> userSelectedCategory = new HashMap<>();
    private final Map<Long, List<MenuItemDto>> categoryDishesCache = new HashMap<>();
    private final Map<Long, Integer> categoryDishIndexes = new HashMap<>();
    private final Map<Long, Integer> lastMenuMessageIds = new HashMap<>();
    private final Map<Long, Integer> userReviewIndexes = new HashMap<>();
    private final Map<Long, List<ReviewBot>> userReviews = new HashMap<>();
    private final Map<Long, String> userTokens = new HashMap<>();
    private final Set<Long> awaitingCredentials = new HashSet<>();
    private final Map<Long, ReservationDto> pendingReservations = new HashMap<>();
    //после
    private final Map<Long, List<RestaurantTableDto>> availableTablesCache = new HashMap<>();
    private final Map<Long, String> reservationDateCache = new HashMap<>();
    private final Map<Long, Integer> reservationDurationCache = new HashMap<>();
    private final Map<Long, Integer> reservationPeopleCache = new HashMap<>();
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile("^\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}$");
    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private void sendReservationRequestToServer(Long chatId, ReservationDto reservation) {
        String token = userTokens.get(chatId);
        if (token == null) {
            sendSimpleMessage(chatId, "❌ Ошибка авторизации. Попробуйте снова.");
            return;
        }

        try {
            // Формируем JSON с правильным форматом даты
            String json = String.format(
                    "{\"name\":\"%s\"," +
                            "\"table\":{\"id\":%d}," +
                            "\"reservationTime\":\"%s\"," +  // Уже в правильном формате
                            "\"numberOfPeople\":%d}",
                    reservation.getName(),
                    reservation.getTable().getId(),
                    reservation.getReservationTime(),  // "2025-12-30T19:00"
                    reservation.getNumberOfPeople()
            );

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8080/api/reserve"))
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + token)
                                    .POST(HttpRequest.BodyPublishers.ofString(json))
                                    .build(),
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() == 200) {
                pendingReservations.remove(chatId); // Очищаем состояние
                sendSuccess(chatId, "✅ Столик успешно забронирован!");
                sendMainMenu(chatId); // Возвращаем в главное меню
            } else {
                sendSimpleMessage(chatId, "❌ Ошибка при бронировании: " + response.body());
            }
        } catch (Exception e) {
            sendSimpleMessage(chatId, "⚠️ Ошибка при бронировании столика. Пожалуйста, попробуйте позже.");
        }
    }

    private void resetUserState(Long chatId) {
        pendingReservations.remove(chatId);
        awaitingCredentials.remove(chatId);
        // Другие состояния при необходимости
    }

    // ========== Callback & Commands Handling ==========

    private void handleCallback(Update update) throws TelegramApiException {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        switch (callbackData) {
            case "show_auth_options":
                sendAuthOptions(chatId);
                break;
            case "auth_login":
                awaitingCredentials.add(chatId);
                sendSimpleMessage(chatId, "🔑 Введите ваш email и пароль через пробел:\n\nПример: user@example.com mypassword123");
                break;
            case "auth_register":
                awaitingCredentials.add(chatId);
                sendSimpleMessage(chatId, "📝 Введите данные для регистрации в формате:\nИмя email пароль\n\nПример: Иван user@example.com mypassword123");
                break;
            case "menu_prev":
                updateMenuItem(chatId, -1);
                break;
            case "menu_next":
                updateMenuItem(chatId, 1);
                break;
            case "prev":
                editReview(update, -1);
                break;
            case "next":
                editReview(update, 1);
                break;
            case "showAll":
                sendAllDishes(chatId);
                break;
            default:
                if (callbackData.startsWith("menuCategory_")) {
                    handleMenuCategorySelection(chatId, callbackData.substring("menuCategory_".length()));
                }
        }
    }

    private void handleCommand(Long chatId, String text) throws TelegramApiException {
        switch (text) {
            case "/start":
                sendMainMenu(chatId);
                break;
            case "ℹ️ О ресторане":
                sendRestaurantInfo(chatId);
                break;
            case "🍽 Меню":
                sendCategorySelection(chatId);
                break;
            case "🔑 Авторизоваться":
                sendAuthOptions(chatId);
                break;
            case "🚪 Выйти":
                handleLogout(chatId);
                break;
            case "🛎 Бронь столика":
                handleReservationCommand(chatId);
                break;
            case "✍️ Оставить отзыв":
                handleReviewCommand(chatId);
                break;
            case "📞 Контакты":
                handleContactsCommand(chatId);
                break;
            case "👤 Профиль":
                handleProfileCommand(chatId);
                break;
            default:
                if (text.matches("^[1-5]\\s.+") && userTokens.containsKey(chatId)) {
                    handleReviewSubmission(chatId, text);
                } else {
                    sendSimpleMessage(chatId, "❌ Неизвестная команда. Используйте меню ниже.");
                }
        }
    }

    private void handleLogout(Long chatId) throws TelegramApiException {
        if (!userTokens.containsKey(chatId)) {
            sendSimpleMessage(chatId, "ℹ️ Вы не авторизованы");
            return;
        }

        userTokens.remove(chatId);
        awaitingCredentials.remove(chatId);

        // Обновляем меню
        sendMainMenu(chatId);
        sendSimpleMessage(chatId, "✅ Вы успешно вышли из системы");
    }

    private void handleReviewCommand(Long chatId) throws TelegramApiException {
        if (!userTokens.containsKey(chatId)) {
            sendAuthOptions(chatId);
            return;
        }

        execute(createMessageWithCancel(chatId,
                "✍️ Напишите отзыв в формате: [Оценка 1-5] [Текст]\nПример: 5 Отличный ресторан!"));
    }

    private void handleProfileCommand(Long chatId) throws TelegramApiException {
        if (!userTokens.containsKey(chatId)) {
            sendSimpleMessage(chatId, "🔒 Для просмотра профиля необходимо авторизоваться.");
            sendAuthOptions(chatId);
            return;
        }

        UserDto userInfo = authService.getUserInfo(userTokens.get(chatId));
        if (userInfo == null) {
            sendSimpleMessage(chatId, "❌ Не удалось загрузить информацию о профиле.");
            return;
        }

        List<ReservationWithIdDto> reservations = authService.getUserReservations(userTokens.get(chatId));
        ReviewResponse lastReview = authService.getUserLastReview(userTokens.get(chatId));
        List<OrderResponse> orders = authService.getUserOrders(userTokens.get(chatId));
        List<MenuItemDto> favoriteDishes = authService.getFavoriteDishes(userTokens.get(chatId));

        StringBuilder profileText = new StringBuilder();
        profileText.append(String.format(
                "👤 Ваш профиль:\n\n" +
                        "Имя: %s\n" +
                        "Email: %s\n\n",
                userInfo.getName(), userInfo.getEmail()));

        if (lastReview != null && lastReview.getReviewText() != null) {
            profileText.append("📝 Ваш последний отзыв:\n");
            profileText.append("⭐ Оценка: ").append(lastReview.getRating()).append("\n");
            profileText.append("💬 Текст: ").append(lastReview.getReviewText()).append("\n");
            profileText.append("📅 Дата: ").append(lastReview.getFormattedDate()).append("\n\n");
        } else {
            profileText.append("📝 У вас пока нет отзывов\n\n");
        }

        if (!favoriteDishes.isEmpty()) {
            profileText.append("🍽 Ваши любимые блюда:\n");
            for (MenuItemDto dish : favoriteDishes) {
                profileText.append(String.format(
                        "• %s (%s) - %.0f₽\n",
                        dish.getName(),
                        dish.getCategoryName(),
                        dish.getPrice()
                ));
            }
            profileText.append("\n");
        } else {
            profileText.append("🍽 У вас пока нет любимых блюд\n\n");
        }

        if (!orders.isEmpty()) {
            profileText.append("🛒 Ваши последние заказы:\n");
            for (OrderResponse order : orders) {
                profileText.append("📦 Заказ #").append(order.getId()).append("\n");
                for (ItemDto item : order.getItems()) {
                    profileText.append(String.format(
                            "  - %s x%d\n",
                            item.getName(),
                            item.getQuantity()
                    ));
                }
                profileText.append("\n");
            }
        } else {
            profileText.append("🛒 У вас пока нет заказов\n\n");
        }

        if (reservations.isEmpty()) {
            profileText.append("🛎 У вас нет активных бронирований\n");
        } else {
            profileText.append("🛎 Ваши бронирования:\n\n");

            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat fullDateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            SimpleDateFormat onlyTimeFormat = new SimpleDateFormat("HH:mm");

            for (ReservationWithIdDto reservation : reservations) {
                try {
                    Date startDate = apiFormat.parse(reservation.getReservationTime());
                    int duration = (reservation.getDurationHours() != null) ? reservation.getDurationHours() : 3;

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startDate);
                    cal.add(Calendar.HOUR_OF_DAY, duration);
                    Date endDate = cal.getTime();

                    profileText.append(String.format(
                            "📅 Дата: %s - %s\n" +
                                    "👥 Гости: %d\n" +
                                    "💁 Имя: %s\n" +
                                    "🪑 Стол: %s (ID: %d)\n" +
                                    "❌ Отменить: /cancel_%d\n\n",
                            fullDateTimeFormat.format(startDate),
                            onlyTimeFormat.format(endDate),
                            reservation.getNumberOfPeople(),
                            reservation.getName(),
                            reservation.getTable().getTableNumber(),
                            reservation.getTable().getId(),
                            reservation.getId()
                    ));
                } catch (ParseException e) {
                    log.error("Ошибка парсинга даты бронирования", e);
                }
            }
        }

        sendSimpleMessage(chatId, profileText.toString());
    }

    private void handleCancelReservation(Long chatId, Long reservationId) {
        try {
            ResponseEntity<String> response = authService.cancelReservation(
                    userTokens.get(chatId),
                    reservationId
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                sendSimpleMessage(chatId, "✅ Бронь #" + reservationId + " успешно отменена!");
                // Обновляем профиль после отмены
                handleProfileCommand(chatId);
            } else {
                sendSimpleMessage(chatId, "❌ Ошибка при отмене брони: " + response.getBody());
            }
        } catch (Exception e) {
            sendSimpleMessage(chatId, "⚠️ Ошибка при обработке запроса. Пожалуйста, попробуйте позже.");
        }
    }

    // ========== Authorization Methods ==========

    private void sendAuthOptions(Long chatId) throws TelegramApiException {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton loginBtn = new InlineKeyboardButton("🔐 Войти");
        loginBtn.setCallbackData("auth_login");
        row1.add(loginBtn);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton registerBtn = new InlineKeyboardButton("📝 Регистрация");
        registerBtn.setCallbackData("auth_register");
        row2.add(registerBtn);

        markup.setKeyboard(List.of(row1, row2));

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Выберите действие:");
        message.setReplyMarkup(markup);

        execute(message);
    }

    // ========== Menu Methods ==========

    private void handleMenuCategorySelection(Long chatId, String category) {
        List<MenuItemDto> filtered = menuApiClient.fetchMenuByCategory(category);
        if (!filtered.isEmpty()) {
            userSelectedCategory.put(chatId, category);
            categoryDishesCache.put(chatId, filtered);
            categoryDishIndexes.put(chatId, 0);
            sendMenuItem(chatId, filtered.get(0));
        } else {
            sendSimpleMessage(chatId, "❌ Нет блюд в категории " + category);
        }
    }

    private void sendCategorySelection(Long chatId) {
        List<String> categories = menuApiClient.fetchCategories();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (String category : categories) {
            InlineKeyboardButton btn = new InlineKeyboardButton(category);
            btn.setCallbackData("menuCategory_" + category);
            rows.add(Collections.singletonList(btn));
        }

        InlineKeyboardButton showAllBtn = new InlineKeyboardButton("Все блюда");
        showAllBtn.setCallbackData("showAll");
        rows.add(Collections.singletonList(showAllBtn));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);
        SendMessage message = new SendMessage(chatId.toString(), "🍽 Выберите категорию меню:");
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendAllDishes(Long chatId) {
        Map<String, List<MenuItemDto>> allDishesGrouped = menuApiClient.fetchAllMenuItemsGroupedByCategory();

        if (allDishesGrouped.isEmpty()) {
            sendSimpleMessage(chatId, "❌ Нет доступных блюд.");
            return;
        }

        StringBuilder messageText = new StringBuilder("🍽 Все блюда:\n\n");

        for (String category : allDishesGrouped.keySet()) {
            messageText.append("\n🍴 ").append(category).append(":\n");

            List<MenuItemDto> categoryDishes = allDishesGrouped.get(category);
            for (MenuItemDto dish : categoryDishes) {
                messageText.append(String.format(
                        "🍽 %s\n💬 %s\n💵 Цена: %.0f₽\n\n",
                        dish.getName(),
                        dish.getDescription(),
                        dish.getPrice()
                ));
            }
        }

        sendSimpleMessage(chatId, messageText.toString());
    }

    private void sendMenuItem(Long chatId, MenuItemDto item) {
        if (lastMenuMessageIds.containsKey(chatId)) {
            try {
                execute(new DeleteMessage(chatId.toString(), lastMenuMessageIds.get(chatId)));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        String text = String.format(
                "🍽 %s\n💬 %s\n💵 Цена: %.2f₽",
                item.getName(),
                item.getDescription(),
                item.getPrice()
        );

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
            lastMenuMessageIds.put(chatId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup getMenuKeyboard() {
        InlineKeyboardButton prev = new InlineKeyboardButton("<<");
        prev.setCallbackData("menu_prev");

        InlineKeyboardButton next = new InlineKeyboardButton(">>");
        next.setCallbackData("menu_next");

        return new InlineKeyboardMarkup(List.of(List.of(prev, next)));
    }

    private void updateMenuItem(Long chatId, int direction) {
        List<MenuItemDto> dishes = categoryDishesCache.get(chatId);
        if (dishes == null || dishes.isEmpty()) return;

        int currentIndex = categoryDishIndexes.getOrDefault(chatId, 0);
        int newIndex = (currentIndex + direction + dishes.size()) % dishes.size();
        categoryDishIndexes.put(chatId, newIndex);

        sendMenuItem(chatId, dishes.get(newIndex));
    }

    // ========== Review Methods ==========

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
        return String.format(
                "❤️ *Вот отзывы от любимых клиентов:*\n\n" +
                        "👤 *%s*\n" +
                        "📅 %s\n" +
                        "%s\n" +
                        "_%s_",
                review.getUserName(),
                review.getFormattedDate(),
                stars,
                review.getReviewText()
        );
    }

    private InlineKeyboardMarkup getReviewKeyboard() {
        InlineKeyboardButton prev = new InlineKeyboardButton("<<");
        prev.setCallbackData("prev");

        InlineKeyboardButton next = new InlineKeyboardButton(">>");
        next.setCallbackData("next");

        return new InlineKeyboardMarkup(List.of(List.of(prev, next)));
    }

    private void handleReviewSubmission(Long chatId, String text) throws TelegramApiException {
        if (text.equals(CANCEL_COMMAND)) {
            sendMainMenu(chatId);
            sendSimpleMessage(chatId, "❌ Создание отзыва отменено");
            return;
        }

        try {
            int rating = Integer.parseInt(text.substring(0, 1));
            String reviewText = text.substring(1).trim();

            // Проверка на нецензурную лексику
//            if (containsProfanity(reviewText)) {
//                sendSimpleMessage(chatId, "❌ Ваш отзыв содержит недопустимые слова. Пожалуйста, измените текст.");
//                return;
//            }

            ReviewDto reviewDto = new ReviewDto();
            reviewDto.setRating(rating);
            reviewDto.setReviewText(reviewText);

            ResponseEntity<?> response = reviewApiClient.submitReview(reviewDto, userTokens.get(chatId));

            if (response.getStatusCode().is2xxSuccessful()) {
                sendSimpleMessage(chatId, "✅ Спасибо за ваш отзыв!");
            } else {
                sendSimpleMessage(chatId, "❌ Ошибка при сохранении отзыва.");
            }
//        } catch (IOException e) {
//            sendSimpleMessage(chatId, "⚠️ Ошибка проверки текста. Попробуйте позже.");
        } catch (Exception e) {
            sendSimpleMessage(chatId, "⚠️ Неверный формат отзыва. Пример: '5 Отличный ресторан'");
        }
    }

    // ========== Utility Methods ==========

    private void sendMainMenu(Long chatId) throws TelegramApiException {
        boolean isAuthenticated = userTokens.containsKey(chatId);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        keyboard.setSelective(true);

        List<KeyboardRow> rows = new ArrayList<>();

        // Первая строка
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("🍽 Меню"));
        row1.add(new KeyboardButton("ℹ️ О ресторане"));
        rows.add(row1);

        // Вторая строка
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("🛎 Бронь столика"));
        row2.add(new KeyboardButton("✍️ Оставить отзыв"));
        rows.add(row2);

        // Третья строка
        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("📞 Контакты"));
        row3.add(new KeyboardButton(isAuthenticated ? "👤 Профиль" : "🔑 Авторизоваться"));
        rows.add(row3);

        // Если авторизован, добавляем кнопку выхода
        if (isAuthenticated) {
            KeyboardRow row4 = new KeyboardRow();
            row4.add(new KeyboardButton("🚪 Выйти"));
            rows.add(row4);
        }

        // Добавляем кнопку отмены в активные процессы
        if (pendingReservations.containsKey(chatId) || awaitingCredentials.contains(chatId)) {
            KeyboardRow cancelRow = new KeyboardRow();
            cancelRow.add(new KeyboardButton(CANCEL_COMMAND));
            rows.add(cancelRow);
        }

        keyboard.setKeyboard(rows);

        String greeting = isAuthenticated
                ? "👋 С возвращением! Чем могу помочь?"
                : "👋 Добро пожаловать! Для доступа ко всем функциям авторизуйтесь";

        SendMessage message = new SendMessage(chatId.toString(), greeting);
        message.setReplyMarkup(keyboard);
        execute(message);
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

    private void handleContactsCommand(Long chatId) throws TelegramApiException {
        try {
            // Отправляем текстовую информацию с контактами
            sendSimpleMessage(chatId, """
                    🏢 *Ресторан «Деливия»*
                    
                    📍 *Адрес:* г. Воронеж, ул. Примерная, д.1
                    🕒 *Часы работы:* 10:00 - 23:00 (без выходных)
                    📞 *Телефон:* +7 (951) 567-83-73
                    ✉️ *Email:* info@delivia.ru
                    """);

            // Отправляем статическое изображение карты
            sendMapImage(chatId);

            SendLocation location = new SendLocation();
            location.setChatId(chatId.toString());
            location.setLatitude(51.6615);
            location.setLongitude(39.2003);
            location.setLivePeriod(3600);
            execute(location);

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

            InlineKeyboardButton routeBtn = new InlineKeyboardButton("🚖 Построить маршрут");
            routeBtn.setUrl("https://yandex.ru/maps/?pt=39.2003,51.6615&z=15&l=map&rtext=~51.6615,39.2003");

            // Кнопка сайта
            InlineKeyboardButton websiteBtn = new InlineKeyboardButton("🌐 Наш сайт");
            //http://localhost:5500/index.html
            websiteBtn.setUrl("https://delivia.ru"); // Замените на реальный URL

            markup.setKeyboard(List.of(
                    List.of(routeBtn),
                    List.of(websiteBtn)
            ));

            SendMessage message = new SendMessage(chatId.toString(), "Выберите действие:");
            message.setReplyMarkup(markup);
            message.setParseMode("Markdown");
            execute(message);

        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке контактов", e);
            sendSimpleMessage(chatId, "⚠️ Произошла ошибка при отправке контактной информации. Пожалуйста, попробуйте позже.");
        }
    }

    private void sendMapImage(Long chatId) throws TelegramApiException {
        try {
            // URL статического изображения карты из Яндекс.Карт
            String mapUrl = "https://static-maps.yandex.ru/1.x/?ll=39.2003,51.6615&size=600,300&z=15&l=map&pt=39.2003,51.6615,pm2rdl";

            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId.toString());
            photo.setPhoto(new InputFile(mapUrl));
            photo.setCaption("📍 Мы находимся здесь!");
            execute(photo);
        } catch (TelegramApiException e) {
            log.warn("Не удалось отправить изображение карты", e);
            // Если не удалось отправить фото, отправляем ссылку
            sendSimpleMessage(chatId, "📍 Ссылка на карту: https://yandex.ru/maps/?pt=39.2003,51.6615&z=15&l=map");
        }
    }

    private void sendSuccess(Long chatId, String message) {
        try {
            SendMessage msg = new SendMessage(chatId.toString(), "✅ " + message);
            msg.setParseMode("Markdown");
            execute(msg);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    private SendMessage createMessageWithCancel(Long chatId, String text) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);

        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(CANCEL_COMMAND));

        keyboard.setKeyboard(List.of(row));

        SendMessage message = new SendMessage(chatId.toString(), text);
        message.setReplyMarkup(keyboard);
        return message;
    }

    // Метод для проверки валидности email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private boolean containsProfanity(String text) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String encodedText = java.net.URLEncoder.encode(text, "UTF-8");
        String apiUrl = "https://api.api-ninjas.com/v1/profanityfilter?text=" + encodedText;

        HttpGet request = new HttpGet(apiUrl);
        request.setHeader("X-Api-Key", "eisuPl+PoXUgT20d4sR3rw==EMRv5XGYaiA3wAlu"); // Замените на реальный ключ

        try {
            org.apache.http.HttpResponse response = httpClient.execute(request);
            String jsonResponse = EntityUtils.toString(response.getEntity());

            // Пример ответа: {"original":"bad word","filtered":"**** word"}
            return !jsonResponse.contains("\"filtered\":\"" + text + "\"");
        } finally {
            httpClient.close();
        }

    }

    private void handleCredentialsInput(Long chatId, String input) throws TelegramApiException {
        if (input.equals(CANCEL_COMMAND)) {
            awaitingCredentials.remove(chatId);
            sendMainMenu(chatId);
            sendSimpleMessage(chatId, "❌ Авторизация отменена");
            return;
        }

        try {
            if (input.split("\\s+").length == 2) {
                // Логин
                String[] credentials = input.split("\\s+", 2);
                if (!isValidEmail(credentials[0])) {
                    sendSimpleMessage(chatId, "❌ Неверный формат email. Пожалуйста, введите корректный email.");
                    // Не удаляем awaitingCredentials, чтобы продолжать ожидать ввод
                    return;
                }
                handleLogin(chatId, credentials[0], credentials[1]);
            } else if (input.split("\\s+").length >= 3) {
                // Регистрация
                String[] parts = input.split("\\s+", 3);
                if (!isValidEmail(parts[1])) {
                    sendSimpleMessage(chatId, "❌ Неверный формат email. Пожалуйста, введите корректный email.");
                    // Не удаляем awaitingCredentials, чтобы продолжать ожидать ввод
                    return;
                }
                handleRegistration(chatId, parts[0], parts[1], parts[2]);
            } else {
                sendSimpleMessage(chatId, """
                        ❌ Неверный формат. 
                        Для входа: email пароль
                        Для регистрации: имя email пароль
                        """);
                // Не удаляем awaitingCredentials, чтобы продолжать ожидать ввод
            }
        } catch (Exception e) {
            sendSimpleMessage(chatId, "⚠️ Ошибка обработки данных. Попробуйте снова.");
            // Не удаляем awaitingCredentials, чтобы продолжать ожидать ввод
        }
    }

    private void handleLogin(Long chatId, String email, String password) {
        try {
            String token = authService.login(email, password);
            if (token != null) {
                userTokens.put(chatId, token);
                awaitingCredentials.remove(chatId); // Успешная авторизация - сбрасываем ожидание
                sendSimpleMessage(chatId, "✅ Вход выполнен успешно!");
                sendMainMenu(chatId);
            } else {
                sendSimpleMessage(chatId, "❌ Неверный email или пароль. Попробуйте снова или введите '" + CANCEL_COMMAND + "' для отмены.");
                // Оставляем awaitingCredentials для повторной попытки
            }
        } catch (Exception e) {
            sendSimpleMessage(chatId, "⚠️ Ошибка при входе. Попробуйте позже.");
            // Оставляем awaitingCredentials для повторной попытки
        }
    }

    private void handleRegistration(Long chatId, String name, String email, String password) {
        try {
            String token = authService.register(name, email, password);
            if (token != null) {
                userTokens.put(chatId, token);
                awaitingCredentials.remove(chatId); // Успешная регистрация - сбрасываем ожидание
                sendSimpleMessage(chatId, "✅ Регистрация прошла успешно! Вы автоматически вошли в систему.");
                sendMainMenu(chatId);
            } else {
                sendSimpleMessage(chatId, "❌ Ошибка регистрации. Возможно, email уже занят. Попробуйте снова или введите '" + CANCEL_COMMAND + "' для отмены.");
                // Оставляем awaitingCredentials для повторной попытки
            }
        } catch (Exception e) {
            sendSimpleMessage(chatId, "⚠️ Ошибка при регистрации. Попробуйте позже.");
            // Оставляем awaitingCredentials для повторной попытки
        }
    }

    private void resetReservationState(Long chatId) {
        pendingReservations.remove(chatId);
        availableTablesCache.remove(chatId);
        reservationDateCache.remove(chatId);
        reservationDurationCache.remove(chatId);
        reservationPeopleCache.remove(chatId);
    }

    private void handleReservationName(Long chatId, String name) {
        if (name.trim().isEmpty()) {
            sendSimpleMessage(chatId, "❌ Имя не может быть пустым. Введите снова:");
            return;
        }

        pendingReservations.get(chatId).setName(name.trim());
        confirmReservation(chatId);
    }

    private InlineKeyboardButton createInlineButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private void confirmReservation(Long chatId) {
        try {
            ReservationDto reservation = pendingReservations.get(chatId);
            int duration = reservationDurationCache.getOrDefault(chatId, 3);
            String token = userTokens.get(chatId);

            if (token == null) {
                sendSimpleMessage(chatId, "❌ Ошибка авторизации. Пожалуйста, войдите снова.");
                return;
            }

            // Получаем полную информацию о столике для отображения мест
            RestaurantTable table = restaurantTableRepository.findById(reservation.getTable().getId())
                    .orElseThrow(() -> new Exception("Столик не найден"));

            SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date date = serverFormat.parse(reservation.getReservationTime());

            Calendar endTime = Calendar.getInstance();
            endTime.setTime(date);
            endTime.add(Calendar.HOUR, duration);

            String confirmation = String.format(
                    "✅ Подтвердите бронирование:\n\n" +
                            "📅 Дата: %s\n" +
                            "⏳ Продолжительность: %d часа\n" +
                            "🕒 До: %s\n" +
                            "👥 Гости: %d\n" +
                            "💁 Имя: %s\n" +
                            "🪑 Столик: #%s (%d мест)",
                    displayFormat.format(date),
                    duration,
                    displayFormat.format(endTime.getTime()),
                    reservation.getNumberOfPeople(),
                    reservation.getName(),
                    table.getTableNumber(),
                    table.getSeats()
            );

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(List.of(
                    List.of(createInlineButton("✅ Подтвердить", "confirm_reservation")),
                    List.of(createInlineButton("❌ Отменить", "cancel_reservation"))
            ));

            SendMessage message = new SendMessage(chatId.toString(), confirmation);
            message.setReplyMarkup(markup);
            execute(message);

        } catch (Exception e) {
            log.error("Error confirming reservation", e);
            sendSimpleMessage(chatId, "❌ Ошибка при подтверждении брони. Попробуйте снова.");
        }
    }


    //=======fdb
    private void updateReservationTime(Long chatId, String newTime) {
        try {
            reservationDateCache.put(chatId, newTime);

            SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date date = inputFormat.parse(newTime);

            ReservationDto reservation = pendingReservations.get(chatId);
            reservation.setReservationTime(serverFormat.format(date));

            int people = reservation.getNumberOfPeople();
            int duration = reservationDurationCache.getOrDefault(chatId, 3);
            List<RestaurantTableDto> tables = findAvailableTables(chatId, people, newTime, duration);

            if (tables.isEmpty()) {
                suggestAlternativeTimes(chatId, people, duration);
            } else {
                availableTablesCache.put(chatId, tables);
                showAvailableTables(chatId);
            }
        } catch (ParseException e) {
            sendSimpleMessage(chatId, "❌ Неверный формат времени. Используйте ДД.ММ.ГГГГ ЧЧ:ММ");
        } catch (Exception e) {
            log.error("Error updating reservation time", e);
            sendSimpleMessage(chatId, "⚠️ Ошибка при обновлении времени. Попробуйте снова.");
        }
    }

    private void startReservationProcess(Long chatId) throws TelegramApiException {
        pendingReservations.put(chatId, new ReservationDto());
        sendSimpleMessage(chatId, "📅 Введите дату и время бронирования в формате: ДД.ММ.ГГГГ ЧЧ:ММ\n\nПример: 25.12.2023 19:30");
    }

    private boolean validateAndSaveDate(Long chatId, String dateStr) {
        try {
            if (!DATE_TIME_PATTERN.matcher(dateStr).matches()) {
                sendSimpleMessage(chatId, "❌ Неверный формат даты. Используйте ДД.ММ.ГГГГ ЧЧ:ММ\nПример: 30.12.2025 19:00");
                return false;
            }

            SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            inputFormat.setLenient(false);
            Date date = inputFormat.parse(dateStr);

            if (date.before(new Date())) {
                sendSimpleMessage(chatId, "❌ Нельзя забронировать на прошедшую дату. Введите корректную дату:");
                return false;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if (hour < 10 || hour >= 23) {
                sendSimpleMessage(chatId, "❌ Ресторан работает с 10:00 до 23:00. Выберите время в этом интервале.");
                return false;
            }

            // --- Вот это ключевое исправление ---
            // Конвертируем сразу дату в формат сервера и сохраняем
            SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            String serverDateStr = serverFormat.format(date);

            pendingReservations.get(chatId).setReservationTime(serverDateStr);

            // Если хочешь, можешь также сохранить пользовательский текст для показа
            reservationDateCache.put(chatId, dateStr);

            return true;
        } catch (ParseException e) {
            sendSimpleMessage(chatId, "❌ Неверная дата или время. Используйте ДД.ММ.ГГГГ ЧЧ:ММ\nПример: 30.12.2025 19:00");
            return false;
        }
    }

    private void handleReservationCommand(Long chatId) throws TelegramApiException {
        if (!userTokens.containsKey(chatId)) {
            sendAuthOptions(chatId);
            return;
        }

        if (!pendingReservations.containsKey(chatId)) {
            startReservationProcess(chatId);
        } else {
            // Продолжаем существующий процесс
            ReservationDto res = pendingReservations.get(chatId);
            if (res.getReservationTime() == null) {
                sendSimpleMessage(chatId, "📅 Введите дату и время бронирования (ДД.ММ.ГГГГ ЧЧ:ММ):");
            } else if (res.getNumberOfPeople() == null) {
                sendSimpleMessage(chatId, "👥 Введите количество гостей:");
            } else if (res.getName() == null) {
                sendSimpleMessage(chatId, "💁 Введите ваше имя для брони:");
            } else if (res.getTable() == null) {
                showAvailableTables(chatId);
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            // 1. Обработка callback-запросов (нажатия кнопок)
            if (update.hasCallbackQuery()) {
                handleCallbackQuery(update);
                return;
            }

            // 2. Обработка текстовых сообщений
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleTextMessage(update);
            }
        } catch (Exception e) {
            handleError(update, e);
        }
    }

    private void handleTextMessage(Update update) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();

        // Глобальная обработка отмены
        if (text.equals(CANCEL_COMMAND)) {
            resetUserState(chatId);
            sendMainMenu(chatId);
            return;
        }

        // Обработка команд бронирования
        if (text.startsWith("/cancel_")) {
            try {
                Long reservationId = Long.parseLong(text.substring("/cancel_".length()));
                handleCancelReservation(chatId, reservationId);
            } catch (NumberFormatException e) {
                sendSimpleMessage(chatId, "❌ Неверный формат команды. Используйте /cancel_номер");
            }
            return;
        }

        // Обработка ввода учетных данных
        if (awaitingCredentials.contains(chatId)) {
            handleCredentialsInput(chatId, text);
            return;
        }

        // Обработка процесса бронирования
        if (pendingReservations.containsKey(chatId)) {
            handleReservationStep(chatId, text);
            return;
        }

        // Обработка отзывов
        if (text.matches("^[1-5]\\s.+") && userTokens.containsKey(chatId)) {
            handleReviewSubmission(chatId, text);
            return;
        }

        // Обработка обычных команд
        handleCommand(chatId, text);
    }

    private void handleReservationStep(Long chatId, String text) throws TelegramApiException {
        ReservationDto reservation = pendingReservations.get(chatId);

        if (reservation.getReservationTime() == null) {
            // Этап ввода даты
            if (validateAndSaveDate(chatId, text)) {
                sendSimpleMessage(chatId, "👥 Введите количество гостей:");
            }
        }
        else if (reservation.getNumberOfPeople() == null) {
            // Этап ввода количества гостей
            handleReservationPeople(chatId, text);
        }
        else if (reservation.getName() == null) {
            // Этап ввода имени
            handleReservationName(chatId, text);
        }
        else if (reservation.getTable() == null) {
            // Этап выбора столика (обрабатывается через кнопки)
            sendSimpleMessage(chatId, "Пожалуйста, выберите столик из предложенных вариантов");
        }
    }

    private void handleError(Update update, Exception e) {
        log.error("Error processing update", e);
        try {
            Long chatId = update.hasCallbackQuery() ?
                    update.getCallbackQuery().getMessage().getChatId() :
                    update.getMessage().getChatId();
            sendSimpleMessage(chatId, "⚠️ Произошла ошибка. Пожалуйста, попробуйте позже.");
        } catch (Exception ex) {
            log.error("Error sending error message", ex);
        }
    }


    private void handleReservationPeople(Long chatId, String peopleStr) {
        try {
            int people = Integer.parseInt(peopleStr);
            if (people <= 0) {
                sendSimpleMessage(chatId, "❌ Количество гостей должно быть больше 0. Введите снова:");
                return;
            }
            if (people > 20) {
                sendSimpleMessage(chatId, "❌ Для компаний более 20 человек звоните по телефону +7 (XXX) XXX-XX-XX");
                return;
            }

            pendingReservations.get(chatId).setNumberOfPeople(people);
            reservationPeopleCache.put(chatId, people);

            // ❗ Вместо поиска столиков — предложим выбрать длительность брони
            sendDurationSelection(chatId);

        } catch (NumberFormatException e) {
            sendSimpleMessage(chatId, "❌ Введите корректное число гостей:");
        } catch (Exception e) {
            log.error("Ошибка при обработке количества гостей", e);
            sendSimpleMessage(chatId, "⚠️ Ошибка при поиске столиков. Попробуйте позже.");
        }
    }
    private void sendDurationSelection(Long chatId) throws TelegramApiException {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(Collections.singletonList(createInlineButton("🕒 1 час", "duration_1")));
        rows.add(Collections.singletonList(createInlineButton("🕒 2 часа", "duration_2")));
        rows.add(Collections.singletonList(createInlineButton("🕒 3 часа", "duration_3")));
        rows.add(Collections.singletonList(createInlineButton("🕒 4 часа", "duration_4")));

        markup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("⏳ Выберите продолжительность бронирования:");
        message.setReplyMarkup(markup);

        execute(message);
    }


    private void showAvailableTables(Long chatId) throws TelegramApiException {
        List<RestaurantTableDto> tables = availableTablesCache.get(chatId);
        if (tables == null || tables.isEmpty()) {
            sendSimpleMessage(chatId, "❌ Нет доступных столиков. Попробуйте выбрать другое время.");
            return;
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (RestaurantTableDto table : tables) {
            String text = "Столик #" + table.getTableNumber();
            InlineKeyboardButton button = new InlineKeyboardButton(text);
            button.setCallbackData("select_table_" + table.getId());
            rows.add(Collections.singletonList(button));
        }

        markup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🪑 Доступные столики (гостей: " + reservationPeopleCache.get(chatId) + "):");
        message.setReplyMarkup(markup);

        execute(message);
    }

    private List<RestaurantTableDto> findAvailableTables(Long chatId, int people, String dateStr, int durationHours) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime startDateTime = LocalDateTime.parse(dateStr, inputFormatter);
            LocalDateTime endDateTime = startDateTime.plusHours(durationHours);

            Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());

            List<Reservation> conflictingReservations = restaurantTableRepository.findConflictingReservations(startDate, endDate);
            List<RestaurantTable> allTables = restaurantTableRepository.findAll();

            Set<Long> occupiedTableIds = conflictingReservations.stream()
                    .map(r -> r.getRestaurantTable().getId())
                    .collect(Collectors.toSet());

            // Свободные столики
            List<RestaurantTable> availableTables = allTables.stream()
                    .filter(table -> !occupiedTableIds.contains(table.getId()))
                    .toList();

            // Ищем столики с точным количеством мест
            List<RestaurantTable> exactMatchTables = availableTables.stream()
                    .filter(table -> table.getSeats() == people)
                    .toList();

            if (!exactMatchTables.isEmpty()) {
                return exactMatchTables.stream()
                        .map(table -> new RestaurantTableDto(
                                table.getId(),
                                 table.getTableNumber() + " на " + table.getSeats() + " человек"
                        ))
                        .collect(Collectors.toList());
            }

            // Ищем столики с большим количеством мест
            List<RestaurantTable> largerTables = availableTables.stream()
                    .filter(table -> table.getSeats() > people)
                    .sorted(Comparator.comparingInt(RestaurantTable::getSeats))
                    .toList();

            return largerTables.stream()
                    .map(table -> new RestaurantTableDto(
                            table.getId(),
                            "Столик №" + table.getTableNumber() + " на " + table.getSeats() + " человек"
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Ошибка при поиске доступных столиков", e);
            sendSimpleMessage(chatId, "⚠️ Ошибка обработки запроса. Попробуйте позже.");
            return Collections.emptyList();
        }
    }

    private void handleCallbackQuery(Update update) throws TelegramApiException {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (callbackData.startsWith("duration_")) {
            int hours = Integer.parseInt(callbackData.substring("duration_".length()));
            reservationDurationCache.put(chatId, hours);

            // После выбора длительности — сразу ищем доступные столики
            ReservationDto res = pendingReservations.get(chatId);
            if (res == null) {
                sendSimpleMessage(chatId, "⚠️ Ошибка: бронирование не найдено. Начните заново.");
                return;
            }

            int people = res.getNumberOfPeople();
            String dateStr = reservationDateCache.get(chatId);

            List<RestaurantTableDto> availableTables = findAvailableTables(chatId, people, dateStr, hours);

            if (availableTables.isEmpty()) {
                suggestAlternativeTimes(chatId, people, hours);
            } else {
                availableTablesCache.put(chatId, availableTables);
                showAvailableTables(chatId);
            }
        }
        else if (callbackData.startsWith("select_table_")) {
            Long tableId = Long.parseLong(callbackData.substring("select_table_".length()));
            handleTableSelection(chatId, tableId);
        }
        else if (callbackData.startsWith("alt_time_")) {
            String newTime = callbackData.substring("alt_time_".length());
            updateReservationTime(chatId, newTime);
        }
        else if (callbackData.equals("change_time")) {
            sendSimpleMessage(chatId, "📅 Введите новое время в формате ДД.ММ.ГГГГ ЧЧ:ММ");
        }
        else if (callbackData.equals("change_people")) {
            sendSimpleMessage(chatId, "👥 Введите новое количество гостей:");
        }
        else if (callbackData.equals("confirm_reservation")) {
            completeReservation(chatId);
        }
        else if (callbackData.equals("cancel_reservation")) {
            resetReservationState(chatId);
            sendMainMenu(chatId);
            sendSimpleMessage(chatId, "❌ Бронирование отменено");
        }
        else {
            handleCallback(update);
        }
    }

    // 1) Обновленный метод completeReservation:
    private void completeReservation(Long chatId) {
        try {
            ReservationDto reservation = pendingReservations.get(chatId);
            int duration = reservationDurationCache.getOrDefault(chatId, 3);
            String token = userTokens.get(chatId);

            if (token == null) {
                sendSimpleMessage(chatId, "❌ Ошибка авторизации. Пожалуйста, войдите снова.");
                return;
            }

            // Формируем JSON с уже выбранными данными пользователя (имя, количество людей)
            String json = String.format(
                    "{\"name\":\"%s\"," +
                            "\"table\":{\"id\":%d}," +
                            "\"reservationTime\":\"%s\"," +
                            "\"numberOfPeople\":%d," +
                            "\"durationHours\":%d}",
                    reservation.getName(),
                    reservation.getTable().getId(),
                    reservation.getReservationTime(),
                    reservation.getNumberOfPeople(),
                    duration
            );

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8080/api/reserve"))
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + token)
                                    .POST(HttpRequest.BodyPublishers.ofString(json))
                                    .build(),
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() == 200) {
                resetReservationState(chatId);
                sendSimpleMessage(chatId, "✅ Столик успешно забронирован!");
                sendMainMenu(chatId);
            } else {
                String body = response.body();
                if (body.contains("already reserved at this time")) {
                    // Если столик занят, ищем альтернативы
                    handleBookingConflict(chatId);
                } else {
                    sendSimpleMessage(chatId, "❌ Ошибка бронирования: " + body);
                }
            }
        } catch (Exception e) {
            log.error("Error completing reservation", e);
            sendSimpleMessage(chatId, "❌ Ошибка при бронировании. Попробуйте позже.");
        }
    }

    // Метод-обработчик конфликта бронирования, где только время выбирается
    private void handleBookingConflict(Long chatId) {
        ReservationDto res = pendingReservations.get(chatId);
        int people = reservationPeopleCache.getOrDefault(chatId, res.getNumberOfPeople());
        int duration = reservationDurationCache.getOrDefault(chatId, 3);

        // Первым делом — ближайшие слоты в тот же день
        suggestAlternativeTimes(chatId, people, duration);

        // Если нет доступных слотов в тот же день — проверяем на следующий день
        String orig = reservationDateCache.get(chatId);
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime dt = LocalDateTime.parse(orig, fmt).plusDays(1);
            String nextDay = dt.format(fmt);

            List<RestaurantTableDto> nextDayTables = findAvailableTables(chatId, people, nextDay, duration);
            if (!nextDayTables.isEmpty()) {
                // Обновляем дату в кэше и показываем варианты
                reservationDateCache.put(chatId, nextDay);
                availableTablesCache.put(chatId, nextDayTables);

                SendMessage msg = new SendMessage(chatId.toString(),
                        "🗓️ На завтра в то же время тоже есть свободные столики: " + nextDay);
                InlineKeyboardMarkup m = new InlineKeyboardMarkup();
                m.setKeyboard(List.of(
                        List.of(createInlineButton("✅ Забронировать на " + nextDay, "alt_time_" + nextDay)),
                        List.of(createInlineButton("📅 Ввести другую дату", "change_time"))
                ));
                msg.setReplyMarkup(m);
                execute(msg);
            } else {
                sendSimpleMessage(chatId,
                        "❌ К сожалению, ни сегодня, ни завтра в это время нет свободных столиков. Попробуйте выбрать другое время.");
            }
        } catch (Exception e) {
            log.error("Error proposing next-day slot", e);
            sendSimpleMessage(chatId, "⚠️ Не удалось найти альтернативные варианты. Попробуйте позже.");
        }
    }

    private void suggestAlternativeTimes(Long chatId, int people, int duration) {
        try {
            String originalDateStr = reservationDateCache.get(chatId);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date originalDate = dateFormat.parse(originalDateStr);

            List<String> alternatives = new ArrayList<>();
            Calendar cal = Calendar.getInstance();

            // Проверка доступности столиков на текущую дату
            for (int i = 1; i <= 4; i++) {
                cal.setTime(originalDate);
                cal.add(Calendar.MINUTE, 30 * i); // 30 минут на каждый шаг

                int hour = cal.get(Calendar.HOUR_OF_DAY);
                if (hour < 10 || hour >= 23) break;  // Проверка на допустимое время (с 10:00 до 23:00)
                String timeStr = dateFormat.format(cal.getTime());
                boolean available = !findAvailableTables(chatId, people, timeStr, duration).isEmpty();
                if (available) {
                    alternatives.add(timeStr);  // Добавляем доступное время
                }
                // Логирование для отладки
                log.debug("Проверка времени: {} - Доступность: {}", timeStr, available);
            }

            // Логирование результатов поиска альтернатив в текущем дне
            log.debug("Найденные альтернативы в текущем дне: {}", alternatives);

            // Если в текущем дне не найдены альтернативы, ищем на следующие дни
            if (alternatives.isEmpty()) {
                for (int dayOffset = 1; dayOffset <= 7; dayOffset++) {  // Проверяем следующие 7 дней
                    cal.setTime(originalDate);
                    cal.add(Calendar.DAY_OF_YEAR, dayOffset);  // Перемещаем на следующий день

                    // Логирование для отладки
                    log.debug("Проверка доступности на следующий день: {}", dateFormat.format(cal.getTime()));

                    // Пробуем найти доступные столики на следующий день с 10:00 до 23:00
                    for (int i = 1; i <= 4; i++) {
                        cal.setTime(originalDate);
                        cal.add(Calendar.DAY_OF_YEAR, dayOffset);  // Увеличиваем день на 1
                        cal.add(Calendar.MINUTE, 30 * i);  // 30 минут на каждый шаг

                        int hour = cal.get(Calendar.HOUR_OF_DAY);
                        if (hour < 10 || hour >= 23) break;

                        String timeStr = dateFormat.format(cal.getTime());
                        boolean available = !findAvailableTables(chatId, people, timeStr, duration).isEmpty();

                        if (available) {
                            alternatives.add(timeStr);  // Добавляем доступное время
                        }

                        // Логирование для отладки
                        log.debug("Проверка времени на следующий день: {} - Доступность: {}", timeStr, available);
                    }

                    // Если были найдены альтернативы на следующий день, выходим из цикла
                    if (!alternatives.isEmpty()) {
                        break;
                    }
                }
            }

            // Если были найдены альтернативы, отображаем их
            if (!alternatives.isEmpty()) {
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = alternatives.stream()
                        .distinct()
                        .map(t -> List.of(createInlineButton("⏰ " + t, "alt_time_" + t)))
                        .collect(Collectors.toList());
                rows.add(List.of(createInlineButton("📅 Выбрать другую дату", "change_time")));

                markup.setKeyboard(rows);
                SendMessage message = new SendMessage(chatId.toString(),
                        "❌ Нет свободных столиков в выбранное время. Вот ближайшие варианты:");
                message.setReplyMarkup(markup);
                execute(message);
            } else {
                sendSimpleMessage(chatId, "❌ Нет доступных столиков в выбранное время.");
            }
        } catch (Exception e) {
            log.error("Ошибка при поиске альтернативных времен", e);
            sendSimpleMessage(chatId, "⚠️ Ошибка при поиске альтернатив. Попробуйте позже.");
        }
    }




    private void handleTableSelection(Long chatId, Long tableId) throws TelegramApiException {
        ReservationDto reservation = pendingReservations.get(chatId);
        try {
            RestaurantTable table = restaurantTableRepository.findById(tableId)
                    .orElseThrow(() -> new Exception("Столик не найден"));

            // Сохраняем выбранный столик
            reservation.setTable(new RestaurantTableDto(table.getId(), table.getTableNumber()));

            // Если имя ещё не введено — спрашиваем,
            // иначе сразу показываем подтверждение брони
            if (reservation.getName() == null || reservation.getName().isBlank()) {
                sendSimpleMessage(chatId, "👤 Введите ваше имя для брони:");
            } else {
                confirmReservation(chatId);
            }
        } catch (Exception e) {
            sendSimpleMessage(chatId, "❌ Ошибка при выборе столика: " + e.getMessage());
            log.error("Error selecting table", e);
        }
    }
}



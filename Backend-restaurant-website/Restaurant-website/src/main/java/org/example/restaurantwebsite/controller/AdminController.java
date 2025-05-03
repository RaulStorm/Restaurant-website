package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.dto.MenuItemDto;
import org.example.restaurantwebsite.dto.ReservationWithIdDto;
import org.example.restaurantwebsite.dto.RestaurantTableDto;
import org.example.restaurantwebsite.model.*;
import org.example.restaurantwebsite.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.example.restaurantwebsite.service.ReservationService;
import org.example.restaurantwebsite.repository.ReservationRepository;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/adm")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final MenuItemService menuItemService;
    private final CloudinaryService cloudinaryService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;
    public ReservationService reservationService;
    private ReservationRepository reservationRepository;

    public AdminController(MenuItemService menuItemService, CloudinaryService cloudinaryService, CategoryService categoryService, ReviewService reviewService, ReservationService reservationService, ReservationRepository reservationRepository) {
        this.menuItemService = menuItemService;
        this.cloudinaryService = cloudinaryService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
        this.reservationService = reservationService;
        this.reservationRepository = reservationRepository;
    }

    // Добавление блюда в меню
    @PostMapping("/addDish")
    public ResponseEntity<String> addDish(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam double price,
            @RequestParam String categoryName,
            @RequestParam("image") MultipartFile image) {

        try {
            // Загружаем изображение в Cloudinary и получаем URL
            String imageUrl = cloudinaryService.uploadImage(image);

            // Получаем категорию по имени
            Category category = categoryService.getCategoryByName(categoryName);
            if (category == null) {
                return ResponseEntity.status(400).body("Категория с таким именем не найдена.");
            }

            // Создаем новый объект блюда
            MenuItem menuItem = new MenuItem();
            menuItem.setName(name);
            menuItem.setDescription(description);
            menuItem.setPrice(price);
            menuItem.setCategory(category);

            // Сохраняем блюдо в базе данных
            MenuItem savedMenuItem = menuItemService.addMenuItem(menuItem);

            // Создаем объект изображения и связываем с блюдом
            MenuItemImage menuItemImage = new MenuItemImage();
            menuItemImage.setMenuItemId(savedMenuItem.getId());  // Устанавливаем ID блюда
            menuItemImage.setImageUrl(imageUrl);  // Сохраняем URL изображения

            // Сохраняем изображение в базе данных
            menuItemService.addImage(menuItemImage);

            return ResponseEntity.ok("Блюдо добавлено успешно!");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Ошибка загрузки изображения: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Ошибка при добавлении блюда: " + e.getMessage());
        }
    }

    // 1) Получить список всех блюд
    @GetMapping("/menu")
    public ResponseEntity<List<MenuItemDto>> listMenuItems() {
        List<MenuItemDto> dtos = menuItemService.findAll()
                .stream()
                .map(mi -> {
                    MenuItemDto d = new MenuItemDto();
                    d.setId(mi.getId());
                    d.setName(mi.getName());
                    d.setDescription(mi.getDescription());
                    d.setCategoryName(mi.getCategory().getName());  // передаем имя категории
                    d.setPrice(mi.getPrice());
                    d.setImages(mi.getImages().stream()
                            .map(image -> image.getImageUrl())
                            .collect(Collectors.toList()));  // собираем список URL изображений
                    return d;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    // 1) Удаление блюда по ID
    @DeleteMapping("/menu/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteById(id);
        return ResponseEntity.noContent().build();  // Успешное удаление, возвращаем 204
    }


    @GetMapping("/reviews")
    public ResponseEntity<?> getReviews(@RequestParam String period) {
        Calendar calendar = Calendar.getInstance();
        Date startDate = null;
        Date endDate = new Date();

        // Определяем даты в зависимости от периода
        switch (period.toLowerCase()) {
            case "day":
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startDate = calendar.getTime();
                break;
            case "week":
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                startDate = calendar.getTime();
                break;
            case "month":
                calendar.add(Calendar.MONTH, -1);
                startDate = calendar.getTime();
                break;
            default:
                return ResponseEntity.badRequest().body("Invalid period. Please use 'day', 'week', or 'month'.");
        }

        // Получаем отзывы за выбранный период
        List<Review> reviews = reviewService.getReviewsForPeriod(startDate, endDate);

        // Вычисляем средний рейтинг
        double averageRating = reviewService.getAverageRating(reviews);

        // Вычисляем процентное соотношение отзывов с рейтингами от 1 до 5
        double[] ratingPercentages = reviewService.getRatingPercentages(reviews);

        // Формируем ответ
        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", averageRating);
        response.put("ratingPercentages", ratingPercentages);
        response.put("totalReviews", reviews.size());
        response.put("reviews", reviews); // Добавляем полный список отзывов

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservations")
    public ResponseEntity<?> getReservations(@RequestParam String period) {
        try {
            LocalDateTime now = LocalDateTime.now();
            // Начало — сегодня в 00:00
            LocalDateTime startDate = now.toLocalDate().atStartOfDay();
            LocalDateTime endDate;

            switch (period.toLowerCase()) {
                case "day":
                    // Сегодняшний день — до конца сегодняшнего дня
                    endDate = startDate.plusDays(1).minusNanos(1);
                    break;
                case "week":
                    // Следующие 7 дней — от начала сегодня до конца 7-го дня
                    endDate = startDate.plusWeeks(1).minusNanos(1);
                    break;
                case "month":
                    // Следующие 30 дней
                    endDate = startDate.plusDays(30).minusNanos(1);
                    break;
                default:
                    return ResponseEntity.badRequest()
                            .body("Неверный период. Используйте 'day', 'week' или 'month'.");
            }

            Timestamp startTs = Timestamp.valueOf(startDate);
            Timestamp endTs   = Timestamp.valueOf(endDate);

            log.info("Поиск бронирований с {} по {}", startTs, endTs);

            List<Reservation> reservations = reservationRepository
                    .findByReservationTimeBetween(startTs, endTs);

            log.info("Найдено бронирований: {}", reservations.size());

            List<ReservationWithIdDto> dtos = reservations.stream().map(reservation -> {
                ReservationWithIdDto dto = new ReservationWithIdDto();
                dto.setId(reservation.getId());
                dto.setName(reservation.getName());
                dto.setNumberOfPeople(reservation.getNumberOfPeople());
                dto.setReservationTime(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(reservation.getReservationTime())
                );
                dto.setTable(new RestaurantTableDto(
                        reservation.getTable().getId(),
                        reservation.getTable().getTableNumber()
                ));
                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            log.error("Ошибка при получении бронирований", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка сервера: " + e.getMessage());
        }
    }
    // Отмена бронирования администратором
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        try {
            // Проверяем существование брони
//            Reservation reservation = reservationService.getReservationById(id);
//            if (reservation == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body("Бронирование с ID " + id + " не найдено");
//            }

            // Отменяем бронирование
            reservationService.cancelReservation(id);

            return ResponseEntity.ok()
                    .body("Бронирование с ID " + id + " успешно отменено");
        } catch (Exception e) {
            log.error("Ошибка при отмене бронирования", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при отмене бронирования: " + e.getMessage());
        }
    }

}



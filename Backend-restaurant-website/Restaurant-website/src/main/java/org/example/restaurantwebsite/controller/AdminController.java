package org.example.restaurantwebsite.controller;

import org.example.restaurantwebsite.dto.MenuItemDto;
import org.example.restaurantwebsite.dto.ReservationWithIdDto;
import org.example.restaurantwebsite.dto.RestaurantTableDto;
import org.example.restaurantwebsite.model.*;
import org.example.restaurantwebsite.service.*;
import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.format.DateTimeFormatter;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.example.restaurantwebsite.service.ReservationService;
import org.example.restaurantwebsite.repository.ReservationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    private final ReportDocumentGenerator generator;

    public AdminController(MenuItemService menuItemService, CloudinaryService cloudinaryService, CategoryService categoryService, ReviewService reviewService, ReservationService reservationService, ReservationRepository reservationRepository, ReportDocumentGenerator generator) {
        this.menuItemService = menuItemService;
        this.cloudinaryService = cloudinaryService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
        this.reservationService = reservationService;
        this.reservationRepository = reservationRepository;
        this.generator = generator;
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
        // 1) вычисляем границы
        Calendar calendar = Calendar.getInstance();
        Date startDate;
        Date endDate = new Date();

        switch (period.toLowerCase()) {
            case "day":
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
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
                return ResponseEntity
                        .badRequest()
                        .body("Invalid period. Use 'day', 'week' or 'month'.");
        }

        // 2) форматируем уже заданные даты
        SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
        String startStr = fmt.format(startDate);
        String endStr   = fmt.format(endDate);

        // 3) получаем и считаем отзывы
        List<Review> reviews = reviewService.getReviewsForPeriod(startDate, endDate);
        double averageRating = reviewService.getAverageRating(reviews);
        double[] percentages = reviewService.getRatingPercentages(reviews);

        // 4) собираем ответ
        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", averageRating);
        response.put("ratingPercentages", percentages);
        response.put("totalReviews", reviews.size());
        response.put("reviews", reviews);
        response.put("startDate", startStr);
        response.put("endDate", endStr);

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
                    endDate = startDate.plusDays(1).minusNanos(1);
                    break;
                case "week":
                    endDate = startDate.plusWeeks(1).minusNanos(1);
                    break;
                case "month":
                    endDate = startDate.plusDays(30).minusNanos(1);
                    break;
                default:
                    return ResponseEntity.badRequest()
                            .body("Неверный период. Используйте 'day', 'week' или 'month'.");
            }

            Timestamp startTs = Timestamp.valueOf(startDate);
            Timestamp endTs = Timestamp.valueOf(endDate);

            log.info("Поиск бронирований с {} по {}", startTs, endTs);

            // Получаем бронирования по заданному времени
            List<Reservation> reservations = reservationRepository
                    .findByReservationTimeBetween(startTs, endTs);

            log.info("Найдено бронирований: {}", reservations.size());

            // Создаём DTO для ответных данных
            List<ReservationWithIdDto> dtos = reservations.stream().map(reservation -> {
                ReservationWithIdDto dto = new ReservationWithIdDto();
                dto.setId(reservation.getId());
                dto.setName(reservation.getName());
                dto.setNumberOfPeople(reservation.getNumberOfPeople());
                dto.setReservationTime(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(reservation.getReservationTime())
                );
                dto.setReservationEndTime(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(reservation.getReservationEndTime())
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



    @PostMapping("/reviews/export/word")
    public ResponseEntity<byte[]> exportReviewsWord(@RequestBody Map<String, Object> data) throws IOException {
        // имя пользователя
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // время скачивания
        String downloadedAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

        data.put("downloadedBy", username);
        data.put("downloadedAt", downloadedAt);

        ByteArrayOutputStream doc = generator.generateReviewReportWord(data);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reviews.docx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(doc.toByteArray());
    }

    @PostMapping("/reviews/export/excel")
    public ResponseEntity<byte[]> exportReviewsExcel(@RequestBody Map<String, Object> data) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String downloadedAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

        data.put("downloadedBy", username);
        data.put("downloadedAt", downloadedAt);

        ByteArrayOutputStream doc = generator.generateReviewReportExcel(data);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reviews.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(doc.toByteArray());
    }

    //резерв
    @GetMapping("/reservations/export/word")
    public ResponseEntity<byte[]> exportReservationsWord(@RequestParam String period) {
        log.info("Экспорт WORD, период = {}", period);
        return buildAndSend(period, true);
    }

    @GetMapping("/reservations/export/excel")
    public ResponseEntity<byte[]> exportReservationsExcel(@RequestParam String period) {
        log.info("Экспорт EXCEL, период = {}", period);
        return buildAndSend(period, false);
    }

    private ResponseEntity<byte[]> buildAndSend(String period, boolean isWord) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            Object principal = auth.getPrincipal();
            String downloadedByName;
            String downloadedByEmail;

            if (principal instanceof User) {
                User user = (User) principal;
                downloadedByName = user.getName();
                downloadedByEmail = user.getEmail();
            } else if (principal instanceof UserDetails) {
                UserDetails ud = (UserDetails) principal;
                downloadedByName = ud.getUsername();
                downloadedByEmail = ud.getUsername();
            } else {
                downloadedByName = auth.getName();
                downloadedByEmail = auth.getName();
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.toLocalDate().atStartOfDay();
            LocalDateTime end;
            switch (period.toLowerCase()) {
                case "day":   end = start.plusDays(1).minusNanos(1); break;
                case "week":  end = start.plusWeeks(1).minusNanos(1); break;
                case "month": end = start.plusMonths(1).minusNanos(1); break;
                default:
                    log.error("Неверный период: {}", period);
                    return ResponseEntity.badRequest()
                            .body(("Invalid period: " + period).getBytes());
            }

            SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
            String startStr = fmt.format(Timestamp.valueOf(start));
            String endStr   = fmt.format(Timestamp.valueOf(end));

            List<ReservationWithIdDto> dtoList = reservationRepository
                    .findByReservationTimeBetween(Timestamp.valueOf(start), Timestamp.valueOf(end)).stream()
                    .map(r -> {
                        ReservationWithIdDto dto = new ReservationWithIdDto();
                        dto.setId(r.getId());
                        dto.setName(r.getName());
                        dto.setNumberOfPeople(r.getNumberOfPeople());
                        dto.setReservationTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(r.getReservationTime()));
                        dto.setReservationEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(r.getReservationEndTime()));
                        dto.setTable(new RestaurantTableDto(r.getTable().getId(), r.getTable().getTableNumber()));
                        return dto;
                    })
                    .sorted(Comparator.comparing(r -> LocalDateTime.parse(r.getReservationTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                    .collect(Collectors.toList());

            Map<String, Object> data = new HashMap<>();
            data.put("reservations", dtoList);
            data.put("startDate", startStr);
            data.put("endDate", endStr);
            data.put("downloadedByName", downloadedByName);
            data.put("downloadedByEmail", downloadedByEmail);
            data.put("downloadedAt", now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

            ByteArrayOutputStream out = isWord
                    ? generator.generateReservationReportWord(data)
                    : generator.generateReservationReportExcel(data);

            String filename = isWord ? "reservations.docx" : "reservations.xlsx";
            MediaType mediaType = isWord
                    ? MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(mediaType)
                    .body(out.toByteArray());

        } catch (Exception e) {
            log.error("Ошибка при экспорте: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Server error: " + e.getMessage()).getBytes());
        }
    }
}



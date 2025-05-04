package org.example.restaurantwebsite.service;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.restaurantwebsite.controller.AdminController;
import org.example.restaurantwebsite.dto.ReservationWithIdDto;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class ReportDocumentGenerator {
    private static final DateTimeFormatter DTF   = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter OUT_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter OUT_TIME = DateTimeFormatter.ofPattern("HH:mm");
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);


    // === Универсальный метод для Word ===
    public ByteArrayOutputStream generateReviewReportWord(Map<String, Object> data) throws IOException {
        List<Map<String, Object>> reviews = (List<Map<String, Object>>) data.get("reviews");
        double averageRating = ((Number) data.get("averageRating")).doubleValue();
        List<Number> percentagesList = (List<Number>) data.get("ratingPercentages");
        double[] percentages = percentagesList.stream().mapToDouble(Number::doubleValue).toArray();

        // новый: формируем periodLabel из данных
        String start = data.get("startDate").toString();
        String end   = data.get("endDate").toString();
        String periodLabel = start + " - " + end;

        // новые поля
        String downloadedBy = data.get("downloadedBy").toString();
        String downloadedAt = data.get("downloadedAt").toString();

        XWPFDocument doc = new XWPFDocument();

        // Заголовок
        addTitle(doc, "Отзывы за период: " + periodLabel);
        addParagraph(doc, String.format("Средняя оценка: %.2f | Всего отзывов: %d",
                averageRating, reviews.size()));

        // Добавляем строчки с информацией о скачивании
        addParagraph(doc, "Сформировано пользователем: " + downloadedBy);
        addParagraph(doc, "Дата скачивания: " + downloadedAt);

        addParagraph(doc, "Распределение оценок:");
        for (int i = 0; i < percentages.length; i++) {
            addParagraph(doc, (i + 1) + " ★: " + String.format("%.1f", percentages[i]) + " %");
        }

        addTable(doc,
                List.of("Пользователь", "Дата", "Оценка", "Отзыв"),
                reviews.stream().map(r -> List.of(
                        ((Map<?, ?>) r.get("user")).get("name").toString(),
                        r.get("formattedDate").toString(),
                        r.get("rating").toString(),
                        r.get("reviewText").toString()
                )).toList());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.write(out);
        doc.close();
        return out;
    }


    // === Универсальный метод для Excel ===
    public ByteArrayOutputStream generateReviewReportExcel(Map<String, Object> data) throws IOException {
        // 1) вынимаем из data все поля
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> reviews = (List<Map<String, Object>>) data.get("reviews");
        double averageRating = ((Number) data.get("averageRating")).doubleValue();
        @SuppressWarnings("unchecked")
        List<Number> percentagesList = (List<Number>) data.get("ratingPercentages");
        double[] ratingPercentages = percentagesList.stream()
                .mapToDouble(Number::doubleValue)
                .toArray();

        String start = data.get("startDate").toString();
        String end   = data.get("endDate").toString();
        String periodLabel = start + " - " + end;

        String downloadedBy = data.get("downloadedBy").toString();
        String downloadedAt = data.get("downloadedAt").toString();

        // 2) вызываем уже «детальный» Excel-метод
        return generateReviewReportExcel(
                reviews,
                averageRating,
                ratingPercentages,
                periodLabel,
                downloadedBy,
                downloadedAt
        );
    }



    // === Excel: детальный отчёт ===
    public ByteArrayOutputStream generateReviewReportExcel(
            List<Map<String, Object>> reviews,
            double averageRating,
            double[] ratingPercentages,
            String periodLabel,
            String downloadedBy,
            String downloadedAt
    ) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Отзывы");
        int rowIdx = 0;

        // ЗАГОЛОВОК
        Row titleRow = sheet.createRow(rowIdx++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Отзывы за период: " + periodLabel);
        CellStyle titleStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        titleStyle.setFont(font);
        titleCell.setCellStyle(titleStyle);

        // СТАТИСТИКА
        Row statRow = sheet.createRow(rowIdx++);
        statRow.createCell(0).setCellValue("Средняя оценка:");
        statRow.createCell(1).setCellValue(averageRating);
        statRow.createCell(2).setCellValue("Всего отзывов:");
        statRow.createCell(3).setCellValue(reviews.size());

        // ИНФО О СКАЧИВАНИИ
        Row infoRow1 = sheet.createRow(rowIdx++);
        infoRow1.createCell(0).setCellValue("Сформировано пользователем:");
        infoRow1.createCell(1).setCellValue(downloadedBy);

        Row infoRow2 = sheet.createRow(rowIdx++);
        infoRow2.createCell(0).setCellValue("Дата скачивания:");
        infoRow2.createCell(1).setCellValue(downloadedAt);

        rowIdx++; // пустая строка

        // ТАБЛИЦА ОТЗЫВОВ
        String[] headers = {"Пользователь", "Дата", "Оценка", "Отзыв"};
        Row headerRow = sheet.createRow(rowIdx++);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(titleStyle);
        }
        for (Map<String, Object> review : reviews) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(((Map<?, ?>) review.get("user")).get("name").toString());
            row.createCell(1).setCellValue(review.get("formattedDate").toString());
            row.createCell(2).setCellValue(Double.parseDouble(review.get("rating").toString()));
            row.createCell(3).setCellValue(review.get("reviewText").toString());
        }

        // авто-размер
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out;
    }

    // Word export with styling
    public ByteArrayOutputStream generateReservationReportWord(Map<String, Object> data) throws IOException {
        log.info("Генерация цветного Word-отчёта по бронированиям...");
        List<ReservationWithIdDto> reservations = ((List<ReservationWithIdDto>) data.get("reservations")).stream()
                .sorted(Comparator.comparing(r -> LocalDateTime.parse(r.getReservationTime(), DTF)))
                .toList();

        String periodLabel       = data.get("startDate") + " - " + data.get("endDate");
        String downloadedByName  = data.get("downloadedByName").toString();
        String downloadedByEmail = data.get("downloadedByEmail").toString();
        String downloadedAt      = data.get("downloadedAt").toString();

        XWPFDocument doc = new XWPFDocument();
        addTitle(doc, "Отчёт о бронированиях");
        addParagraph(doc, "Период: " + periodLabel, true);
        addParagraph(doc, "Скачал: " + downloadedByName + " (" + downloadedByEmail + ")", false);
        addParagraph(doc, "Дата скачивания: " + downloadedAt, false);
        addParagraph(doc, "", false);

        // Таблица бронирований
        XWPFTable table = doc.createTable();
        XWPFTableRow headerRow = table.getRow(0);
        String[] headers = {"ID", "Имя", "Стол", "Гостей", "Дата", "Начало", "Конец"};
        for (int i = 0; i < headers.length; i++) {
            XWPFTableCell cell = i == 0 ? headerRow.getCell(0) : headerRow.addNewTableCell();
            cell.setText(headers[i]);
            // Фон заголовка
            CTTcPr tcPr = cell.getCTTc().addNewTcPr();
            CTShd ctShd = tcPr.addNewShd();
            ctShd.setFill("A7BFDE"); // светло-голубой
        }
        // Данные
        for (ReservationWithIdDto r : reservations) {
            XWPFTableRow row = table.createRow();
            LocalDateTime start = LocalDateTime.parse(r.getReservationTime(), DTF);
            LocalDateTime end   = LocalDateTime.parse(r.getReservationEndTime(), DTF);
            String[] vals = {
                    r.getId().toString(),
                    r.getName(),
                    "№" + r.getTable().getTableNumber(),
                    String.valueOf(r.getNumberOfPeople()),
                    start.format(OUT_DATE),
                    start.format(OUT_TIME),
                    end.format(OUT_TIME)
            };
            for (int i = 0; i < vals.length; i++) {
                row.getCell(i).setText(vals[i]);
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.write(out);
        doc.close();
        log.info("Word-отчёт с цветом создан, размер: {} bytes", out.size());
        return out;
    }

    // Excel export with styling
    public ByteArrayOutputStream generateReservationReportExcel(Map<String, Object> data) throws IOException {
        log.info("Генерация цветного Excel-отчёта по бронированиям...");
        List<ReservationWithIdDto> reservations = ((List<ReservationWithIdDto>) data.get("reservations")).stream()
                .sorted(Comparator.comparing(r -> LocalDateTime.parse(r.getReservationTime(), DTF)))
                .toList();

        String periodLabel       = data.get("startDate") + " - " + data.get("endDate");
        String downloadedByName  = data.get("downloadedByName").toString();
        String downloadedByEmail = data.get("downloadedByEmail").toString();
        String downloadedAt      = data.get("downloadedAt").toString();

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Бронирования");
        int rowIdx = 0;

        // Заголовок
        Row titleRow = sheet.createRow(rowIdx++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Отчёт о бронированиях");
        CellStyle titleStyle = wb.createCellStyle();
        Font titleFont = wb.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short)16);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);

        // Инфо
        Row infoRow = sheet.createRow(rowIdx++);
        infoRow.createCell(0).setCellValue("Период:");
        infoRow.createCell(1).setCellValue(periodLabel);
        infoRow = sheet.createRow(rowIdx++);
        infoRow.createCell(0).setCellValue("Скачал:");
        infoRow.createCell(1).setCellValue(downloadedByName + " (" + downloadedByEmail + ")");
        infoRow = sheet.createRow(rowIdx++);
        infoRow.createCell(0).setCellValue("Дата скачивания:");
        infoRow.createCell(1).setCellValue(downloadedAt);

        rowIdx++;
        // Заголовки таблицы
        String[] headers2 = {"ID","Имя","Стол","Гостей","Дата","Начало","Конец"};
        Row headerRow2 = sheet.createRow(rowIdx++);
        CellStyle hdrStyle = wb.createCellStyle();
        Font hdrFont = wb.createFont(); hdrFont.setBold(true); hdrStyle.setFont(hdrFont);
        hdrStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        hdrStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        for (int i = 0; i < headers2.length; i++) {
            Cell c = headerRow2.createCell(i);
            c.setCellValue(headers2[i]);
            c.setCellStyle(hdrStyle);
        }

        // Данные
        for (ReservationWithIdDto r : reservations) {
            Row row = sheet.createRow(rowIdx++);
            LocalDateTime start = LocalDateTime.parse(r.getReservationTime(), DTF);
            LocalDateTime end   = LocalDateTime.parse(r.getReservationEndTime(), DTF);
            Object[] vals = {
                    r.getId(), r.getName(), r.getTable().getTableNumber(), r.getNumberOfPeople(),
                    start.format(OUT_DATE), start.format(OUT_TIME), end.format(OUT_TIME)
            };
            for (int i = 0; i < vals.length; i++) {
                row.createCell(i).setCellValue(vals[i].toString());
            }
        }

        for (int i = 0; i < headers2.length; i++) sheet.autoSizeColumn(i);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();
        log.info("Excel-отчёт с цветом создан, размер: {} bytes", out.size());
        return out;
    }



    private void addParagraph(XWPFDocument doc, String text, boolean bold) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        r.setBold(bold); r.setText(text);
    }

    // === Вспомогательные методы для добавления заголовков, параграфов и таблиц в Word ===
    private void addTitle(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = p.createRun();
        run.setBold(true);
        run.setFontSize(16);
        run.setText(text);
    }

    private void addParagraph(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun run = p.createRun();
        run.setText(text);
    }

    private void addTable(XWPFDocument doc, List<String> headers, List<List<String>> rows) {
        XWPFTable table = doc.createTable();
        XWPFTableRow headerRow = table.getRow(0);

        for (int i = 0; i < headers.size(); i++) {
            if (i == 0) {
                headerRow.getCell(0).setText(headers.get(i));
            } else {
                headerRow.addNewTableCell().setText(headers.get(i));
            }
        }

        for (List<String> rowData : rows) {
            XWPFTableRow row = table.createRow();
            for (int i = 0; i < rowData.size(); i++) {
                row.getCell(i).setText(rowData.get(i));
            }
        }
    }


}
///* Общие стили для страницы */
//* {
//margin: 0;
//padding: 0;
//box-sizing: border-box;
//}
//
//body {
//    font-family: Arial, sans-serif;
//    background-color: #f4f4f4;
//    color: #333;
//    padding-top: 60px; /* Отступ сверху для страницы, чтобы не перекрывал хедер */
//}
//
///* Стили для бургерного меню */
//#new-burger-menu {
//    display: flex;
//    flex-direction: column;
//    justify-content: space-between;
//    width: 30px;
//    height: 25px;
//    cursor: pointer;
//    position: fixed;  /* Фиксированное положение */
//    top: 15px;  /* Немного ниже хедера */
//    left: 15px;  /* В левом углу */
//    z-index: 1000;
//    transition: all 0.3s ease;
//}
//
//#new-burger-menu div {
//height: 4px;
//background-color: #333;
//border-radius: 2px;
//}
//
//        /* Стили для бокового меню */
//        .side-menu {
//    height: 100%;
//    width: 250px;
//    background-color: #333;
//    position: fixed;
//    top: 0;
//    left: -250px;
//    transition: left 0.3s ease;
//    z-index: 999;
//    overflow-y: auto;
//    padding-top: 60px; /* Отступ сверху */
//}
//
///* Когда меню активно (открыто), меняем положение */
//.side-menu.active {
//    left: 0;
//}
//
///* Стиль для ссылок в боковом меню */
//.side-menu ul {
//list-style: none;
//}
//
//        .side-menu ul li {
//    padding: 10px 20px;
//    text-align: left;
//}
//
//.side-menu ul li a {
//color: white;
//text-decoration: none;
//font-size: 18px;
//}
//
//        .side-menu ul li a:hover {
//    background-color: #555;
//}
//
///* Стили для основного контента */
//.content {
//    transition: margin-left 0.3s ease;
//    padding: 20px;
//    margin-top: 80px; /* Отступ от хедера */
//    margin-left: 0;
//}
//
//.content.active {
//    margin-left: 250px; /* Когда меню открыто, сдвигаем контент вправо */
//}
//
///* Вкладки */
//.tab-content {
//    display: none;
//    margin-top: 20px;
//}
//
//.tab-content.active {
//    display: block;
//}
//
///* Стили для элементов управления (кнопок, форм) */
//.controls {
//    margin-bottom: 20px;
//}
//
//.controls select {
//padding: 5px;
//margin-right: 10px;
//}
//
//button {
//    padding: 10px 20px;
//    background-color: #333;
//    color: white;
//    border: none;
//    cursor: pointer;
//    transition: background-color 0.3s ease;
//}
//
//button:hover {
//    background-color: #555;
//}
//
///* Стили для таблиц */
//table {
//    width: 100%;
//    border-collapse: collapse;
//}
//
//th, td {
//    padding: 12px;
//    text-align: left;
//    border-bottom: 1px solid #ddd;
//}
//
//th {
//    background-color: #333;
//    color: white;
//}
//
//td {
//    background-color: #f9f9f9;
//}
//
//tr:hover td {
//background-color: #f1f1f1;
//}
//
//        /* Стили для загрузки и ошибок */
//        .loading {
//    display: none;
//    font-size: 18px;
//    color: #333;
//}
//
//.error {
//    display: none;
//    color: red;
//    font-size: 18px;
//}
//
///* Стили для скрытия/отображения меню на мобильных устройствах */
//@media (max-width: 768px) {
//        #new-burger-menu {
//    display: block;
//}
//
//    .side-menu {
//    position: fixed;
//    top: 0;
//    left: -250px;
//    height: 100%;
//    width: 250px;
//    background-color: #333;
//    transition: left 0.3s ease;
//}
//
//    .side-menu.active {
//    left: 0;
//}
//
//    .content.active {
//    margin-left: 0; /* Контент не сдвигается на мобильных */
//}
//}
//
///* Стили для заголовка */
//header {
//    display: flex; /* Используем flexbox для удобного выравнивания */
//    justify-content: space-between; /* Распределяем пространство между элементами */
//    align-items: center; /* Центрируем элементы по вертикали */
//    background-color: #7e56563b; /* Цвет фона */
//    width: 100%; /* Фиксированная ширина */
//    height: 70px; /* Фиксированная высота */
//    margin: 0 auto; /* Центрируем заголовок на странице */
//    box-sizing: border-box; /* Учитываем отступы в размерах */
//}
//
///* Стили для навигации */
//nav {
//    flex-grow: 1;
//    text-align: center;
//    position: relative;
//}
//
//nav ul {
//list-style-type: none;
//padding: 0;
//margin: 0;
//        }
//
//nav ul li {
//    display: inline-block;
//}
//
//nav ul li a {
//color: #fff;
//text-decoration: none;
//font-weight: 500;
//padding: 10px 20px;
//position: relative;
//display: inline-block;
//transition: transform 0.3s, box-shadow 0.3s; /* Плавный эффект при наведении */
//}
//
//nav ul li a:hover {
//    background-color: #644545; /* Цвет фона при наведении */
//    border-radius: 20px;
//}
//
///* Стили для тела страницы */
//body {
//    display: flex;
//    flex-direction: column;
//    min-height: 100vh; /* Минимальная высота для заполнения всего окна браузера */
//}
//
//main {
//    flex: 1; /* Заставляем основной контент занимать оставшееся пространство */
//}
//
//body {
//    font-family: 'Roboto', sans-serif;
//    background: linear-gradient(135deg, #ccd9e8, #e4997e);
//    margin: 0;
//    padding: 0;
//    color: #333;
//}
//
//*::-webkit-scrollbar{
//    width: 8px;
//}
//
//*::-webkit-scrollbar-thumb{
//    background-color: #b4acac;
//    border: 1px solid #443d3d;
//}
//
//*::-webkit-scrollbar-track{
//    background-color: #ccd9e8;
//}
//
///* Стили для форм */
//form {
//    background: white;
//    padding: 20px;
//    border-radius: 8px;
//    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
//    margin-bottom: 30px;
//}
//
//label {
//    display: inline-block;
//    width: 120px;
//    margin: 10px 0 5px;
//    font-weight: bold;
//}
//
//input[type="text"],
//input[type="number"],
//input[type="file"],
//select {
//    width: 300px;
//    padding: 8px;
//    margin-bottom: 10px;
//    border: 1px solid #ddd;
//    border-radius: 4px;
//    box-sizing: border-box;
//}
//
//button {
//    background-color: #3498db;
//    color: white;
//    border: none;
//    padding: 10px 20px;
//    margin-top: 10px;
//    border-radius: 4px;
//    cursor: pointer;
//    font-size: 16px;
//    transition: background-color 0.3s;
//}
//
//button:hover {
//    background-color: #2980b9;
//}
//
///* Стили для отзывов */
//#review-stats {
//    background: white;
//    padding: 20px;
//    border-radius: 8px;
//    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
//}
//
//#reviews-list {
//    display: grid;
//    grid-template-columns: 1fr;
//    gap: 15px;
//}
//
//#reviews-list div {
//background: white;
//padding: 15px;
//border-radius: 8px;
//box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
//transition: transform 0.2s;
//}
//
//        #reviews-list div:hover {
//    transform: translateY(-2px);
//}
//
//#reviews-list p {
//margin: 8px 0;
//        }
//
//        /* Стили для звезд рейтинга */
//        .star-rating {
//    color: #f1c40f;
//    font-size: 1.2em;
//    letter-spacing: 2px;
//}
//
///* Стили для распределения оценок */
//#rating-distribution {
//    margin-top: 15px;
//}
//
//#rating-distribution p {
//margin: 5px 0;
//padding-left: 20px;
//}
//
///* Адаптивность */
//@media (max-width: 768px) {
//form {
//    padding: 15px;
//}
//
//label {
//    width: 100%;
//    display: block;
//}
//
//input[type="text"],
//input[type="number"],
//input[type="file"],
//select {
//    width: 100%;
//}
//
//button {
//    width: 100%;
//}
//}
//
//        /* Стили для выбора периода */
//        #review-period {
//    padding: 8px;
//    margin-right: 10px;
//    border-radius: 4px;
//    border: 1px solid #ddd;
//}
//
//#load-reviews-btn {
//    padding: 8px 15px;
//}
//
//.admin-section {
//    background: white;
//    padding: 20px;
//    border-radius: 8px;
//    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
//    margin-bottom: 30px;
//}
//
//.period-selector {
//    margin: 20px 0;
//    display: flex;
//    align-items: center;
//    gap: 10px;
//}
//
//.period-selector select {
//padding: 8px 12px;
//border-radius: 4px;
//border: 1px solid #ddd;
//}
//
//        .reservations-table {
//    width: 100%;
//    border-collapse: collapse;
//    margin-top: 15px;
//}
//
//.reservations-table th,
//.reservations-table td {
//padding: 12px 15px;
//text-align: left;
//border-bottom: 1px solid #e0e0e0;
//}
//
//        .reservations-table th {
//background-color: #3498db;
//color: white;
//font-weight: bold;
//}
//
//        .reservations-table tr:hover {
//    background-color: #f5f5f5;
//}
//
//.btn {
//    padding: 8px 16px;
//    border: none;
//    border-radius: 4px;
//    cursor: pointer;
//    font-size: 14px;
//    transition: background-color 0.3s;
//}
//
//.btn:hover {
//    opacity: 0.9;
//}
//
//.btn-danger {
//    background-color: #e74c3c;
//    color: white;
//}
//
//.error {
//    color: #e74c3c;
//    padding: 15px;
//    background: #fdecea;
//    border-radius: 4px;
//    margin-top: 15px;
//}
//
//.no-data {
//    color: #7f8c8d;
//    padding: 15px;
//    text-align: center;
//    margin-top: 15px;
//}
//

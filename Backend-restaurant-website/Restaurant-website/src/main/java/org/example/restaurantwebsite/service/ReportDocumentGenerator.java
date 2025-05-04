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


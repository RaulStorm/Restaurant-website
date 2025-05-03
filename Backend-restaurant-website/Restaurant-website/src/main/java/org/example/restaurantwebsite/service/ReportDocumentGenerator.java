package org.example.restaurantwebsite.service;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ReportDocumentGenerator {

    // === Универсальный метод для Word ===
    public ByteArrayOutputStream generateReviewReportWord(Map<String, Object> data) throws IOException {
        List<Map<String, Object>> reviews = (List<Map<String, Object>>) data.get("reviews");
        double averageRating = ((Number) data.get("averageRating")).doubleValue();
        List<Number> percentagesList = (List<Number>) data.get("ratingPercentages");
        double[] percentages = percentagesList.stream().mapToDouble(Number::doubleValue).toArray();
        String periodLabel = "Выбранный период";

        return generateReviewReportWord(reviews, averageRating, percentages, periodLabel);
    }

    // === Универсальный метод для Excel ===
    public ByteArrayOutputStream generateReviewReportExcel(Map<String, Object> data) throws IOException {
        List<Map<String, Object>> reviews = (List<Map<String, Object>>) data.get("reviews");
        double averageRating = ((Number) data.get("averageRating")).doubleValue();
        List<Number> percentagesList = (List<Number>) data.get("ratingPercentages");
        double[] percentages = percentagesList.stream().mapToDouble(Number::doubleValue).toArray();
        String periodLabel = "Выбранный период";

        return generateReviewReportExcel(reviews, averageRating, percentages, periodLabel);
    }

    // === Word: детальный отчёт ===
    public ByteArrayOutputStream generateReviewReportWord(
            List<Map<String, Object>> reviews,
            double averageRating,
            double[] ratingPercentages,
            String periodLabel) throws IOException {

        XWPFDocument doc = new XWPFDocument();

        // Заголовок
        addTitle(doc, "Отзывы за период: " + periodLabel);
        addParagraph(doc, String.format("Средняя оценка: %.2f | Всего отзывов: %d",
                averageRating, reviews.size()));

        addParagraph(doc, "Распределение оценок:");
        for (int i = 0; i < ratingPercentages.length; i++) {
            addParagraph(doc, (i + 1) + " ★: " + String.format("%.1f", ratingPercentages[i]) + " %");
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

    // === Excel: детальный отчёт ===
    public ByteArrayOutputStream generateReviewReportExcel(
            List<Map<String, Object>> reviews,
            double averageRating,
            double[] ratingPercentages,
            String periodLabel) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Отзывы");

        int rowIdx = 0;

        Row titleRow = sheet.createRow(rowIdx++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Отзывы за период: " + periodLabel);
        CellStyle titleStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        titleStyle.setFont(font);
        titleCell.setCellStyle(titleStyle);

        Row statRow = sheet.createRow(rowIdx++);
        statRow.createCell(0).setCellValue("Средняя оценка:");
        statRow.createCell(1).setCellValue(averageRating);
        statRow.createCell(2).setCellValue("Всего отзывов:");
        statRow.createCell(3).setCellValue(reviews.size());

        rowIdx++;

        Row headerRow = sheet.createRow(rowIdx++);
        String[] headers = {"Пользователь", "Дата", "Оценка", "Отзыв"};
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

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out;
    }

    // === Вспомогательные методы ===

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

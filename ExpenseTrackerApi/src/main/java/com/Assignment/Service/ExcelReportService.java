package com.Assignment.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelReportService {

    public byte[] generateExcelReport(Map<String, Double> categories, double total, int year, int month) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        
        // Create sheet with month-year as name (e.g., "April-2025")
        Sheet sheet = workbook.createSheet(Month.of(month).name() + "-" + year);

        // Title Row
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("EXPENSE REPORT - " + Month.of(month).name() + " " + year);
        
        // Merge title cells
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

        // Style for title
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short)14);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCell.setCellStyle(titleStyle);

        // Header Row
        Row headerRow = sheet.createRow(1);
        headerRow.createCell(0).setCellValue("CATEGORY");
        headerRow.createCell(1).setCellValue("AMOUNT (INR)");

        // Data Rows
        int rowNum = 2;
        for (Map.Entry<String, Double> entry : categories.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }

        // Total Row
        Row totalRow = sheet.createRow(rowNum);
        totalRow.createCell(0).setCellValue("TOTAL");
        totalRow.createCell(1).setCellValue(total);
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        // Add date generated footer
        Row footerRow = sheet.createRow(rowNum + 2);
        footerRow.createCell(0).setCellValue("Generated on: " + LocalDate.now().toString());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream.toByteArray();
    }
}
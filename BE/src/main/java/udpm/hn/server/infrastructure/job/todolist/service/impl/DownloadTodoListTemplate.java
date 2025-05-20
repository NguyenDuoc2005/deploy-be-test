package udpm.hn.server.infrastructure.job.todolist.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.ResponseObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class DownloadTodoListTemplate {

    public ResponseObject<?> downloadTemplate() {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Template Thông Tin Task");

            Row row = sheet.createRow(0);

            Font font = workbook.createFont();
            font.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setLocked(true);
            headerCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setFont(font);
            headerCellStyle.setWrapText(true);

            Cell cell = row.createCell(0);
            cell.setCellValue("STT");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("Mã Task");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("Tên Task");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(3);
            cell.setCellValue("Mô tả");
            cell.setCellStyle(headerCellStyle);

//            cell = row.createCell(4);
//            cell.setCellValue("Tên Dự án");
//            cell.setCellStyle(headerCellStyle);

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
//            sheet.autoSizeColumn(4);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ResponseObject<>(new ByteArrayInputStream(outputStream.toByteArray()), HttpStatus.OK, "Download template successfully");
        } catch (IOException ex) {
            log.error("Error during export Excel file", ex);
            return null;
        }
    }

}

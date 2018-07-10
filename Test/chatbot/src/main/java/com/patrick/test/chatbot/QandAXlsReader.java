package com.patrick.test.chatbot;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * 
 * @author Patrick Pan
 *
 */
public enum QandAXlsReader {
	STRING("@Patrick@"), KEY(",");

	private String delimiter;

	private QandAXlsReader(String delimiter) {
		this.delimiter = delimiter;
	}

	public List<List<Map<String, List<String>>>> read(String excelName) throws IOException {
		String excelNameForTest = createXlsForTest(excelName);

		if (excelNameForTest == null) {
			return null;
		}

		List<List<Map<String, List<String>>>> questionsAndQuestionsList = new ArrayList<>();

		try (InputStream excel = new FileInputStream(excelNameForTest); HSSFWorkbook wb = new HSSFWorkbook(excel);) {
			HSSFSheet sheet = wb.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();

			// skip first row "Questions/Answers"
			if (rows.hasNext()) {
				rows.next();
			}

			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();

				short maxColumnIndex = row.getLastCellNum();

				if (maxColumnIndex % 2 != 0) {
					// invalid row because question and question should exist at the same time
					continue;
				}

				List<Map<String, List<String>>> questionsAndQuestions = new ArrayList<>();

				for (short columnIndex = 0; columnIndex < maxColumnIndex - 1; columnIndex += 2) {
					String question = row.getCell(columnIndex).getStringCellValue();
					String[] answers = row.getCell(columnIndex + 1).getStringCellValue().split(delimiter);
					List<String> answersList = Arrays.asList(answers);
					answersList.forEach(e -> {
						e.trim();
					});
					Map<String, List<String>> questionAndQuestions = new HashMap<>();
					questionAndQuestions.put(question, answersList);
					questionsAndQuestions.add(questionAndQuestions);
				}

				questionsAndQuestionsList.add(questionsAndQuestions);
			}

		}

		return questionsAndQuestionsList;
	}

	/**
	 * Create an excel for test whose empty cells will be filled with the value of
	 * the cell in last row.
	 * 
	 * @param excelName
	 * @return
	 * @throws IOException
	 */
	private String createXlsForTest(String excelName) throws IOException {
		String excelNameForTest = new StringBuilder("tmp/").append(excelName.substring(0, excelName.indexOf('.'))).append('_')
				.append((new Date()).getTime()).append(".xls").toString();

		try (InputStream excel = QandAXlsReader.class.getClassLoader().getResourceAsStream(excelName);
				HSSFWorkbook wb = new HSSFWorkbook(excel);
				HSSFWorkbook newWorkbook = new HSSFWorkbook();
				FileOutputStream out = new FileOutputStream(excelNameForTest)) {

			HSSFSheet sheet = wb.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();

			if (!rows.hasNext()) {
				return null;
			}

			HSSFSheet newSheet = newWorkbook.createSheet();
			int newRowNum = 0;
			HSSFRow lastRow = null;

			// copy first two rows since they cannot contain empty cells
			while (rows.hasNext() && newRowNum < 2) {
				HSSFRow row = (HSSFRow) rows.next();
				Iterator<Cell> cells = row.cellIterator();
				HSSFRow newRow = newSheet.createRow(newRowNum);

				short columnIndex = 0;
				while (cells.hasNext()) {
					HSSFCell cell = (HSSFCell) cells.next();
					newRow.createCell(columnIndex).setCellValue(cell.getStringCellValue().trim());
					columnIndex++;
				}

				if (newRowNum == 1) {
					lastRow = newRow;
				}

				newRowNum++;
			}

			// empty cells may probably exist
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();
				HSSFRow newRow = newSheet.createRow(newRowNum);

				short maxColumnIndex = row.getLastCellNum();

				for (short columnIndex = 0; columnIndex < maxColumnIndex; columnIndex++) {
					HSSFCell cell = row.getCell(columnIndex);

					if (cell == null || "".equals(cell.getStringCellValue().trim())) {
						// get value from last row
						cell = lastRow.getCell(columnIndex);
					}

					if (cell == null) {
						// cell cannot be null in general case
						continue;
					}

					newRow.createCell(columnIndex).setCellValue(cell.getStringCellValue());
				}

				lastRow = newRow;
				newRowNum++;
			}

			newWorkbook.write(out);
		}

		return excelNameForTest;
	}
}

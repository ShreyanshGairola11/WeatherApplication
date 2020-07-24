package automation.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class Dataprovider {

	public static String sheetName = "Sheet1";
	static ExcelReader objExcelFile;
	static String env = getEnvironment();
	public static String excelName = "odi_"+ env.toUpperCase() +".xls";

	@DataProvider
	public static Iterator<Object[]> fileDataProvider() {
		Sheet templateSheet = null;
		List<String[]> testData;
		List<Object[]> dataToBeReturned = new ArrayList<Object[]>();

		try {
			objExcelFile = new ExcelReader();
			try {
				templateSheet = objExcelFile.readExcel(sheetName, excelName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			testData = getFileContent(templateSheet);

			for (String[] userData : testData) {
				dataToBeReturned.add(new Object[] { userData });
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return dataToBeReturned.iterator();

	}

	private static String getEnvironment() {
		String env = System.getProperty("tier");
		if(env==null) 
			env  = ConfigPropertyReader.getProperty("tier");
		return env;
	}

	public static List<String[]> getFileContent(Sheet templateSheet) {
		List<String[]> lines = new ArrayList<String[]>();

		CellReference ref = null;
		int columnCount = 0;
		String templateContent = null;
		int rowCount = templateSheet.getLastRowNum() - templateSheet.getFirstRowNum();

		ref = new CellReference("C4");
		columnCount = 8;

		Row r = null;
		if (ref != null) {
			r = templateSheet.getRow(ref.getRow());
		}
		for (int i = 1; i <= rowCount; i++) {
			Row row = templateSheet.getRow(i);
			String[] arr1 = new String[columnCount];

			for (int j = 0; j < columnCount; j++) {
				if ((HSSFCell.CELL_TYPE_STRING == row.getCell(j).getCellType())) {

					arr1[j] = row.getCell(j).getStringCellValue().trim();
				} else {
					arr1[j] = NumberToTextConverter.toText(row.getCell(j).getNumericCellValue());
				}
			}
			lines.add(arr1);
		}
		return lines;
	}

}

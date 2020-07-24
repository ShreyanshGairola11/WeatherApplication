package automation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelReader {

	static String currentDir = System.getProperty("user.dir");

	public static Sheet readExcel(String sheetName, String excelName) throws IOException {
		Sheet templateSheet = null;
		String fileName = null;
		// filename must be sent along with the extension
		fileName = excelName;// + ".xlsx";

		String filePath = currentDir + File.separator + "src" + File.separator + "test" + File.separator + "resources"
				+ File.separator + "excelData";

		try {

			File file = new File(filePath + File.separator + fileName);

			FileInputStream inputStream = new FileInputStream(file);

			Workbook wrkbkTemplates = null;

			String fileExtensionName = fileName.substring(fileName.indexOf("."));

			if (fileExtensionName.trim().equals(".xls")) {
				wrkbkTemplates = new HSSFWorkbook(inputStream);
			}

			else if (fileExtensionName.trim().equals(".xlsx")) {
				wrkbkTemplates = new HSSFWorkbook(inputStream);

			}
			templateSheet = wrkbkTemplates.getSheet(sheetName);

			inputStream.close();

		} catch (Exception ex) {
			System.out.println(ex);
		}

		return templateSheet;
	}

}
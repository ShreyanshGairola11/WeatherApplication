package automation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelWriter {

	static String currentDir = System.getProperty("user.dir");

	public static void writeExcel(String excelName, List<String> data) {
		Sheet templateSheet = null;
		String fileName = excelName;
		String filePath = currentDir + File.separator + "src" + File.separator + "test" + File.separator + "resources"
				+ File.separator + "excelData";
		File file = new File(filePath + File.separator + fileName);
		try {
			FileInputStream inputStream = new FileInputStream(file);
			Workbook wrkbkTemplates = null;
			wrkbkTemplates = new HSSFWorkbook();
			templateSheet = wrkbkTemplates.createSheet();
			int rows = data.size();
			for (int i = 0; i < rows; i++) {
				Row row = templateSheet.createRow(i);
				row.createCell(0).setCellValue(data.get(i));
			}
			FileOutputStream outputStream = new FileOutputStream(file);
			wrkbkTemplates.write(outputStream);
			wrkbkTemplates.close();
			inputStream.close();
			outputStream.close();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	private static final ExcelWriter INSTANCE = new ExcelWriter();
	private static int a;

	public static ExcelWriter getInstance() {
		return INSTANCE;
	}

//	public static void main(String args[])
//		   {
//			   Map<Integer, Object[]> data = new TreeMap<Integer, Object[]>();
//		       // data.put("", new Object[]{ "ID", "NAME", "LASTNAME" });
//			   for(int i=0;i<6;i++){
//		        data.put(i, new Object[]{ i, "Pankaj", "Kumar" });
//		       // data.put(2, new Object[]{ 2, "Prakashni", "sfdf" });
//			   }
//			   writeToExcelSheet(data);
//		   }

	public static void writeToExcelSheet(String fileName, String sheetName, Map<Integer, Object[]> data) {
		String filePath = currentDir + File.separator + "src" + File.separator + "test" + File.separator + "resources"
				+ File.separator + "excelData";
		File file = new File(filePath + File.separator + fileName);
		try {
			FileInputStream myxls = new FileInputStream(file);
			HSSFWorkbook studentsSheet = new HSSFWorkbook(myxls);
			// HSSFSheet worksheet = studentsSheet.getSheetAt(0);
			HSSFSheet worksheet = studentsSheet.getSheet(sheetName);
			a = worksheet.getLastRowNum();

			System.out.println(a);
			// Row row = worksheet.createRow(++a);

			Set<Integer> keyset = data.keySet();
			// int rownum = 0;
			for (Integer key : keyset) {
				// this creates a new row in the sheet
				Row row = worksheet.createRow(++a);
				Object[] objArr = data.get(key);
				int cellnum = 0;
				for (Object obj : objArr) {
					// this line creates a cell in the next column of that row
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof String)
						cell.setCellValue((String) obj);
					else if (obj instanceof Integer)
						cell.setCellValue((Integer) obj);
				}
			}

			// row.createCell(1).setCellValue("Dr.Hola");
			myxls.close();
			FileOutputStream output_file = new FileOutputStream(file);
			// write changes
			studentsSheet.write(output_file);
			output_file.close();
			System.out.println(" is successfully written");
		} catch (Exception e) {
		}
	}

	/*
	 * public static void main(String arg[]) { List<String> data = new
	 * ArrayList<String>(); data.add("httt"); data.add("pp"); data.add("dddd");
	 * String[] data = { "httt", "hiii", "byeee", "tathhhha", "ggg", "rfrr" };
	 * writeExcel("ProfileRoles.xls",0,data); System.out.println("done");
	 * 
	 * }
	 */

}

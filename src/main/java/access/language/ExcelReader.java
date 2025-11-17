package access.language;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public final class ExcelReader {
    /**
     * Reads a single cell from the first sheet of an .xlsx file.
     *
     * @param pPath       path to the .xlsx file
     * @param pRow  row number starting at one (Excel style)
     * @param pCol  column number starting at one (Excel style)
     * @return cell value as String, or null when empty or missing
     * @throws IOException when file cannot be read
     */
    public static String readCell(String pPath, int pRow, int pCol) throws IOException {
        // Convert one-based indexes to zero-based indexes
        int rowIndex = pRow - 1;
        int colIndex = pCol - 1;

        try (FileInputStream fis = new FileInputStream(pPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return null;
            }

            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                return null;
            }

            Cell cell = row.getCell(colIndex);
            if (cell == null) {
                return null;
            }

            return cell.toString();
        }
    }
}

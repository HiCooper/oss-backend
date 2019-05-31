package com.berry.oss.common.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2018/9/1 14:41
 * fileName：ImportExcelUtil
 * Use：
 */
public class ExcelUtil {

    /**
     * 2003- 版本的excel
     */
    private final static String EXCEL_2003_L = ".xls";
    /**
     * 2007+ 版本的excel
     */
    private final static String EXCEL_2007_U = ".xlsx";

    public final static String ENC = "utf-8";


    /**
     * 解析excel文件
     *
     * @param file          文件
     * @param formatJsonMap 文件头信息字段匹配信息（如文件中有表头姓名,一个值为'张三'）map.put("表头","name")，返回的map信息为： {key:name,value:"张三"}
     *                      详见 测试接口 importExcel
     * @return List<Map                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               <                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               String                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               Object>>
     * @throws Exception 解析异常
     */
    public static List<Map<String, Object>> parseExcel(MultipartFile file, Map<String, String> formatJsonMap) throws Exception {
        List<Map<String, Object>> data = new ArrayList<>();

        Workbook workbook = getWorkbook(file.getInputStream(), file.getOriginalFilename());
        //遍历Excel中所有的sheet
        for (int k = 0; k < workbook.getNumberOfSheets(); k++) {
            Sheet sheet = workbook.getSheetAt(k);
            int firstRow = sheet.getFirstRowNum();
            int lastRow = sheet.getLastRowNum();

            // 行循环
            for (int i = firstRow + 1; i < lastRow + 1; i++) {
                Map<String, Object> map = new HashMap<>(16);

                Row row = sheet.getRow(i);
                int firstCell = row.getFirstCellNum();
                int lastCell = row.getLastCellNum();

                // 单元格循环
                for (int j = firstCell; j < lastCell; j++) {
                    // 获取表头信息
                    Cell head = sheet.getRow(firstRow).getCell(j);
                    String key = formatJsonMap.get(getCellValue(head).toString());

                    // 获取当前单元格
                    Cell cell = row.getCell(j);
                    // 获取单元格值
                    Object val = getCellValue(cell);

                    map.put(key, val);
                }
                data.add(map);
            }
        }
        workbook.close();
        return data;
    }

    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     *
     * @param inStr,fileName
     * @return
     * @throws Exception
     */
    private static Workbook getWorkbook(InputStream inStr, String fileName) throws Exception {
        Workbook wb;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if (EXCEL_2003_L.equals(fileType)) {
            //2003-
            wb = new HSSFWorkbook(inStr);
        } else if (EXCEL_2007_U.equals(fileType)) {
            //2007+
            wb = new XSSFWorkbook(inStr);
        } else {
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }

    /**
     * 描述：对表格中数值进行格式化
     *
     * @param cell
     * @return
     */
    private static Object getCellValue(Cell cell) {
        Object value = null;
        //格式化number String字符
        DecimalFormat df = new DecimalFormat("0");
        //日期格式化
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        //格式化数字
        DecimalFormat df2 = new DecimalFormat("0.00");

        switch (cell.getCellType()) {
            case STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case NUMERIC:
                if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                    value = df.format(cell.getNumericCellValue());
                } else if ("m/d/yy".equals(cell.getCellStyle().getDataFormatString())) {
                    value = sdf.format(cell.getDateCellValue());
                } else {
                    value = df2.format(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case BLANK:
                value = "";
                break;
            default:
                break;
        }
        return value;
    }
}
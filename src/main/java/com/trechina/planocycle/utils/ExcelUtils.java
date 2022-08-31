package com.trechina.planocycle.utils;


import com.google.common.base.Strings;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelUtils {
    private ExcelUtils(){}

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    public static void generateExcel(Map<String, List<String>> headersByClassify,
                                         Map<String, List<String>> columnsByClassify, List<Map<String, Object>> allData,
                                         OutputStream outputStream) {
        try(XSSFWorkbook workbook = new XSSFWorkbook()){
            XSSFSheet sheet = workbook.createSheet();
            Pattern numberPattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?%?");

            //最初の行の索引
            int colIndex=0;
            //2行目の索引
            int headerColIndex=0;
            XSSFCell cell = null;

            //最初の行
            XSSFRow headerClassifyRow = sheet.createRow(0);
            //2行目
            XSSFRow headerRow = sheet.createRow(1);
            for (Map.Entry<String, List<String>> entry : headersByClassify.entrySet()) {
                String headerClassify = entry.getKey();
                List<String> headers = entry.getValue();

                if(!headers.isEmpty()){
                    cell = headerClassifyRow.createCell(colIndex);
                    //最後の列のヘッダーは空の文字列に処理されます
                    cell.setCellValue(headerClassify.equalsIgnoreCase("rank")?"":headerClassify);
                    cell.setCellType(CellType.STRING);
                    //連結カラムの開始インデックス
                    int startMergeIndex = colIndex;
                    //連結カラムの最後のインデックス
                    int endMergeIndex = colIndex+headers.size()-1;
                    if(startMergeIndex!=endMergeIndex){
                        //[startMergeIndex,endMergeIndex]のセルをマージ
                        sheet.addMergedRegion(new CellRangeAddress(0, 0, startMergeIndex, endMergeIndex));
                    }

                    colIndex+=headers.size();

                    //セルを結合しない2行目の列
                    for (String header : headers) {
                        XSSFCell headerCell = headerRow.createCell(headerColIndex);
                        headerCell.setCellType(CellType.STRING);
                        headerCell.setCellValue(header);

                        headerColIndex++;
                    }
                }
            }

            //すべての商品を巡って、一つの商品は一列です。
            XSSFRow janRow = null;
            XSSFCell janCell = null;
            for (int i = 0; i < allData.size(); i++) {
                Map<?, ?> data = allData.get(i);
                int columnIndex = 0;
                //ヘッダーを除き、商品内容は3行目から
                int rowIndex = i+2;
                janRow = sheet.createRow(rowIndex);

                for (List<String> columnList : columnsByClassify.values()) {
                    //すべてのカラム名を巡回し、対応する値を見つけます。
                    for (String columnName : columnList) {
                        //poのgetterメソッドによるフィールド値の取得
                        Object value = data.get(columnName);
                        janCell= janRow.createCell(columnIndex);

                        if(value instanceof BigDecimal){
                            janCell.setCellType(CellType.NUMERIC);
                            janCell.setCellValue(((BigDecimal) value).intValue());
                        }else if(value instanceof Integer){
                            janCell.setCellType(CellType.NUMERIC);
                            janCell.setCellValue((Integer)value);
                        }else{
                            Matcher isNum = numberPattern.matcher(String.valueOf(value));
                            if (!columnName.equals("jan") && !columnName.equals("jan_name") && isNum.matches()){
                                janCell.setCellType(CellType.NUMERIC);
                                janCell.setCellValue(Math.floor(Double.parseDouble(String.valueOf(value))));
                            }else{
                                String valStr = value==null?"":String.valueOf(value);
                                if(columnName.startsWith("intage") && Strings.isNullOrEmpty(valStr)){
                                    janCell.setCellType(CellType.NUMERIC);
                                    janCell.setCellValue(0);
                                }else{
                                    janCell.setCellType(CellType.STRING);
                                    janCell.setCellValue(Objects.nonNull(value)?String.valueOf(value):"");
                                }
                            }
                        }

                        columnIndex++;
                    }
                }
            }

            XSSFSheet sheet1 = workbook.createSheet();

            workbook.write(outputStream);
        }catch (Exception e){
            logger.error("", e);
        }
    }

    public static void generateNormalExcel(List<String[]> allData, OutputStream outputStream) {
        try(XSSFWorkbook workbook = new XSSFWorkbook()){
            XSSFSheet sheet = workbook.createSheet();
            XSSFRow janRow;
            XSSFCell janCell;
            for (int i = 0; i < allData.size(); i++) {
                int columnIndex = 0;
                janRow = sheet.createRow(i);
                for (String column : allData.get(i)) {
                        Object value = column;
                        janCell= janRow.createCell(columnIndex);
                        //if(value instanceof BigDecimal){
                        //    janCell.setCellType(CellType.NUMERIC);
                        //    janCell.setCellValue(((BigDecimal) value).intValue());
                        //}else if(value instanceof Integer){
                        //    janCell.setCellType(CellType.NUMERIC);
                        //    janCell.setCellValue((Integer)value);
                        //}else{
                            janCell.setCellType(CellType.STRING);
                            janCell.setCellValue(Objects.nonNull(value)?String.valueOf(value):"");
                        //}
                        columnIndex++;
                    }
            }
            workbook.write(outputStream);
        }catch (Exception e){
            logger.error("", e);
        }
    }

    public static List<String[]> readExcel(MultipartFile file)
    {
        ArrayList<String[]> excelList = new ArrayList<>();
        try {
            InputStream fis = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            String[] data;
            int rows = sheet.getPhysicalNumberOfRows();
            for(int i=0;i<rows;i++) {
                XSSFRow row = sheet.getRow(i);
                if(row==null) {
                    break;
                }
                data=new String[row.getPhysicalNumberOfCells()];
                for(int j=0;j<row.getPhysicalNumberOfCells();j++) {//16384
                    XSSFCell cell = row.getCell(j);
                    if(cell!=null) {
                        cell.setCellType(CellType.STRING);
                        String cellValue = getStringVal(cell);

                        data[j]=cellValue;
                    }
                }
                excelList.add(data);
            }
            workbook.close();
            fis.close();
        }catch(IOException e) {
            logger.error("excel取り込み", e);
        }
        return excelList;
    }

    public static String getStringVal(XSSFCell cell) {
        CellType cellType=cell.getCellType();
        //if(cellType==CellType.BOOLEAN) {
        //    return cell.getBooleanCellValue() ? "TRUE" : "FALSE";
        //}else if(cellType==CellType.FORMULA) {
        //    return cell.getCellFormula();
        //}else if(cellType==CellType.NUMERIC) {
        //    if(DateUtil.isCellDateFormatted(cell)){
        //        return DateFormat.getInstance().format(
        //                cell.getNumericCellValue());
        //    }
        //    else {
        //        String value = String.valueOf(cell.getNumericCellValue());
        //        if(value.contains("E")){
        //            cell.setCellType(CellType.STRING);
        //            return cell.getStringCellValue();
        //        }
        //        return value;
        //    }
        //}else
        // if(cellType==CellType.STRING) {
            return cell.getStringCellValue();
        //}else {
        //    return "";
        //}
    }
}

package com.trechina.planocycle.utils;


import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExcelUtils {
    private ExcelUtils(){}

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    public static <T> void generateExcel(Map<String, List<String>> headersByClassify,
                                         Map<String, List<String>> columnsByClassify, List<T> allData,
                                         OutputStream outputStream) {
        try(XSSFWorkbook workbook = new XSSFWorkbook()){
            XSSFSheet sheet = workbook.createSheet();

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

            Class<?> clazz = allData.get(0).getClass();
            //すべての商品を巡って、一つの商品は一列です。
            XSSFRow janRow = null;
            XSSFCell janCell = null;
            for (int i = 0; i < allData.size(); i++) {
                T data = allData.get(i);
                int columnIndex = 0;
                //ヘッダーを除き、商品内容は3行目から
                int rowIndex = i+2;
                janRow = sheet.createRow(rowIndex);

                for (List<String> columnList : columnsByClassify.values()) {
                    //すべてのカラム名を巡回し、対応する値を見つけます。
                    for (String columnName : columnList) {
                        //poのgetterメソッドによるフィールド値の取得
                        String getMethodName = "get" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                        Method method = clazz.getMethod(getMethodName);
                        Object value = method.invoke(data);

                        janCell= janRow.createCell(columnIndex);

                        if(value instanceof BigDecimal){
                            janCell.setCellType(CellType.NUMERIC);
                            janCell.setCellValue(((BigDecimal) value).intValue());
                        }else if(value instanceof Integer){
                            janCell.setCellType(CellType.NUMERIC);
                            janCell.setCellValue((Integer)value);
                        }else{
                            janCell.setCellType(CellType.STRING);
                            janCell.setCellValue(Objects.nonNull(value)?String.valueOf(value):"");
                        }

                        columnIndex++;
                    }
                }
            }

            workbook.write(outputStream);
        }catch (Exception e){
            logger.error("", e);
        }
    }
}

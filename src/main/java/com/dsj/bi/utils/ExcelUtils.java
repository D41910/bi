package com.dsj.bi.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel 工具类
 */
@Slf4j
public class ExcelUtils {
    /**
     * excel 转 scv
     *
     * @param multipartFile
     * @return
     */
    public static String excelToCsv(MultipartFile multipartFile) {
        // 读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误", e);
        }
        if (CollUtil.isEmpty(list)) {
            return "";
        }
        // 转换为 csv
        StringBuilder stringBuilder = new StringBuilder();
        // 读取表头
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        stringBuilder.append(StringUtils.join(headerList, ",")).append("\n");
        // 读取数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(dataList, ",")).append("\n");
        }
        return stringBuilder.toString();
    }

    public static List<Map<String, String>> excelToList(MultipartFile multipartFile) {
        //读取数据
        List<Map<String, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误", e);
        }
        return list;
    }


    public static String listToCsv(List<Map<String, String>> list) {
        if (CollUtil.isEmpty(list)) {
            return "";
        }
        //转换为csv
        StringBuilder stringBuilder = new StringBuilder();
        //读取表头
        LinkedHashMap<String, String> headerMap = (LinkedHashMap) list.get(0);
        List<String> headerlist = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        stringBuilder.append(StringUtils.join(headerlist, ",")).append("\n");
        //读取数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<String, String> dataMap = (LinkedHashMap) list.get(i);
            List<String> datalist = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(datalist, ",")).append("\n");
        }
        return stringBuilder.toString();
    }


    public static void listToExcel(List<Map<String, String>> listChartData, OutputStream outputStream) {
        List<List<String>> listData = listChartData.stream()
                .map(map -> {
                    return map.values().stream().collect(Collectors.toList());
                })
                .collect(Collectors.toList());
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("图表数据");

            // 创建表头
            XSSFRow headerRow = sheet.createRow(0);
            List<String> listDataHeader = listData.get(0);
            int length = listDataHeader.size();
            for (int i = 0; i < length; i++) {
                headerRow.createCell(i).setCellValue(listDataHeader.get(i));
            }

            // 填充数据
            XSSFRow dataRow = null;
            List<String> dataList = null;
            for(int i = 1; i < listData.size(); i++) {
                dataRow = sheet.createRow(i);
                dataList = listData.get(i);
                for (int j = 0; j < length; j++) {
                    if(dataList == null) {
                        dataRow.createCell(j).setCellValue("");
                    }else {
                        dataRow.createCell(j).setCellValue(dataList.get(j));
                    }
                }
            }

            workbook.write(outputStream);

            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Excel 生成失败", e);
        }
    }

    public static void main(String[] args) {
        System.out.println(excelToCsv(null));
    }
}

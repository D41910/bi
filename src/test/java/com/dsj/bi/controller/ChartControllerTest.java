package com.dsj.bi.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.dsj.bi.common.ChartExportRequest;
import com.dsj.bi.service.ChartService;
import com.dsj.bi.utils.ExcelUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChartControllerTest {

    @Autowired
    private ChartService chartService;

    @Test
    void genChartByAi() throws IOException {
        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:(全)23数据科学与大数据技术2（本）成绩信息(1).xlsx");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FileInputStream input = new FileInputStream(file);
        // 创建MockMultipartFile，参数分别为：文件名, 原始文件名, 内容类型, 输入流
        MockMultipartFile mockMultipartFile = new MockMultipartFile(file.getName(),
                file.getName(),
                "multipart/form-data",
                input);
        boolean b = chartService.saveChartData(ExcelUtils.excelToList(mockMultipartFile),"table_123");
        System.out.println(b);
    }

    @Test
    void getExcelById() throws IOException {

    }
}
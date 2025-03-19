package com.dsj.bi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dsj.bi.model.entity.Chart;

import java.util.List;
import java.util.Map;

/**
* @author 25729
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2025-03-08 09:21:28
*/
public interface ChartService extends IService<Chart> {

    boolean saveChartData(List<Map<String,String>> listChartData, String tableName);

    List<Map<String, String>> listChartData(Long chartId);
}

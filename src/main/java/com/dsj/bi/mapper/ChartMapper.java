package com.dsj.bi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dsj.bi.model.entity.Chart;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author 25729
* @description 针对表【chart(图表信息表)】的数据库操作Mapper
* @createDate 2025-03-08 09:21:28
* @Entity generator.domain.Chart
*/
public interface ChartMapper extends BaseMapper<Chart> {

    void creatTable(@Param("list") List<String> list,@Param("tableName") String tableName );

    void insertListData(@Param("listData") List<List<String>> listData,@Param("tableName") String tableName);

    List<Map<String, String>> listChartData(@Param("tableName")String tableName);
}





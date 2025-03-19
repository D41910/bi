package com.dsj.bi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dsj.bi.common.ErrorCode;
import com.dsj.bi.exception.BusinessException;
import com.dsj.bi.mapper.ChartMapper;
import com.dsj.bi.model.entity.Chart;
import com.dsj.bi.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 25729
 * @description 针对表【chart(图表信息表)】的数据库操作Service实现
 * @createDate 2025-03-08 09:21:28
 */
@Service
@Slf4j
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
        implements ChartService {

    @Resource
    private ChartMapper chartMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveChartData(List<Map<String, String>> listChartData, String tableName) {
        try {
            // 读取表格
            List<List<String>> listData = new ArrayList<>();
            for (Map<String, String> map : listChartData) {
                listData.add(map.values().stream()
                        .collect(Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    // 使用反向迭代器
                                    ListIterator<String> iterator = list.listIterator(list.size());
                                    while (iterator.hasPrevious()) {
                                        if (ObjectUtils.isNotEmpty(iterator.previous())) {
                                            // 找到位置后截取子列表
                                            int index = iterator.nextIndex();
                                            return list.subList(0, index + 1);
                                        }
                                    }
                                    return Collections.emptyList();
                                })
                        )
                );
            }
            int maxLength = listData.stream()
                    .mapToInt(List::size)
                    .max()
                    .orElse(0);
            listData = listData.stream().filter(list -> list.size() == maxLength).collect(Collectors.toList());
            chartMapper.creatTable(listData.get(0), tableName);
            chartMapper.insertListData(listData, tableName);
            return true;
        } catch (Exception e) {
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            // 可选：记录日志
            log.error("保存图表数据失败", e);
            return false;
        }
    }

    @Override
    public List<Map<String, String>> listChartData(Long chartId) {
        return chartMapper.listChartData("chart_" + chartId);
    }
}





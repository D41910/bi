package com.dsj.bi.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dsj.bi.annotation.AuthCheck;
import com.dsj.bi.common.*;
import com.dsj.bi.constant.CommonConstant;
import com.dsj.bi.constant.UserConstant;
import com.dsj.bi.exception.BusinessException;
import com.dsj.bi.exception.ThrowUtils;
import com.dsj.bi.manager.AiManager;
import com.dsj.bi.manager.RedisLimiterManager;
import com.dsj.bi.model.dto.chart.*;
import com.dsj.bi.model.entity.Chart;
import com.dsj.bi.model.entity.User;
import com.dsj.bi.model.vo.BiResponse;
import com.dsj.bi.service.ChartService;
import com.dsj.bi.service.UserService;
import com.dsj.bi.utils.ExcelUtils;
import com.dsj.bi.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * 帖子接口
 */
@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({"dev","local"})
public class QueueController {
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("/add")
    public void add(String name) {
        CompletableFuture.runAsync(() -> {
            System.out.println("任务执行中" + name + ",执行人：" + Thread.currentThread().getName());
            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, threadPoolExecutor);
    }

    @GetMapping("/get")
    public String get() {
        Map<String, Object> map = new HashMap<>();
        int size = threadPoolExecutor.getQueue().size();
        map.put("队列长度", size);
        long taskCount = threadPoolExecutor.getTaskCount();
        map.put("任务总数", taskCount);
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        map.put("已完成任务数", completedTaskCount);
        int activeCount = threadPoolExecutor.getActiveCount();
        map.put("正在工作线程数", activeCount);
        int corePoolSize = threadPoolExecutor.getCorePoolSize();
        map.put("核心线程数",corePoolSize);
        return JSONUtil.toJsonStr(map);

    }
}

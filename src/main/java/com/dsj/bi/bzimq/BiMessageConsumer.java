package com.dsj.bi.bzimq;

import com.dsj.bi.common.ErrorCode;
import com.dsj.bi.exception.BusinessException;
import com.dsj.bi.manager.AiManager;
import com.dsj.bi.model.entity.Chart;
import com.dsj.bi.service.ChartService;
import com.dsj.bi.utils.ExcelUtils;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class BiMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;


    /**
     * 指定程序监听的消息队列和确认机制
     *
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @SneakyThrows
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if (StringUtils.isEmpty(message)) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        long chartyId = Long.parseLong(message);
        Chart chart = chartService.getById(chartyId);
        if (StringUtils.isEmpty(message)) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表为空");
        }
        //先修改图表任务状态为“执行中” 等执行成功后，修改为“已完成”，保存执行结果：执行失败后，状态修改为“失败”，记录任务失败信息
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus("running");
        boolean b = chartService.updateById(updateChart);
        if (!b) {
            handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
            return;
        }

        //调用AI
        String result = aiManager.doChat(buildUserInput(chart));
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            handleChartUpdateError(chart.getId(), "AI 生成失败");
            return;
        }
        String genResult = splits[2].trim();
        String genChart = splits[1].trim();

        Chart updateChartResult = new Chart();
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);
        updateChartResult.setId(chart.getId());
        updateChartResult.setStatus("succeed");
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
        }
        //消息确认
        channel.basicAck(deliveryTag, false);
    }

    private String buildUserInput(Chart chart) {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = ExcelUtils.listToCsv(chartService.listChartData(chart.getId()));
        //构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(csvData).append("\n");
        return userInput.toString();
    }


    private void handleChartUpdateError(long chartId, String errorMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setExecMessage(errorMessage);
        updateChartResult.setStatus("failed");
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            log.error("更新图表失败状态失败");
        }

    }

}

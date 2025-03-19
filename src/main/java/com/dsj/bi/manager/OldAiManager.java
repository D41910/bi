package com.dsj.bi.manager;

import com.dsj.bi.common.ErrorCode;
import com.dsj.bi.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用于对接AI平台 （已废弃，调用鱼聪明AI）
 */
@Deprecated
@Service
public class OldAiManager {

    @Resource
    private YuCongMingClient yuCongMingClient;

    /**
     * AI 对话
     * @param message
     * @return
     */
    public String doChat(Long modelId, String message) {
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);
        BaseResponse<DevChatResponse> response=yuCongMingClient.doChat(devChatRequest);
        if(response == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"AI 响应错误");
        }
        return response.getData().getContent();
    }

}

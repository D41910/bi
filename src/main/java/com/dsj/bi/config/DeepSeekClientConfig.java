package com.dsj.bi.config;

import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.SSEResponseModel;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.lkeap.v20240522.LkeapClient;
import com.tencentcloudapi.lkeap.v20240522.models.ChatCompletionsRequest;
import com.tencentcloudapi.lkeap.v20240522.models.ChatCompletionsResponse;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("tencent.deepseek.client")
@Data
public class DeepSeekClientConfig {

    /**
     * 腾讯云账号 secretId
     */
    private String secretId;

    /**
     * 腾讯云账号 secretKey
     */
    private String secretKey;

    /**
     * 初始化DeepSeek客户端
     *
     * @return
     */
    @Bean
    public LkeapClient deepSeekClient() {
        Credential cred = new Credential(secretId, secretKey);

        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("lkeap.tencentcloudapi.com");
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        LkeapClient client = new LkeapClient(cred, "ap-shanghai", clientProfile);
        return client;

    }
}

package com.heima.aliyunOSS.service;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.heima.aliyunOSS.config.AliYunOSSArgConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayInputStream;

@Slf4j
public class AliOssService {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    @Autowired
    public AliOssService(AliYunOSSArgConfig aliYunOSSArgConfig) {
        // 通过构造函数注入并初始化字段
        this.endpoint = aliYunOSSArgConfig.getEndpoint();
        this.accessKeyId = aliYunOSSArgConfig.getAccessKeyId();
        this.accessKeySecret = aliYunOSSArgConfig.getAccessKeySecret();
        this.bucketName = aliYunOSSArgConfig.getBucketName();

        // 检查配置是否为空
        if (endpoint == null || accessKeyId == null || accessKeySecret == null || bucketName == null) {
            throw new IllegalStateException("阿里云 OSS 配置不能为空");
        }

        log.info("阿里云 OSS 配置加载成功: endpoint={}, bucketName={}", endpoint, bucketName);
    }

    /**
     * 文件上传
     *
     * @param bytes
     * @param objectName
     * @return
     */
    public String upload(byte[] bytes, String objectName) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        String url = endpoint.split("//")[0] +
                "//" +
                bucketName +
                "." +
                endpoint.split("//")[1] +
                "/" +
                objectName;

        log.info("文件上传到:{}", url);

        return url;
    }
}

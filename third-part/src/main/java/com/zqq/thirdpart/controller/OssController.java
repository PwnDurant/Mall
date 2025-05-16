package com.zqq.thirdpart.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.zqq.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class OssController {

    @Autowired
    private OSS ossClient;

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;

    @Value("${spring.cloud.alicloud.oss.bucketName}")
    private String bucketName;

    @Value("${spring.cloud.alicloud.access-key}")
    private String accessId;

    @Value("${spring.cloud.alicloud.oss.downloadUrl}")
    private String host;

    @RequestMapping("/oss/policy")
    public R policy(){
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dir=format+"/";  //用户上传文件时指定的前缀
        Map<String,String> respMap=null;
        try{
            long expireTime=30;
            long expireEndTime =System.currentTimeMillis()+expireTime*1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConditions = new PolicyConditions();
            policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE,0,1048576000);
            policyConditions.addConditionItem(MatchMode.StartWith,PolicyConditions.COND_KEY,dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConditions);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodePolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            respMap=new LinkedHashMap<>();
            respMap.put("accessid",accessId);
            respMap.put("policy",encodePolicy);
            respMap.put("signature",postSignature);
            respMap.put("dir",dir);
            respMap.put("host",host);
            respMap.put("expire",String.valueOf(expireEndTime/1000));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return R.ok().put("data",respMap);
    }

}

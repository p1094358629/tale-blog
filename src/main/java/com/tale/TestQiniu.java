package com.tale;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.tale.model.dto.Types;

public class TestQiniu {
    private static String accessKey = "9A0HSk3yit_ClUn1ruVm5SRQtYSVN8oiSmp88mus";
    private static String secretKey = "Z7ld61T8eAJ87Z9PfSlHufiI89WFZKQ1AARVrsA2";
    public static void upload(String localFilePath,String fileName) {
      //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone2());//华南zone2
        //...生成上传凭证，然后准备上传
        UploadManager uploadManager = new UploadManager(cfg);
        String bucket = "zzpete";
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
//        localFilePath = "C:/Users/admin/Desktop/111.png";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
    //    String key = null;
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, fileName, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }
    public static String load(String fileName){
        String publicUrl = null;
        try {
            String domainOfBucket = Types.ATTACH_URL;
            String encodedFileName = URLEncoder.encode(fileName, "utf-8");
            publicUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Auth auth = Auth.create(accessKey, secretKey);
        long expireInSeconds = 315360000;//10年，可以自定义链接过期时间,单位s
        return auth.privateDownloadUrl(publicUrl, expireInSeconds);
    }
    public static void main(String[] args) {
        TestQiniu.upload("","111");
        System.out.println(load("111"));
    }
}

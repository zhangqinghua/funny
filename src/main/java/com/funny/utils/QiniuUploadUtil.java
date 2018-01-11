package com.funny.utils;

import com.funny.vo.ErrorInfo;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 使用七牛存储图片
 * <p>
 * 2017年5月22日 修改硬编码 wei
 * FILE: com.eumji.zblog.util.PhotoUploadUtil.java
 * MOTTO:  不积跬步无以至千里,不积小流无以至千里
 * AUTHOR: EumJi
 * DATE: 2017/4/21
 * TIME: 22:08
 */
@Component
public class QiniuUploadUtil {
    //设置好账号的ACCESS_KEY和SECRET_KEY
    @Value("${qiniu.accessKey}")
    private String ACCESS_KEY;
    @Value("${qiniu.secretKey}")
    private String SECRET_KEY;
    //要上传的空间
    @Value("${qiniu.bucketName}")
    private String bucketname;
    @Value("${qiniu.basePath}")
    public String basePath;


    public Auth getAuth() {
        return Auth.create(ACCESS_KEY, SECRET_KEY);
    }

    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken() {
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        return auth.uploadToken(bucketname);
    }


    public ErrorInfo uploadPhoto(byte[] data, String fileName) {
        ErrorInfo result = new ErrorInfo();
        try {
            // xx.jpg -> 2017/10/22/0010101.jpg
            fileName = getFilePath(fileName.substring(fileName.lastIndexOf(".")));

            Configuration cfg = new Configuration(Zone.zone2());
            Response response = new UploadManager(cfg).put(data, fileName, getUpToken());
            if (response.isOK()) {
                result.setStatus(true);
                result.setMsg("文件上传成功！");
                result.setData("/" + fileName);
                return result;
            }
        } catch (QiniuException e) {
            result.setMsg(e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 删除图片
     *
     * @param fileNames
     * @return
     */
    public int deletePhoto(String[] fileNames) {
        int result = 0;
        Configuration cfg = new Configuration(Zone.zone2());
        BucketManager bucketManager = new BucketManager(getAuth(), cfg);
        for (String filename : fileNames) {
            try {
                bucketManager.delete(bucketname, filename);
                result++;
            } catch (QiniuException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return result;
    }

    /**
     * 根据时间生成保存文件的路径名
     *
     * @param suffix 后缀名
     * @return 保存文件的路径名
     */
    private static String getFilePath(String suffix) {
        Calendar instance = Calendar.getInstance();
        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH) + 1;
        int day = instance.get(Calendar.DATE);
        String filePath = "file/" + year + "/" + month + "/" + day;
        String fileName = new SimpleDateFormat("HHmmssSSS").format(new Date()) + (int) (Math.random() * 9000 + 1000);
        return filePath + "/" + fileName + suffix;
    }

}

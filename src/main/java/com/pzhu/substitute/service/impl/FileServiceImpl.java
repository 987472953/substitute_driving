package com.pzhu.substitute.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.pzhu.substitute.common.ConstantPropertiesUtil;
import com.pzhu.substitute.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author dengyiqing
 * @description Oss文件服务
 * @date 2022/4/9
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {
    private static final String[] TYPE_STR = {".png", ".jpg", ".bmp", ".gif", ".jpeg"};

    @Override
    public String upload(MultipartFile file) {
        OSS ossClient = null;
        String url = null;
        boolean flag = true;
        try {
            //创建OSSClient实例。
            ossClient = new OSSClientBuilder().build(ConstantPropertiesUtil.END_POINT
                    , ConstantPropertiesUtil.ACCESS_KEY_ID
                    , ConstantPropertiesUtil.ACCESS_KEY_SECRET);

            //判断文件格式
            for (String type : TYPE_STR) {
                if (StringUtils.endsWithIgnoreCase(file.getOriginalFilename(), type)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                return "格式不正确！";
            }

            //判断文件内容
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (read != null) {
                log.debug("图片高度={}", read.getHeight());
                log.debug("图片宽度={}", read.getWidth());
            } else {
                return "图片内容错误";
            }

            //获得文件保存路径
            String filename = file.getOriginalFilename();
            String ext = filename.substring(filename.lastIndexOf('.'));
            String newName = UUID.randomUUID() + ext;
            String datePath = new DateTime().toString("yyyy/MM/dd");
            String newPath = ConstantPropertiesUtil.FILE_HOST + "/" + datePath + "/" + newName;


            // 上传文件流。
            InputStream inputStream = file.getInputStream();
            ossClient.putObject(ConstantPropertiesUtil.BUCKET_NAME, newPath, inputStream);

            url = "https://" + ConstantPropertiesUtil.BUCKET_NAME + "." + ConstantPropertiesUtil.END_POINT + "/" + newPath;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭OSSClient。
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return url;
    }


}

package com.pzhu.substitute.controller;

import com.pzhu.substitute.common.ConstantPropertiesUtil;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author dengyiqing
 * @description oss文件管理
 * @date 2022/4/9
 */
@RestController
@Api(tags = "Oss管理")
@RequestMapping("oss")
@Slf4j
public class OssController {

    private final FileService fileService;

    @Autowired
    public OssController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("file/upload")
    @ApiOperation("文件上传")
    public Result upload(
            @ApiParam(name = "file", value = "文件", required = true)
            @RequestParam("file") MultipartFile file,

            @ApiParam(name = "host", value = "文件上传路径")
            @RequestParam(value = "host", required = false) String host) {

        if (!StringUtils.isEmpty(host)) {
            ConstantPropertiesUtil.FILE_HOST = host;
        } else {
            ConstantPropertiesUtil.FILE_HOST = "avatar";
        }
        String url = fileService.upload(file);
        return Result.ok().data("url", url);
    }

//    public String getUrl(String key) {
//        initClient();
//        // 设置URL过期时间为1小时。
//        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
//        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
//        URL url = client.generatePresignedUrl(bucketName, key, expiration);
//        return url.toString();
//    }
}

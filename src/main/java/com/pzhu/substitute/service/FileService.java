package com.pzhu.substitute.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author dengyiqing
 * @description 文件上传服务
 * @date 2022/4/9
 */
public interface FileService {
    String upload(MultipartFile file);
}

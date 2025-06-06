package com.example.ssmnew.service.impl;

import com.example.ssmnew.service.ImageService;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {
    @Override
    public String getImageUrl(String imageId) {
        // 这里可以根据实际需求实现图片URL的获取逻辑
        // 例如从数据库、文件系统或第三方存储服务获取
        return "images\\" + imageId + ".jpg";
    }
} 
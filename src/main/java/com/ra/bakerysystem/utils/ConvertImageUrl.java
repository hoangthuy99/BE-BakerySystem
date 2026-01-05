package com.ra.bakerysystem.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConvertImageUrl {
    @Value("${server.port:8098}")
    private String serverPort;

    /**
     * Chuyển tên file ảnh thành URL đầy đủ
     * @param fileName tên file ảnh (ví dụ "image.png")
     * @return URL đầy đủ để client view ảnh
     */
    public String buildImageUrl(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        return "http://localhost:" + serverPort + "/uploads/" + fileName;
    }
}

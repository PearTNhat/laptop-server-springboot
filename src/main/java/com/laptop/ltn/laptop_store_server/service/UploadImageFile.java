package com.laptop.ltn.laptop_store_server.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface UploadImageFile {
    public Map uploadImageFile(MultipartFile file) throws IOException;
}

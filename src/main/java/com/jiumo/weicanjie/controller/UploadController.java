package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * type → folder 对照表
     * 可自由扩展
     */
    private static final Map<String, String> FOLDER_MAP = new HashMap<>();
    static {
        FOLDER_MAP.put("avatar", "avatar/");
        FOLDER_MAP.put("review", "review/");
        FOLDER_MAP.put("restaurant", "restaurant/");
        FOLDER_MAP.put("dish", "dish/");
        FOLDER_MAP.put("category", "category/");
        FOLDER_MAP.put("admin", "admin/");
        FOLDER_MAP.put("banner", "banner/");
    }

    /**
     * 上传图片
     * @param type 前端传入的类型，用于决定目录
     */
    @PostMapping("/image")
    public Result<?> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "other") String type
    ) {
        try {
            // 1. 判断目录
            String folder = FOLDER_MAP.getOrDefault(type, "other/");

            // 2. 生成唯一文件名
            String fileName = folder + System.currentTimeMillis() + "-" + file.getOriginalFilename();

            // 3. 上传到 MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 4. 拼接可访问 URL
            String url = endpoint + "/" + bucketName + "/" + fileName;

            return Result.ok(url);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}

package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import org.springframework.beans.factory.annotation.Value;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.endpoint}")     //  http://10.10.138.140:9000
    private String endpoint;

    /**
     * 上传图片（头像 or 评价图片）
     * @param file 前端上传文件
     * @param type avatar | review
     */
    @PostMapping("/image")
    public Result<?> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "review") String type
    ) {
        try {
            // 1. 根据 type 决定上传目录
            String folder = type.equals("avatar") ? "avatar/" : "review/";

            // 2. 生成唯一文件名
            String fileName = folder + System.currentTimeMillis() + "-" + file.getOriginalFilename();

            // 3. 上传文件到 MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 4. 拼接可访问 URL
            // endpoint 示例：http://10.10.138.140:9000
            String url = endpoint + "/" + bucketName + "/" + fileName;

            return Result.ok(url);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}

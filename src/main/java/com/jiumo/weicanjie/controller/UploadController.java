package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * 上传图片：支持餐厅分类目录与用户头像目录
     */
    @PostMapping("/image")
    public Result<?> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam String type,                  // avatar / review / logo / hall / store / food
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) Long userId
    ) {
        try {
            String folder = "";

            switch (type) {

                case "avatar":
                    folder = "avatar/" + (userId == null ? "unknown" : userId) + "/";
                    break;

                case "review":
                    folder = "restaurant/" + restaurantId + "/review/";
                    break;

                case "logo":
                    if (restaurantId == null) {
                        return Result.error("restaurantId 不能为空");
                    }
                    folder = "restaurant/" + restaurantId + "/logo/";
                    break;

                case "store":
                    folder = "restaurant/" + restaurantId + "/store/";
                    break;

                case "hall":
                    folder = "restaurant/" + restaurantId + "/hall/";
                    break;

                case "food":
                    folder = "restaurant/" + restaurantId + "/food/";
                    break;

                default:
                    folder = "other/";
            }

            // 构建唯一文件名
            String fileName = folder + System.currentTimeMillis() + "-" + file.getOriginalFilename();

            // 上传 Minio
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 对公网可访问
            String url = endpoint + "/" + bucketName + "/" + fileName;

            return Result.ok(url);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}

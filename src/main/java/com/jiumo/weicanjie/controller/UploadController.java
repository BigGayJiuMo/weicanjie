package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 * 该控制器提供文件上传功能，支持上传图片到不同的目录，具体包括餐厅分类、用户头像等。
 */
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
     * 上传图片
     *
     * 该接口支持上传多种类型的图片（如餐厅头像、评论图片等），根据上传类型决定文件存储位置。
     *
     * @param file 图片文件，使用MultipartFile传输
     * @param type 图片类型，如 avatar（头像）、review（评论）、logo（餐厅logo）等
     * @param restaurantId 餐厅ID，仅在特定类型（如 logo、store）时需要提供
     * @param userId 用户ID，仅在头像上传时需要提供
     * @return 返回上传成功后的图片访问URL，或错误信息
     */
    @PostMapping("/image")
    public Result<?> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam String type,                  // avatar / review / logo / hall / store / food
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) Long userId
    ) {
        try {
            // 确定图片存储目录
            String folder = "";

            // 根据类型决定存储路径
            switch (type) {

                case "avatar":
                    // 用户头像存储路径，若未提供userId，使用"unknown"作为默认值
                    folder = "avatar/" + (userId == null ? "unknown" : userId) + "/";
                    break;

                case "review":
                    // 评论图片存储路径，如果未提供餐厅ID，则使用临时目录
                    if (restaurantId == null) {
                        folder = "review/temp/";
                    } else {
                        folder = "restaurant/" + restaurantId + "/review/";
                    }
                    break;

                case "logo":
                    // 餐厅logo存储路径，logo类型必须提供restaurantId
                    if (restaurantId == null) {
                        return Result.error("restaurantId 不能为空");
                    }
                    folder = "restaurant/" + restaurantId + "/logo/";
                    break;

                case "store":
                    // 餐厅店铺图片存储路径
                    folder = "restaurant/" + restaurantId + "/store/";
                    break;

                case "hall":
                    // 餐厅大厅图片存储路径
                    folder = "restaurant/" + restaurantId + "/hall/";
                    break;

                case "dish":
                    // 菜品图片存储路径
                    folder = "restaurant/" + restaurantId + "/dish/";
                    break;

                default:
                    // 默认存储路径
                    folder = "other/";
            }

            // 构建唯一的文件名，使用当前时间戳和原文件名
            String fileName = folder + System.currentTimeMillis() + "-" + file.getOriginalFilename();

            // 将文件上传到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 生成可以公网访问的文件URL
            String url = endpoint + "/" + bucketName + "/" + fileName;

            return Result.ok(url);  // 返回上传成功后的URL

        } catch (Exception e) {
            // 捕获异常并返回失败信息
            e.printStackTrace();
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}

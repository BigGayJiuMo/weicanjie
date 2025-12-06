package com.jiumo.weicanjie.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {

        // 创建时间
        this.strictInsertFill(
                metaObject,
                "createdTime",
                LocalDateTime.class,
                LocalDateTime.now()
        );

        // 更新时间（新增时也需要填充）
        this.strictInsertFill(
                metaObject,
                "updatedTime",
                LocalDateTime.class,
                LocalDateTime.now()
        );
    }

    @Override
    public void updateFill(MetaObject metaObject) {

        //  每次更新自动刷新 updatedTime
        this.strictUpdateFill(
                metaObject,
                "updatedTime",
                LocalDateTime.class,
                LocalDateTime.now()
        );
    }
}

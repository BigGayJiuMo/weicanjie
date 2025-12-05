package com.jiumo.weicanjie.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(
                metaObject,
                "createdTime",
                LocalDateTime.class,
                LocalDateTime.now()
        );
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 如果你的图片不会更新，这里可留空
    }
}

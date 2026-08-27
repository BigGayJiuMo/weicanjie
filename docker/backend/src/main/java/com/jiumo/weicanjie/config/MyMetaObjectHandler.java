package com.jiumo.weicanjie.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 自定义元数据填充处理器，用于自动填充数据库中的字段（如创建时间、更新时间）。
 * 该处理器在插入和更新操作时自动填充指定字段的值。
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 在插入操作时自动填充字段。
     *
     * @param metaObject MyBatis 提供的元数据对象，包含当前操作的对象信息
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        // 填充创建时间（插入时自动设置为当前时间）
        this.strictInsertFill(
                metaObject,
                "createdTime",  // 填充的字段名
                LocalDateTime.class,  // 字段类型
                LocalDateTime.now()  // 字段值：当前时间
        );

        // 填充更新时间（插入时也需要设置为当前时间）
        this.strictInsertFill(
                metaObject,
                "updatedTime",  // 填充的字段名
                LocalDateTime.class,  // 字段类型
                LocalDateTime.now()  // 字段值：当前时间
        );
    }

    /**
     * 在更新操作时自动填充字段。
     *
     * @param metaObject MyBatis 提供的元数据对象，包含当前操作的对象信息
     */
    @Override
    public void updateFill(MetaObject metaObject) {

        // 每次更新时自动刷新更新时间
        this.strictUpdateFill(
                metaObject,
                "updatedTime",  // 填充的字段名
                LocalDateTime.class,  // 字段类型
                LocalDateTime.now()  // 字段值：当前时间
        );
    }
}

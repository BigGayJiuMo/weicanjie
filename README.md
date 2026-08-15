# 微餐捷餐厅管理系统 - 后端服务

微餐捷（weicanjie）餐厅点餐管理系统的后端服务，为微信小程序提供完整的 REST API。包含用户端、商家端、管理后台、后厨端四类角色接口。

## 技术栈

| 分类 | 技术 |
|---|---|
| 框架 | Spring Boot 2.7.5（Java 8） |
| 数据库 | MySQL 8.0 + MyBatis-Plus 3.5.3 |
| 缓存 | Redis（Lettuce） |
| 对象存储 | MinIO（图片上传） |
| 认证 | JWT（jjwt 0.11.5）+ Spring Security Crypto |
| 其他 | FastJSON、Lombok |

## 功能模块

**用户端**
- 用户注册 / 登录（微信授权 + JWT 认证）
- 餐厅浏览、分页查询、搜索、收藏、浏览历史
- 菜品分类与菜单查看、购物车管理
- 下单、订单查询、退款
- 评价与评价举报

**商家端**
- 餐厅信息、营业时间管理
- 菜品 / 分类管理
- 订单处理

**管理后台（Admin）**
- 用户、餐厅、菜品、订单、评价全量管理
- 统计报表

**后厨端**
- 后厨订单视图与处理

## 目录结构

```
weicanjie/
├── src/main/java/com/jiumo/weicanjie/
│   ├── common/          # 通用返回、异常处理
│   ├── config/          # 配置类与拦截器
│   ├── controller/      # 接口层（含 admin/ 管理端）
│   ├── dto/             # 数据传输对象
│   ├── entity/          # 实体类
│   ├── mapper/          # MyBatis-Plus Mapper
│   ├── service/         # 业务逻辑层
│   ├── task/            # 定时任务
│   └── util/            # 工具类
└── src/main/resources/
    ├── application.yml  # 应用配置
    └── mapper/          # MyBatis XML
```

## 快速开始

### 环境要求
- JDK 8+
- Maven 3.6+
- MySQL 8.0（数据库名 `weicanjie_db`）
- Redis 6+
- MinIO（bucket：`weicanjie`）

### 启动步骤

1. 创建数据库并导入表结构：
   ```sql
   CREATE DATABASE weicanjie_db DEFAULT CHARACTER SET utf8mb4;
   ```

2. 修改 `src/main/resources/application.yml` 中的配置：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/weicanjie_db?...
       username: root
       password: 你的数据库密码
   minio:
     endpoint: http://localhost:9000
     accessKey: minioadmin
     secretKey: minioadmin
   ```

3. 启动服务：
   ```bash
   mvn spring-boot:run
   ```

4. 服务默认端口 `8080`，接口前缀 `/api`：
   ```
   http://localhost:8080/api/...
   ```

## 主要接口

| 模块 | 路径前缀 | 说明 |
|---|---|---|
| 用户 | `/user` | 注册、登录、个人信息 |
| 餐厅 | `/restaurant` | 餐厅列表、详情、搜索 |
| 菜品 | `/dish` | 菜品查询 |
| 购物车 | `/cart` | 购物车增删改查 |
| 订单 | `/order` | 下单、订单查询、退款 |
| 评价 | `/review` | 评价与举报 |
| 管理端 | `/admin/**` | 后台管理接口 |

## 相关项目

- 前端小程序：[weicanjie-wechat](https://github.com/BigGayJiuMo/weicanjie-wechat)

## License

内部项目，未经授权请勿用于商业用途。

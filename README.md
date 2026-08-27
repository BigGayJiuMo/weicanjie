# 微餐捷餐厅管理系统 · 后端服务

微餐捷（weicanjie）餐厅点餐管理系统的后端服务，为微信小程序与管理端提供完整 REST API。包含用户端、商家端、管理后台、后厨端四类角色接口。支持 **Docker Compose 一键部署**。

## 技术栈

| 分类 | 技术 |
|---|---|
| 框架 | Spring Boot 2.7.5（Java 17） |
| 数据库 | MySQL 8.0 + MyBatis-Plus 3.5.3 |
| 缓存 | Redis（Lettuce） |
| 对象存储 | MinIO（图片上传） |
| 认证 | JWT（jjwt 0.11.5）+ Spring Security Crypto |
| AOP 幂等 | spring-boot-starter-aop + Redis SETNX 分布式锁 |
| 接口文档 | Knife4j 4.4.0（springdoc-openapi） |
| 部署 | Docker Compose（多阶段构建 + nginx 反代） |
| 其他 | FastJSON、Lombok、javax.validation |

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
- 统计报表（KPI、销量、趋势）

**后厨端**
- 后厨订单视图与处理

## 工程化与亮点

1. **工程化四件套**：统一异常处理（`@RestControllerAdvice`）、参数校验（`@Valid`）、接口文档（Knife4j）、Redis 缓存
2. **订单接口幂等防重**：`@Idempotent` 注解 + AOP 切面，Redis `SETNX` 分布式锁挡并发 + 结果缓存挡时间重复，绝不重复下单（见 `IdempotentAspect` / `OrderIdempotencyTest`）
3. **缓存实战**：LocalDateTime 序列化、`@Cacheable` 内部调用等三坑修复
4. **Docker 一键部署**：`docker compose up -d --build` 拉起 MySQL + Redis + MinIO + 后端 + 管理端

## 目录结构

```
weicanjie/
├── docker/                # Docker Compose 一键部署
│   ├── compose.yaml       # 编排 5 个服务
│   ├── backend/           # 后端多阶段构建镜像
│   ├── admin/             # 管理端构建 + nginx
│   └── mysql/init/        # 首启自动建库灌数据
└── src/main/java/com/jiumo/weicanjie/
    ├── annotation/        # 自定义注解（@Idempotent）
    ├── aspect/            # AOP 切面（幂等）
    ├── common/            # 通用返回、异常处理
    ├── config/            # 配置类与拦截器
    ├── controller/        # 接口层（含 admin/ 管理端）
    ├── dto/               # 数据传输对象
    ├── entity/            # 实体类
    ├── mapper/            # MyBatis-Plus Mapper
    ├── service/           # 业务逻辑层
    ├── task/              # 定时任务
    └── util/              # 工具类
src/main/resources/
    ├── application.yml       # 本地直连配置
    ├── application-docker.yml# Docker 部署配置（环境变量注入）
    └── mapper/               # MyBatis XML
```

## 快速开始

### 方式一：本地开发

**环境要求**
- JDK 17、Maven 3.6+
- MySQL 8.0（库名 `weicanjie_db`）、Redis 6+、MinIO（bucket `weicanjie`）

**启动步骤**
1. 创建数据库：
   ```sql
   CREATE DATABASE weicanjie_db DEFAULT CHARACTER SET utf8mb4;
   ```
2. 修改 `application.yml` 中的数据库密码与 MinIO 配置
3. 启动：`mvn spring-boot:run`
4. 服务端口 `8080`，接口前缀 `/api`，API 文档：`http://localhost:8080/api/doc.html`

### 方式二：Docker 一键部署（推荐）

```bash
cd docker
docker compose up -d --build
```

| 服务 | 地址 | 说明 |
|---|---|---|
| 管理端 | http://localhost:8081 | nginx 托管 + `/api` 反代 |
| 后端 API | http://localhost:8080/api | - |
| API 文档 | http://localhost:8080/api/doc.html | Knife4j |
| MinIO 控制台 | http://localhost:9001 | minioadmin / minioadmin |

> 端口冲突处理与详细说明见 `docker/README.md`。

## 主要接口

| 模块 | 路径前缀 | 说明 |
|---|---|---|
| 用户 | `/user` | 注册、登录、个人信息 |
| 餐厅 | `/restaurant` | 餐厅列表、详情、搜索 |
| 菜品 | `/dish` | 菜品查询 |
| 购物车 | `/cart` | 购物车增删改查 |
| 订单 | `/order` | 下单（幂等）、订单查询、退款 |
| 评价 | `/review` | 评价与举报 |
| 管理端 | `/admin/**` | 后台管理接口 |

## 相关项目

- 前端小程序：[weicanjie-wechat](https://github.com/BigGayJiuMo/weicanjie-wechat)
- 管理端：[weicanjie-admin](https://github.com/BigGayJiuMo/weicanjie-admin)

## License

内部项目，未经授权请勿用于商业用途。

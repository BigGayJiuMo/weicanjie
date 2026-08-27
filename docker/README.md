# 微餐捷 Weicanjie —— Docker 一键部署

MySQL + Redis + MinIO + Spring Boot 后端 + 管理端(nginx),一条命令起全套。

## 快速开始

```bash
# 1. 构建并启动(首次会拉镜像+构建,较慢)
docker compose up -d --build

# 2. 查看状态
docker compose ps

# 3. 看日志(排查用)
docker compose logs -f backend

# 4. 停止(保留数据)
docker compose down

# 5. 彻底重置(删除数据卷,谨慎!)
docker compose down -v
```

## 访问地址

| 服务 | 地址 | 账号 |
|---|---|---|
| 管理端 | http://localhost:8081 | 管理账号 |
| 后端 API | http://localhost:8080/api | - |
| Knife4j API 文档 | http://localhost:8080/api/doc.html | - |
| MinIO 控制台 | http://localhost:9001 | minioadmin / minioadmin |

## 端口冲突的处理

本机已运行 MySQL(3306)/Redis(6379)/MinIO(9000)会与容器端口冲突,二选一:
- **改宿主机映射端口**(推荐,不干扰本机服务):
  - MySQL: `"3307:3306"`
  - Redis: `"6380:6379"`
  - MinIO: `"9002:9000"`, 控制台 `"9003:9001"`
  - 后端: `"8082:8080"`
  - 管理端: `"8083:80"`(同时把 admin 构建参数 `VITE_API_BASE_URL` 改为对应端口)
- 或先停掉本机对应服务再启动。

## 目录结构

```
docker/
├── compose.yaml            # 编排主文件
├── README.md
├── mysql/init/01_schema.sql  # 首次启动自动建库灌种子数据
├── backend/                # 后端构建上下文(pom + src 由脚本同步)
│   └── Dockerfile          # maven 多阶段构建
└── admin/                  # 管理端构建上下文(从 admin 仓库同步)
    ├── Dockerfile          # node 构建 + nginx 托管
    └── nginx/default.conf  # 单页回退 + /api 反向代理到后端
```

## 说明

- 后端启动用 `--spring.profiles.active=docker`,配置来自**环境变量**(见 `application-docker.yml`),不硬编码 host。
- 管理端构建时把 `VITE_API_BASE_URL` 打进产物,浏览器通过 nginx 同源 `/api` 反代到后端,无跨域。
- MySQL 初始化 SQL 来自本机库导出(结构+种子数据),首次启动自动执行,之后不再执行。
- 小程序不能直接容器化(需微信开发者工具),把 `wechat/utils/config.js` 的 `BASE_URL` 改成电脑局域网 IP 即可连上容器里的后端。
# ============================================================
# 管理端镜像:Vite 构建 + nginx 托管
# 构建时通过 ARG 注入后端 API 地址,打包进静态资源
#   docker build --build-arg VITE_API_BASE_URL=http://localhost:8080/api
# compose 里 build.args.VITE_API_BASE_URL 默认指向本机 8080
# ============================================================
FROM node:20-alpine AS build
WORKDIR /app
# 先装依赖再拷源码(层缓存)
COPY package.json ./
RUN npm install --registry=https://registry.npmmirror.com
COPY . .
ARG VITE_API_BASE_URL=http://localhost:8080/api
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
RUN npm run build

# 托管镜像
FROM nginx:1.27-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx/default.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
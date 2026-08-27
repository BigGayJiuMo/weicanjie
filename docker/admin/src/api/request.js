import axios from "axios";
import { ElMessage } from "element-plus";
import { useUserStore } from "../store/user"; // 引入 Pinia store 用于管理用户信息

// baseURL 优先读 Vite 环境变量(.env.development / .env.production),部署时改配置即可
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api",
  timeout: 10000, // 请求超时 10 秒
  headers: {
    "Content-Type": "application/json",
  },
});

// 生成幂等 key(防重复提交):{yyyyMMddHHmmss}-{6位随机}
function genIdempotentKey() {
  const d = new Date();
  const pad = (n) => String(n).padStart(2, "0");
  const ts =
    `${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())}` +
    `${pad(d.getHours())}${pad(d.getMinutes())}${pad(d.getSeconds())}`;
  const rand = String(Math.floor(Math.random() * 1000000)).padStart(6, "0");
  return `${ts}-${rand}`;
}

// 请求拦截器:自动携带 token,并为写请求(POST/PUT/DELETE)自动生成幂等 key
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore();
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`;
    }
    // 写操作自动带幂等 key(后端 @Idempotent 端点消费;其它端点忽略该头)
    const method = (config.method || "get").toLowerCase();
    if (["post", "put", "delete"].includes(method) && !config.headers["X-Idempotent-Key"]) {
      config.headers["X-Idempotent-Key"] = genIdempotentKey();
    }
    return config;
  },
  (error) => {
    console.error("请求错误：", error);
    return Promise.reject(error);
  }
);

// 401:清除登录态并跳转登录页
function handleUnauthorized() {
  const userStore = useUserStore();
  userStore.token = "";
  localStorage.removeItem("token");
  localStorage.removeItem("userInfo");
  ElMessage.error("登录已过期，请重新登录");
  setTimeout(() => {
    window.location.href = "/login";
  }, 800);
}

// 响应拦截器:统一处理业务错误码与 HTTP 错误
request.interceptors.response.use(
  (res) => {
    // 后端统一返回 Result{ code, message, data, timestamp }
    const body = res.data;

    // 业务成功(与页面现有 res.data.xxx 用法保持兼容)
    if (body && body.code === 200) {
      return body;
    }

    // 业务 401:token 失效
    if (body && body.code === 401) {
      handleUnauthorized();
      return Promise.reject(new Error(body.message || "登录已过期"));
    }

    // 其他业务失败:统一提示后端返回的 message
    ElMessage.error(body?.message || "操作失败");
    return Promise.reject(new Error(body?.message || "操作失败"));
  },
  (err) => {
    const status = err.response?.status;
    if (status === 401) {
      handleUnauthorized();
    } else if (status === 403) {
      ElMessage.error("没有权限执行此操作");
    } else if (status === 404) {
      ElMessage.error("请求的接口不存在");
    } else if (status >= 500) {
      ElMessage.error("服务器开小差了，请稍后重试");
    } else if (err.code === "ECONNABORTED") {
      ElMessage.error("请求超时，请稍后重试");
    } else {
      ElMessage.error("网络错误，请检查后端服务是否启动");
    }
    return Promise.reject(err);
  }
);

export default request;
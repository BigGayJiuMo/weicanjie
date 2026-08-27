# 微餐捷(weicanjie)项目 · 会话交接文档

> **用途**:本会话上下文压缩/结束后,新开对话时先读取本文件即可无缝恢复上下文。
> **新对话第一条消息建议**:"请先读取 `D:\Idea\weicanjie\CONVERSATION_CONTEXT.md`,然后我们继续微餐捷项目。"
> 生成时间:2026-08-26(会话末)
> 最近更新:2026-08-27 新增「订单接口幂等防重」commit `6b728a3`

---

## 一、项目概况

- **项目名**:微餐捷餐厅管理系统(weicanjie)
- **技术栈**:Spring Boot 2.7.5 + Java 17 + MyBatis + MySQL + Redis + 微信小程序 + Vue3(管理端)
- **四端**:
  | 端 | 路径 | 仓库 |
  |---|---|---|
  | 后端 | `D:\Idea\weicanjie` | BigGayJiuMo/weicanjie(master) |
  | 管理端 | `D:\weicanjie-admin\admin` | BigGayJiuMo/weicanjie-admin(main) |
  | 用户端(小程序) | `C:\Users\jinghanwu\WeChatProjects\weicanjie` | BigGayJiuMo/weicanjie-wechat(master) |
- **后端启动**:IDEA 运行 `RestaurantApplication`,端口 **8080**,context-path **/api**(如 `http://localhost:8080/api/restaurant/1`)
- **接口文档**:`http://localhost:8080/api/doc.html`(Knife4j)
- **用户画像**:应届生(吴锦釬),目标 **Java 后端 + AI 应用复合岗**,按《Java与AI能力补齐路线图v2》学习;偏好中文回复、动手实做、面试话术讲解。

---

## 二、已完成工作(全部已推送 GitHub)

### 1. 基线搭建(早期会话)
- 装 **JDK 17**(`C:\Users\jinghanwu\.jdks\jdk-17.0.20.1+1`);原因:lombok 1.18.24 不支持 JDK 26,`NoSuchFieldException TypeTag::UNKNOWN`
- pom `java.version` 8→17;导入 `spring-boot-starter-validation`、`knife4j-openapi3-spring-boot-starter 4.4.0`
- 导入数据库 `weicanjie_db`(桌面 SQL,17 表 + 测试数据)
- 启动脚本 `D:\qidongliucheng`;Redis-x64 3.0.504 在 `D:\study\毕业相关\毕设项目\Redis-x64-3.0.504`,端口 6379

### 2. 后端工程化四件套 — commit `a486ffc`
1. **统一异常处理**:`exception/BusinessException.java`(RuntimeException + code)、`exception/GlobalExceptionHandler.java`(@RestControllerAdvice,处理 BusinessException / MethodArgumentNotValid / Bind / ConstraintViolation / Exception)
2. **参数校验**:DTO 注解(@NotBlank/@NotNull/@DecimalMin/@Min/@Size/@Pattern);**OrderController 内部类 DTO 迁移到 dto 包**(OrderRequest/CartRequest/RefundApplyRequest/BatchOrderRequest),修复 service→controller 反向依赖;Controller 加 @Valid;**嵌套校验每层 @Valid**
3. **Knife4j 文档**:`config/OpenApiConfig.java` + `application.yml knife4j.enable: true`;Controller 加 @Tag/@Operation
4. **Redis 缓存**:`config/CacheConfig.java` + `RestaurantServiceImpl` 加 @Cacheable/@CacheEvict(cacheNames: restaurant/restaurantDetail/restaurantPage,TTL 5min)
5. 附带修复:OrderMapper.searchOrders 删除 @Select 注解(被 XML 覆盖 bug)、删死代码(Main.java、OrderDetailResponse.java)、AdminController 去掉打印密码

### 3. 前端工程化 — 管理端 `1759527`、用户端 `316f053`
- **管理端** `src/api/request.js`:统一业务错误处理(code!=200 → ElMessage)、401 清 token 跳登录、baseURL 环境变量化(`.env.development` VITE_API_BASE_URL)
- **用户端**:`app.js` 登录接口 `/user/login` → **`/user/loginByWeChat`**(原为 404 隐患);新增 `utils/config.js`(baseUrl 集中配置,真机预览改局域网 IP);`utils/request.js` 401 清 token+userInfo、超时 15s

### 4. 小程序编译 500 修复
- 根因:wxml 引用 `/images/empty-restaurant.png`、`/images/default-dish.png` 但文件缺失 → 编译 500 页面不显示
- 修复:在 `images\` 下生成两个占位图(300x300 PNG)

### 5. 🔥 缓存三连环坑 — commit `e734bb4`(今日核心价值)
| 坑 | 现象 | 根因 | 修复 |
|---|---|---|---|
| ① 缓存静默失效 | 压测 QPS 没提升 | `LocalDateTime` 序列化异常,Spring Cache put 失败**只记日志不抛** | 注册 JavaTimeModule |
| ② ClassCastException | `LinkedHashMap cannot be cast to Result` | 自定义 ObjectMapper 构造 `GenericJackson2JsonRedisSerializer` **不会自动启用类型标记**(无 @class) | `activateDefaultTyping(...)` |
| ③ UnrecognizedPropertyException | `Unrecognized field "error"` | `Result.isSuccess()/isError()` 是 `isXxx()` **被 Jackson 当 getter** → JSON 多出 error/success | 加 `@JsonIgnore` |

- 排查方法:`redis-cli keys restaurant*` + `get restaurant::all` 看 JSON 内容 + 后端 debug 日志
- 修复后需**清空旧格式缓存**(删除 restaurant* key)

### 6. JMeter 压测数据(写入简历)
| 场景 | QPS | 平均响应 |
|---|---|---|
| 无缓存(NoOp) | 653 | 64ms |
| 带缓存(修复后) | 1320 | 25ms |

- 工具:JMeter 5.6.3(`D:\tools\jmeter\apache-jmeter-5.6.3`),`java -jar ApacheJMeter.jar` 方式运行;计划文件 `D:\tools\jmeter\plan-restaurant50.jmx` 等
- 压测后半段 `Address already in use: connect` = **客户端端口耗尽**(Windows TIME_WAIT),非后端问题;后端 1300+ QPS 仍 0 错误

### 7. 桌面笔记两篇(学习资料)
- `C:\Users\jinghanwu\Desktop\工程化四件套笔记.md`(四件套原理+代码+面试问答)
- `C:\Users\jinghanwu\Desktop\weicanjie-实战笔记-20260826.md`(缓存三坑排查故事+压测+面试话术)

### 8. 🔥 订单接口幂等防重 — commit `6b728a3`(今日新增,已推送 GitHub)
- **方案**:`@Idempotent` 注解 + `IdempotentAspect` 切面(AOP),Redis **SET NX 分布式锁 + 结果缓存双保险**
- **文件**:
  - `annotation/Idempotent.java`(注解:prefix + expireSeconds,默认 TTL 600s)
  - `aspect/IdempotentAspect.java`(切面:锁 key `idempotent:lock:`、结果 key `idempotent:result:` 分离)
  - `controller/OrderController.java`:`/create`、`/create/batch`、`/pay/{id}`、`/refund/apply` 加 `@Idempotent`
  - `common/Result.java` 新增 `idempotentConflict()`(code=409,前端按 code!=200 走重复提交提示)
- **三场景语义**(集成测试 `src/test/.../OrderIdempotencyTest` 真实 MySQL+Redis 全过):
  1. 同 key 重复两次 → 第二次返回**缓存结果(同一订单 id)**,不新建
  2. 不同 key → 各自创建独立订单(key 需按一次业务一个 key 用)
  3. 并发同 key → 一个执行业务,另一个 409(或返回缓存结果),绝无两笔订单
- **踩坑**:幂等结果序列化 Order.createdTime(LocalDateTime) 需注册 JavaTimeModule(缓存三坑①同款坑);
  锁 key 与结果 key 必须分离,否则删锁会误删结果缓存导致重复校验失效
- **面试话术**:SET NX 防并发 + 结果缓存防时间重复,双保险;业务失败删锁允许重试,成功才缓存

### 9. 🔥 幂等前端接入 — 小程序 `3e04963`、管理端 `672959b`(2026-08-27,已推送)
- **小程序**:`utils/config.js` 新增 `genIdempotentKey()`;下单(单/批量)、批量支付、单笔支付、申请退款的 `wx.request` 均加 `X-Idempotent-Key` 头
  - 涉及页:`submitOrder`(创建/批量支付/支付)、`restaurant-detail`(创建/支付)、`order-detail`(支付)、`refund`(退款)
- **管理端**:`src/api/request.js` 请求拦截器为 **POST/PUT/DELETE** 自动生成 `X-Idempotent-Key`(已有该头则不覆盖),覆盖"后台代用户申请退款"等场景
- 说明:小程序没用统一 request.js 二次封装,下单走的是各页面的 `wx.request`(带 `content-type` 头),需手工逐处加;管理端是 axios 拦截器,一次搞定

---

## 三、当前运行状态

- ✅ 后端 8080:运行中,**已重启加载幂等新代码(6b728a3)**,已用真实 HTTP 验证同 key 重复下单返回同一订单 id
- ✅ Redis 6379:运行中(后台管理端页面已验证缓存正常)
- ✅ 三个前端仓库与后端均已 git 同步
- ✅ 管理端 dev server 已重启,`.env.development` 生效
- ⚠️ 小程序需在微信开发者工具重新编译以加载幂等 key 改动

---

## 四、待办与下一步建议

1. **主线(面试八股)**:JVM 内存模型/GC、并发(JUC)、MySQL 索引与事务隔离级别 —— 按路线图整理笔记
2. **项目再升级(简历加分)**:
   - ✅ ~~订单接口幂等~~(2026-08-27 完成,commit `6b728a3`)
   - **Docker Compose** 一键部署(MySQL+Redis+后端+管理端),简历写"已可部署上线"
3. **幂等后续可做**:✅ 前端已接 key——小程序 `3e04963`、管理端 `672959b`(2026-08-27);
   已覆盖下单/支付/退款。剩余可考虑:管理端 admin 评论/审核/举报写接口后端加 @Idempotent(前端已自动带 key)
4. **简历更新**:把"工程化四件套 + 压测 QPS 653→1320 + 订单接口幂等(后端+前端已接通)"写入项目经历
5. **路线图剩余**:AOP 日志、MySQL 索引优化、RabbitMQ 异步、AI RAG/Agent(目标 AI 应用岗)

---

## 五、环境与操作要点(新对话必读)

### 路径/命令
- JDK17 java: `C:\Users\jinghanwu\.jdks\jdk-17.0.20.1+1\bin\java.exe`(mvn/java 不在 PATH,IDEA 里手动 Rebuild + 重启后端点生效)
- Redis cli: `D:\study\毕业相关\毕设项目\Redis-x64-3.0.504\redis-cli.exe`(命令:`keys "restaurant*"`、`get restaurant::all`、`del key`)
- JMeter: `D:\tools\jmeter\apache-jmeter-5.6.3\bin\ApacheJMeter.jar`,运行方式:
  `& $java -jar $jar -n -t 计划.jmx -l 结果.jtl -e -o 报告目录`
- Git 推送:需设 `$env:GIT_SSH_COMMAND = "ssh -o BatchMode=yes -o StrictHostKeyChecking=accept-new"`;SSH key ed25519 已配好(`BigGayJiuMo`)

### 沙箱限制(重要)
- pwsh 命令:workspace-write 对 `D:\Idea\weicanjie` 目录 **grantWrite 失败**(SetNamedSecurityInfoW Win32 5),操作工作区外/该目录时需 `danger-full-access` 一次性升级
- 写文件优先用 write/edit 工具;读写 `D:\tools`、桌面、`C:\Users\jinghanwu\WeChatProjects` 均需 danger-full-access
- 读文件用 read 工具,不用 cat;搜内容用 grep/glob

### 关键文件索引
- 缓存配置:`src/main/java/com/jiumo/weicanjie/config/CacheConfig.java`
- 全局异常:`src/main/java/com/jiumo/weicanjie/exception/GlobalExceptionHandler.java`
- 统一返回:`src/main/java/com/jiumo/weicanjie/common/Result.java`(@JsonIgnore 在 isSuccess/isError;幂等冲突 409 在 idempotentConflict)
- 缓存注解示例:`service/impl/RestaurantServiceImpl.java`(getRestaurantDetail/getAllRestaurants/getPage @Cacheable;deleteRestaurant @CacheEvict)
- **幂等(新)**:注解 `annotation/Idempotent.java`、切面 `aspect/IdempotentAspect.java`、测试 `src/test/java/com/jiumo/weicanjie/OrderIdempotencyTest.java`
- 命令行编译/测试(本机 mvn 不在 PATH):
  ```powershell
  $env:JAVA_HOME="C:\Users\jinghanwu\.jdks\jdk-17.0.20.1+1"
  $mvn="D:\Idea\IntelliJ IDEA 2025.1.1.1\plugins\maven\lib\maven3\bin\mvn.cmd"
  & $mvn -f D:\Idea\weicanjie\pom.xml test -Dtest=OrderIdempotencyTest   # 需 MySQL+Redis 运行
  ```

---

## 六、Git 提交记录速查

| 仓库 | 提交 | 说明 |
|---|---|---|
| weicanjie | a486ffc | 后端工程化四件套 |
| weicanjie | e734bb4 | 缓存三坑修复(activateDefaultTyping + @JsonIgnore) |
| weicanjie | 6b728a3 | 订单接口幂等防重(@Idempotent + Redis SETNX 分布式锁 + 结果缓存) |
| weicanjie-admin | 1759527 | 请求封装优化 |
| weicanjie-admin | 672959b | 请求封装为写接口自动生成 X-Idempotent-Key 幂等头 |
| weicanjie-wechat | 316f053 | 登录修复+请求优化 |
| weicanjie-wechat | 3e04963 | 下单/支付/退款接入幂等 key |

> 注:本文件 `CONVERSATION_CONTEXT.md` 是工作区交接文档,**如不打算入库请勿 `git add -A`**,或先加入 .gitignore。

---

## 七、后续会话起点建议

新对话开场(可直接复制):
```
请先读取 D:\Idea\weicanjie\CONVERSATION_CONTEXT.md 恢复上下文。
今天我们从"待办与下一步建议"继续:首选[项目升级:Docker Compose 一键部署]或[主线 JVM/并发/MySQL 笔记]。
另外:①GITHUB_TOKEN 已撤销重建 ②幂等后端(6b728a3)+前端(小程序 3e04963/管理端 672959b)已全部接通并推送,后端已重启生效,小程序需重新编译。
```
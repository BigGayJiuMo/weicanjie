<template>
  <div class="login-wrapper">
    <div class="header-bg"></div>
    <div class="login-card">
      <!-- 头像 -->
      <div class="avatar-box">
        <img class="avatar" src="../assets/admin-avatar.png" />
      </div>

      <!-- 标题 -->
      <h2 class="title">微餐捷 · 管理后台</h2>
      <p class="subtitle">请登录以管理餐厅、菜品与订单</p>

      <!-- 表单 -->
      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入管理员账号"
            :prefix-icon="User"
            class="input-round"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            type="password"
            v-model="form.password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            class="input-round"
          />
        </el-form-item>

        <el-button
          type="primary"
          class="login-btn"
          round
          @click="onSubmit"
        >
          登录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
  import { User, Lock } from "@element-plus/icons-vue";
  import { reactive, ref } from "vue";
  import { useUserStore } from "../store/user";
  import { adminLogin } from "../api/admin";
  import { useRouter } from "vue-router";
  import { ElMessage } from "element-plus";
  
  const router = useRouter();
  const userStore = useUserStore();
  
  const form = reactive({
    username: "",
    password: ""
  });
  const formRef = ref(null);
  
  const rules = {
    username: [{ required: true, message: "请输入账号" }],
    password: [{ required: true, message: "请输入密码" }]
  };
  
  const onSubmit = () => {
    formRef.value.validate(async (valid) => {
      if (!valid) return;

      const res = await adminLogin(form);

      if (res.code === 200) {
        // 登录成功
        userStore.setToken(res.data.token);
        userStore.setUserInfo(res.data);

        ElMessage.success("登录成功");
        router.push("/dashboard");
      } else {
        // 登录失败，根据后端 msg 提示
        ElMessage.error(res.message || "登录失败");
      }
    });
  };
</script>
  

<style scoped>
/* 整体布局 */
.login-wrapper {
  width: 100%;
  height: 100vh;
  background: #f5f5f5;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 顶部渐变背景 */
.header-bg {
  position: absolute;
  top: 0;
  width: 100%;
  height: 38%;
  background: linear-gradient(135deg, #ff8c3c, #ff6f3c);
  border-bottom-left-radius: 50% 20%;
  border-bottom-right-radius: 50% 20%;
}

/* 登录卡片 */
.login-card {
  width: 360px;
  padding: 35px 25px;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.15);
  z-index: 2;
  text-align: center;
  animation: fadeIn 0.6s ease-out;
}

/* 头像 */
.avatar-box {
  display: flex;
  justify-content: center;
  margin-top: -65px;
}
.avatar {
  width: 85px;
  height: 85px;
  border-radius: 50%;
  border: 3px solid #fff;
}

/* 标题 */
.title {
  font-size: 22px;
  margin: 10px 0 5px;
  color: #333;
}

.subtitle {
  font-size: 14px;
  color: #777;
  margin-bottom: 20px;
}

/* 输入框圆角 */
.input-round .el-input__wrapper {
  border-radius: 15px !important;
  padding: 4px 14px;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 40px;
  font-size: 15px;
  margin-top: 10px;
  background: #ff7b39;
  border-color: #ff7b39;
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

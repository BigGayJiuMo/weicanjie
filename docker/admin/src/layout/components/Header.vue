<template>
  <header class="topbar">
    <div class="left-title">后台管理系统</div>

    <div class="right-user">
      <span class="username">当前用户：{{ userStore.username }}</span>

      <el-button size="small" type="danger" @click="logout">
        退出
      </el-button>
    </div>
  </header>
</template>

<script setup>
import { useUserStore } from "@/store/user";
import { useRouter } from "vue-router";

const userStore = useUserStore();
const router = useRouter();

const logout = () => {
  userStore.setToken("");
  userStore.setUserInfo("", null, "");
  localStorage.removeItem("token");

  router.push("/login");
};
</script>

<style scoped>
.topbar {
  height: 60px;
  min-height: 60px;
  background: #fff;
  border-bottom: 1px solid #e5e5e5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  flex-shrink: 0;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.left-title {
  font-size: 18px;
}

.right-user {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  color: #666;
}
</style>
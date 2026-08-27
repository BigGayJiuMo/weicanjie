vue
<template>
  <aside class="sidebar">
    <div class="logo">微餐捷后台</div>

    <el-menu
      :default-active="activeMenu"
      class="el-menu-vertical"
      router
      background-color="#2d3a4b"
      text-color="#eee"
      active-text-color="#ffd04b"
    >
      <el-menu-item index="/dashboard" v-if="role !== 'kitchen'">
        <el-icon><HomeFilled /></el-icon>
        <span>首页总览</span>
      </el-menu-item>

      <el-menu-item index="/restaurant" v-if="role !== 'kitchen'">
        <el-icon><OfficeBuilding /></el-icon>
        <span>餐厅管理</span>
      </el-menu-item>

      <el-menu-item index="/restaurant-category" v-if="role === 'super'">
        <el-icon><Menu /></el-icon>
        <span>餐厅分类</span>
      </el-menu-item>

      <el-menu-item index="/dish-category" v-if="role !== 'kitchen'">
        <el-icon><Menu /></el-icon>
        <span>菜品分类</span>
      </el-menu-item>

      <el-menu-item index="/dish" v-if="role !== 'kitchen'">
        <el-icon><ForkSpoon /></el-icon>
        <span>菜品管理</span>
      </el-menu-item>

      <el-menu-item index="/order" v-if="role !== 'kitchen'">
        <el-icon><List /></el-icon>
        <span>订单管理</span>
      </el-menu-item>

      <el-menu-item index="/kitchen">
        <el-icon><Monitor /></el-icon>
        <span>后厨看板</span>
      </el-menu-item>

      <el-menu-item index="/review" v-if="role !== 'kitchen'">
        <el-icon><ChatDotRound /></el-icon>
        <span>评价管理</span>
      </el-menu-item>

      <el-menu-item index="/review-audit" v-if="role === 'super'">
        <el-icon><ChatDotRound /></el-icon>
        <span>评价审核</span>
      </el-menu-item>

      <el-menu-item index="/review-report" v-if="role === 'super'">
        <el-icon><ChatDotRound /></el-icon>
        <span>举报审核</span>
      </el-menu-item>

      <el-menu-item index="/report" v-if="role !== 'kitchen'">
        <el-icon><DataLine /></el-icon>
        <span>统计报表</span>
      </el-menu-item>

      <el-menu-item index="/account-management" v-if="role === 'super'">
        <el-icon><UserFilled /></el-icon>
        <span>商家账号管理</span>
      </el-menu-item>

      <el-menu-item index="/account-management" v-if="role !== 'super'">
        <el-icon><UserFilled /></el-icon>
        <span>账号设置</span>
      </el-menu-item>

    </el-menu>
  </aside>
</template>

<script setup>
  import {
    HomeFilled,
    OfficeBuilding,
    Menu,
    List,
    Monitor,
    DataLine,
    ForkSpoon,
    UserFilled
  } from "@element-plus/icons-vue";
  import { ChatDotRound } from "@element-plus/icons-vue";
  import { useRoute } from "vue-router";
  import { useUserStore } from "@/store/user";
  import { computed } from "vue";
  const route = useRoute();

  const activeMenu = computed(() => {
  return route.path;
  });

  const role = useUserStore().role;
  </script>
  
  <style scoped>
  .sidebar {
    width: 160px;
    background: #2d3a4b;
    color: #fff;
    display: flex;
    flex-direction: column;
    height: 100vh; /* 新增：固定高度 */
    overflow-y: auto; /* 允许侧边栏菜单滚动 */
    overflow-x: hidden; /* 禁止水平滚动 */
  }
  
  .logo {
    height: 60px;
    line-height: 60px;
    text-align: center;
    font-size: 18px;
    font-weight: bold;
    background: #1f2a38;
    color: #ffd04b;
    flex-shrink: 0; /* 防止logo被压缩 */
  }
  
  .el-menu-vertical {
    border: none;
    flex: 1;
    overflow-y: auto; /* 菜单可以滚动 */
    overflow-x: hidden;
  }
  
  .sidebar::-webkit-scrollbar {
    width: 4px;
  }
  
  .sidebar::-webkit-scrollbar-track {
    background: #1f2a38;
  }
  
  .sidebar::-webkit-scrollbar-thumb {
    background: #4a5a70;
    border-radius: 2px;
  }
  
  .sidebar::-webkit-scrollbar-thumb:hover {
    background: #5a6a80;
  }
  </style>
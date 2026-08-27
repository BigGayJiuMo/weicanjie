<template>
  <div>
    <div>
      <h2>后厨看板</h2>
    </div>
    <el-alert
      title="系统每 5 秒自动刷新最新订单"
      type="info"
      show-icon
      style="margin-bottom: 15px"
    />

    <!-- 订单列表 -->
    <el-row :gutter="20">
      <el-col
        v-for="order in orders"
        :key="order.id"
        :span="8"
      >
        <el-card shadow="hover" class="order-card">

          <div class="order-header">
            <strong>订单号：</strong> {{ order.orderNumber }}
          </div>

          <div class="order-status">
            <el-tag v-if="order.status === 2" type="warning">待处理</el-tag>
            <el-tag v-if="order.status === 3" type="success">制作中</el-tag>
            <el-tag v-if="order.status === 4" type="success">待取餐</el-tag>
          </div>

          <!-- 备注 -->
          <div v-if="order.remark" class="remark-box">
            <strong>备注：</strong> {{ order.remark }}
          </div>

          <el-divider>菜品</el-divider>

          <!-- 菜品列表（含图片） -->
          <div class="items">
            <div class="item" v-for="it in order.orderItems" :key="it.id">

              <img
                class="dish-img"
                :src="it.dishImageUrl || it.imageUrl || '/images/default-dish.png'"
              />

              <div class="dish-name">{{ it.dishName }}</div>

              <div class="dish-qty">x {{ it.quantity }}</div>

            </div>
          </div>

          <el-divider />

          <div class="actions">
            <el-button
              v-if="order.status === 2"
              type="primary"
              @click="accept(order.id)"
            >
              接单
            </el-button>

            <el-button
              v-if="order.status === 3"
              type="success"
              @click="finish(order.id)"
            >
              完成制作
            </el-button>
          </div>

        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { ElMessage } from "element-plus";

import { kitchenOrderList, kitchenAccept, kitchenFinish } from "@/api/kitchen";
import { useUserStore } from "@/store/user";

const userStore = useUserStore();

const orders = ref([]);
let timer = null;

// 加载订单
function load() {
  kitchenOrderList({
    restaurantId: userStore.restaurantId
  }).then((res) => {
    orders.value = res.data || [];
  });
}

// 接单
function accept(id) {
  kitchenAccept(id).then(() => {
    ElMessage.success("已接单");
    load();
  });
}

// 完成制作
function finish(id) {
  kitchenFinish(id).then(() => {
    ElMessage.success("制作完成");
    load();
  });
}

onMounted(() => {
  load();
  timer = setInterval(load, 5000);
});

onBeforeUnmount(() => {
  clearInterval(timer);
});
</script>

<style scoped>
.title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 20px;
}

.order-card {
  margin-bottom: 20px;
  padding: 10px;
}

.order-header {
  font-size: 18px;
  margin-bottom: 10px;
}

.order-status {
  margin-bottom: 15px;
}

/* 备注 */
.remark-box {
  font-size: 14px;
  background: #fdf6ec;
  padding: 8px;
  border-left: 4px solid #e6a23c;
  border-radius: 4px;
  margin-bottom: 10px;
}

/* 菜品区域 */
.items {
  font-size: 16px;
}

/* 菜品一行：左图片，中间菜名，右侧数量 */
.item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.item:last-child {
  border-bottom: none;
}

.dish-img {
  width: 55px;
  height: 55px;
  border-radius: 8px;
  object-fit: cover;
  margin-right: 12px;
}

/* 菜名占据剩余空间 */
.dish-name {
  flex: 1;
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

/* 数量靠最右 & 更醒目 */
.dish-qty {
  width: 50px;
  text-align: right;
  font-size: 16px;
  font-weight: bold;
  color: #ff4d4f;
}

.actions {
  text-align: center;
}
</style>

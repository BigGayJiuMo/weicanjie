<template>
  <el-dialog
    :model-value="visible"
    title="订单详情"
    width="600px"
    @update:model-value="val => $emit('update:visible', val)"
  >
    <div v-if="detail.order" class="order-detail-box">
      <!-- 餐厅信息 + 状态标签 -->
      <div class="restaurant-row">
        <div class="left">
          <img class="restaurant-logo" :src="detail.restaurant.logoUrl" />
          <div class="restaurant-name">{{ detail.restaurant.name }}</div>
        </div>
        <el-tag
          class="status-tag-inline"
          :type="getStatusTagType(detail.order.status)"
        >
          {{ getStatusText(detail.order.status) }}
        </el-tag>
      </div>

      <!-- 菜品列表 -->
      <div class="dish-list">
        <div class="dish-item" v-for="it in detail.orderItems" :key="it.id">
          <img class="dish-img" :src="it.dishImageUrl || it.imageUrl" />
          <div class="dish-info">
            <div class="dish-name">{{ it.dishName }}</div>
            <div class="dish-qty">×{{ it.quantity }}</div>
          </div>
          <div class="dish-price">￥{{ it.dishPrice }}</div>
        </div>
      </div>

      <!-- 金额信息 -->
      <div class="amount-box">
        <div class="line"><span>打包费</span><span>￥{{ detail.packingFee }}</span></div>
        <div class="line"><span>商品金额</span><span>￥{{ detail.subTotal }}</span></div>
        <div class="line total"><span>实付金额</span><span>￥{{ detail.totalAmount }}</span></div>
      </div>

      <!-- 订单其他信息 -->
      <div class="info-box">
        <div class="info-line"><span>订单号</span><span>{{ detail.order.orderNumber }}</span></div>
        <div class="info-line"><span>备注</span><span>{{ detail.remark || "无" }}</span></div>
        <div class="info-line"><span>下单时间</span><span>{{ detail.order.createdTime }}</span></div>
        <div class="info-line" v-if="detail.order.payTime"><span>支付时间</span><span>{{ detail.order.payTime }}</span></div>
        <div class="info-line"><span>支付方式</span><span>{{ detail.order.payStatus === 1 ? "微信支付" : "未支付" }}</span></div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  visible: Boolean,
  detail: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['update:visible']);

// 状态颜色（与父组件保持一致）
function getStatusTagType(status) {
  return {
    1: "info",      // 待支付
    2: "warning",   // 待处理
    3: "primary",   // 制作中
    4: "primary",   // 待取餐
    5: "danger",    // 已取消
    6: "success",   // 已完成
    7: "warning",   // 退款中
    8: "info"       // 已退款
  }[status] || "default";
}

// 状态文字
function getStatusText(status) {
  return {
    1: "待支付",
    2: "待处理",
    3: "制作中",
    4: "待取餐",
    5: "已取消",
    6: "已完成",
    7: "退款中",
    8: "已退款"
  }[status] || "未知状态";
}
</script>

<style scoped>
/* 订单详情样式 */
.restaurant-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.restaurant-row .left {
  display: flex;
  align-items: center;
}

.restaurant-logo {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
  margin-right: 10px;
}

.restaurant-name {
  font-size: 18px;
  font-weight: bold;
}

.status-tag-inline {
  font-size: 14px;
  transform: translateY(2px);
}

.dish-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.dish-img {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
  margin-right: 10px;
}

.dish-info {
  flex: 1;
}

.dish-name {
  font-size: 14px;
  font-weight: 600;
}

.dish-qty {
  font-size: 12px;
  color: #888;
}

.dish-price {
  font-size: 14px;
  font-weight: bold;
  color: #f56c6c;
}

.amount-box {
  margin-top: 20px;
  border-top: 1px solid #eee;
  padding-top: 10px;
}

.amount-box .line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}

.amount-box .total {
  font-size: 16px;
  font-weight: bold;
}

.info-box {
  margin-top: 20px;
}

.info-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.info-line span {
  color: #666;
  font-size: 14px;
}
</style>
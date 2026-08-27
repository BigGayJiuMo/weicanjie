<template>
  <div class="page-container">
    <div>
      <h2>订单管理</h2>
    </div>
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <!-- 超管选餐厅 -->
      <el-select
        v-if="userStore.role === 'super'"
        v-model="restaurantId"
        placeholder="选择餐厅"
        clearable
        style="width: 200px"
      >
        <el-option
          v-for="r in restaurantList"
          :key="r.id"
          :label="r.name"
          :value="r.id"
        />
      </el-select>

      <!-- 状态筛选 -->
      <el-select
        v-model="status"
        placeholder="订单状态"
        clearable
        style="width: 180px"
      >
        <el-option label="待支付" :value="1"/>
        <el-option label="待处理" :value="2"/>
        <el-option label="制作中" :value="3"/>
        <el-option label="待取餐" :value="4"/>
        <el-option label="已取消" :value="5"/>
        <el-option label="已完成" :value="6"/>
        <el-option label="退款中" :value="7"/>
        <el-option label="已退款" :value="8"/>
      </el-select>

      <el-input
        v-model="keyword"
        placeholder="搜索订单号"
        clearable
        style="width: 200px"
        @keyup.enter="load"
      />

      <el-button type="primary" @click="load">搜索</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" border style="width: 100%" v-loading="loading">
      <el-table-column prop="orderNumber" label="订单号" width="190"/>
      <el-table-column label="餐厅" width="120">
        <template #default="{ row }">
          {{ restaurantName[row.restaurantId] || "-" }}
        </template>
      </el-table-column>

      <el-table-column label="金额" width="90">
        <template #default="{ row }">
          ￥{{ Number(row.totalAmount).toFixed(2) }}
        </template>
      </el-table-column>

      <el-table-column label="状态" width="140">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="createdTime" label="创建时间" width="180"/>

      <!-- 操作栏 -->
      <el-table-column label="操作" min-width="380">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row.id)">详情</el-button>

          <!-- 待处理 -> 制作中 -->
          <el-button
            v-if="row.status === 2"
            size="small"
            type="primary"
            @click="changeStatus(row.id, 3)"
          >设为制作中</el-button>

          <!-- 制作中 -> 待取餐 -->
          <el-button
            v-if="row.status === 3"
            size="small"
            type="success"
            @click="changeStatus(row.id, 4)"
          >设为待取餐</el-button>

          <!-- 待取餐 -> 已完成 -->
          <el-button
            v-if="row.status === 4"
            size="small"
            type="success"
            @click="changeStatus(row.id, 6)"
          >确认完成</el-button>

          <!-- 取消订单 -->
          <el-button
            v-if="row.status === 1 || row.status === 2"
            size="small"
            type="danger"
            @click="changeStatus(row.id, 5)"
          >取消订单</el-button>

          <!-- 退款流程 -->
          <!-- 退款中 -> 同意退款（变为已退款） -->
          <el-button
            v-if="row.status === 7"
            size="small"
            type="success"
            @click="approveRefund(row.id)"
          >同意退款</el-button>

          <!-- 退款中 -> 拒绝退款（恢复原状态） -->
          <el-button
            v-if="row.status === 7"
            size="small"
            type="warning"
            @click="rejectRefund(row.id)"
          >拒绝退款</el-button>

          <!-- 手动发起退款 -->
          <el-button
            v-if="row.status === 4 || row.status === 6"
            size="small"
            type="warning"
            @click="refundRequest(row.id)"
          >申请退款</el-button>

          <el-button
            v-if="row.remark"
            size="small"
            type="info"
            @click="showRemark(row.remark)"
          >查看备注</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      class="pager"
      background
      layout="total, prev, pager, next"
      :total="total"
      :page-size="pageSize"
      :current-page="pageNum"
      @current-change="load"
    />

    <!-- 订单详情弹窗（子组件） -->
    <OrderDetailDialog
      v-model:visible="detailVisible"
      :detail="detail"
    />

    <!-- 备注弹窗（子组件） -->
    <OrderRemarkDialog
      v-model:visible="remarkVisible"
      :remark="remarkText"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/store/user";
import {
  adminOrderPage,
  adminOrderDetail,
  adminOrderUpdateStatus,
  adminRefundApprove,
  adminRefundReject,
  requestRefund
} from "@/api/order";
import { getRestaurantPage } from "@/api/restaurant";
import { useRouter, useRoute } from "vue-router";

// 引入拆分的子组件
import OrderDetailDialog from "./OrderDetailDialog.vue";
import OrderRemarkDialog from "./OrderRemarkDialog.vue";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 数据定义
const restaurantList = ref([]);
const restaurantName = ref({});
const restaurantId = ref(userStore.role === "super" ? null : userStore.restaurantId);
const status = ref(null);
const keyword = ref("");
const tableData = ref([]);
const loading = ref(false);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const detailVisible = ref(false);
const detail = ref({});
const remarkVisible = ref(false);
const remarkText = ref("");

// 加载餐厅
function loadRestaurantList() {
  if (userStore.role !== "super") return;
  getRestaurantPage({ pageNum: 1, pageSize: 999 }).then((res) => {
    restaurantList.value = res.data.records;
    res.data.records.forEach((r) => {
      restaurantName.value[r.id] = r.name;
    });
  });
}

// 加载订单
function load(page) {
  loading.value = true;
  if (page instanceof Event || page instanceof PointerEvent) {
    page = pageNum.value;
  }
  if (typeof page === "number") {
    pageNum.value = page;
  }

  adminOrderPage({
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    restaurantId: restaurantId.value || null,
    status: status.value || null,
    keyword: keyword.value || null
  })
    .then((res) => {
      tableData.value = res.data.records;
      total.value = res.data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}

// 打开详情
function openDetail(id) {
  adminOrderDetail(id).then((res) => {
    detail.value = res.data;
    detailVisible.value = true;
  });
}

// 修改订单状态
function changeStatus(id, toStatus) {
  adminOrderUpdateStatus(id, toStatus).then(() => {
    ElMessage.success("状态已更新");
    load(pageNum.value);
  });
}

// 退款流程
function approveRefund(id) {
  adminRefundApprove(id).then(() => {
    ElMessage.success("已同意退款");
    load(pageNum.value);
  });
}

function rejectRefund(id) {
  adminRefundReject(id).then(() => {
    ElMessage.success("已拒绝退款");
    load(pageNum.value);
  });
}

// 查看备注
function showRemark(text) {
  remarkText.value = text || "无备注";
  remarkVisible.value = true;
}

// 代用户申请退款
function refundRequest(id) {
  requestRefund({
    orderId: id,
    reason: "后台代用户申请退款",
    remark: ""
  }).then(() => {
    ElMessage.success("退款申请已提交");
    load(pageNum.value);
  });
}

// 状态颜色
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

onMounted(() => {
  loadRestaurantList();

  const q = route.query;

  if (q.restaurantId) restaurantId.value = Number(q.restaurantId);
  if (q.status) status.value = Number(q.status);
  if (q.keyword) keyword.value = String(q.keyword);

  pageNum.value = 1;
  load(1);
});
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
}

.pager {
  margin-top: 20px;
  text-align: right;
}
</style>
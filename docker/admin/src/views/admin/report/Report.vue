<template>
  <div>
    <!-- 标题 -->
    <div class="report-header">
      <h2>统计报表</h2>
    </div>

    <!-- 筛选区 -->
    <div class="report-filters">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        placeholder="选择日期范围"
        style="margin-top: 5px;"
      />
      <el-radio-group v-model="timeGranularity" style="margin-left: 16px;">
        <el-radio-button label="day">日</el-radio-button>
        <el-radio-button label="week">周</el-radio-button>
        <el-radio-button label="month">月</el-radio-button>
      </el-radio-group>
      <el-button type="primary" style="margin-left: 16px; margin-top: 5px;" @click="fetchReportData">
        查询报表
      </el-button>
    </div>

    <!-- 餐厅筛选 -->
    <el-select
      v-if="userRole === 'super'"
      v-model="currentRestaurantId"
      clearable
      placeholder="全部餐厅"
      style="width: 200px; margin-bottom: 12px"
      @change="handleRestaurantChange"
    >
      <el-option
        v-for="r in restaurantList"
        :key="r.id"
        :label="r.name"
        :value="r.id"
      />
    </el-select>

    <!-- KPI 卡片 -->
    <div class="kpi-container">
      <!-- 订单总数 -->
      <div class="kpi-card clickable" @click="goOrderList">
        <div class="kpi-title">订单总数</div>
        <div class="kpi-value">{{ kpi.orderCount }}</div>

        <div
          class="kpi-compare"
          v-if="timeGranularity === 'month' && kpiCompare.orderCountRate !== null"
        >
          <span v-if="kpiCompare.orderCountRate === 'NEW'" class="up">
            新增长
          </span>
          <span
            v-else
            :class="kpiCompare.orderCountRate >= 0 ? 'up' : 'down'"
          >
            {{ kpiCompare.orderCountRate >= 0 ? '↑' : '↓' }}
            {{ Math.abs(kpiCompare.orderCountRate) }}%
          </span>
          <span class="kpi-compare-text">较去年同月</span>
        </div>
      </div>

      <!-- 总营业额 -->
      <div class="kpi-card clickable" @click="goOrderListByAmount">
        <div class="kpi-title">总营业额</div>
        <div class="kpi-value">¥ {{ kpi.totalSales.toFixed(2) }}</div>

        <div
          class="kpi-compare"
          v-if="timeGranularity === 'month' && kpiCompare.totalSalesRate !== null"
        >
          <span v-if="kpiCompare.totalSalesRate === 'NEW'" class="up">
            新增长
          </span>
          <span
            v-else
            :class="kpiCompare.totalSalesRate >= 0 ? 'up' : 'down'"
          >
            {{ kpiCompare.totalSalesRate >= 0 ? '↑' : '↓' }}
            {{ Math.abs(kpiCompare.totalSalesRate) }}%
          </span>
          <span class="kpi-compare-text">较去年同月</span>
        </div>
      </div>

      <!-- 客单价 -->
      <div class="kpi-card">
        <div class="kpi-title">客单价</div>
        <div class="kpi-value">¥ {{ kpi.avgOrderPrice.toFixed(2) }}</div>
      </div>

      <!-- 菜品销量 -->
      <div class="kpi-card">
        <div class="kpi-title">菜品销量</div>
        <div class="kpi-value">{{ kpi.totalItemsSold }}</div>
      </div>
    </div>

    <!-- 图表 - 仅在有数据时显示 -->
    <div v-if="hasChartData" class="chart-container">
      <div ref="salesChartRef" class="chart"></div>
      <div ref="orderChartRef" class="chart"></div>
    </div>

    <!-- 明细表 -->
    <el-table v-if="hasTableData" :data="filteredData" style="width: 100%">
      <el-table-column label="统计时间" prop="timeKey"/>
      <el-table-column label="餐厅名称" prop="restaurantName"/>
      <el-table-column label="订单数量" prop="orderCount"/>
      <el-table-column label="总销售额" prop="totalSales"/>
      <el-table-column label="菜品销量" prop="totalItemsSold"/>
    </el-table>
    <!-- 菜品销量分析 -->
    <div v-if="dishSales.length" style="margin-top: 32px">
    <h3 style="margin-bottom: 12px">菜品销量明细</h3>
    <!-- 菜品销量表 -->
    <el-table :data="dishSales" style="width: 100%">
      <el-table-column label="所属餐厅" prop="restaurantName" />
      <el-table-column label="菜品名称" prop="dishName" />
      <el-table-column label="销量" prop="totalSold" />
    </el-table>
    <!-- 柱状图 -->
    <div
      ref="dishChartRef"
      style="width: 100%; height: 400px; margin-top: 24px"
    ></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed, watch } from "vue";
import { useRouter } from "vue-router";
import * as echarts from "echarts";
import { getReportData } from "@/api/report";
import { getRestaurantPage } from "@/api/restaurant";
import { useUserStore } from "@/store/user";
import { ElMessage } from "element-plus";

const userRole = useUserStore().role;
const router = useRouter();

/* ================= 状态 ================= */

const dateRange = ref([]);
const timeGranularity = ref("day");
const currentRestaurantId = ref(null);
const hasChartData = ref(false);
const hasTableData = ref(false);

const reportData = ref([]);
const restaurantList = ref([]);

const dishSales = ref([]);         
const dishChartRef = ref(null);     
let dishChart = null;

/* ================= KPI ================= */

const kpi = ref({
  orderCount: 0,
  totalSales: 0,
  avgOrderPrice: 0,
  totalItemsSold: 0
});

const kpiCompare = ref({
  orderCountRate: null,
  totalSalesRate: null
});

/* ================= 图表 ================= */

const salesChartRef = ref(null);
const orderChartRef = ref(null);

let salesChart = null;
let orderChart = null;

onMounted(async () => {
  const res = await getRestaurantPage({ pageNum: 1, pageSize: 999 });
  restaurantList.value = res.data.records || [];
});

/* ================= 查询 ================= */

const fetchReportData = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    ElMessage.warning("请选择日期范围");
    return;
  }

  const startDate = formatDate(dateRange.value[0]);
  const endDate = formatDate(dateRange.value[1]);

  try {
    const res = await getReportData(
      startDate,
      endDate,
      currentRestaurantId.value,
      timeGranularity.value
    );

    reportData.value = res.data?.data || [];
    dishSales.value = res.data?.dishSales || [];
    
    // 更新显示状态
    hasTableData.value = reportData.value.length > 0;
    hasChartData.value = reportData.value.length > 0;

    const kpiResp = res.data?.kpi;

    if (kpiResp?.current && kpiResp?.previous) {
      kpiCompare.value.orderCountRate = calcRate(
        kpiResp.current.orderCount,
        kpiResp.previous.orderCount
      );
      kpiCompare.value.totalSalesRate = calcRate(
        kpiResp.current.totalSales,
        kpiResp.previous.totalSales
      );
    } else {
      kpiCompare.value.orderCountRate = null;
      kpiCompare.value.totalSalesRate = null;
    }

    calcKpi();
    
    // 如果有数据才渲染图表
    if (hasChartData.value) {
      await nextTick();
      renderCharts();
    }
    if (dishSales.value.length) {
      await nextTick();
      renderDishChart();
    }
  } catch (error) {
    console.error("获取报表数据失败:", error);
    ElMessage.error("获取报表数据失败");
  }
};

/* ================= 工具 ================= */

function formatDate(d) {
  return d instanceof Date ? d.toISOString().split("T")[0] : d;
}

function calcRate(current, previous) {
  if (current == null || previous == null) return null;
  if (previous === 0) return current === 0 ? 0 : "NEW";

  const rate = ((current - previous) / previous) * 100;
  if (!isFinite(rate)) return null;

  return Number(rate.toFixed(1));
}

/* ================= 其他 ================= */

function calcKpi() {
  let orderCount = 0;
  let totalSales = 0;
  let totalItemsSold = 0;

  filteredData.value.forEach(item => {
    orderCount += item.orderCount;
    totalSales += Number(item.totalSales);
    totalItemsSold += item.totalItemsSold;
  });

  kpi.value.orderCount = orderCount;
  kpi.value.totalSales = totalSales;
  kpi.value.totalItemsSold = totalItemsSold;
  kpi.value.avgOrderPrice =
    orderCount === 0 ? 0 : totalSales / orderCount;
}

function renderCharts() {
  // 销毁旧的图表实例
  if (salesChart) {
    salesChart.dispose();
    salesChart = null;
  }
  if (orderChart) {
    orderChart.dispose();
    orderChart = null;
  }

  // 按日期聚合数据
  const aggMap = new Map();
  filteredData.value.forEach(item => {
    const key = item.timeKey;
    if (!aggMap.has(key)) {
      aggMap.set(key, { totalSales: 0, orderCount: 0 });
    }
    const agg = aggMap.get(key);
    agg.totalSales += Number(item.totalSales);
    agg.orderCount += item.orderCount;
  });

  // 转换为数组并按日期排序
  const aggregated = Array.from(aggMap.entries())
    .map(([timeKey, values]) => ({
      timeKey,
      totalSales: values.totalSales,
      orderCount: values.orderCount
    }))
    .sort((a, b) => new Date(a.timeKey) - new Date(b.timeKey));

  const xAxisData = aggregated.map(i => i.timeKey);
  const sales = aggregated.map(i => i.totalSales);
  const orders = aggregated.map(i => i.orderCount);

  // 创建新的图表实例
  if (salesChartRef.value && orderChartRef.value) {
    salesChart = echarts.init(salesChartRef.value);
    orderChart = echarts.init(orderChartRef.value);

    salesChart.setOption({
      title: { text: "营业额趋势" },
      tooltip: { trigger: "axis" },
      xAxis: { type: "category", data: xAxisData },
      yAxis: { type: "value" },
      series: [{ type: "line", smooth: true, data: sales }]
    });

    orderChart.setOption({
      title: { text: "订单量趋势" },
      tooltip: { trigger: "axis" },
      xAxis: { type: "category", data: xAxisData },
      yAxis: { type: "value" },
      series: [{ type: "line", smooth: true, data: orders }]
    });
  }
}

function renderDishChart() {
  if (!dishChartRef.value) return;

  if (dishChart) {
    dishChart.dispose();
    dishChart = null;
  }

  dishChart = echarts.init(dishChartRef.value);

  const names = dishSales.value.map(d => d.dishName);
  const values = dishSales.value.map(d => d.totalSold);

  dishChart.setOption({
    title: {
      text: "菜品销量统计"
    },
    tooltip: {
      trigger: "axis",
      axisPointer: { type: "shadow" }
    },
    xAxis: {
      type: "category",
      data: names,
      axisLabel: {
        rotate: 30
      }
    },
    yAxis: {
      type: "value"
    },
    series: [
      {
        type: "bar",
        data: values,
        barWidth: "50%"
      }
    ]
  });
}

function resetData() {
  // 清空数据和图表
  reportData.value = [];
  hasChartData.value = false;
  hasTableData.value = false;
  
  kpi.value = {
    orderCount: 0,
    totalSales: 0,
    avgOrderPrice: 0,
    totalItemsSold: 0
  };
  
  kpiCompare.value = {
    orderCountRate: null,
    totalSalesRate: null
  };
  
  // 销毁图表
  if (salesChart) {
    salesChart.dispose();
    salesChart = null;
  }
  if (orderChart) {
    orderChart.dispose();
    orderChart = null;
  }
  dishSales.value = [];
  if (dishChart) {
    dishChart.dispose();
    dishChart = null;
  }
}

function handleRestaurantChange() {
  // 切换餐厅时清空数据
  if (hasChartData.value || hasTableData.value) {
    resetData();
  }
}

const filteredData = computed(() => {
  if (!currentRestaurantId.value) return reportData.value;
  return reportData.value.filter(r => r.restaurantId === currentRestaurantId.value);
});

watch(timeGranularity, () => {
  dateRange.value = [];
  // 切换时间粒度时清空数据
  if (hasChartData.value || hasTableData.value) {
    resetData();
  }
});
</script>

<style scoped>
.report-filters {
  margin-bottom: 20px;
}

.kpi-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin: 20px 0;
}

.kpi-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,.06);
}

.kpi-title {
  font-size: 14px;
  color: #666;
}

.kpi-value {
  font-size: 26px;
  font-weight: bold;
  margin-top: 6px;
}

.kpi-compare {
  margin-top: 6px;
  font-size: 12px;
  display: flex;
  gap: 6px;
}

.kpi-compare-text {
  color: #999;
}

.up { color: #67c23a; }
.down { color: #f56c6c; }

.chart-container {
  display: flex;
  gap: 20px;
  margin: 20px 0;
}

.chart {
  flex: 1;
  height: 360px;
}
</style>
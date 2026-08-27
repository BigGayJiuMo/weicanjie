<template>
  <div>
    <div>
      <h2>餐厅管理</h2>
    </div>
    <!-- 顶部搜索  -->
    <div class="toolbar">
      <el-input
        v-model="keyword"
        placeholder="搜索餐厅名称..."
        style="width: 250px"
        clearable
        @keyup.enter="loadData"
      />
      <el-button type="primary" @click="loadData">搜索</el-button>

      <el-button
        type="success"
        v-if="userStore.role === 'super'"
        @click="openForm(null)"
      >
        + 新增餐厅
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" border>
      <el-table-column prop="id" label="ID" width="70" />

      <el-table-column label="Logo" width="120">
        <template #default="{ row }">
          <el-image
            v-if="row.logoUrl"
            :src="row.logoUrl"
            style="width: 60px; height: 60px; border-radius: 5px; cursor:pointer"
            @click="openViewer([row.logoUrl], row.logoUrl)"
          />
          <span v-else style="color:#999;">无</span>
        </template>
      </el-table-column>

      <el-table-column prop="name" label="餐厅名称" min-width="150" />
      <el-table-column prop="contactPhone" label="联系电话" width="130" />
      <el-table-column prop="address" label="地址" min-width="180" />

      <el-table-column label="停业/开业" width="120" v-if="userStore.role === 'super'">
        <template #default="{ row }">
          <el-switch
            v-model="row.status" 
            :active-value="1"   
            :inactive-value="0"  
            @change="toggleStatus(row)"
            :disabled="userStore.role !== 'super'"
          />
        </template>
      </el-table-column>

      <el-table-column label="营业模式" width="160" v-if="userStore.role !== 'kitchen'">
        <template #default="{ row }">
          <el-select 
            v-model="row.manualBusinessStatus"
            placeholder="选择状态"
            style="width: 120px"
            @change="changeManualStatus(row)"
          >
            <el-option label="自动" :value="0" />
            <el-option label="营业中" :value="1" />
            <el-option label="休息中" :value="2" />
          </el-select>
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button size="small" @click="openForm(row)">编辑</el-button>
          <el-button size="small" type="warning" @click="openImages(row)">图片</el-button>
          <el-button size="small" type="primary" @click="openBusinessHours(row)">
            营业时间
          </el-button>
          <el-button
            size="small"
            type="danger"
            v-if="userStore.role === 'super'"
            @click="remove(row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="userStore.role === 'super'"
      class="pager"
      :current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="changePage"
    />

    <el-image-viewer
      v-if="viewerVisible"
      :url-list="viewerList"
      :initial-index="viewerIndex"
      @close="viewerVisible = false"
    />

    <RestaurantForm ref="formRef" @refresh="loadData" />
    <RestaurantImages ref="imagesRef" />
    <BusinessHoursDialog ref="businessHoursRef" @refresh="emit('refresh')" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/store/user";
import BusinessHoursDialog from "./BusinessHoursDialog.vue";
import {
  getRestaurantPage,
  deleteRestaurant,
  toggleRestaurant,
  setManualBusinessStatus
} from "@/api/restaurant";

import RestaurantForm from "./RestaurantForm.vue";
import RestaurantImages from "./RestaurantImages.vue";

const userStore = useUserStore();

const keyword = ref("");
const tableData = ref([]);

const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const formRef = ref();
const imagesRef = ref();
const businessHoursRef = ref(); 

const viewerVisible = ref(false);
const viewerList = ref([]);
const viewerIndex = ref(0);

function openViewer(list, img) {
  viewerList.value = list;
  viewerIndex.value = list.indexOf(img);
  viewerVisible.value = true;
}

/* 加载列表 */
function loadData() {
  getRestaurantPage({
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    keyword: keyword.value,
  }).then((res) => {
    tableData.value = res.data.records || [];
    total.value = res.data.total || 0;
  });
}
function openBusinessHours(row) {
  if (userStore.role === 'merchant') {
    if (userStore.restaurantId && userStore.restaurantId !== row.id) {
      ElMessage.warning("您只能管理自己餐厅的营业时间");
      return;
    }
  }
  businessHoursRef.value.open(row.id);
}

/* 切换分页 */
function changePage(p) {
  pageNum.value = p;
  loadData();
}

/* 切换营业状态 */
function toggleStatus(row) {
  toggleRestaurant(row.id, row.status).then(() => {
    ElMessage.success("已切换状态");
  });
}

function changeManualStatus(row) {
  setManualBusinessStatus(row.id, row.manualBusinessStatus).then(() => {
    ElMessage.success("营业状态已更新");
    loadData();
  });
}

/* 打开编辑 */
function openForm(row) {
  formRef.value.open(row);
}

/* 打开图片管理 */
function openImages(row) {
  imagesRef.value.open(row);
}

/* 删除餐厅 */
function remove(id) {
  ElMessageBox.confirm("确定删除该餐厅？", "提示", {
    type: "warning",
    lockScroll: false, //  修复页面抖动的关键 
  }).then(() => {
    deleteRestaurant(id).then(() => {
      ElMessage.success("删除成功");
      loadData();
    });
  });
}

onMounted(loadData);
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.pager {
  margin-top: 20px;
  text-align: right;
}
</style>

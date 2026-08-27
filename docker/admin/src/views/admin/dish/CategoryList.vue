<template>
  <div class="page-container">
    <div>
      <h2>菜品分类</h2>
    </div>
    <!-- SUPER 管理员选择餐厅 -->
    <div class="restaurant-select" v-if="userStore.role === 'super'">
      <el-select
        v-model="restaurantId"
        placeholder="请选择餐厅"
        style="width: 250px;margin-bottom: 15px;"
        @change="load"
        clearable
      >
        <el-option
          v-for="item in restaurantList"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
      <el-input
        v-model="keyword"
        placeholder="搜索分类名称..."
        clearable
        style="width: 250px"
        @keyup.enter="search"
      />
      <el-button type="primary" @click="search">搜索</el-button>

      <el-button
        type="success"
        @click="openForm(null)"
        :disabled="!restaurantId"
      >
        + 新增分类
      </el-button>
    </div>

    <!-- 分类表格 -->
    <el-table
      ref="tableRef"
      :data="tableData"
      border
      style="width: 100%"
      v-loading="loading"
      empty-text="暂无分类，请添加新的分类"
      row-key="id"
    >

      <!-- 拖拽图标 -->
      <el-table-column label="" width="60" align="center">
        <template #default>
          <span class="drag-handle">
            <el-icon><Rank /></el-icon>
          </span>
        </template>
      </el-table-column>

      <!-- 排序号显示 -->
      <el-table-column prop="sortOrder" label="排序" width="80" />

      <el-table-column prop="name" label="分类名称" min-width="150" />

      <el-table-column label="是否启用" width="120">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="toggleStatus(row)"
          />
        </template>
      </el-table-column>

      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="openForm(row)">编辑</el-button>
        </template>
      </el-table-column>

    </el-table>

    <!-- 分页 -->
    <el-pagination
      class="pager"
      background
      layout="total, prev, pager, next"
      :total="total"
      :current-page="pageNum"
      :page-size="pageSize"
      @current-change="load"
    />

    <!-- 弹窗 -->
    <el-dialog
      v-model="visible"
      :title="form.id ? '编辑分类' : '新增分类'"
      width="420px"
    >
      <el-form :model="form" label-width="90px">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import Sortable from "sortablejs";
import { Rank } from "@element-plus/icons-vue"; // ← 引入拖拽图标

import {
  getCategoryPage,
  addCategory,
  updateCategory,
  deleteCategory,
  toggleCategory,
  sortCategory,
} from "@/api/category";

import { getRestaurantPage } from "@/api/restaurant";
import { useUserStore } from "@/store/user";

const userStore = useUserStore();

const restaurantId = ref(userStore.role === "super" ? null : userStore.restaurantId);
const restaurantList = ref([]);

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const keyword = ref("");

const visible = ref(false);
const form = ref({});
const tableRef = ref(null);

function loadRestaurantList() {
  if (userStore.role !== "super") return;

  getRestaurantPage({ pageNum: 1, pageSize: 999 }).then((res) => {
    restaurantList.value = res.data.records || [];
  });
}

function search() {
  pageNum.value = 1;
  load();
}

function load(page = pageNum.value) {
  if (!restaurantId.value) {
    tableData.value = [];
    return;
  }

  loading.value = true;
  pageNum.value = page;

  getCategoryPage({
    pageNum: pageNum.value,  
    pageSize: pageSize.value,  
    restaurantId: restaurantId.value,
    keyword: keyword.value,
  })
    .then((res) => {
      tableData.value = res.data.records || [];
      total.value = res.data.total || 0;
    })
    .finally(() => {
      loading.value = false;
      nextTick(() => initSort());
    });
}


watch(restaurantId, () => {
  pageNum.value = 1;
  load();
});

function openForm(row) {
  form.value = row
    ? { ...row }
    : { restaurantId: restaurantId.value, name: "", status: 1 };

  visible.value = true;
}

function submit() {
  const api = form.value.id ? updateCategory : addCategory;

  api(form.value).then(() => {
    ElMessage.success("保存成功");
    visible.value = false;
    load();
  });
}

function remove(id) {
  ElMessageBox.confirm("删除后不可恢复，确定删除吗？", "提示", {
    type: "warning",
  }).then(() => {
    deleteCategory(id).then(() => {
      ElMessage.success("删除成功");
      load();
    });
  });
}

function toggleStatus(row) {
  toggleCategory(row.id).then(() => {
    ElMessage.success("状态已更新");
  });
}

function initSort() {
  const tbody = tableRef.value?.$el.querySelector(".el-table__body-wrapper tbody");
  if (!tbody) return;

  Sortable.create(tbody, {
    animation: 150,
    handle: ".drag-handle", // 仅允许拖图标
    onEnd({ oldIndex, newIndex }) {
      if (oldIndex === newIndex) return;

      const moved = tableData.value.splice(oldIndex, 1)[0];
      tableData.value.splice(newIndex, 0, moved);

      tableData.value.forEach((item, index) => {
        item.sortOrder = index + 1;
      });

      sortCategory(tableData.value).then(() => {
        ElMessage.success("排序已更新");
      });
    }
  });
}

onMounted(() => {
  loadRestaurantList();
  load();
});
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.sort-text,
.drag-handle {
  cursor: grab;
}

.drag-handle:hover {
  color: #409eff;
}

.drag-handle {
  font-size: 18px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
</style>

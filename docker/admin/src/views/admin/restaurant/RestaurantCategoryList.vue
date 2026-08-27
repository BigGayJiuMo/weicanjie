<template>
  <div>
    <div>
      <h2>餐厅分类</h2>
    </div>
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <el-input
        v-model="keyword"
        placeholder="搜索分类名称..."
        clearable
        style="width: 250px"
        @keyup.enter="loadData"
      />
      <el-button type="primary" @click="openForm(null)">
        + 新增分类
      </el-button>
    </div>

    <!-- 分类列表（拖拽排序） -->
    <el-table
      :data="tableData"
      border
      row-key="id"
      ref="tableRef"
      style="width: 100%"
    >
      <!-- 拖拽手柄 -->
      <el-table-column label="排序" width="60">
        <template #default>
          <el-icon><Rank /></el-icon>
        </template>
      </el-table-column>

      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" />

      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openForm(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="formVisible"
      :title="form.id ? '编辑分类' : '新增分类'"
      width="400px"
      append-to-body
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import Sortable from "sortablejs";
import { Rank } from "@element-plus/icons-vue";

import {
  getRestaurantCategoryList,
  addRestaurantCategory,
  updateRestaurantCategory,
  deleteRestaurantCategory,
  updateRestaurantCategorySort
} from "@/api/restaurant";

const keyword = ref("");
const tableData = ref([]);
const tableRef = ref(null);

const formVisible = ref(false);
const form = ref({
  id: null,
  name: "",
  sortOrder: 0
});

/* 加载分类列表 */
async function loadData() {
  const res = await getRestaurantCategoryList();
  tableData.value = res.data || [];

  // 自动补充排序字段
  tableData.value.forEach((item, index) => {
    if (!item.sortOrder) item.sortOrder = index + 1;
  });

  // 表格加载完再初始化拖拽
  nextTick(() => initDrag());
}

/* 拖拽排序功能 */
function initDrag() {
  const el = tableRef.value.$el.querySelector(".el-table__body-wrapper tbody");

  Sortable.create(el, {
    animation: 150,
    handle: ".el-table__row", // 整行可拖拽
    onEnd({ oldIndex, newIndex }) {
      const moved = tableData.value.splice(oldIndex, 1)[0];
      tableData.value.splice(newIndex, 0, moved);
      updateSortOrder();
    }
  });
}

/* 保存排序 */
async function updateSortOrder() {
  tableData.value.forEach((item, index) => {
    item.sortOrder = index + 1;
  });

  try {
    await updateRestaurantCategorySort(tableData.value);
    ElMessage.success("排序已保存");
  } catch (e) {
    ElMessage.error("排序保存失败");
  }
}

/* 打开新增/编辑弹窗 */
function openForm(row) {
  if (row) {
    form.value = { ...row };
  } else {
    form.value = {
      id: null,
      name: "",
      sortOrder: tableData.value.length + 1
    };
  }
  formVisible.value = true;
}

/* 保存分类 */
async function save() {
  if (!form.value.name.trim()) {
    ElMessage.warning("分类名称不能为空");
    return;
  }

  try {
    if (form.value.id) {
      await updateRestaurantCategory(form.value);
    } else {
      await addRestaurantCategory(form.value);
    }

    ElMessage.success("保存成功");
    formVisible.value = false;
    loadData();
  } catch {
    ElMessage.error("保存失败");
  }
}

/* 删除分类 */
function remove(id) {
  ElMessageBox.confirm("确定删除该分类？", "提示", {
    type: "warning",
  }).then(async () => {
    try {
      await deleteRestaurantCategory(id);
      ElMessage.success("删除成功");
      loadData();
    } catch {
      ElMessage.error("删除失败（分类可能被使用）");
    }
  });
}

onMounted(() => {
  loadData();
});
</script>

<style scoped>

.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.el-table .el-table__row {
  cursor: move;
}
</style>

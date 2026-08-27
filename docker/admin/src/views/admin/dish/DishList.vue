<template>
  <div class="page-container">
    <div>
      <h2>菜品管理</h2>
    </div>
    <!-- SUPER 选择餐厅 -->
    <div class="restaurant-select" v-if="userStore.role === 'super'">
      <el-select
        v-model="restaurantId"
        placeholder="请选择餐厅"
        style="width: 260px"
        clearable
        @change="onRestaurantChange"
      >
        <el-option
          v-for="r in restaurantList"
          :key="r.id"
          :label="r.name"
          :value="r.id"
        />
      </el-select>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
      <el-input
        v-model="keyword"
        placeholder="搜索菜品名称..."
        clearable
        style="width: 260px"
        @keyup.enter="search"
      />

      <el-select
        v-model="categoryId"
        placeholder="分类筛选"
        clearable
        style="width: 200px"
        @change="search"
      >
        <el-option
          v-for="c in categoryList"
          :key="c.id"
          :label="c.name"
          :value="c.id"
        />
      </el-select>

      <el-button type="primary" @click="search">搜索</el-button>

      <el-button
        type="success"
        @click="openForm(null)"
        :disabled="!restaurantId"
      >
        + 新增菜品
      </el-button>
    </div>

    <!-- 菜品表格 -->
    <el-table
      :data="tableData"
      border
      style="width: 100%"
      v-loading="loading"
      empty-text="暂无菜品，请添加新菜品"
    >
      <!-- 图片 -->
      <el-table-column label="图片" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.imageUrl"
            :src="row.imageUrl"
            fit="cover"
            style="width: 60px; height: 60px; border-radius: 8px; cursor:pointer"
            @click="openViewer([row.imageUrl], row.imageUrl)"
          />
          <span v-else style="color:#999">无</span>
        </template>
      </el-table-column>

      <el-table-column prop="name" label="菜品名称" min-width="150" />

      <el-table-column label="价格" width="120">
        <template #default="{ row }">
          ￥{{ row.price.toFixed(2) }}
        </template>
      </el-table-column>

      <el-table-column label="分类" width="140">
        <template #default="{ row }">
          {{ categoryNameMap[row.categoryId] || "无" }}
        </template>
      </el-table-column>

      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag type="success" v-if="row.status === 1">上架</el-tag>
          <el-tag type="info" v-else>下架</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <el-button size="small" @click="openForm(row)">编辑</el-button>

          <el-button size="small" type="warning" @click="toggle(row.id)">
            {{ row.status === 1 ? "下架" : "上架" }}
          </el-button>

          <el-button size="small" type="danger" @click="remove(row.id)">
            删除
          </el-button>
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

    <el-image-viewer
      v-if="viewerVisible"
      :url-list="viewerList"
      :initial-index="viewerIndex"
      @close="viewerVisible = false"
    />

    <!-- 菜品编辑弹窗 -->
    <el-dialog v-model="visible" :title="form.id ? '编辑菜品' : '新增菜品'" width="480px">
      <el-form :model="form" label-width="100px">

        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>

        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :step="0.5" />
        </el-form-item>

        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="c in categoryList"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>

        <el-form-item label="配料">
          <el-input v-model="form.ingredients" />
        </el-form-item>

        <el-form-item label="口味">
          <el-input v-model="form.taste" />
        </el-form-item>

        <el-form-item label="重量">
          <el-input v-model="form.weight" />
        </el-form-item>

        <!-- 菜品图片上传 -->
        <el-form-item label="菜品图片">
          <el-upload
            class="avatar-uploader"
            action="/api/upload/image"
            :headers="uploadHeaders"
            :data="{ type: 'dish', restaurantId: restaurantId }"
            :show-file-list="false"
            accept="image/*"
            :on-success="handleUploadSuccess"
          >
            <img v-if="form.imageUrl" :src="form.imageUrl" class="preview-img" />
            <el-icon v-else class="avatar-uploader-icon">
              <Plus />
            </el-icon>
          </el-upload>
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
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import { useUserStore } from "@/store/user";

import { getRestaurantPage } from "@/api/restaurant";
import { getCategoryPage } from "@/api/category";
import { getDishPage, addDish, updateDish, deleteDish, toggleDish } from "@/api/dish";

const userStore = useUserStore();

// localStorage 安全写法
const uploadHeaders = {
  token: typeof localStorage !== "undefined" ? localStorage.getItem("token") : ""
};

// 数据
const restaurantId = ref(userStore.role === "super" ? null : userStore.restaurantId);
const restaurantList = ref([]);

const keyword = ref("");
const categoryId = ref(null);
const categoryList = ref([]);
const categoryNameMap = ref({});

const tableData = ref([]);
const loading = ref(false);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const visible = ref(false);
const form = ref({});

const viewerVisible = ref(false);
const viewerList = ref([]);
const viewerIndex = ref(0);

function openViewer(list, img) {
  viewerList.value = list;
  viewerIndex.value = list.indexOf(img);
  viewerVisible.value = true;
}

// 上传成功
function handleUploadSuccess(res) {
  if (res.code === 200) {
    form.value.imageUrl = res.data;
    ElMessage.success("上传成功");
  } else {
    ElMessage.error("上传失败");
  }
}

// 切换餐厅时：分类 → 菜品
function onRestaurantChange() {
  tableData.value = [];
  categoryId.value = null;
  loadCategory().then(() => load());
}

// 加载分类
function loadCategory() {
  return getCategoryPage({
    restaurantId: restaurantId.value,
    pageNum: 1,
    pageSize: 999
  }).then((res) => {
    categoryList.value = res.data.records || [];
    categoryNameMap.value = {};
    categoryList.value.forEach((c) => {
      categoryNameMap.value[c.id] = c.name;
    });
  });
}

// 加载菜品
function load(page = pageNum.value) {
  if (!restaurantId.value) return;

  loading.value = true;
  pageNum.value = page;

  getDishPage({
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    restaurantId: restaurantId.value,
    categoryId: categoryId.value,
    keyword: keyword.value,
  })
    .then((res) => {
      tableData.value = res.data.records;
      total.value = res.data.total;
    })
    .finally(() => (loading.value = false));
}

function search() {
  pageNum.value = 1;
  load();
}

// 打开表单
function openForm(row) {
  form.value = row
    ? { ...row }
    : {
        restaurantId: restaurantId.value,
        name: "",
        price: 0,
        categoryId: null,
        description: "",
        ingredients: "",
        taste: "",
        weight: "",
        imageUrl: "",
        status: 1
      };

  visible.value = true;
}

// 保存
function submit() {
  const api = form.value.id ? updateDish : addDish;

  api(form.value).then(() => {
    ElMessage.success("保存成功");
    visible.value = false;
    load();
  });
}

// 删除
function remove(id) {
  ElMessageBox.confirm("删除后不可恢复，确认删除？", "提示", { type: "warning" }).then(() => {
    deleteDish(id).then(() => {
      ElMessage.success("删除成功");
      load();
    });
  });
}

// 上架下架
function toggle(id) {
  toggleDish(id).then(() => {
    ElMessage.success("状态已更新");
    load();
  });
}

onMounted(() => {
  if (userStore.role === "super") {
    getRestaurantPage({ pageNum: 1, pageSize: 999 }).then((res) => {
      restaurantList.value = res.data.records;
    });
  }

  if (restaurantId.value) {
    loadCategory().then(() => load());
  }
});
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.restaurant-select {
  margin-bottom: 15px;
}

.pager {
  margin-top: 20px;
  text-align: right;
}

/* 上传组件美化 */
.avatar-uploader {
  width: 140px;
  height: 140px;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.preview-img {
  width: 140px;
  height: 140px;
  border-radius: 6px;
  object-fit: cover;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #909399;
}
</style>

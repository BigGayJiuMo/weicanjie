<template>
  <div class="review-container">
    <div>
      <h2>评价管理</h2>
    </div>
    <!-- SUPER 才显示餐厅选择 -->
    <div v-if="role === 'super'" class="select-row">

      <!-- 餐厅选择 -->
      <el-select v-model="selectRestaurantId" placeholder="请选择餐厅" style="width:260px">
        <el-option 
          v-for="r in restaurantList" 
          :key="r.id"
          :label="r.name"
          :value="r.id"
        />
      </el-select>

      <el-button type="primary" @click="loadReviews">加载评价</el-button>

      <!-- 筛选 -->
      <el-select v-model="filterType" placeholder="筛选" style="width:150px">
        <el-option label="全部" value="all" />
        <el-option label="未回复" value="noReply" />
        <el-option label="已回复" value="replied" />
      </el-select>

    </div>

    <!-- === 表格 === -->
    <el-table :data="list" border style="width:100%; margin-top:20px;">

      <!-- 用户 -->
      <el-table-column label="用户" width="120">
        <template #default="{ row }">
          <span>{{ row.username }}</span>
        </template>
      </el-table-column>

      <!-- 评分 -->
      <el-table-column label="评分" prop="rating" width="80" />

      <!-- 内容 -->
      <el-table-column label="内容" prop="content" />

      <!-- 图片 -->
      <el-table-column label="图片" width="150">
        <template #default="{ row }">
          <el-image
            v-for="img in row.images"
            :key="img"
            :src="img"
            style="width:40px;height:40px;margin-right:6px;border-radius:6px;cursor:pointer"
            fit="cover"
            @click="openViewer(row.images, img)"
          />
        </template>
      </el-table-column>

      <!-- 回复内容 -->
      <el-table-column label="回复内容" width="260">
        <template #default="{ row }">
          <div v-if="row.replyContent">
            <p>{{ row.replyContent }}</p>
          </div>
          <div v-else style="color:#999">（未回复）</div>
        </template>
      </el-table-column>

      <!-- 回复时间 -->
      <<el-table-column label="回复时间" width="110">
        <template #default="{ row }">
          <span>{{ formatTime(row.replyTime) }}</span>
        </template>
      </el-table-column>

      <!-- 操作 -->
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button 
            type="primary" 
            size="small"
            @click="openReplyDialog(row)"
          >
            {{ row.replyContent ? '修改回复' : '回复' }}
          </el-button>

          <el-button 
            v-if="row.replyContent"
            type="danger"
            size="small"
            @click="deleteReply(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>

    </el-table>

    <el-image-viewer
      v-if="viewerVisible"
      :url-list="viewerList"
      :initial-index="viewerIndex"
      @close="viewerVisible = false"
    />

    <!-- ==== 回复弹窗 ==== -->
    <el-dialog 
      v-model="dialogVisible" 
      title="回复用户评价" 
      width="500px"
    >
      <el-input
        v-model="replyText"
        type="textarea"
        :rows="4"
        placeholder="请输入回复内容"
      />

      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitReply">提交</el-button>
      </template>
    </el-dialog>

  </div>
</template>


<script setup>
import { ref, onMounted, watch } from "vue";
import { useUserStore } from "@/store/user";
import http from "@/api/request";
import { ElMessage } from "element-plus";

const role = useUserStore().role;
const restaurantId = useUserStore().restaurantId;

const list = ref([]);
const restaurantList = ref([]);
const selectRestaurantId = ref(null);

const dialogVisible = ref(false);
const replyText = ref("");
let currentRow = null;

//  筛选状态
const filterType = ref("all");

const viewerVisible = ref(false);
const viewerList = ref([]);
const viewerIndex = ref(0);

function openViewer(list, img) {
  viewerList.value = list;
  viewerIndex.value = list.indexOf(img);
  viewerVisible.value = true;
}

// super 加载餐厅列表
const loadRestaurantList = () => {
  if (role !== "super") return;
  http.get("/restaurant/all").then(res => {
    restaurantList.value = res.data;
  });
};

// 加载评价（含筛选）
const loadReviews = () => {
  let rid = role === "merchant" ? restaurantId : selectRestaurantId.value;
  if (!rid) return ElMessage.warning("请选择餐厅");

  http.get("/review/list", { params: { restaurantId: rid } }).then(res => {
    let data = res.data;

    // 未回复
    if (filterType.value === "noReply") {
      data = data.filter(v => !v.replyContent);
    }

    // 已回复
    if (filterType.value === "replied") {
      data = data.filter(v => v.replyContent);
    }

    list.value = data;
  });
};

// 筛选变化 → 刷新数据
watch(filterType, () => {
  loadReviews();
});

// 打开弹窗
const openReplyDialog = row => {
  currentRow = row;
  replyText.value = row.replyContent || "";
  dialogVisible.value = true;
};

// 提交回复
const submitReply = () => {
  if (!replyText.value) return ElMessage.warning("请输入内容");

  http.post("/admin/merchant/review/reply", null, {
    params: {
      reviewId: currentRow.id,
      replyContent: replyText.value
    }
  }).then(() => {
    ElMessage.success("回复成功");
    dialogVisible.value = false;
    loadReviews();
  });
};

// 删除回复
const deleteReply = row => {
  http.post("/admin/merchant/review/reply/delete", null, {
    params: { reviewId: row.id }
  }).then(() => {
    ElMessage.success("删除成功");
    loadReviews();
  });
};

onMounted(() => {
  loadRestaurantList();
  if (role === "merchant") loadReviews();
});

function formatTime(t) {
  if (!t) return "—";
  return t.split("T")[0];   // 只显示日期
}
</script>


<style scoped>
.select-row {
  margin-bottom: 10px;
  display: flex;
  gap: 10px;
  align-items: center;
}
</style>

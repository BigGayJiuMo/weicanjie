<template>
  <div class="audit-container">
    <div>
      <h2>举报审核</h2>
    </div>
    <!-- SUPER：筛选栏 -->
    <div class="filter-row">

      <!-- 餐厅筛选 -->
      <el-select v-model="restaurantId" placeholder="选择餐厅" style="width:240px">
        <el-option
          v-for="r in restaurantList"
          :key="r.id"
          :label="r.name"
          :value="r.id"
        />
      </el-select>

      <!-- 举报状态 -->
      <el-select v-model="status" placeholder="审核状态" style="width:180px">
        <el-option label="全部" :value="-1" />
        <el-option label="待审核" :value="0" />
        <el-option label="已通过" :value="1" />
        <el-option label="已驳回" :value="2" />
      </el-select>

      <el-button type="primary" @click="loadReports" :loading="loadingList">
        查询
      </el-button>

    </div>

    <!-- 表格 -->
    <el-table :data="list" border v-loading="loadingList" style="margin-top:20px">

      <!-- 举报者 -->
      <el-table-column label="举报者" width="130">
        <template #default="{ row }">
          <div>{{ row.reporterName || "匿名用户" }}</div>
        </template>
      </el-table-column>

      <!-- 举报原因 -->
      <el-table-column label="原因" width="120">
        <template #default="{ row }">
          {{ reasonMap[row.reason] || row.reason }}
        </template>
      </el-table-column>

      <!-- 举报描述 -->
      <el-table-column label="举报描述" min-width="180" prop="reporterDetail" show-overflow-tooltip />

      <!-- 举报者图片列 -->
      <el-table-column label="举报图片" min-width="120" max-width="160">
        <template #default="{ row }">
          <el-image
            v-for="img in row.reportImages"
            :key="img"
            :src="img"
            style="width:42px;height:42px;margin-right:6px;border-radius:6px"
            @click="previewImages(row.reportImages)"
          />
        </template>
      </el-table-column>

      <!-- 被举报者 -->
      <el-table-column label="被举报者" width="130">
        <template #default="{ row }">
          <div>{{ row.username || "匿名用户" }}</div>
        </template>
      </el-table-column>

      <!-- 被举报内容 -->
      <el-table-column label="被举报内容" min-width="180">
        <template #default="{ row }">
          <div>{{ row.reviewContent }}</div>
        </template>
      </el-table-column>

      <!-- 原评价图片 -->
      <el-table-column label="原评价图片" min-width="120" max-width="160">
        <template #default="{ row }">
          <el-image
            v-for="img in row.reviewImages"
            :key="img"
            :src="img"
            style="width:42px;height:42px;margin-right:6px;border-radius:6px"
            @click="previewImages(row.reviewImages)"
          />
        </template>
      </el-table-column>

      <!-- 状态 -->
      <el-table-column label="审核状态" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
          <el-tag v-if="row.status === 1" type="success">通过</el-tag>
          <el-tag v-if="row.status === 2" type="danger">驳回</el-tag>
        </template>
      </el-table-column>

      <!-- 操作 -->
      <el-table-column label="操作" width="110">
        <template #default="{ row }">

          <!-- 通过 -->
          <el-button
            v-if="row.status === 0"
            type="success"
            link
            @click="approve(row)"
          >
            通过
          </el-button>

          <!-- 驳回 -->
          <el-button
            v-if="row.status === 0"
            type="danger"
            link
            @click="openRejectDialog(row)"
          >
            驳回
          </el-button>

          <!-- 查看处理结果 -->
          <el-button
            v-if="row.status !== 0"
            type="info"
            link
            @click="showResult(row)"
          >
            查看
          </el-button>

        </template>
      </el-table-column>

    </el-table>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="visibleViewer"
      :url-list="viewerImages"
      @close="visibleViewer = false"
    />

    <!-- 驳回弹窗 -->
    <el-dialog title="驳回举报" v-model="rejectDialogVisible" width="420">
      <el-input
        type="textarea"
        rows="4"
        v-model="rejectReason"
        placeholder="请输入驳回理由"
      />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="loadingReject" @click="submitReject">
          提交
        </el-button>
      </template>
    </el-dialog>

    <!-- 审核结果弹窗 -->
    <el-dialog title="审核结果" v-model="resultDialogVisible" width="480">
      <div style="white-space: pre-wrap; line-height: 1.6;">
        {{ resultCommentText || "无备注" }}
      </div>
      <template #footer>
        <el-button @click="resultDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import http from "@/api/request";
import { ElMessage } from "element-plus";

const reasonMap = {
  fake: "虚假评价",
  malicious: "恶意差评",
  insult: "侮辱攻击",
  ads: "广告/违规内容",
  privacy: "涉及隐私"
};

const restaurantList = ref([]);
const list = ref([]);

const restaurantId = ref(null);
const status = ref(-1);

const loadingList = ref(false);
const loadingReject = ref(false);

const visibleViewer = ref(false);
const viewerImages = ref([]);

const rejectDialogVisible = ref(false);
const rejectReason = ref("");
let currentRejectId = null;

// 新增：查看结果弹窗相关
const resultDialogVisible = ref(false);
const resultCommentText = ref("");

/* 加载餐厅 */
async function loadRestaurantList() {
  const res = await http.get("/restaurant/all");
  restaurantList.value = res.data;
}

/* 加载举报列表 */
async function loadReports() {
  loadingList.value = true;

  try {
    const res = await http.get("/admin/review/report/list", {
      params: {
        restaurantId: restaurantId.value,
        status: status.value
      }
    });

    // 解析图片 JSON
    list.value = res.data.map(item => {
      // 处理原评价图片（ur.image_urls）
      if (item.image_urls) {
        if (typeof item.image_urls === 'string') {
          try {
            item.reviewImages = JSON.parse(item.image_urls);
          } catch (e) {
            item.reviewImages = [];
          }
        } else if (Array.isArray(item.image_urls)) {
          item.reviewImages = item.image_urls; // 已经是数组
        } else {
          item.reviewImages = [];
        }
      } else {
        item.reviewImages = [];
      }

      // 处理举报者图片（reportImages）
      if (item.reportImages) {
        if (typeof item.reportImages === 'string') {
          try {
            item.reportImages = JSON.parse(item.reportImages);
          } catch (e) {
            item.reportImages = [];
          }
        } else if (Array.isArray(item.reportImages)) {
          // 已经是数组，无需处理
        } else {
          item.reportImages = [];
        }
      } else {
        item.reportImages = [];
      }

      return item;
    });

  } catch (err) {
    ElMessage.error("加载失败");
  }

  loadingList.value = false;
}

/* 通过举报 */
async function approve(row) {
  try {
    await http.post("/admin/review/report/audit", null, {
      params: {
        id: row.id,
        status: 1,          // 通过
        reviewAction: 1     // 隐藏评价
      }
    });

    ElMessage.success("举报已处理");
    loadReports();

  } catch (err) {
    ElMessage.error("操作失败");
  }
}

/* 打开驳回弹窗 */
function openRejectDialog(row) {
  currentRejectId = row.id;
  rejectReason.value = "";
  rejectDialogVisible.value = true;
}

/* 提交驳回 */
async function submitReject() {
  if (!rejectReason.value.trim()) {
    return ElMessage.warning("请输入理由");
  }

  loadingReject.value = true;

  try {
    await http.post("/admin/review/report/audit", null, {
      params: {
        id: currentRejectId,
        status: 2,
        resultComment: rejectReason.value
      }
    });

    ElMessage.success("已驳回");
    rejectDialogVisible.value = false;
    loadReports();

  } catch (err) {
    ElMessage.error("操作失败");
  }

  loadingReject.value = false;
}

/* 查看处理结果 —— 改为弹窗显示 */
function showResult(row) {
  // 兼容字段命名：result_comment 或 resultComment
  const comment = row.result_comment || row.resultComment || "无备注";
  resultCommentText.value = comment;
  resultDialogVisible.value = true;
}

/* 预览图片 */
function previewImages(list) {
  viewerImages.value = list;
  visibleViewer.value = true;
}

onMounted(() => {
  loadRestaurantList();
});
</script>

<style scoped>
.filter-row {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}
.user-info {
  display: flex;
  align-items: center;
}
.avatar {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  margin-right: 6px;
}
</style>
<template>
    <div class="audit-container">
      <div>
        <h2>评价审核</h2>
      </div>
      <!-- SUPER：筛选栏 -->
      <div class="filter-row">
        <el-select v-model="restaurantId" placeholder="选择餐厅" style="width:240px">
          <el-option
            v-for="r in restaurantList"
            :key="r.id"
            :label="r.name"
            :value="r.id"
          />
        </el-select>
  
        <el-select v-model="reviewStatus" placeholder="审核状态" style="width:180px">
          <el-option label="全部" :value="-1" />
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已拒绝" :value="2" />
        </el-select>
  
        <el-button type="primary" @click="loadReviews" :loading="loadingList">查询</el-button>
      </div>
  
      <!-- 表格 -->
      <el-table :data="list" border style="margin-top:20px" v-loading="loadingList">
  
        <!-- 用户信息 -->
        <el-table-column label="用户" width="150">
          <template #default="{ row }">
            <div class="user-info">
              <span>{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
  
        <el-table-column label="评分" prop="rating" width="80" />
        <el-table-column label="内容" prop="content" />
  
        <!-- 图片 -->
        <el-table-column label="图片" width="160">
          <template #default="{ row }">
            <el-image
              v-for="img in row.images"
              :key="img"
              :src="img"
              style="width:42px;height:42px;margin-right:6px;border-radius:4px;cursor:pointer"
              @click="previewImages(row.images)"
              fit="cover"
            />
          </template>
        </el-table-column>
  
        <!-- 状态 -->
        <el-table-column label="审核状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.reviewStatus === 0" type="warning">待审核</el-tag>
            <el-tag v-if="row.reviewStatus === 1" type="success">已通过</el-tag>
            <el-tag v-if="row.reviewStatus === 2" type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="显示状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.displayStatus === 1" type="success">正常显示</el-tag>
            <el-tag v-if="row.displayStatus === 0" type="danger">已隐藏</el-tag>
          </template>
        </el-table-column>
        <!-- 操作 -->
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
  
            <!-- 通过 -->
            <el-button
              type="success"
              link
              @click="approve(row)"
              v-if="row.reviewStatus === 0"
              :loading="loadingApprove"
            >
              通过
            </el-button>
  
            <!-- 拒绝 -->
            <el-button
              type="danger"
              link
              @click="openRejectDialog(row)"
              v-if="row.reviewStatus === 0"
            >
              拒绝
            </el-button>
  
            <!-- 查看原因 -->
            <el-button
              v-if="row.reviewStatus === 2 && row.rejectReason"
              type="info"
              link
              @click="showReason(row)"
            >
              查看原因
            </el-button>

            <!-- 删除 -->
            <el-button
              type="danger"
              link
              v-if="row.reviewStatus !== 0"
              @click="deleteReview(row)"
            >
              删除
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
  
      <!-- 拒绝弹窗 -->
      <el-dialog title="拒绝评价" v-model="rejectDialogVisible" width="420">
        <el-input
          type="textarea"
          rows="4"
          v-model="rejectReason"
          placeholder="请输入拒绝理由"
        />
        <template #footer>
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="danger" :loading="loadingReject" @click="submitReject">
            提交
          </el-button>
        </template>
      </el-dialog>
  
    </div>
  </template>
  
  <script setup>
    import { ref, onMounted } from "vue";
    import http from "@/api/request";
    import { ElMessage, ElMessageBox } from "element-plus";  // ⭐必须加上 ElMessageBox
    
    const list = ref([]);
    const restaurantList = ref([]);
    
    const restaurantId = ref(null);
    const reviewStatus = ref(-1);
    
    const loadingList = ref(false);
    const loadingApprove = ref(false);
    const loadingReject = ref(false);
    
    const visibleViewer = ref(false);
    const viewerImages = ref([]);
    
    const rejectDialogVisible = ref(false);
    const rejectReason = ref("");
    let currentRejectId = null;
    
    // 加载餐厅
    async function loadRestaurantList() {
      try {
        const res = await http.get("/restaurant/all");
        restaurantList.value = res.data;
      } catch (err) {
        ElMessage.error("加载餐厅失败");
      }
    }
    
    // 加载审核列表
    async function loadReviews() {
      loadingList.value = true;
    
      try {
        const res = await http.get("/admin/review/list", {
          params: {
            restaurantId: restaurantId.value,
            reviewStatus: reviewStatus.value,
          },
        });
    
        list.value = res.data;
      } catch (err) {
        ElMessage.error("加载评价失败");
      } finally {
        loadingList.value = false;
      }
    }
    
    /* 审核通过 */
    async function approve(row) {
      loadingApprove.value = true;
    
      try {
        await http.post("/admin/review/audit", null, {
          params: { reviewId: row.id, reviewStatus: 1 },
        });
    
        ElMessage.success("审核通过");
        loadReviews();
    
      } catch (err) {
        ElMessage.error("审核失败");
      } finally {
        loadingApprove.value = false;
      }
    }
    
    /* 删除评价（超级管理员） */
    async function deleteReview(row) {
      ElMessageBox.confirm(
        "确定要删除此评价吗？删除后不可恢复！",
        "警告",
        { type: "warning" }
      )
        .then(async () => {
          try {
            await http.post(`/admin/review/delete/${row.id}`);
            ElMessage.success("删除成功");
            loadReviews();
          } catch (err) {
            ElMessage.error("删除失败");
          }
        })
        .catch(() => {});
    }
    
    /* 打开拒绝弹窗 */
    function openRejectDialog(row) {
      currentRejectId = row.id;
      rejectReason.value = "";
      rejectDialogVisible.value = true;
    }
    
    /* 提交拒绝 */
    async function submitReject() {
      if (!rejectReason.value.trim()) {
        return ElMessage.warning("请输入拒绝理由");
      }
    
      loadingReject.value = true;
    
      try {
        await http.post("/admin/review/audit", null, {
          params: {
            reviewId: currentRejectId,
            reviewStatus: 2,
            rejectReason: rejectReason.value,
          },
        });
    
        ElMessage.success("已拒绝该评价");
        rejectDialogVisible.value = false;
        loadReviews();
    
      } catch (err) {
        ElMessage.error("提交拒绝失败");
      } finally {
        loadingReject.value = false;
      }
    }
    
    /* 查看拒绝理由 */
    function showReason(row) {
      ElMessage.info(`拒绝原因：${row.rejectReason}`);
    }
    
    /* 图片预览 */
    function previewImages(imgList) {
      viewerImages.value = imgList;
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
    object-fit: cover;
  }
  </style>
  
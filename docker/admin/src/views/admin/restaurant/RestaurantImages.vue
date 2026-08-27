<template>
  <el-dialog
    v-model="visible"
    title="餐厅图片管理"
    width="640px"
    append-to-body
    :lock-scroll="false"
  >
    <div class="upload-section" v-if="canUpload">
      <el-upload
        action="/upload/image"
        :data="uploadData"
        :show-file-list="false"
        :on-success="uploadSuccess"
        :before-upload="beforeUpload"
        :disabled="!canManageImages"
      >
        <el-button type="primary" :disabled="!canManageImages">
          上传图片 ({{ images.length }}/3)
        </el-button>
        <div class="upload-tips">
          最多可上传3张门店展示图片，支持jpg、png格式，单张不超过2MB
        </div>
      </el-upload>
    </div>

    <div v-if="!canUpload && canManageImages" class="max-tip">
      <el-alert type="info" :closable="false">
        已达到最大上传数量（3张），如需上传新图片，请先删除旧图片
      </el-alert>
    </div>

    <div class="img-list" v-if="images.length > 0">
      <div class="item" v-for="img in images" :key="img.id">
        <div class="img-container">
          <img :src="img.imageUrl" @click="openViewer(images.map(i=>i.imageUrl), img.imageUrl)" />
          <div class="img-actions" v-if="canManageImages">
            <el-button
              type="danger"
              size="small"
              @click="remove(img.id)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <el-empty v-if="images.length === 0" description="暂无门店展示图片" />

    <el-image-viewer
      v-if="viewerVisible"
      :url-list="viewerList"
      :initial-index="viewerIndex"
      @close="viewerVisible = false"
    />

  </el-dialog>
</template>

<script setup>
import { ref, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus"; // 引入ElMessageBox
import { listImages, addImage, deleteImage } from "@/api/restaurant";
import { useUserStore } from "@/store/user";

const userStore = useUserStore();

const visible = ref(false);
const restaurant = ref({});
const images = ref([]);

const viewerVisible = ref(false);
const viewerList = ref([]);
const viewerIndex = ref(0);

// 判断用户是否有权限管理图片
const canManageImages = computed(() => {
  return userStore.role === 'super' || userStore.role === 'merchant';
});

const canUpload = computed(() => {
  return restaurant.value?.id && images.value.length < 3;
});

const uploadData = computed(() => {
  if (!restaurant.value?.id) return {};
  return {
    restaurantId: restaurant.value.id,
    type: "store"
  };
});

function openViewer(list, img) {
  viewerList.value = list;
  viewerIndex.value = list.indexOf(img);
  viewerVisible.value = true;
}

// 上传前验证
function beforeUpload(file) {
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!');
    return false;
  }
  
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB!');
    return false;
  }
  
  return true;
}

function open(row) {
  restaurant.value = row;
  visible.value = true;

  // 权限验证 - 商家只能管理自己的餐厅
  if (userStore.role === 'merchant') {
    // 从用户信息中获取餐厅ID
    if (userStore.restaurantId && userStore.restaurantId !== row.id) {
      ElMessage.warning('您只能管理自己的餐厅图片');
      visible.value = false;
      return;
    }
  }

  setTimeout(() => {
    if (restaurant.value?.id) load();
  }, 50);
}

function load() {
  if (!restaurant.value?.id) return;

  listImages(restaurant.value.id).then(res => {
    images.value = res.data || [];
  });
}

function uploadSuccess(res) {
  addImage({
    restaurantId: restaurant.value.id,
    imageUrl: res.data,
    sortOrder: images.value.length + 1,
  }).then(() => {
    ElMessage.success("上传成功");
    load();
  });
}

function remove(id) {
  ElMessageBox.confirm('确定删除这张图片吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteImage(id).then(() => {
      ElMessage.success("删除成功");
      load();
    });
  }).catch(() => {
    // 用户取消删除
  });
}

defineExpose({ open });
</script>

<style scoped>
.upload-section {
  margin-bottom: 20px;
}

.upload-tips {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
  margin-left: 20px;
}

.max-tip {
  margin-bottom: 20px;
}

.img-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  gap: 20px;
  margin-top: 20px;
}

.item {
  width: 180px;
}

.img-container {
  position: relative;
  width: 180px;
  height: 120px;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.img-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.3s;
}

.img-container img:hover {
  transform: scale(1.05);
}

.img-actions {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.6);
  padding: 8px;
  text-align: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.img-container:hover .img-actions {
  opacity: 1;
}
</style>
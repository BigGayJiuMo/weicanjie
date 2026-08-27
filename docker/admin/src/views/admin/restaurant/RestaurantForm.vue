<template>
  <el-dialog
    v-model="visible"
    :title="form.id ? '编辑餐厅' : '新增餐厅'"
    width="600px"
    append-to-body
    :lock-scroll="false"
  >
    <el-form :model="form" label-width="90px">

      <el-form-item label="名称">
        <el-input v-model="form.name" />
      </el-form-item>

      <el-form-item label="分类">
        <el-select
          v-model="form.categoryType"
          placeholder="请选择分类"
          style="width: 200px"
          :disabled="userStore.role !== 'super'"
        >
          <el-option
            v-for="item in categoryList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="描述">
        <el-input v-model="form.description" type="textarea" />
      </el-form-item>

      <el-form-item label="电话">
        <el-input v-model="form.contactPhone" />
      </el-form-item>

      <el-form-item label="地址">
        <el-input v-model="form.address" />
      </el-form-item>

      <el-form-item label="Logo">
        <div v-if="!form.id" style="color: #999;">
          请先保存餐厅，再上传 Logo
        </div>

        <el-upload
          v-else
          class="logo-uploader"
          action="/api/upload/image"
          :data="{ restaurantId: form.id, type: 'logo' }"
          :show-file-list="false"
          :on-success="uploadSuccess"
          :disabled="userStore.role !== 'super'"
        >
          <img v-if="form.logoUrl" :src="form.logoUrl" class="logo" />
          <el-icon v-else class="upload-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>

      <el-form-item label="包装费">
        <el-input-number v-model="form.packingFee" :min="0" :step="0.5" />
      </el-form-item>

      <el-form-item label="营业">
        <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
      </el-form-item>

    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submit">保存</el-button>
    </template>

  </el-dialog>
</template>

<script setup>
  import { ref, onMounted } from "vue";
  import { addRestaurant, updateRestaurant, getRestaurantCategoryList } from "@/api/restaurant";
  import { ElMessage } from "element-plus";
  import { Plus } from "@element-plus/icons-vue";
  import { useUserStore } from "@/store/user";
  
  const userStore = useUserStore();
  
  const visible = ref(false);
  
  const form = ref({
    id: null,
    name: "",
    categoryType: null,
    description: "",
    contactPhone: "",
    address: "",
    logoUrl: "",
    packingFee: 0,
    status: 1
  });
  
  const emit = defineEmits(["refresh"]);
  
  /* 打开餐厅弹窗 */
  function open(row) {
    visible.value = true;
  
    if (row) {
      form.value = { ...row };
    } else {
      form.value = {
        id: null,
        name: "",
        categoryType: null,
        description: "",
        contactPhone: "",
        address: "",
        logoUrl: "",
        packingFee: 0,
        status: 1
      };
    }
  }
  
  /* 上传 Logo 回调 */
  function uploadSuccess(res) {
    form.value.logoUrl = res.data;
  }
  
  /* ---------- 加载分类列表 ---------- */
  const categoryList = ref([]);
  
  function loadCategoryList() {
    getRestaurantCategoryList().then(res => {
      categoryList.value = res.data || [];
    });
  }
  
  /* ---------- 保存餐厅 ---------- */
  async function submit() {
    try {
      const isAdd = !form.value.id;
      const api = isAdd ? addRestaurant : updateRestaurant;
  
      const res = await api(form.value);
  
      if (isAdd && res.data) {
        form.value.id = res.data;
      }
  
      ElMessage.success("保存成功");
      visible.value = false;
      emit("refresh");
  
    } catch (e) {
      console.error(e);
      ElMessage.error("保存失败，请查看控制台");
    }
  }
  
  onMounted(() => {
    loadCategoryList();
  });
  
  defineExpose({ open });
  </script>
<style scoped>
.logo-uploader {
  width: 90px;
  height: 90px;
}

.logo {
  width: 90px;
  height: 90px;
  border-radius: 6px;
  object-fit: cover;
}

.upload-icon {
  width: 90px;
  height: 90px;
  border: 1px dashed #ccc;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>

<template>
  <div>
    <!-- ================= SUPER 管理员：账号管理 ================= -->
    <div v-if="role === 'super'">
      <div>
        <h2>商家账号管理</h2>
      </div>
      <div class="toolbar">
        <el-input
          v-model="keyword"
          placeholder="搜索账号 / 用户名"
          clearable
          style="width: 220px; margin-right: 12px;"
          @input="loadData"
        />
        <el-button type="primary" @click="openForm()" v-if="role === 'super'">新增账号</el-button>
      </div>

      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="账号" width="180" />
        <el-table-column prop="role" label="类型" width="120">
          <template #default="{ row }">
            <el-tag type="success" v-if="row.role === 'merchant'">商家</el-tag>
            <el-tag type="warning" v-else-if="row.role === 'kitchen'">后厨</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="restaurantId" label="餐厅ID" width="120" />

        <el-table-column label="操作" width="240" v-if="role === 'super'">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
            <el-button size="small" @click="resetPwd(row.id)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 创建账号弹窗 -->
      <el-dialog
        v-model="visible"
        title="新增账号"
        width="420px"
        class="merchant-dialog"
        :close-on-click-modal="false"
        append-to-body
      >
        <el-form :model="form" :rules="rules" ref="formRef" label-width="90px" class="dialog-form">

          <!-- 账号 -->
          <el-form-item label="账号" prop="username">
            <el-input
              v-model="form.username"
              maxlength="20"
              clearable
              placeholder="以字母开头，只能字母数字（4-20 位）"
              @input="onUsernameInput"
            />
          </el-form-item>

          <!-- 密码 -->
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              maxlength="20"
              clearable
              placeholder="请输入密码"
            />
          </el-form-item>

          <!-- 餐厅 ID -->
          <el-form-item label="餐厅ID" prop="restaurantId">
            <el-input
              v-model="form.restaurantId"
              maxlength="10"
              clearable
              placeholder="输入餐厅ID（数字）"
              @input="form.restaurantId = form.restaurantId.replace(/\D/g, '')"
            />
          </el-form-item>

          <!-- 账号类型 -->
          <el-form-item label="账号类型" prop="role">
            <el-select v-model="form.role" placeholder="请选择类型">
              <el-option label="商家账号" value="merchant" />
              <el-option label="后厨账号" value="kitchen" />
            </el-select>
          </el-form-item>

        </el-form>

        <template #footer>
          <div class="dialog-footer">
            <el-button @click="visible = false">取消</el-button>
            <el-button type="primary" @click="submit">保存</el-button>
          </div>
        </template>

      </el-dialog>
    </div>

    <!-- ================= 商家 / 后厨：账号设置 ================= -->
    <div v-else>
      <div class="toolbar">
        <h2>账号设置</h2>
      </div>

      <!-- 只显示当前登录用户 -->
      <el-table :data="[selfAccount]" border>

        <el-table-column prop="id" label="ID" width="70" />

        <el-table-column prop="username" label="账号" width="180" />

        <el-table-column prop="phone" label="手机号" width="180">
          <template #default="{ row }">
            <el-tag type="info" v-if="!row.phone">未绑定</el-tag>
            <el-tag type="success" v-else>{{ row.phone }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="300">
          <template #default="{ row }">

            <!-- 修改密码 -->
            <el-button size="small" @click="openPwdDialog">修改密码</el-button>

            <!-- 绑定 or 修改手机号 -->
            <el-button size="small" type="primary" @click="openPhoneDialog">
              {{ row.phone ? "修改手机号" : "绑定手机号" }}
            </el-button>

          </template>
        </el-table-column>
      </el-table>

      <!-- ========== 修改密码弹窗 ========== -->
      <el-dialog v-model="pwdVisible" title="验证码修改密码" width="420px">
        <el-form :model="pwdForm" label-width="90px">

          <el-form-item label="手机号">
            <el-input :value="selfAccount.phone" disabled />
          </el-form-item>

          <el-form-item label="验证码">
            <div style="display:flex; gap:10px;">
              <el-input v-model="pwdForm.code" />
              <el-button 
                :disabled="pwdCountdown > 0"
                @click="sendPwdCode">
                {{ pwdCountdown > 0 ? pwdCountdown + '秒' : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>

          <el-form-item label="新密码">
            <el-input v-model="pwdForm.newPwd" type="password" />
          </el-form-item>

        </el-form>

        <template #footer>
          <el-button @click="pwdVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPassword">保存</el-button>
        </template>
      </el-dialog>

      <!-- ========== 绑定/修改手机号弹窗 ========== -->
      <el-dialog v-model="phoneVisible" title="绑定手机号" width="420px">
        <el-form :model="phoneForm" label-width="90px">

          <el-form-item label="手机号">
            <el-input v-model="phoneForm.phone" maxlength="11" />
          </el-form-item>

          <el-form-item label="验证码">
            <div style="display:flex; gap:10px;">
              <el-input v-model="phoneForm.code" />
              <el-button 
                :disabled="phoneCountdown > 0"
                @click="sendPhoneCode">
                {{ phoneCountdown > 0 ? phoneCountdown + '秒' : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>

        </el-form>

        <template #footer>
          <el-button @click="phoneVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPhone">保存</el-button>
        </template>
      </el-dialog>
    </div>

  </div>
</template>


<script setup>
  import { ElMessage, ElMessageBox } from "element-plus";
  import { ref, onMounted } from "vue";
  import {
    getMerchantList,
    createMerchant,
    deleteMerchant,
    resetMerchantPassword,
    sendCode,
    changePasswordByCode,
    bindPhoneByCode
  } from "@/api/admin.js";
  
  import { useUserStore } from "@/store/user";
  const keyword = ref("");
  // 登录用户信息
  const userStore = useUserStore();
  const role = userStore.role;
  
  // SUPER 管理员数据
  const tableData = ref([]);
  const visible = ref(false);
  
  // 表单引用
  const formRef = ref();
  
  // 新增账号表单
  const form = ref({
    username: "",
    password: "",
    restaurantId: "",
    role: "merchant",
  });
  
  // 输入过滤
  function onUsernameInput() {
    form.value.username = form.value.username.replace(/[^a-zA-Z0-9]/g, "");
  }
  
  // 校验规则
  const rules = {
    username: [
      { required: true, message: "请输入账号", trigger: "blur" },
      {
        pattern: /^[a-zA-Z][a-zA-Z0-9]{3,19}$/,
        message: "账号必须以字母开头，只能字母数字（4–20 位）",
        trigger: "blur",
      },
    ],
    password: [{ required: true, message: "请输入密码", trigger: "blur" }],
    role: [{ required: true, message: "请选择账号类型", trigger: "change" }],
    restaurantId: [{ required: true, message: "请输入餐厅ID", trigger: "blur" }],
  };
  
  // 打开新增弹窗
  function openForm() {
    form.value = {
      username: "",
      password: "",
      restaurantId: "",
      role: "merchant",
    };
    visible.value = true;
  }
  
  // 加载数据
  function loadData() {
    if (role === "super") {
      getMerchantList({ keyword: keyword.value }).then((res) => {
        tableData.value = res.data;
      });
    }
  }
  
  // 提交新增账号
  function submit() {
    formRef.value.validate((valid) => {
      if (!valid) return;
  
      createMerchant(form.value).then(() => {
        ElMessage.success("创建成功");
        visible.value = false;
        loadData();
      });
    });
  }
  
  // 删除账号
  function remove(id) {
    ElMessageBox.confirm("确定删除该账号？", "提示", {
      type: "warning",
      lockScroll: false,
    }).then(() => {
      deleteMerchant(id).then(() => {
        ElMessage.success("删除成功");
        loadData();
      });
    });
  }
  
  // 重置密码
  function resetPwd(id) {
    resetMerchantPassword(id).then(() => {
      ElMessage.success("密码已重置为 123456");
    });
  }
  
  /* =====================
     商家 / 后厨：账号设置区
   ===================== */
  const selfAccount = ref({
    id: userStore.userId,
    username: userStore.username,
    phone: userStore.phone || null,
  });
  
  /* ====== 修改密码弹窗 ====== */
  const pwdVisible = ref(false);
  const pwdForm = ref({
    code: "",
    newPwd: "",
  });
  let pwdCountdown = ref(0);
  
  function openPwdDialog() {
    pwdForm.value = { code: "", newPwd: "" };
    pwdVisible.value = true;
  }
  
  // 发送验证码
  function sendPwdCode() {
    if (!selfAccount.value.phone) {
      return ElMessage.error("请先绑定手机号");
    }
  
    sendCode({ phone: selfAccount.value.phone }).then((res) => {
      const code = res.data.code; // ✔ 正确取法

      ElMessage.success("验证码已发送（模拟）：" + code);

      pwdForm.value.code = code;

      pwdCountdown.value = 60;
      const timer = setInterval(() => {
        pwdCountdown.value--;
        if (pwdCountdown.value <= 0) clearInterval(timer);
      }, 1000);
    });
  }
  
  // 提交修改密码
  function submitPassword() {
    const data = {
      phone: selfAccount.value.phone,
      code: pwdForm.value.code,
      newPwd: pwdForm.value.newPwd,
    };
  
    changePasswordByCode(data).then(() => {
      ElMessage.success("密码修改成功");
      pwdVisible.value = false;
    });
  }
  
  /* ======= 修改/绑定 手机号 ======= */
  const phoneVisible = ref(false);
  const phoneForm = ref({
    phone: "",
    code: "",
  });
  let phoneCountdown = ref(0);
  
  function openPhoneDialog() {
    phoneForm.value.phone = selfAccount.value.phone || "";
    phoneForm.value.code = "";
    phoneVisible.value = true;
  }
  
  // 发送验证码（绑定手机用）
  function sendPhoneCode() {
    if (!phoneForm.value.phone) {
      return ElMessage.error("请输入手机号");
    }

    sendCode({ phone: phoneForm.value.phone }).then((res) => {
      const code = res.data.code; // ✔ 正确取法

      ElMessage.success("验证码已发送（模拟）：" + code);
      phoneForm.value.code = code;

      phoneCountdown.value = 60;
      const timer = setInterval(() => {
        phoneCountdown.value--;
        if (phoneCountdown.value <= 0) clearInterval(timer);
      }, 1000);
    });
  }
  
  // 提交手机号绑定
  function submitPhone() {
    const data = {
      phone: phoneForm.value.phone,
      code: phoneForm.value.code,
    };
  
    bindPhoneByCode(data).then(() => {
      ElMessage.success("手机号已更新");
      selfAccount.value.phone = phoneForm.value.phone;
      userStore.phone = phoneForm.value.phone;
      phoneVisible.value = false;
    });
  }
  
  onMounted(loadData);
</script>  

<style scoped>
.toolbar {
  margin-bottom: 15px;
}

.merchant-dialog .el-dialog {
  border-radius: 10px;
  padding: 0;
}

.merchant-dialog .el-dialog__header {
  padding: 18px 20px;
  border-bottom: 1px solid #eee;
}

.dialog-form {
  width: 350px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.dialog-footer {
  padding: 10px 20px 20px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>

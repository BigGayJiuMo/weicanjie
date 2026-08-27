<template>
  <el-dialog
    v-model="visible"
    title="营业时间设置"
    width="600px"
    append-to-body
    :lock-scroll="false"
  >
    <el-table :data="hours" border>

      <el-table-column label="星期" width="120">
        <template #default="{ row }">
          周{{ row.dayOfWeek }}
        </template>
      </el-table-column>

      <el-table-column label="营业" width="80">
        <template #default="{ row }">
          <el-switch v-model="row.isOpen" :active-value="1" :inactive-value="0" />
        </template>
      </el-table-column>

      <el-table-column label="开门时间">
        <template #default="{ row }">
          <el-time-picker
            v-model="row.openTime"
            format="HH:mm"
            value-format="HH:mm"
            size="small"
            style="width: 90px"
            :editable="false"
            :clearable="false"
          />
        </template>
      </el-table-column>

      <el-table-column label="关门时间">
        <template #default="{ row }">
          <el-time-picker
            v-model="row.closeTime"
            format="HH:mm"
            value-format="HH:mm"
            size="small"
            style="width: 90px"
            :editable="false"
            :clearable="false"
          />
        </template>
      </el-table-column>

    </el-table>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="save">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from "vue";
import { ElMessage } from "element-plus";
import request from "@/api/request";

const visible = ref(false);
const restaurantId = ref(null);
const hours = ref([]);

function open(id) {
restaurantId.value = id;
visible.value = true;
load();
}

function load() {
request({
  url: `/admin/restaurant/business/list/${restaurantId.value}`,
  method: "get",
}).then(res => {
  hours.value = res.data;
});
}

function save() {
const tasks = hours.value.map(h => {

  const api = h.id ? "/admin/restaurant/business/update"
                  : "/admin/restaurant/business/add";

  return request({
    url: api,
    method: "post",
    data: h,
  });

});

Promise.all(tasks).then(() => {
  ElMessage.success("营业时间已保存");
  visible.value = false;
});
}

defineExpose({ open });
</script>

<template>
  <el-dialog
    :model-value="visible"
    title="订单备注"
    width="400px"
    @update:model-value="val => $emit('update:visible', val)"
  >
    <div style="font-size: 15px; white-space: pre-wrap;">
      {{ remark }}
    </div>
  </el-dialog>
</template>

<script setup>
defineProps({
  visible: Boolean,
  remark: String
});

defineEmits(['update:visible']);
</script>
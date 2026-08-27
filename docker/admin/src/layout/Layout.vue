<template>
  <div class="layout-container">
    <Sidebar />

    <div class="main-wrapper">
      <Header />
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import Sidebar from "./components/Sidebar.vue";
import Header from "./components/Header.vue";
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  width: 100vw; 
  overflow: hidden; 
  position: fixed; 
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f5f6f8;
  height: 100vh;
  overflow: hidden; 
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  overflow-x: hidden; 
  max-height: calc(100vh - 100px);
}
</style>
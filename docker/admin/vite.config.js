import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    }
  },

  server: {
    proxy: {
      "/upload": {
        target: "http://localhost:8080/api",
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/upload/, "/upload")
      },
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true
      }
    }
  }
})

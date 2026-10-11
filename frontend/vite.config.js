import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { fileURLToPath } from 'node:url'
// https://vite.dev/config/
export default defineConfig({
  base: './',
  plugins: [
    vue(),
    // 自动引入 Vue 的常用 API（ref / reactive / computed 等），不用每次 import
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    // 自动注册 Element Plus 的组件，<el-button> 直接就能用
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})

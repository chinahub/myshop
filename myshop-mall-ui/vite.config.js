import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// 开发模式：页面由 Vite 提供（5174，base=/mall/），/mall 代理到 Summer 后端（8080）。
// 生产模式：npm run sync 构建并把 dist 同步到后端 resources/static/mall，访问 http://localhost:8080/mall/。
export default defineConfig({
  plugins: [react()],
  base: '/mall/',
  server: {
    port: 5174,
    proxy: {
      '/mall': 'http://localhost:8080',
    },
  },
  build: {
    outDir: 'dist',
  },
})

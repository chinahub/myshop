import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// 开发模式：页面由 Vite 提供（5173，base=/admin/），/items /users /listDemo 代理到 Summer 后端（8080）。
// 生产模式：npm run sync 构建并把 dist 同步到后端 resources/static/admin，由 Summer 一体托管，
// 访问路径 http://localhost:8080/admin/（HashRouter，根路径 / 是门户页）。
export default defineConfig({
  plugins: [react()],
  base: '/admin/',
  server: {
    port: 5173,
    proxy: {
      '/items': 'http://localhost:8080',
      '/users': 'http://localhost:8080',
      '/listDemo': 'http://localhost:8080',
      '/showData': 'http://localhost:8080',
    },
  },
  build: {
    outDir: 'dist',
  },
})

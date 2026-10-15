import { cpSync, mkdirSync, rmSync, existsSync } from 'node:fs'
import { join, dirname } from 'node:path'
import { fileURLToPath } from 'node:url'

/**
 * 把 Vite 构建产物 dist 同步到后端 Summer 静态资源目录的 admin/ 子目录，
 * 实现"前后端一体"：myshop-manager-boot.jar 内嵌前端页面（访问 /admin/）。
 * 注意：static 根下还有手写门户页 index.html 与商城 static/mall，只能清空本子目录，不能清空 static 根。
 * 用法：npm run sync（先 vite build 再执行本脚本）。
 */
const root = dirname(dirname(fileURLToPath(import.meta.url)))
const source = join(root, 'dist')
const staticRoot = join(root, '..', 'myshop-backend', 'myshop-manager', 'myshop-manager-web', 'src', 'main', 'resources', 'static')
const target = join(staticRoot, 'admin')

if (!existsSync(source)) {
  console.error('dist 不存在，请先执行 npm run build')
  process.exit(1)
}
rmSync(target, { recursive: true, force: true })
mkdirSync(target, { recursive: true })
cpSync(source, target, { recursive: true })
console.log('已同步 dist ->', target)

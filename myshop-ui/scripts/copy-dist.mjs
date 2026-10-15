import { cpSync, rmSync, existsSync } from 'node:fs'
import { join, dirname } from 'node:path'
import { fileURLToPath } from 'node:url'

/**
 * 把 Vite 构建产物 dist 同步到后端 Summer 静态资源目录，
 * 实现"前后端一体"：myshop-manager-boot.jar 内嵌前端页面。
 * 用法：npm run sync（先 vite build 再执行本脚本）。
 */
const root = dirname(dirname(fileURLToPath(import.meta.url)))
const source = join(root, 'dist')
const target = join(root, '..', 'myshop-manager', 'myshop-manager-web', 'src', 'main', 'resources', 'static')

if (!existsSync(source)) {
  console.error('dist 不存在，请先执行 npm run build')
  process.exit(1)
}
rmSync(target, { recursive: true, force: true })
cpSync(source, target, { recursive: true })
console.log('已同步 dist ->', target)

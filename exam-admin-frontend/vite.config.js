import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/user': {
        target: 'http://localhost:8079',
        changeOrigin: true
      },
      '/courses': {
        target: 'http://localhost:8079',
        changeOrigin: true
      },
      '/classes': {
        target: 'http://localhost:8079',
        changeOrigin: true
      },
      '/exam': {
        target: 'http://localhost:8087',
        changeOrigin: true
      },
      '/tags': {
        target: 'http://localhost:8087',
        changeOrigin: true
      },
      '/oauth': {
        target: 'http://localhost:10003',
        changeOrigin: true
      },
      '/auth': {
        target: 'http://localhost:10003',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/auth/, '')
      },
      '/message': {
        target: 'http://localhost:8083',
        changeOrigin: true
      }
    }
  }
})

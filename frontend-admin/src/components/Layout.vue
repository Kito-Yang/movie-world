<template>
  <el-container class="layout-container">
    <el-aside width="240px" class="sidebar">
      <div class="logo">
        <div class="logo-icon">
          <el-icon><VideoCamera /></el-icon>
        </div>
        <h3>电影世界管理系统</h3>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="transparent"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        class="sidebar-menu"
      >
        <el-menu-item index="/admin/dashboard" class="menu-item">
          <el-icon><Odometer /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item index="/admin/movies" class="menu-item">
          <el-icon><VideoCamera /></el-icon>
          <span>电影管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/feedback" class="menu-item">
          <el-icon><ChatLineRound /></el-icon>
          <span>反馈管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="header-title">管理后台</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand" placement="bottom-end">
            <span class="user-dropdown">
              <el-avatar :size="32" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ authStore.userInfo?.username || '管理员' }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <div class="content-wrapper">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const router = useRouter()

const handleCommand = (command) => {
  switch (command) {
    case 'logout':
      authStore.logout()
      ElMessage.success('退出成功')
      router.push('/login')
      break
    case 'profile':
      ElMessage.info('个人资料功能开发中...')
      break
    case 'settings':
      ElMessage.info('系统设置功能开发中...')
      break
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  background-color: #f0f2f5;
}

/* 侧边栏样式 */
.sidebar {
  background: linear-gradient(180deg, #304156 0%, #273142 100%);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 100;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.1);
  gap: 12px;
}

.logo-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  flex-shrink: 0;
}

.logo h3 {
  color: white;
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.sidebar-menu {
  border: none;
  padding: 12px 0;
}

.menu-item {
  margin: 4px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.menu-item:hover {
  background-color: rgba(64, 158, 255, 0.1) !important;
}

.menu-item.is-active {
  background: linear-gradient(135deg, #409eff 0%, #36a3f7 100%) !important;
  color: white !important;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.menu-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 3px;
  height: 100%;
  background: white;
  border-radius: 0 2px 2px 0;
}

/* 头部样式 */
.header {
  background: white;
  color: #303133;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 99;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-dropdown {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background-color: #f8f9fa;
}

.user-dropdown:hover {
  background-color: #e9ecef;
}

.user-avatar {
  background: linear-gradient(135deg, #409eff 0%, #36a3f7 100%);
  color: white;
  flex-shrink: 0;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-icon {
  font-size: 12px;
  color: #909399;
  transition: transform 0.3s ease;
}

.user-dropdown:hover .dropdown-icon {
  transform: rotate(180deg);
}

/* 主内容区域 */
.main-content {
  padding: 0;
  background-color: #f0f2f5;
  overflow: auto;
}

.content-wrapper {
  padding: 24px;
  min-height: calc(100vh - 60px);
  max-width: 1400px;
  margin: 0 auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .layout-container {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100% !important;
    height: auto;
  }
  
  .logo {
    height: 50px;
    padding: 0 16px;
  }
  
  .logo h3 {
    font-size: 14px;
  }
  
  .sidebar-menu {
    display: flex;
    overflow-x: auto;
    padding: 8px 12px;
  }
  
  .menu-item {
    margin: 0 4px;
    min-width: 80px;
    text-align: center;
  }
  
  .header {
    padding: 0 16px;
    height: 50px;
  }
  
  .header-title {
    font-size: 16px;
  }
  
  .username {
    display: none;
  }
  
  .content-wrapper {
    padding: 16px;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-menu-item) {
  height: 44px;
  line-height: 44px;
  border-radius: 8px;
  margin: 4px 12px;
}

:deep(.el-menu-item .el-icon) {
  margin-right: 8px;
}

:deep(.el-menu-item span) {
  font-weight: 500;
}

:deep(.el-dropdown-menu) {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #e4e7ed;
  padding: 8px 0;
}

:deep(.el-dropdown-menu__item) {
  padding: 8px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: #f0f2f5;
  color: #409eff;
}

:deep(.el-dropdown-menu__item.is-divided) {
  border-top: 1px solid #e4e7ed;
  margin-top: 4px;
  padding-top: 12px;
}

/* 滚动条样式 */
.main-content::-webkit-scrollbar {
  width: 6px;
}

.main-content::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.main-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.main-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
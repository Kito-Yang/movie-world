<template>
  <div class="profile-container">
    <!-- 顶部导航栏 -->
    <nav class="navbar">
      <div class="nav-content">
        <h1 @click="goHome" class="nav-logo clickable">
          <span class="logo-icon">🎬</span>
          <span class="logo-text">电影世界</span>
          <div class="logo-glow"></div>
        </h1>
        
        <div class="nav-title">
          <h2>
            <span class="title-icon">👤</span>
            <span class="title-text">个人中心</span>
          </h2>
        </div>
        
        <div class="nav-actions">
          <button @click="goHome" class="nav-btn home-btn">
            <span class="btn-icon">🏠</span>
            <span class="btn-text">返回首页</span>
          </button>
          <button @click="logout" class="nav-btn logout-btn">
            <span class="btn-icon">🚪</span>
            <span class="btn-text">退出登录</span>
          </button>
        </div>
      </div>
    </nav>

    <!-- 主要内容 -->
    <div class="content-section">
      <!-- 个人信息卡片 -->
      <div class="profile-card">
        <div class="card-header">
          <h2>个人信息</h2>
          <p>管理您的账户信息和偏好设置</p>
        </div>
        
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <div class="avatar">
              <span class="avatar-text">{{ avatarText }}</span>
              <div class="avatar-glow"></div>
            </div>
            <div class="avatar-decoration">
              <div class="decoration-ring"></div>
              <div class="decoration-ring"></div>
            </div>
          </div>
          
          <div class="user-basic">
            <h3 class="username">{{ userInfo.username }}</h3>
            <span class="role-badge" :class="roleClass">
              <span class="badge-icon">{{ roleIcon }}</span>
              <span>{{ roleText }}</span>
            </span>
            <div class="user-status">
              <span class="status-dot" :class="{ active: userInfo.status === 1 }"></span>
              <span class="status-text">{{ userInfo.status === 1 ? '账户正常' : '账户已禁用' }}</span>
            </div>
          </div>
        </div>

        <div class="info-section">
          <div class="info-grid">
            <div class="info-item">
              <div class="info-label">
                <span class="label-icon">👤</span>
                <span>用户名</span>
              </div>
              <div class="info-value">{{ userInfo.username }}</div>
            </div>
            
            <div class="info-item editable-item">
              <div class="info-label">
                <span class="label-icon">📧</span>
                <span>邮箱地址</span>
              </div>
              <div class="info-value editable">
                <span v-if="!editing" class="value-text">{{ userInfo.email || '未设置邮箱' }}</span>
                <input 
                  v-else
                  v-model="editData.email"
                  type="email"
                  class="edit-input"
                  placeholder="请输入邮箱地址"
                />
                <button @click="toggleEdit" class="edit-btn" :class="{ active: editing }">
                  <svg v-if="!editing" width="16" height="16" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"></path>
                  </svg>
                  <span v-else>✕</span>
                </button>
              </div>
            </div>
            
            <div class="info-item">
              <div class="info-label">
                <span class="label-icon">📅</span>
                <span>注册时间</span>
              </div>
              <div class="info-value">{{ formatDate(userInfo.createdTime) }}</div>
            </div>
            
            <div class="info-item">
              <div class="info-label">
                <span class="label-icon">🔄</span>
                <span>最后更新</span>
              </div>
              <div class="info-value">{{ formatDate(userInfo.updatedTime) }}</div>
            </div>
          </div>
        </div>

        <div v-if="editing" class="action-section">
          <button @click="toggleEdit" class="cancel-btn">
            <span>取消</span>
          </button>
          <button @click="saveChanges" class="save-btn" :disabled="saving">
            <span v-if="!saving" class="btn-content">
              <span class="btn-icon">💾</span>
              <span>保存修改</span>
            </span>
            <span v-else class="loading-content">
              <div class="btn-spinner"></div>
              <span>保存中...</span>
            </span>
          </button>
        </div>
      </div>

      <!-- 数据统计 -->
      <div class="stats-section">
        <div class="section-header">
          <h2>数据统计</h2>
          <p>您的活动数据一览</p>
        </div>
        
        <div class="stats-grid">
          <div class="stat-card favorites-card" @click="goToFavorites">
            <div class="stat-icon">
              <span class="icon-emoji">❤️</span>
              <div class="icon-glow"></div>
            </div>
            <div class="stat-info">
              <h3 class="stat-number">{{ stats.favorites }}</h3>
              <p class="stat-label">收藏电影</p>
            </div>
            <div class="stat-action">
              <span class="action-text">查看全部</span>
              <svg class="action-arrow" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path d="M5 2l6 6-6 6"/>
              </svg>
            </div>
          </div>
          
          <div class="stat-card feedbacks-card" @click="goToFeedback">
            <div class="stat-icon">
              <span class="icon-emoji">💬</span>
              <div class="icon-glow"></div>
            </div>
            <div class="stat-info">
              <h3 class="stat-number">{{ stats.feedbacks }}</h3>
              <p class="stat-label">反馈意见</p>
            </div>
            <div class="stat-action">
              <span class="action-text">查看全部</span>
              <svg class="action-arrow" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path d="M5 2l6 6-6 6"/>
              </svg>
            </div>
          </div>
          
          <div class="stat-card watchlist-card">
            <div class="stat-icon">
              <span class="icon-emoji">🎬</span>
              <div class="icon-glow"></div>
            </div>
            <div class="stat-info">
              <h3 class="stat-number">{{ stats.watchlist }}</h3>
              <p class="stat-label">想看清单</p>
            </div>
            <div class="stat-action coming-soon">
              <span class="action-text">即将推出</span>
              <span class="coming-badge">Soon</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="quick-actions-section">
        <div class="section-header">
          <h2>快捷操作</h2>
          <p>常用功能快速入口</p>
        </div>
        
        <div class="actions-grid">
          <button @click="goToFeedback" class="action-card feedback-action">
            <div class="action-icon">
              <span class="icon-emoji">📝</span>
              <div class="icon-ripple"></div>
            </div>
            <div class="action-content">
              <h4>提交反馈</h4>
              <p>分享您的意见和建议</p>
            </div>
          </button>
          
          <button @click="goToFavorites" class="action-card favorites-action">
            <div class="action-icon">
              <span class="icon-emoji">❤️</span>
              <div class="icon-ripple"></div>
            </div>
            <div class="action-content">
              <h4>我的收藏</h4>
              <p>管理您收藏的电影</p>
            </div>
          </button>
          
          <button @click="goHome" class="action-card browse-action">
            <div class="action-icon">
              <span class="icon-emoji">🎥</span>
              <div class="icon-ripple"></div>
            </div>
            <div class="action-content">
              <h4>浏览电影</h4>
              <p>发现更多精彩电影</p>
            </div>
          </button>
          
          <button class="action-card password-action disabled">
            <div class="action-icon">
              <span class="icon-emoji">🔒</span>
            </div>
            <div class="action-content">
              <h4>修改密码</h4>
              <p>即将推出此功能</p>
            </div>
            <div class="disabled-overlay">
              <span class="disabled-text">敬请期待</span>
            </div>
          </button>
        </div>
      </div>
    </div>

    <!-- 提示消息 -->
    <div v-if="message" class="toast" :class="messageType">
      <div class="toast-content">
        <span class="toast-icon">{{ messageType === 'success' ? '✅' : '❌' }}</span>
        <div class="toast-text">
          <strong>{{ messageType === 'success' ? '操作成功' : '操作失败' }}</strong>
          <p>{{ message }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'Profile',
  setup() {
    const router = useRouter()
    const userInfo = ref({
      username: '',
      email: '',
      role: '',
      createdTime: '',
      updatedTime: '',
      status: 1
    })
    
    const stats = ref({
      favorites: 0,
      feedbacks: 0,
      watchlist: 0
    })
    
    const editing = ref(false)
    const saving = ref(false)
    const message = ref('')
    const messageType = ref('success')
    
    const editData = ref({
      email: ''
    })

    // 计算属性
    const avatarText = computed(() => {
      return userInfo.value.username ? userInfo.value.username.charAt(0).toUpperCase() : '?'
    })
    
    const roleText = computed(() => {
      return userInfo.value.role === 'ADMIN' ? '管理员' : '普通用户'
    })

    const roleClass = computed(() => {
      return userInfo.value.role === 'ADMIN' ? 'admin-role' : 'user-role'
    })

    const roleIcon = computed(() => {
      return userInfo.value.role === 'ADMIN' ? '👑' : '👤'
    })

    // 获取请求头
    const getHeaders = () => ({
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    })

    // 加载用户信息
    const loadUserInfo = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/user/profile', {
          headers: getHeaders()
        })
        
        if (response.ok) {
          userInfo.value = await response.json()
          editData.value.email = userInfo.value.email || ''
        } else if (response.status === 401) {
          router.push('/login')
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
      }
    }

    // 加载统计数据
    const loadStats = async () => {
      try {
        // 获取收藏数量
        const favResponse = await fetch('http://localhost:8080/api/favorites/my', {
          headers: getHeaders()
        })
        if (favResponse.ok) {
          const favorites = await favResponse.json()
          stats.value.favorites = favorites.length
        }
        
        // 获取反馈数量
        const feedbackResponse = await fetch('http://localhost:8080/api/feedback/my', {
          headers: getHeaders()
        })
        if (feedbackResponse.ok) {
          const feedbacks = await feedbackResponse.json()
          stats.value.feedbacks = feedbacks.length
        }
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    }

    // 切换编辑状态
    const toggleEdit = () => {
      if (editing.value) {
        editData.value.email = userInfo.value.email || ''
      }
      editing.value = !editing.value
    }

    // 保存修改
    const saveChanges = async () => {
      saving.value = true
      message.value = ''
      
      try {
        const response = await fetch('http://localhost:8080/api/user/profile', {
          method: 'PUT',
          headers: getHeaders(),
          body: JSON.stringify({
            email: editData.value.email
          })
        })
        
        if (response.ok) {
          userInfo.value.email = editData.value.email
          editing.value = false
          showMessage('个人信息更新成功', 'success')
          loadUserInfo() // 重新加载最新信息
        } else {
          showMessage('保存失败，请重试', 'error')
        }
      } catch (error) {
        console.error('保存失败:', error)
        showMessage('网络错误，请稍后重试', 'error')
      } finally {
        saving.value = false
      }
    }

    // 显示消息
    const showMessage = (msg, type) => {
      message.value = msg
      messageType.value = type
      setTimeout(() => {
        message.value = ''
      }, 3000)
    }

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '未知'
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    // 导航功能
    const goHome = () => router.push('/movies')
    const goToFavorites = () => router.push('/favorites')
    const goToSmartQA = () => router.push('/smart-qa')
    const goToFeedback = () => router.push('/feedback')
    
    const logout = () => {
      if (confirm('确定要退出登录吗？')) {
        localStorage.clear()
        router.push('/login')
      }
    }

    onMounted(() => {
      const token = localStorage.getItem('token')
      if (!token) {
        router.push('/login')
        return
      }
      
      // 从本地存储获取用户名
      userInfo.value.username = localStorage.getItem('username') || 'User'
      userInfo.value.role = localStorage.getItem('role') || 'USER'
      
      loadUserInfo()
      loadStats()
    })

    return {
      userInfo,
      stats,
      editing,
      saving,
      editData,
      message,
      messageType,
      avatarText,
      roleText,
      roleClass,
      roleIcon,
      toggleEdit,
      saveChanges,
      formatDate,
      goHome,
      goToFavorites,
      goToFeedback,
      logout,
      goToSmartQA
    }
  }
}
</script>

<style scoped>
/* 全局容器 */
.profile-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-attachment: fixed;
}

/* 导航栏 */
.navbar {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.nav-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-logo {
  position: relative;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.8rem;
  font-weight: 800;
  margin: 0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logo-icon {
  font-size: 2rem;
  animation: float 3s ease-in-out infinite;
}

.logo-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-glow {
  position: absolute;
  inset: -10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 20px;
  opacity: 0;
  filter: blur(20px);
  z-index: -1;
  transition: opacity 0.3s ease;
}

.nav-logo:hover .logo-glow {
  opacity: 0.3;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-5px); }
}

.nav-title h2 {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-icon {
  font-size: 1.5rem;
  animation: wiggle 2s infinite;
}

@keyframes wiggle {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(-5deg); }
  75% { transform: rotate(5deg); }
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.nav-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  color: white;
  border: none;
  border-radius: 50px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.home-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.logout-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
}

.nav-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.logout-btn:hover {
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.4);
}

/* 内容区域 */
.content-section {
  min-height: 600px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  margin: 2rem;
  border-radius: 24px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.section-header {
  text-align: center;
  margin-bottom: 2rem;
}

.section-header h2 {
  font-size: 2.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 0.5rem;
}

.section-header p {
  color: #666;
  font-size: 1.1rem;
  margin: 0;
}

/* 个人信息卡片 */
.profile-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  margin-bottom: 2rem;
}

.card-header {
  text-align: center;
  margin-bottom: 2rem;
}

.card-header h2 {
  font-size: 2rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 0.5rem;
}

.card-header p {
  color: #666;
  margin: 0;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 2rem;
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}

.avatar-wrapper {
  position: relative;
}

.avatar {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 3rem;
  font-weight: 700;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
  z-index: 2;
}

.avatar-text {
  animation: bounce 3s infinite;
}

@keyframes bounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.avatar-glow {
  position: absolute;
  inset: -10px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  filter: blur(20px);
  opacity: 0.5;
  z-index: -1;
  animation: pulse-glow 3s infinite;
}

@keyframes pulse-glow {
  0%, 100% { transform: scale(0.9); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 0.8; }
}

.avatar-decoration {
  position: absolute;
  inset: -20px;
  z-index: 1;
}

.decoration-ring {
  position: absolute;
  inset: 0;
  border: 2px solid transparent;
  border-top-color: rgba(102, 126, 234, 0.3);
  border-radius: 50%;
  animation: rotate 8s linear infinite;
}

.decoration-ring:nth-child(2) {
  inset: 10px;
  border-top-color: rgba(118, 75, 162, 0.3);
  animation-delay: -4s;
}

@keyframes rotate {
  to { transform: rotate(360deg); }
}

.user-basic {
  flex: 1;
}

.username {
  font-size: 2rem;
  font-weight: 700;
  margin: 0 0 1rem;
  background: linear-gradient(135deg, #333 0%, #555 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.role-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border-radius: 25px;
  font-size: 0.9rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.admin-role {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  color: #b8860b;
  box-shadow: 0 4px 15px rgba(255, 215, 0, 0.3);
}

.user-role {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  color: #1976d2;
  box-shadow: 0 4px 15px rgba(25, 118, 210, 0.2);
}

.user-status {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #666;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ccc;
  animation: blink 2s infinite;
}

.status-dot.active {
  background: #4caf50;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 信息网格 */
.info-section {
  margin-bottom: 2rem;
}

.info-grid {
  display: grid;
  gap: 1.5rem;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 16px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.info-item:hover {
  background: rgba(255, 255, 255, 0.7);
  transform: translateY(-2px);
}

.info-label {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: #333;
  font-weight: 600;
  font-size: 1rem;
}

.label-icon {
  font-size: 1.2rem;
}

.info-value {
  display: flex;
  align-items: center;
  gap: 1rem;
  color: #666;
  font-size: 1rem;
}

.info-value.editable {
  flex: 1;
  justify-content: space-between;
  margin-left: 2rem;
}

.value-text {
  color: #333;
  font-weight: 500;
}

.edit-input {
  flex: 1;
  padding: 0.5rem 1rem;
  border: 2px solid rgba(102, 126, 234, 0.2);
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
}

.edit-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.edit-btn {
  padding: 0.5rem 1rem;
  background: rgba(102, 126, 234, 0.1);
  border: none;
  border-radius: 12px;
  color: #667eea;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 500;
}

.edit-btn:hover {
  background: rgba(102, 126, 234, 0.2);
  transform: scale(1.05);
}

.edit-btn.active {
  background: rgba(255, 107, 107, 0.1);
  color: #ff6b6b;
}

/* 操作按钮 */
.action-section {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
}

.cancel-btn {
  padding: 0.75rem 1.5rem;
  background: rgba(0, 0, 0, 0.05);
  color: #666;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background: rgba(0, 0, 0, 0.1);
}

.save-btn {
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.save-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.save-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.btn-content, .loading-content {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 统计卡片 */
.stats-section {
  margin-bottom: 2rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.stat-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  cursor: pointer;
  transition: all 0.4s ease;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s ease;
}

.stat-card:hover::before {
  left: 100%;
}

.stat-icon {
  position: relative;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
}

.icon-glow {
  position: absolute;
  inset: -10px;
  border-radius: 50%;
  filter: blur(15px);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.favorites-card .icon-glow {
  background: #ff6b6b;
}

.feedbacks-card .icon-glow {
  background: #4ecdc4;
}

.watchlist-card .icon-glow {
  background: #667eea;
}

.stat-card:hover .icon-glow {
  opacity: 0.6;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 0.5rem;
}

.stat-label {
  color: #666;
  font-size: 1.1rem;
  margin: 0;
}

.stat-action {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #667eea;
  font-weight: 500;
}

.action-arrow {
  transition: transform 0.3s ease;
}

.stat-card:hover .action-arrow {
  transform: translateX(5px);
}

.coming-soon {
  opacity: 0.6;
}

.coming-badge {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  color: #b8860b;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

/* 快捷操作 */
.quick-actions-section {
  margin-bottom: 2rem;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
}

.action-card {
  position: relative;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  padding: 2rem;
  cursor: pointer;
  transition: all 0.4s ease;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  overflow: hidden;
}

.action-card:hover:not(.disabled) {
  transform: translateY(-5px);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.95);
}

.action-card.feedback-action:hover {
  border-color: rgba(255, 193, 7, 0.5);
}

.action-card.favorites-action:hover {
  border-color: rgba(255, 107, 107, 0.5);
}

.action-card.browse-action:hover {
  border-color: rgba(102, 126, 234, 0.5);
}

.action-icon {
  position: relative;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  flex-shrink: 0;
}

.icon-ripple {
  position: absolute;
  inset: -10px;
  border-radius: 50%;
  background: rgba(102, 126, 234, 0.1);
  opacity: 0;
  transform: scale(0.8);
  transition: all 0.4s ease;
}

.action-card:hover .icon-ripple {
  opacity: 1;
  transform: scale(1.2);
}

.action-content {
  flex: 1;
  text-align: left;
}

.action-content h4 {
  font-size: 1.25rem;
  font-weight: 600;
  color: #333;
  margin: 0 0 0.5rem;
}

.action-content p {
  color: #666;
  margin: 0;
  font-size: 0.9rem;
}

.action-card.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.disabled-overlay {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  background: rgba(255, 193, 7, 0.9);
  color: #b8860b;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

/* 提示消息 */
.toast {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  padding: 1.5rem 2rem;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(20px);
  animation: slideIn 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1000;
  max-width: 400px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.toast.success {
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.9) 0%, rgba(56, 142, 60, 0.9) 100%);
  color: white;
}

.toast.error {
  background: linear-gradient(135deg, rgba(244, 67, 54, 0.9) 0%, rgba(211, 47, 47, 0.9) 100%);
  color: white;
}

.toast-content {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
}

.toast-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.toast-text strong {
  display: block;
  font-size: 1.1rem;
  margin-bottom: 0.25rem;
}

.toast-text p {
  margin: 0;
  opacity: 0.9;
  font-size: 0.9rem;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .content-section {
    margin: 1rem;
    padding: 1.5rem;
  }
  
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1.5rem;
  }
}

@media (max-width: 768px) {
  .nav-content {
    flex-wrap: wrap;
    gap: 1rem;
    padding: 1rem;
  }
  
  .nav-title {
    order: 3;
    flex-basis: 100%;
    text-align: center;
  }
  
  .nav-actions {
    gap: 0.5rem;
  }
  
  .nav-btn {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
  }
  
  .btn-text {
    display: none;
  }
  
  .content-section {
    margin: 0.5rem;
    padding: 1rem;
    border-radius: 16px;
  }
  
  .section-header h2 {
    font-size: 2rem;
  }
  
  .avatar-section {
    flex-direction: column;
    text-align: center;
    gap: 1.5rem;
  }
  
  .avatar {
    width: 100px;
    height: 100px;
    font-size: 2.5rem;
  }
  
  .username {
    font-size: 1.5rem;
  }
  
  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .info-value.editable {
    width: 100%;
    margin-left: 0;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .actions-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .action-section {
    flex-direction: column-reverse;
    gap: 1rem;
  }
  
  .save-btn, .cancel-btn {
    width: 100%;
  }
  
  .toast {
    bottom: 1rem;
    right: 1rem;
    left: 1rem;
    max-width: none;
  }
}

@media (max-width: 480px) {
  .profile-card {
    padding: 1.5rem;
  }
  
  .stat-card {
    padding: 1.5rem;
  }
  
  .action-card {
    padding: 1.5rem;
    flex-direction: column;
    text-align: center;
    gap: 1rem;
  }
  
  .stat-number {
    font-size: 2rem;
  }
}
</style>
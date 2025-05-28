<template>
  <div class="feedback-container">
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
            <span class="title-icon">💬</span>
            <span class="title-text">意见反馈</span>
          </h2>
        </div>
        
        <div class="nav-actions">
          <button @click="goHome" class="nav-btn home-btn">
            <span class="btn-icon">🏠</span>
            <span class="btn-text">返回首页</span>
          </button>
          <div class="user-menu">
            <button class="user-btn">
              <div class="user-avatar">
                <span>👤</span>
              </div>
              <span class="user-name">{{ username }}</span>
              <svg class="dropdown-arrow" width="12" height="12" fill="currentColor" viewBox="0 0 12 12">
                <path d="M6 8L2 4h8L6 8z"/>
              </svg>
            </button>
            <div class="dropdown-menu">
              <a @click="goToProfile" class="dropdown-item">
                <span class="item-icon">👤</span>
                <span>个人中心</span>
              </a>
              <a @click="goToFavorites" class="dropdown-item">
                <span class="item-icon">❤️</span>
                <span>我的收藏</span>
              </a>
              <a @click="goToFeedback" class="dropdown-item">
                <span class="item-icon">💬</span>
                <span>意见反馈</span>
              </a>
              <!-- 新增智能问答链接 -->
              <a @click="goToSmartQA" class="dropdown-item">
                <span class="item-icon">🤖</span>
                <span>智能问答</span>
              </a>
              <div class="dropdown-divider"></div>
              <a @click="logout" class="dropdown-item logout-item">
                <span class="item-icon">🚪</span>
                <span>退出登录</span>
              </a>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主要内容 -->
    <div class="content-section">
      <!-- 标签切换 -->
      <div class="tab-switch-container">
        <div class="tab-switch">
          <button 
            :class="['tab-btn', { active: activeTab === 'submit' }]" 
            @click="activeTab = 'submit'"
          >
            <span class="tab-icon">📝</span>
            <span class="tab-text">提交反馈</span>
          </button>
          <button 
            :class="['tab-btn', { active: activeTab === 'history' }]" 
            @click="loadMyFeedbacks"
          >
            <span class="tab-icon">📋</span>
            <span class="tab-text">我的反馈</span>
          </button>
          <div class="tab-indicator" :class="{ 'tab-right': activeTab === 'history' }"></div>
        </div>
      </div>

      <!-- 提交反馈表单 -->
      <div v-if="activeTab === 'submit'" class="submit-section">
        <div class="section-header">
          <h2>提交您的反馈</h2>
          <p>您的每一条反馈对我们都很重要，我们会认真处理每一个意见和建议</p>
        </div>
        
        <div class="form-card">
          <form @submit.prevent="submitFeedback" class="feedback-form">
            <div class="form-row">
              <div class="form-group">
                <label for="type" class="form-label">
                  <span class="label-icon">🏷️</span>
                  <span>反馈类型</span>
                </label>
                <select v-model="formData.type" id="type" class="form-select" required>
                  <option value="">请选择反馈类型</option>
                  <option value="GENERAL">💭 一般反馈</option>
                  <option value="BUG">🐛 问题报告</option>
                  <option value="SUGGESTION">💡 功能建议</option>
                </select>
              </div>
            </div>

            <div class="form-group">
              <label for="title" class="form-label">
                <span class="label-icon">📝</span>
                <span>反馈标题</span>
              </label>
              <input
                v-model="formData.title"
                id="title"
                type="text"
                placeholder="请简述您的反馈内容..."
                class="form-input"
                maxlength="100"
                required
              />
              <div class="char-count">{{ formData.title.length }}/100</div>
            </div>

            <div class="form-group">
              <label for="content" class="form-label">
                <span class="label-icon">📄</span>
                <span>详细描述</span>
              </label>
              <textarea
                v-model="formData.content"
                id="content"
                placeholder="请详细描述您遇到的问题或建议..."
                class="form-textarea"
                maxlength="1000"
                rows="6"
                required
              ></textarea>
              <div class="char-count">{{ formData.content.length }}/1000</div>
            </div>

            <button type="submit" class="submit-btn" :disabled="submitting">
              <span v-if="!submitting" class="btn-content">
                <span class="btn-icon">🚀</span>
                <span>提交反馈</span>
              </span>
              <span v-else class="loading-content">
                <div class="btn-spinner"></div>
                <span>提交中...</span>
              </span>
            </button>
          </form>
        </div>
      </div>

      <!-- 我的反馈历史 -->
      <div v-if="activeTab === 'history'" class="history-section">
        <div class="section-header" v-if="!loading && feedbacks.length > 0">
          <h2>我的反馈</h2>
          <p>查看您提交的所有反馈和处理状态</p>
        </div>
        
        <div v-if="!loading && feedbacks.length > 0" class="feedback-list">
          <div v-for="feedback in feedbacks" :key="feedback.id" class="feedback-card">
            <div class="feedback-header">
              <div class="feedback-meta">
                <span class="type-badge" :class="getTypeBadgeClass(feedback.type)">
                  <span class="badge-icon">{{ getTypeIcon(feedback.type) }}</span>
                  <span>{{ getTypeText(feedback.type) }}</span>
                </span>
                <span class="status-badge" :class="getStatusBadgeClass(feedback.status)">
                  <span class="badge-icon">{{ getStatusIcon(feedback.status) }}</span>
                  <span>{{ getStatusText(feedback.status) }}</span>
                </span>
              </div>
              <span class="feedback-time">
                <span class="time-icon">⏰</span>
                <span>{{ formatDate(feedback.createdTime) }}</span>
              </span>
            </div>
            
            <h4 class="feedback-title">{{ feedback.title }}</h4>
            <p class="feedback-content">{{ feedback.content }}</p>
            
            <div v-if="feedback.adminReply" class="admin-reply">
              <div class="reply-header">
                <div class="reply-avatar">
                  <span>👨‍💼</span>
                </div>
                <div class="reply-info">
                  <span class="reply-label">管理员回复</span>
                  <span class="reply-time">{{ formatDate(feedback.updatedTime) }}</span>
                </div>
              </div>
              <p class="reply-content">{{ feedback.adminReply }}</p>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && feedbacks.length === 0" class="empty-state">
          <div class="empty-content">
            <div class="empty-icon">
              <span class="main-icon">📝</span>
              <div class="icon-animation"></div>
            </div>
            <h3>还没有提交任何反馈</h3>
            <p>点击"提交反馈"来分享您的想法和建议</p>
            <button @click="activeTab = 'submit'" class="action-btn">
              <span class="btn-icon">✍️</span>
              <span>提交反馈</span>
            </button>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <div class="loading-content">
            <div class="loading-spinner">
              <div class="spinner-ring"></div>
              <div class="spinner-ring"></div>
              <div class="spinner-ring"></div>
            </div>
            <p class="loading-text">正在加载反馈记录...</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 成功提示 -->
    <div v-if="showSuccess" class="success-toast">
      <div class="toast-content">
        <span class="toast-icon">✅</span>
        <div class="toast-text">
          <strong>反馈提交成功！</strong>
          <p>我们会尽快处理您的反馈</p>
        </div>
      </div>
    </div>

    <!-- 错误提示 -->
    <div v-if="error" class="error-toast">
      <div class="toast-content">
        <span class="toast-icon">❌</span>
        <div class="toast-text">
          <strong>提交失败</strong>
          <p>{{ error }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'Feedback',
  setup() {
    const router = useRouter()
    const activeTab = ref('submit')
    const feedbacks = ref([])
    const loading = ref(false)
    const submitting = ref(false)
    const showSuccess = ref(false)
    const error = ref('')
    
    const username = localStorage.getItem('username') || 'User'
    
    const formData = ref({
      type: '',
      title: '',
      content: ''
    })

    // 获取请求头
    const getHeaders = () => ({
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    })

    // 提交反馈
    const submitFeedback = async () => {
      submitting.value = true
      error.value = ''
      
      try {
        const response = await fetch('http://localhost:8080/api/feedback/submit', {
          method: 'POST',
          headers: getHeaders(),
          body: JSON.stringify(formData.value)
        })
        
        if (response.ok) {
          // 重置表单
          formData.value = {
            type: '',
            title: '',
            content: ''
          }
          
          // 显示成功提示
          showSuccess.value = true
          setTimeout(() => {
            showSuccess.value = false
          }, 3000)
          
          // 如果当前在历史标签页，重新加载数据
          if (activeTab.value === 'history') {
            loadMyFeedbacks()
          }
        } else {
          const errorData = await response.json()
          error.value = errorData.message || '提交失败，请重试'
        }
      } catch (err) {
        error.value = '网络错误，请稍后重试'
        console.error('提交反馈失败:', err)
      } finally {
        submitting.value = false
        if (error.value) {
          setTimeout(() => {
            error.value = ''
          }, 3000)
        }
      }
    }

    // 加载我的反馈
    const loadMyFeedbacks = async () => {
      activeTab.value = 'history'
      loading.value = true
      
      try {
        const response = await fetch('http://localhost:8080/api/feedback/my', {
          headers: getHeaders()
        })
        
        if (response.ok) {
          feedbacks.value = await response.json()
        } else if (response.status === 401) {
          router.push('/login')
        }
      } catch (error) {
        console.error('加载反馈历史失败:', error)
      } finally {
        loading.value = false
      }
    }

    // 获取类型文本
    const getTypeText = (type) => {
      const typeMap = {
        'GENERAL': '一般反馈',
        'BUG': '问题报告',
        'SUGGESTION': '功能建议'
      }
      return typeMap[type] || '未知'
    }

    // 获取类型图标
    const getTypeIcon = (type) => {
      const iconMap = {
        'GENERAL': '💭',
        'BUG': '🐛', 
        'SUGGESTION': '💡'
      }
      return iconMap[type] || '❓'
    }

    // 获取类型样式类
    const getTypeBadgeClass = (type) => {
      return {
        'GENERAL': 'type-general',
        'BUG': 'type-bug',
        'SUGGESTION': 'type-suggestion'
      }[type] || ''
    }

    // 获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        0: '待处理',
        1: '处理中',
        2: '已解决',
        3: '已关闭'
      }
      return statusMap[status] || '未知'
    }

    // 获取状态图标
    const getStatusIcon = (status) => {
      const iconMap = {
        0: '⏳',
        1: '🔄',
        2: '✅',
        3: '🔒'
      }
      return iconMap[status] || '❓'
    }

    // 获取状态样式类
    const getStatusBadgeClass = (status) => {
      return {
        0: 'status-pending',
        1: 'status-processing',
        2: 'status-resolved',
        3: 'status-closed'
      }[status] || ''
    }

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '未知'
      const date = new Date(dateString)
      const now = new Date()
      const diff = now - date
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      
      if (days === 0) {
        return '今天'
      } else if (days === 1) {
        return '昨天'
      } else if (days < 7) {
        return `${days}天前`
      } else {
        return date.toLocaleDateString('zh-CN')
      }
    }

    // 导航功能
    const goHome = () => router.push('/movies')
    const goToProfile = () => router.push('/profile')
    const goToSmartQA = () => router.push('/smart-qa')
    const goToFavorites = () => router.push('/favorites')
    
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
    })

    return {
      activeTab,
      feedbacks,
      loading,
      submitting,
      showSuccess,
      error,
      formData,
      username,
      submitFeedback,
      loadMyFeedbacks,
      getTypeText,
      getTypeIcon,
      getTypeBadgeClass,
      getStatusText,
      getStatusIcon,
      getStatusBadgeClass,
      formatDate,
      goHome,
      goToProfile,
      goToFavorites,
      logout,
      goToSmartQA
    }
  }
}
</script>

<style scoped>
/* 全局容器 */
.feedback-container {
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
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 50px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.nav-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.user-menu {
  position: relative;
}

.user-btn {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1.25rem;
  background: rgba(255, 255, 255, 0.1);
  color: #333;
  border: 2px solid rgba(102, 126, 234, 0.2);
  border-radius: 50px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-btn:hover {
  background: rgba(102, 126, 234, 0.1);
  border-color: rgba(102, 126, 234, 0.4);
}

.user-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
}

.dropdown-arrow {
  transition: transform 0.3s ease;
}

.user-menu:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 0.5rem);
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  padding: 0.5rem;
  min-width: 200px;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.3s ease;
}

.user-menu:hover .dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  color: #333;
  text-decoration: none;
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.dropdown-item:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.logout-item:hover {
  background: rgba(255, 75, 87, 0.1);
  color: #ff4757;
}

.dropdown-divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.1);
  margin: 0.5rem 0;
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

/* 标签切换 */
.tab-switch-container {
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
}

.tab-switch {
  position: relative;
  display: flex;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 60px;
  padding: 0.5rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.tab-btn {
  position: relative;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 2rem;
  border: none;
  background: transparent;
  color: #666;
  font-size: 1rem;
  font-weight: 500;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 2;
}

.tab-btn.active {
  color: white;
}

.tab-indicator {
  position: absolute;
  top: 0.5rem;
  left: 0.5rem;
  width: calc(50% - 0.5rem);
  height: calc(100% - 1rem);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
}

.tab-indicator.tab-right {
  left: calc(50%);
}

/* 表单样式 */
.form-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.form-group {
  position: relative;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #333;
  font-weight: 600;
  margin-bottom: 0.75rem;
  font-size: 1rem;
}

.label-icon {
  font-size: 1.2rem;
}

.form-input, .form-select, .form-textarea {
  width: 100%;
  padding: 1rem 1.25rem;
  border: 2px solid rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
}

.form-input:focus, .form-select:focus, .form-textarea:focus {
  outline: none;
  border-color: #667eea;
  background: rgba(255, 255, 255, 1);
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.2);
}

.form-textarea {
  resize: vertical;
  min-height: 150px;
  font-family: inherit;
}

.char-count {
  position: absolute;
  right: 1rem;
  bottom: -1.75rem;
  color: #999;
  font-size: 0.875rem;
  font-weight: 500;
}

.submit-btn {
  width: 100%;
  padding: 1.25rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 16px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
  margin-top: 1rem;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.4);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.btn-content, .loading-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
}

.btn-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

/* 反馈卡片列表 */
.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.feedback-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.4s ease;
}

.feedback-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.15);
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.feedback-meta {
  display: flex;
  gap: 0.75rem;
}

.type-badge, .status-badge {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.type-general { 
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%); 
  color: #1976d2; 
}

.type-bug { 
  background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%); 
  color: #d32f2f; 
}

.type-suggestion { 
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%); 
  color: #388e3c; 
}

.status-pending { 
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%); 
  color: #f57c00; 
}

.status-processing { 
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%); 
  color: #1976d2; 
}

.status-resolved { 
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%); 
  color: #388e3c; 
}

.status-closed { 
  background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%); 
  color: #757575; 
}

.feedback-time {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
  font-size: 0.875rem;
  font-weight: 500;
}

.feedback-title {
  font-size: 1.4rem;
  font-weight: 700;
  color: #333;
  margin: 0 0 1rem;
  line-height: 1.4;
}

.feedback-content {
  color: #666;
  line-height: 1.7;
  margin: 0 0 1.5rem;
  font-size: 1rem;
}

.admin-reply {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-radius: 16px;
  padding: 1.5rem;
  border-left: 4px solid #667eea;
  margin-top: 1rem;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.reply-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
}

.reply-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.reply-label {
  font-weight: 600;
  color: #333;
  font-size: 1rem;
}

.reply-time {
  color: #666;
  font-size: 0.875rem;
}

.reply-content {
  color: #555;
  line-height: 1.7;
  margin: 0;
  font-size: 1rem;
}

/* 空状态 */
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

.empty-content {
  text-align: center;
  padding: 2rem;
}

.empty-icon {
  position: relative;
  font-size: 4rem;
  margin-bottom: 2rem;
  display: inline-block;
}

.main-icon {
  display: inline-block;
  animation: writeMotion 3s infinite;
}

@keyframes writeMotion {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(-5deg); }
  75% { transform: rotate(5deg); }
}

.icon-animation {
  position: absolute;
  inset: -20px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  opacity: 0.2;
  filter: blur(20px);
  animation: pulse-glow 3s infinite;
}

@keyframes pulse-glow {
  0%, 100% { transform: scale(0.8); opacity: 0.2; }
  50% { transform: scale(1.2); opacity: 0.4; }
}

.empty-state h3 {
  font-size: 1.8rem;
  font-weight: 700;
  color: #333;
  margin: 0 0 1rem;
}

.empty-state p {
  color: #666;
  font-size: 1.1rem;
  margin: 0 0 2rem;
  max-width: 400px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 50px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.action-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.4);
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  color: #333;
}

.loading-content {
  text-align: center;
}

.loading-spinner {
  position: relative;
  width: 80px;
  height: 80px;
  margin-bottom: 2rem;
}

.spinner-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 4px solid transparent;
  border-radius: 50%;
  animation: spin 2s linear infinite;
}

.spinner-ring:nth-child(1) {
  border-top-color: #667eea;
  animation-delay: 0s;
}

.spinner-ring:nth-child(2) {
  border-right-color: #764ba2;
  animation-delay: 0.3s;
}

.spinner-ring:nth-child(3) {
  border-bottom-color: #667eea;
  animation-delay: 0.6s;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  font-size: 1.2rem;
  font-weight: 500;
  color: #667eea;
  margin: 0;
}

/* 提示消息 */
.success-toast, .error-toast {
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

.success-toast {
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.9) 0%, rgba(56, 142, 60, 0.9) 100%);
  color: white;
}

.error-toast {
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
  
  .nav-btn, .user-btn {
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
  
  .tab-switch {
    flex-direction: column;
    align-items: stretch;
  }
  
  .tab-indicator {
    width: calc(100% - 1rem);
    height: calc(50% - 0.5rem);
    top: 0.5rem;
    left: 0.5rem;
  }
  
  .tab-indicator.tab-right {
    top: calc(50%);
    left: 0.5rem;
  }
  
  .feedback-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .feedback-meta {
    flex-wrap: wrap;
    gap: 0.5rem;
  }
  
  .char-count {
    position: static;
    text-align: right;
    margin-top: 0.5rem;
  }
  
  .reply-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }
  
  .success-toast, .error-toast {
    bottom: 1rem;
    right: 1rem;
    left: 1rem;
    max-width: none;
  }
}

@media (max-width: 480px) {
  .feedback-card {
    padding: 1.5rem;
  }
  
  .form-card {
    padding: 1.5rem;
  }
  
  .empty-state h3 {
    font-size: 1.5rem;
  }
  
  .empty-state p {
    font-size: 1rem;
  }
}
</style>
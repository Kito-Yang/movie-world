<template>
  <div class="favorites-container">
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
            <span class="title-icon">❤️</span>
            <span class="title-text">我的收藏</span>
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

    <!-- 统计信息 -->
    <div class="stats-section">
      <div class="stats-container">
        <div class="stats-header">
          <h2>收藏统计</h2>
          <p>你的电影收藏数据一览</p>
        </div>
        
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon favorite-icon">
              <span>❤️</span>
              <div class="icon-glow"></div>
            </div>
            <div class="stat-info">
              <h3>{{ totalFavorites }}</h3>
              <p>收藏总数</p>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon recent-icon">
              <span>📅</span>
              <div class="icon-glow"></div>
            </div>
            <div class="stat-info">
              <h3>{{ recentCount }}</h3>
              <p>最近7天</p>
            </div>
          </div>
          
          <div class="stat-card">
            <div class="stat-icon type-icon">
              <span>🎭</span>
              <div class="icon-glow"></div>
            </div>
            <div class="stat-info">
              <h3>{{ typeCount }}</h3>
              <p>电影类型</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 收藏列表 -->
    <div class="content-section">
      <div class="section-header" v-if="!loading && favorites.length > 0">
        <h2>我的收藏</h2>
        <p>共收藏了 {{ totalFavorites }} 部精彩电影</p>
      </div>
      
      <div v-if="!loading && favorites.length > 0" class="favorites-grid">
        <div v-for="favorite in favorites" :key="favorite.id" class="favorite-card" @click="viewMovie(favorite.movieId)">
          <div class="card-header">
            <div class="movie-poster">
              <img :src="`https://picsum.photos/400/600?random=${favorite.movieId}`" :alt="favorite.movieName" />
              <div class="poster-overlay">
                <button @click.stop="removeFavorite(favorite)" class="remove-btn">
                  <span class="remove-icon">❌</span>
                </button>
              </div>
            </div>
          </div>
          
          <div class="card-body">
            <h3 class="movie-name">{{ favorite.movieName }}</h3>
            
            <div class="movie-meta">
              <div class="meta-item collect-time">
                <span class="meta-icon">⏰</span>
                <span class="meta-text">收藏于 {{ formatDate(favorite.createdTime) }}</span>
              </div>
              
              <div class="meta-item movie-type" v-if="favorite.movieType">
                <span class="meta-icon">🎭</span>
                <span class="meta-text">{{ favorite.movieType }}</span>
              </div>
            </div>
            
            <div class="card-actions">
              <button @click.stop="viewMovie(favorite.movieId)" class="view-btn">
                <span>查看详情</span>
                <svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                  <path d="M5 2l6 6-6 6"/>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && favorites.length === 0" class="empty-state">
      <div class="empty-content">
        <div class="empty-icon">
          <span class="broken-heart">💔</span>
          <div class="icon-animation"></div>
        </div>
        <h3>还没有收藏任何电影</h3>
        <p>去发现更多精彩的电影，开始你的收藏之旅吧</p>
        <button @click="goHome" class="action-btn">
          <span class="btn-icon">🎬</span>
          <span>探索电影</span>
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
        <p class="loading-text">正在加载收藏内容...</p>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="!loading && totalPages > 1" class="pagination">
      <div class="pagination-container">
        <button 
          @click="changePage(currentPage - 1)" 
          :disabled="currentPage === 1"
          class="page-btn prev-btn"
        >
          <svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
            <path d="M11 2L5 8l6 6"/>
          </svg>
          <span>上一页</span>
        </button>
        
        <div class="page-numbers">
          <span class="page-info">
            第 <strong>{{ currentPage }}</strong> / {{ totalPages }} 页
          </span>
        </div>
        
        <button 
          @click="changePage(currentPage + 1)" 
          :disabled="currentPage === totalPages"
          class="page-btn next-btn"
        >
          <span>下一页</span>
          <svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
            <path d="M5 2l6 6-6 6"/>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'Favorites',
  setup() {
    const router = useRouter()
    const favorites = ref([])
    const loading = ref(false)
    const currentPage = ref(1)
    const totalPages = ref(1)
    const total = ref(0)
    const pageSize = 9
    
    const username = localStorage.getItem('username') || 'User'

    // 统计数据
    const totalFavorites = computed(() => total.value)
    
    const recentCount = computed(() => {
      const sevenDaysAgo = new Date()
      sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7)
      return favorites.value.filter(f => new Date(f.createdTime) > sevenDaysAgo).length
    })
    
    const typeCount = computed(() => {
      const types = new Set(favorites.value.map(f => f.movieType || '未知'))
      return types.size
    })

    // 获取请求头
    const getHeaders = () => ({
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    })

    // 加载收藏列表
    const loadFavorites = async (page = 1) => {
      loading.value = true
      currentPage.value = page
      
      try {
        const response = await fetch(
          `http://localhost:8080/api/favorites/my/page?pageNum=${page}&pageSize=${pageSize}`,
          { headers: getHeaders() }
        )
        
        if (response.ok) {
          const data = await response.json()
          favorites.value = data.records || []
          total.value = data.total || 0
          totalPages.value = data.pages || 1
        } else if (response.status === 401) {
          router.push('/login')
        }
      } catch (error) {
        console.error('加载收藏失败:', error)
      } finally {
        loading.value = false
      }
    }

    // 移除收藏
    const removeFavorite = async (favorite) => {
      if (!confirm(`确定要取消收藏《${favorite.movieName}》吗？`)) {
        return
      }
      
      try {
        const response = await fetch(
          `http://localhost:8080/api/favorites/remove/${favorite.movieId}`,
          { 
            method: 'DELETE',
            headers: getHeaders() 
          }
        )
        
        if (response.ok) {
          // 重新加载当前页
          loadFavorites(currentPage.value)
        }
      } catch (error) {
        console.error('取消收藏失败:', error)
      }
    }

    // 格式化日期
    const formatDate = (dateString) => {
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

    // 分页
    const changePage = (page) => {
      if (page >= 1 && page <= totalPages.value) {
        loadFavorites(page)
      }
    }

    // 导航功能
    const goHome = () => router.push('/movies')
    const goToProfile = () => router.push('/profile')
    const goToFeedback = () => router.push('/feedback')
    const goToSmartQA = () => router.push('/smart-qa')
    const viewMovie = (movieId) => router.push(`/movie/${movieId}`)
    
    const logout = () => {
      localStorage.clear()
      router.push('/login')
    }

    onMounted(() => {
      const token = localStorage.getItem('token')
      if (!token) {
        router.push('/login')
        return
      }
      
      loadFavorites()
    })

    return {
      favorites,
      loading,
      currentPage,
      totalPages,
      totalFavorites,
      recentCount,
      typeCount,
      username,
      loadFavorites,
      removeFavorite,
      formatDate,
      changePage,
      goHome,
      goToProfile,
      goToFeedback,
      viewMovie,
      logout,
      goToSmartQA
    }
  }
}
</script>

<style scoped>
/* 全局容器 */
.favorites-container {
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
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
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

/* 统计区域 */
.stats-section {
  padding: 2rem 0;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.stats-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

.stats-header {
  text-align: center;
  margin-bottom: 2rem;
}

.stats-header h2 {
  font-size: 2rem;
  font-weight: 700;
  color: white;
  margin: 0 0 0.5rem;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.stats-header p {
  color: rgba(255, 255, 255, 0.8);
  font-size: 1.1rem;
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 2rem;
  transition: all 0.4s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.stat-icon {
  position: relative;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
  font-size: 2.5rem;
  transition: all 0.3s ease;
}

.favorite-icon {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
}

.recent-icon {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
}

.type-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.icon-glow {
  position: absolute;
  inset: -5px;
  background: inherit;
  border-radius: 25px;
  opacity: 0;
  filter: blur(15px);
  z-index: -1;
  transition: opacity 0.3s ease;
}

.stat-card:hover .icon-glow {
  opacity: 0.5;
}

.stat-info h3 {
  font-size: 2.5rem;
  font-weight: 700;
  color: #333;
  margin: 0 0 0.25rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-info p {
  color: #666;
  margin: 0;
  font-size: 1.1rem;
  font-weight: 500;
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

/* 收藏网格 */
.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 2rem;
}

.favorite-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.4s ease;
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.favorite-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.card-header {
  position: relative;
}

.movie-poster {
  position: relative;
  padding-bottom: 120%;
  overflow: hidden;
}

.movie-poster img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.favorite-card:hover .movie-poster img {
  transform: scale(1.1);
}

.poster-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom, 
    transparent 0%, 
    rgba(0, 0, 0, 0.3) 60%, 
    rgba(0, 0, 0, 0.8) 100%
  );
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
  padding: 1rem;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.favorite-card:hover .poster-overlay {
  opacity: 1;
}

.remove-btn {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.remove-btn:hover {
  background: rgba(255, 75, 87, 0.9);
  transform: scale(1.1) rotate(90deg);
}

.remove-icon {
  font-size: 1.2rem;
  transition: all 0.3s ease;
}

.remove-btn:hover .remove-icon {
  color: white;
}

.card-body {
  padding: 1.5rem;
}

.movie-name {
  font-size: 1.3rem;
  font-weight: 700;
  color: #333;
  margin: 0 0 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.movie-meta {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
  font-size: 0.9rem;
}

.meta-icon {
  font-size: 1rem;
}

.meta-text {
  font-weight: 500;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
}

.view-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.view-btn:hover {
  transform: translateX(5px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

/* 空状态 */
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 500px;
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

.broken-heart {
  display: inline-block;
  animation: heartbeat 2s infinite;
}

@keyframes heartbeat {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.icon-animation {
  position: absolute;
  inset: -20px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a24);
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
  min-height: 400px;
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

/* 分页 */
.pagination {
  padding: 3rem 2rem;
  display: flex;
  justify-content: center;
}

.pagination-container {
  display: flex;
  align-items: center;
  gap: 2rem;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  padding: 1rem 2rem;
  border-radius: 50px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

.page-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.page-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.page-info {
  color: #333;
  font-weight: 500;
  font-size: 1rem;
}

.page-info strong {
  color: #667eea;
  font-size: 1.1rem;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .favorites-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
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
  
  .nav-btn, .user-btn {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
  }
  
  .btn-text {
    display: none;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .content-section {
    margin: 1rem;
    padding: 1rem;
    border-radius: 16px;
  }
  
  .favorites-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .section-header h2 {
    font-size: 2rem;
  }
  
  .stats-header h2 {
    font-size: 1.5rem;
  }
  
  .pagination-container {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }
  
  .empty-state h3 {
    font-size: 1.5rem;
  }
  
  .empty-state p {
    font-size: 1rem;
  }
}

@media (max-width: 480px) {
  .favorites-grid {
    grid-template-columns: 1fr;
  }
  
  .favorite-card {
    max-width: 350px;
    margin: 0 auto;
  }
  
  .stats-container {
    padding: 0 1rem;
  }
  
  .stat-card {
    flex-direction: column;
    text-align: center;
    gap: 1rem;
  }
}
</style>
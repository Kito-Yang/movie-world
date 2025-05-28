<template>
  <div class="movies-container">
    <!-- 顶部导航栏 -->
    <nav class="navbar">
      <div class="nav-content">
        <h1 class="nav-logo">
          <span class="logo-icon">🎬</span>
          <span class="logo-text">电影世界</span>
          <div class="logo-glow"></div>
        </h1>
        
        <div class="nav-search">
          <div class="search-wrapper">
            <input
              v-model="searchQuery"
              @keyup.enter="handleSearch"
              type="text"
              placeholder="搜索你喜欢的电影..."
              class="search-input"
            />
            <button @click="handleSearch" class="search-btn">
              <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd"></path>
              </svg>
            </button>
          </div>
        </div>
        
        <div class="nav-actions">
          <button @click="goToFavorites" class="nav-btn favorites-btn">
            <span class="btn-icon">❤️</span>
            <span class="btn-text">我的收藏</span>
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

    <!-- 筛选区域 -->
    <div class="filters-section">
      <div class="filters-container">
        <div class="filters-header">
          <h2>精准筛选</h2>
          <p>找到你最爱的电影</p>
        </div>
        
        <div class="filters-grid">
          <div class="filter-group">
            <label class="filter-label">
              <span class="label-icon">🎭</span>
              <span>类型</span>
            </label>
            <select v-model="filters.type" @change="loadMovies" class="filter-select">
              <option value="">全部类型</option>
              <option v-for="type in movieTypes" :key="type" :value="type">{{ type }}</option>
            </select>
          </div>
          
          <div class="filter-group">
            <label class="filter-label">
              <span class="label-icon">📅</span>
              <span>年份</span>
            </label>
            <select v-model="filters.year" @change="loadMovies" class="filter-select">
              <option value="">全部年份</option>
              <option v-for="year in years" :key="year" :value="year">{{ year }}</option>
            </select>
          </div>
          
          <div class="filter-group">
            <label class="filter-label">
              <span class="label-icon">🌍</span>
              <span>产地</span>
            </label>
            <select v-model="filters.origin" @change="loadMovies" class="filter-select">
              <option value="">全部地区</option>
              <option value="中国">🇨🇳 中国</option>
              <option value="美国">🇺🇸 美国</option>
              <option value="日本">🇯🇵 日本</option>
              <option value="韩国">🇰🇷 韩国</option>
              <option value="英国">🇬🇧 英国</option>
            </select>
          </div>
          
          <div class="filter-actions">
            <button @click="clearFilters" class="clear-btn">
              <span>清除筛选</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 电影列表 -->
    <div class="content-section">
      <div class="section-header" v-if="!loading">
        <h2>热门电影</h2>
      </div>
      
      <div class="movies-grid" v-if="!loading">
        <div v-for="movie in movies" :key="movie.id" class="movie-card" @click="viewMovieDetail(movie)">
          <div class="movie-poster">
            <img :src="`https://picsum.photos/400/600?random=${movie.id}`" :alt="movie.name" />
            <div class="movie-overlay">
              <button 
                @click.stop="toggleFavorite(movie)" 
                class="favorite-btn" 
                :class="{ active: movie.isFavorite }"
              >
                <span class="heart-icon">{{ movie.isFavorite ? '❤️' : '🤍' }}</span>
              </button>
              <div class="overlay-info">
                <span class="overlay-rating">⭐ {{ movie.rate }}</span>
              </div>
            </div>
            <div class="movie-genre">{{ movie.type }}</div>
          </div>
          
          <div class="movie-info">
            <h3 class="movie-title">{{ movie.name }}</h3>
            <div class="movie-meta">
              <div class="meta-row">
                <span class="meta-item">
                  <span class="meta-icon">📅</span>
                  <span>{{ movie.year }}年</span>
                </span>
                <span class="meta-item">
                  <span class="meta-icon">⏱️</span>
                  <span>{{ movie.duration }}分钟</span>
                </span>
              </div>
              <div class="meta-row">
                <span class="meta-item origin">
                  <span class="meta-icon">🌍</span>
                  <span>{{ movie.origin }}</span>
                </span>
                <span class="rating-badge">
                  <span class="rating-star">⭐</span>
                  <span class="rating-value">{{ movie.rate }}</span>
                </span>
              </div>
            </div>
          </div>
        </div>
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
        <p class="loading-text">正在加载精彩内容...</p>
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
  name: 'Movies',
  setup() {
    const router = useRouter()
    const movies = ref([])
    const loading = ref(false)
    const searchQuery = ref('')
    const movieTypes = ref([])
    const currentPage = ref(1)
    const totalPages = ref(1)
    const pageSize = 12
    
    const username = localStorage.getItem('username') || 'User'
    
    const filters = ref({
      type: '',
      year: '',
      origin: ''
    })

    // 生成年份选项
    const currentYear = new Date().getFullYear()
    const years = computed(() => {
      const yearList = []
      for (let i = currentYear; i >= 1990; i--) {
        yearList.push(i)
      }
      return yearList
    })

    // 获取请求头
    const getHeaders = () => ({
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    })

    // 加载电影类型
    const loadMovieTypes = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/movies/types')
        if (response.ok) {
          movieTypes.value = await response.json()
        }
      } catch (error) {
        console.error('加载电影类型失败:', error)
      }
    }

    // 加载电影列表
    const loadMovies = async (page = 1) => {
      loading.value = true
      currentPage.value = page
      
      try {
        let url = `http://localhost:8080/api/movies/conditions/page?pageNum=1&pageSize=${pageSize}`
        
        if (searchQuery.value) {
          url += `&name=${encodeURIComponent(searchQuery.value)}`
        }
        if (filters.value.type) {
          url += `&type=${encodeURIComponent(filters.value.type)}`
        }
        if (filters.value.year) {
          url += `&year=${filters.value.year}`
        }
        if (filters.value.origin) {
          url += `&origin=${encodeURIComponent(filters.value.origin)}`
        }

        const response = await fetch(url)
        if (response.ok) {
          const data = await response.json()
          movies.value = data.records || []
          totalPages.value = data.pages || 1
          
          // 检查收藏状态
          await checkFavoriteStatus()
        }
      } catch (error) {
        console.error('加载电影失败:', error)
      } finally {
        loading.value = false
      }
    }

    // 检查收藏状态
    const checkFavoriteStatus = async () => {
      const token = localStorage.getItem('token')
      if (!token) return
      
      for (const movie of movies.value) {
        try {
          const response = await fetch(
            `http://localhost:8080/api/favorites/check/${movie.movieId}`,
            { headers: getHeaders() }
          )
          if (response.ok) {
            const data = await response.json()
            movie.isFavorite = data.isFavorite
          }
        } catch (error) {
          console.error('检查收藏状态失败:', error)
        }
      }
    }

    // 切换收藏状态
    const toggleFavorite = async (movie) => {
      const token = localStorage.getItem('token')
      if (!token) {
        alert('请先登录')
        router.push('/login')
        return
      }

      try {
        if (movie.isFavorite) {
          const response = await fetch(
            `http://localhost:8080/api/favorites/remove/${movie.movieId}`,
            { method: 'DELETE', headers: getHeaders() }
          )
          if (response.ok) {
            movie.isFavorite = false
          }
        } else {
          const response = await fetch(
            'http://localhost:8080/api/favorites/add',
            {
              method: 'POST',
              headers: getHeaders(),
              body: JSON.stringify({
                movieId: movie.movieId,
                movieName: movie.name
              })
            }
          )
          if (response.ok) {
            movie.isFavorite = true
          }
        }
      } catch (error) {
        console.error('操作收藏失败:', error)
      }
    }

    // 搜索
    const handleSearch = () => {
      loadMovies(1)
    }

    // 分页
    const changePage = (page) => {
      if (page >= 1 && page <= totalPages.value) {
        loadMovies(page)
      }
    }

    // 清除筛选
    const clearFilters = () => {
      filters.value = {
        type: '',
        year: '',
        origin: ''
      }
      searchQuery.value = ''
      loadMovies(1)
    }

    // 查看电影详情
    const viewMovieDetail = (movie) => {
      // 可以跳转到电影详情页
      console.log('查看电影详情:', movie)
    }

    // 导航功能
    const goToFavorites = () => router.push('/favorites')
    const goToProfile = () => router.push('/profile')
    const goToSmartQA = () => router.push('/smart-qa')
    const goToFeedback = () => router.push('/feedback')
    
    const logout = () => {
      localStorage.clear()
      router.push('/login')
    }

    onMounted(() => {
      // 检查登录状态
      const token = localStorage.getItem('token')
      if (!token) {
        router.push('/login')
        return
      }
      
      loadMovieTypes()
      loadMovies()
    })

    return {
      movies,
      loading,
      searchQuery,
      movieTypes,
      filters,
      years,
      currentPage,
      totalPages,
      username,
      loadMovies,
      handleSearch,
      changePage,
      toggleFavorite,
      clearFilters,
      viewMovieDetail,
      goToFavorites,
      goToProfile,
      goToFeedback,
      logout,
      goToSmartQA
    }
  }
}
</script>

<style scoped>
/* 全局容器 */
.movies-container {
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

.nav-search {
  flex: 1;
  max-width: 600px;
}

.search-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.search-wrapper:focus-within {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.3);
}

.search-input {
  flex: 1;
  padding: 1rem 1.5rem;
  border: none;
  background: transparent;
  font-size: 1rem;
  color: #333;
}

.search-input::placeholder {
  color: #999;
}

.search-input:focus {
  outline: none;
}

.search-btn {
  padding: 1rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
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

.favorites-btn .btn-icon {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
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

/* 筛选区域 */
.filters-section {
  padding: 2rem 0;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.filters-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

.filters-header {
  text-align: center;
  margin-bottom: 2rem;
}

.filters-header h2 {
  font-size: 2rem;
  font-weight: 700;
  color: white;
  margin: 0 0 0.5rem;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.filters-header p {
  color: rgba(255, 255, 255, 0.8);
  font-size: 1.1rem;
  margin: 0;
}

.filters-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  align-items: end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.filter-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  color: white;
  font-size: 1rem;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

.label-icon {
  font-size: 1.2rem;
}

.filter-select {
  padding: 1rem 1.25rem;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  font-size: 1rem;
  color: #333;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-select:focus {
  outline: none;
  border-color: rgba(255, 255, 255, 0.6);
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.1);
}

.filter-actions {
  display: flex;
  align-items: end;
}

.clear-btn {
  padding: 1rem 1.5rem;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.clear-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
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

/* 电影网格 */
.movies-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}

.movie-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.4s ease;
  cursor: pointer;
  position: relative;
}

.movie-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
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

.movie-card:hover .movie-poster img {
  transform: scale(1.1);
}

.movie-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom, 
    transparent 0%, 
    rgba(0, 0, 0, 0.3) 60%, 
    rgba(0, 0, 0, 0.8) 100%
  );
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 1.5rem;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.movie-card:hover .movie-overlay {
  opacity: 1;
}

.favorite-btn {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  font-size: 1.5rem;
  cursor: pointer;
  transition: all 0.3s ease;
  align-self: flex-end;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.favorite-btn:hover {
  transform: scale(1.1);
  background: rgba(255, 255, 255, 1);
}

.favorite-btn.active {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  color: white;
}

.overlay-info {
  align-self: flex-start;
}

.overlay-rating {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-weight: 600;
  color: #333;
  font-size: 0.9rem;
}

.movie-genre {
  position: absolute;
  top: 1rem;
  left: 1rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 500;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.movie-info {
  padding: 1.5rem;
}

.movie-title {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0 0 1rem;
  color: #333;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.movie-meta {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.origin {
  font-weight: 500;
}

.rating-badge {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  color: #333;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-weight: 600;
  font-size: 0.875rem;
  box-shadow: 0 2px 10px rgba(255, 215, 0, 0.3);
}

.rating-star {
  font-size: 0.875rem;
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
  .movies-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 1.5rem;
  }
}

@media (max-width: 768px) {
  .movies-container {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }
  
  .nav-content {
    flex-wrap: wrap;
    gap: 1rem;
    padding: 1rem;
  }
  
  .nav-search {
    order: 3;
    flex-basis: 100%;
    max-width: none;
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
  
  .filters-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .content-section {
    margin: 1rem;
    padding: 1rem;
    border-radius: 16px;
  }
  
  .movies-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 1rem;
  }
  
  .section-header h2 {
    font-size: 2rem;
  }
  
  .filters-header h2 {
    font-size: 1.5rem;
  }
  
  .pagination-container {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }
}

@media (max-width: 480px) {
  .movies-grid {
    grid-template-columns: 1fr;
  }
  
  .movie-card {
    max-width: 350px;
    margin: 0 auto;
  }
  
  .filters-container {
    padding: 0 1rem;
  }
}
</style>
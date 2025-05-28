<template>
  <div class="notfound-container">
    <div class="notfound-content">
      <div class="error-animation">
        <div class="number">4</div>
        <div class="film-reel">
          <div class="reel"></div>
          <div class="reel"></div>
        </div>
        <div class="number">4</div>
      </div>
      
      <h1 class="error-title">页面未找到</h1>
      <p class="error-message">
        哎呀！您访问的页面似乎不存在，<br>
        可能是链接错误或页面已被移除。
      </p>
      
      <div class="action-buttons">
        <button @click="goHome" class="primary-btn">
          🏠 返回首页
        </button>
        <button @click="goBack" class="secondary-btn">
          ← 返回上页
        </button>
      </div>
      
      <div class="suggestions">
        <h3>您可以尝试：</h3>
        <ul>
          <li>检查网址是否正确</li>
          <li>浏览我们的 <a @click="goHome" class="link">电影首页</a></li>
          <li>查看您的 <a @click="goToFavorites" class="link">收藏列表</a></li>
          <li><a @click="goToFeedback" class="link">反馈问题</a> 给我们</li>
        </ul>
      </div>
    </div>
    
    <div class="background-decoration">
      <div class="floating-element element-1">🎬</div>
      <div class="floating-element element-2">🍿</div>
      <div class="floating-element element-3">🎭</div>
      <div class="floating-element element-4">🎪</div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'NotFound',
  setup() {
    const router = useRouter()
    
    const goHome = () => {
      router.push('/movies')
    }
    
    const goBack = () => {
      if (window.history.length > 1) {
        router.go(-1)
      } else {
        router.push('/movies')
      }
    }
    
    const goToFavorites = () => {
      const token = localStorage.getItem('token')
      if (token) {
        router.push('/favorites')
      } else {
        router.push('/login')
      }
    }
    
    const goToFeedback = () => {
      const token = localStorage.getItem('token')
      if (token) {
        router.push('/feedback')
      } else {
        router.push('/login')
      }
    }
    
    return {
      goHome,
      goBack,
      goToFavorites,
      goToFeedback
    }
  }
}
</script>

<style scoped>
.notfound-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  padding: 2rem;
}

.notfound-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  padding: 3rem;
  text-align: center;
  max-width: 600px;
  width: 100%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
}

.error-animation {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 2rem;
  gap: 1rem;
}

.number {
  font-size: 6rem;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: bounce 2s infinite;
}

.film-reel {
  position: relative;
  width: 100px;
  height: 100px;
  animation: spin 3s linear infinite;
}

.reel {
  position: absolute;
  width: 100px;
  height: 100px;
  border: 8px solid #667eea;
  border-radius: 50%;
}

.reel:before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 20px;
  height: 20px;
  background: #667eea;
  border-radius: 50%;
}

.reel:after {
  content: '';
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  bottom: 10px;
  border: 2px dashed rgba(102, 126, 234, 0.3);
  border-radius: 50%;
}

.error-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #333;
  margin: 0 0 1rem;
}

.error-message {
  font-size: 1.1rem;
  color: #666;
  line-height: 1.6;
  margin: 0 0 2rem;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.primary-btn, .secondary-btn {
  padding: 1rem 2rem;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 140px;
}

.primary-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.secondary-btn {
  background: transparent;
  color: #667eea;
  border: 2px solid #667eea;
}

.secondary-btn:hover {
  background: #667eea;
  color: white;
  transform: translateY(-2px);
}

.suggestions {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 1.5rem;
  text-align: left;
}

.suggestions h3 {
  color: #333;
  margin: 0 0 1rem;
  font-size: 1.1rem;
}

.suggestions ul {
  margin: 0;
  padding-left: 1.5rem;
  color: #666;
}

.suggestions li {
  margin-bottom: 0.5rem;
  line-height: 1.4;
}

.link {
  color: #667eea;
  cursor: pointer;
  text-decoration: underline;
  transition: all 0.3s ease;
}

.link:hover {
  color: #764ba2;
}

.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.floating-element {
  position: absolute;
  font-size: 3rem;
  opacity: 0.1;
  animation: float 6s ease-in-out infinite;
}

.element-1 {
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.element-2 {
  top: 20%;
  right: 15%;
  animation-delay: 1s;
}

.element-3 {
  bottom: 20%;
  left: 15%;
  animation-delay: 2s;
}

.element-4 {
  bottom: 10%;
  right: 10%;
  animation-delay: 3s;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-20px);
  }
  60% {
    transform: translateY(-10px);
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(30px, -30px) rotate(120deg);
  }
  66% {
    transform: translate(-20px, 20px) rotate(240deg);
  }
}

@media (max-width: 768px) {
  .notfound-content {
    padding: 2rem;
    margin: 1rem;
  }
  
  .number {
    font-size: 4rem;
  }
  
  .film-reel {
    width: 60px;
    height: 60px;
  }
  
  .reel {
    width: 60px;
    height: 60px;
    border-width: 5px;
  }
  
  .error-title {
    font-size: 2rem;
  }
  
  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .primary-btn, .secondary-btn {
    width: 100%;
    max-width: 200px;
  }
}
</style>
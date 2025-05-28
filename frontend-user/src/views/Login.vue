<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1 class="logo">🎬 电影世界</h1>
        <p class="subtitle">发现精彩电影世界</p>
      </div>
      
      <div class="tab-switch">
        <button 
          :class="['tab-btn', { active: isLogin }]" 
          @click="isLogin = true"
        >
          登录
        </button>
        <button 
          :class="['tab-btn', { active: !isLogin }]" 
          @click="isLogin = false"
        >
          注册
        </button>
      </div>

      <form @submit.prevent="handleSubmit" class="login-form">
        <div class="form-group">
          <input
            v-model="formData.username"
            type="text"
            placeholder="用户名"
            required
            class="form-input"
          />
        </div>
        
        <div class="form-group">
          <input
            v-model="formData.password"
            type="password"
            placeholder="密码"
            required
            class="form-input"
          />
        </div>
        
        <div v-if="!isLogin" class="form-group">
          <input
            v-model="formData.email"
            type="email"
            placeholder="邮箱"
            required
            class="form-input"
          />
        </div>

        <button type="submit" class="submit-btn" :disabled="loading">
          <span v-if="!loading">{{ isLogin ? '登录' : '注册' }}</span>
          <span v-else class="loading-spinner"></span>
        </button>
      </form>

      <div v-if="error" class="error-message">
        {{ error }}
      </div>
    </div>
    
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const isLogin = ref(true)
    const loading = ref(false)
    const error = ref('')
    
    const formData = ref({
      username: '',
      password: '',
      email: ''
    })

    const handleSubmit = async () => {
      error.value = ''
      loading.value = true
      
      try {
        const endpoint = isLogin.value ? '/api/auth/login' : '/api/auth/register'
        const body = isLogin.value 
          ? { username: formData.value.username, password: formData.value.password }
          : formData.value
          
        const response = await fetch(`http://localhost:8080${endpoint}`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(body)
        })

        const data = await response.json()
        
        if (response.ok) {
          // 保存token和用户信息
          localStorage.setItem('token', data.token)
          localStorage.setItem('username', data.username)
          localStorage.setItem('role', data.role)
          
          // 跳转到主页
          router.push('/movies')
        } else {
          error.value = data.message || '操作失败'
        }
      } catch (err) {
        error.value = '网络错误，请稍后重试'
      } finally {
        loading.value = false
      }
    }

    return {
      isLogin,
      loading,
      error,
      formData,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 3rem;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 420px;
  position: relative;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 2rem;
}

.logo {
  font-size: 2.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0;
}

.subtitle {
  color: #666;
  margin-top: 0.5rem;
  font-size: 1rem;
}

.tab-switch {
  display: flex;
  background: #f5f5f5;
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 2rem;
}

.tab-btn {
  flex: 1;
  padding: 0.75rem;
  border: none;
  background: transparent;
  color: #666;
  font-size: 1rem;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-btn.active {
  background: white;
  color: #667eea;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-input {
  width: 90%;
  padding: 1rem;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: #fafafa;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
}

.submit-btn {
  width: 100%;
  padding: 1rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-spinner {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-message {
  margin-top: 1rem;
  padding: 0.75rem;
  background: #fee;
  color: #c00;
  border-radius: 8px;
  font-size: 0.9rem;
  text-align: center;
}

.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -150px;
  left: -150px;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -100px;
  right: -100px;
  animation-delay: -5s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 70%;
  animation-delay: -10s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

@media (max-width: 768px) {
  .login-card {
    margin: 1rem;
    padding: 2rem;
  }
  
  .logo {
    font-size: 2rem;
  }
}
</style>
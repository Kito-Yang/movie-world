<template>
  <div class="qa-container">
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
            <span class="title-icon">🤖</span>
            <span class="title-text">智能问答</span>
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
      <!-- 欢迎区域 -->
      <div class="welcome-section" v-if="messages.length === 0">
        <div class="welcome-content">
          <div class="ai-avatar">
            <span class="avatar-emoji">🤖</span>
            <div class="avatar-glow"></div>
          </div>
          <h2>电影世界 智能助手</h2>
          <p>我是您的电影小助手，基于 DeepSeek-V3 模型，可以帮您解答关于电影的各种问题</p>
          
          <div class="suggested-questions">
            <h3>您可以问我：</h3>
            <div class="questions-grid">
              <button 
                v-for="question in suggestedQuestions" 
                :key="question.id"
                @click="askQuestion(question.text)"
                class="question-card"
              >
                <span class="question-icon">{{ question.icon }}</span>
                <span class="question-text">{{ question.text }}</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 聊天消息区域 -->
      <div class="chat-section" v-if="messages.length > 0">
        <div class="messages-container" ref="messagesContainer">
          <div 
            v-for="message in messages" 
            :key="message.id"
            class="message-wrapper"
            :class="{ 'user-message': message.type === 'user', 'ai-message': message.type === 'ai' }"
          >
            <div class="message-avatar">
              <span v-if="message.type === 'user'">👤</span>
              <span v-else class="ai-avatar-small">🤖</span>
            </div>
            <div class="message-content">
              <div class="message-bubble" :class="message.type">
                <div class="message-text" v-html="formatMessage(message.content)"></div>
                <div class="message-time">{{ formatTime(message.timestamp) }}</div>
              </div>
            </div>
          </div>
          
          <!-- 打字指示器 -->
          <div v-if="isTyping" class="message-wrapper ai-message typing-indicator">
            <div class="message-avatar">
              <span class="ai-avatar-small">🤖</span>
            </div>
            <div class="message-content">
              <div class="message-bubble ai typing">
                <div class="typing-animation">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="input-section">
        <div class="input-container">
          <div class="input-wrapper">
            <textarea
              v-model="inputMessage"
              @keydown="handleKeyDown"
              :disabled="isTyping"
              placeholder="问我任何关于电影的问题..."
              class="message-input"
              rows="1"
              ref="messageInput"
            ></textarea>
            <button 
              @click="sendMessage"
              :disabled="!inputMessage.trim() || isTyping"
              class="send-btn"
            >
              <svg v-if="!isTyping" width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
                <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z"/>
              </svg>
              <div v-else class="send-spinner"></div>
            </button>
          </div>
          
          <!-- 快捷操作 -->
          <div class="quick-actions" v-if="messages.length > 0">
            <button @click="clearChat" class="quick-btn clear-btn">
              <span class="btn-icon">🗑️</span>
              <span>清空对话</span>
            </button>
            <button @click="showSuggestions = !showSuggestions" class="quick-btn suggest-btn">
              <span class="btn-icon">💡</span>
              <span>问题建议</span>
            </button>
          </div>
          
          <!-- 建议问题 -->
          <div v-if="showSuggestions && messages.length > 0" class="suggestions-panel">
            <div class="suggestions-header">
              <h4>推荐问题</h4>
              <button @click="showSuggestions = false" class="close-btn">✕</button>
            </div>
            <div class="suggestions-list">
              <button 
                v-for="question in suggestedQuestions.slice(0, 4)" 
                :key="question.id"
                @click="askQuestion(question.text)"
                class="suggestion-item"
              >
                <span class="suggestion-icon">{{ question.icon }}</span>
                <span class="suggestion-text">{{ question.text }}</span>
              </button>
            </div>
          </div>
          
          <!-- API状态指示器 -->
          <div class="api-status" v-if="apiError">
            <div class="error-message">
              <span class="error-icon">⚠️</span>
              <span class="error-text">{{ apiError }}</span>
              <button @click="clearApiError" class="error-close">✕</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'SmartQA',
  setup() {
    const router = useRouter()
    const messages = ref([])
    const inputMessage = ref('')
    const isTyping = ref(false)
    const showSuggestions = ref(false)
    const messagesContainer = ref(null)
    const messageInput = ref(null)
    const apiError = ref('')
    
    const username = localStorage.getItem('username') || 'User'

    // DeepSeek API 配置
    const DEEPSEEK_API_CONFIG = {
      baseURL: 'https://api.deepseek.com',
      apiKey: 'sk-3779cebb7e8a4f6e97e6b0d7456643fc',
      model: 'deepseek-chat'
    }

    // 建议问题
    const suggestedQuestions = ref([
      { id: 1, icon: '🎬', text: '推荐一些好看的科幻电影' },
      { id: 2, icon: '⭐', text: '最近有什么高分电影值得看' },
      { id: 3, icon: '🏆', text: '奥斯卡获奖电影有哪些推荐' },
      { id: 4, icon: '💕', text: '有什么经典的爱情电影' },
      { id: 5, icon: '😄', text: '推荐一些轻松搞笑的喜剧片' },
      { id: 6, icon: '🔍', text: '如何在电影世界中搜索电影' }
    ])

    // 获取请求头
    const getHeaders = () => ({
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'application/json'
    })

    // 调用DeepSeek API
    const callDeepSeekAPI = async (userMessage, conversationHistory = []) => {
      try {
        // 构建系统提示词
        const systemPrompt = `你是电影世界电影平台的智能助手，专门帮助用户解答关于电影的各种问题。你的特点：

1. 专业性：对电影知识了如指掌，包括电影推荐、影评、演员导演信息等
2. 友好性：语言亲切自然，用表情符号让回答更生动
3. 实用性：提供具体可行的建议，包含电影评分、年份等详细信息
4. 平台相关：了解电影世界平台的功能，如搜索、收藏、分类浏览等

回答格式要求：
- 使用 markdown 格式，重要内容用**粗体**标记
- 用合适的表情符号增加亲和力
- 如果推荐电影，请包含简短的评价和推荐理由
- 保持回答简洁但信息丰富`

        // 构建消息历史
        const messages = [
          { role: 'system', content: systemPrompt }
        ]
        
        // 添加历史对话（最近5轮对话）
        const recentHistory = conversationHistory.slice(-10)
        recentHistory.forEach(msg => {
          messages.push({
            role: msg.type === 'user' ? 'user' : 'assistant',
            content: msg.content
          })
        })
        
        // 添加当前用户消息
        messages.push({ role: 'user', content: userMessage })

        const response = await fetch(`${DEEPSEEK_API_CONFIG.baseURL}/chat/completions`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${DEEPSEEK_API_CONFIG.apiKey}`
          },
          body: JSON.stringify({
            model: DEEPSEEK_API_CONFIG.model,
            messages: messages,
            temperature: 0.7,
            max_tokens: 1000,
            stream: false
          })
        })

        if (!response.ok) {
          const errorData = await response.json().catch(() => ({}))
          throw new Error(`API请求失败: ${response.status} - ${errorData.error?.message || response.statusText}`)
        }

        const data = await response.json()
        return data.choices[0]?.message?.content || '抱歉，我现在无法回答您的问题。'

      } catch (error) {
        console.error('DeepSeek API调用失败:', error)
        
        // 设置用户友好的错误信息
        if (error.message.includes('401')) {
          throw new Error('API密钥无效，请检查配置')
        } else if (error.message.includes('429')) {
          throw new Error('请求过于频繁，请稍后再试')
        } else if (error.message.includes('500')) {
          throw new Error('服务器繁忙，请稍后再试')
        } else {
          throw new Error('网络连接异常，请检查网络后重试')
        }
      }
    }

    // 发送消息
    const sendMessage = async () => {
      if (!inputMessage.value.trim() || isTyping.value) return

      // 清除之前的错误
      apiError.value = ''

      const userMessage = {
        id: Date.now(),
        type: 'user',
        content: inputMessage.value.trim(),
        timestamp: new Date()
      }

      messages.value.push(userMessage)
      const question = inputMessage.value.trim()
      inputMessage.value = ''
      showSuggestions.value = false

      // 自动调整输入框高度
      if (messageInput.value) {
        messageInput.value.style.height = 'auto'
      }

      await nextTick()
      scrollToBottom()

      // 显示打字指示器
      isTyping.value = true

      try {
        // 调用DeepSeek API
        const aiResponse = await callDeepSeekAPI(question, messages.value)
        
        const aiMessage = {
          id: Date.now() + 1,
          type: 'ai',
          content: aiResponse,
          timestamp: new Date()
        }
        
        messages.value.push(aiMessage)
        
      } catch (error) {
        console.error('发送消息失败:', error)
        
        // 显示错误信息
        apiError.value = error.message
        
        const errorMessage = {
          id: Date.now() + 1,
          type: 'ai',
          content: `抱歉，我遇到了一些问题：${error.message}

请稍后重试，或者尝试重新表述您的问题。如果问题持续存在，请联系技术支持。`,
          timestamp: new Date()
        }
        messages.value.push(errorMessage)
        
      } finally {
        isTyping.value = false
        nextTick(() => scrollToBottom())
      }
    }

    // 处理快捷提问
    const askQuestion = (question) => {
      inputMessage.value = question
      showSuggestions.value = false
      sendMessage()
    }

    // 处理键盘事件
    const handleKeyDown = (event) => {
      if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault()
        sendMessage()
      }
      
      // 自动调整输入框高度
      nextTick(() => {
        const textarea = event.target
        textarea.style.height = 'auto'
        textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px'
      })
    }

    // 清空聊天
    const clearChat = () => {
      if (confirm('确定要清空所有对话记录吗？')) {
        messages.value = []
        showSuggestions.value = false
        apiError.value = ''
      }
    }

    // 清除API错误
    const clearApiError = () => {
      apiError.value = ''
    }

    // 滚动到底部
    const scrollToBottom = () => {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }

    // 格式化消息内容
    const formatMessage = (content) => {
      return content
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/\n/g, '<br>')
        .replace(/•/g, '<span class="bullet">•</span>')
    }

    // 格式化时间
    const formatTime = (timestamp) => {
      const now = new Date()
      const time = new Date(timestamp)
      const diff = now - time
      
      if (diff < 60000) { // 1分钟内
        return '刚刚'
      } else if (diff < 3600000) { // 1小时内
        return `${Math.floor(diff / 60000)}分钟前`
      } else if (diff < 86400000) { // 1天内
        return `${Math.floor(diff / 3600000)}小时前`
      } else {
        return time.toLocaleTimeString('zh-CN', { 
          hour: '2-digit', 
          minute: '2-digit' 
        })
      }
    }

    // 导航功能
    const goHome = () => router.push('/movies')
    const goToProfile = () => router.push('/profile')
    const goToFavorites = () => router.push('/favorites')
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
    })

    return {
      messages,
      inputMessage,
      isTyping,
      showSuggestions,
      messagesContainer,
      messageInput,
      username,
      suggestedQuestions,
      apiError,
      sendMessage,
      askQuestion,
      handleKeyDown,
      clearChat,
      clearApiError,
      formatMessage,
      formatTime,
      goHome,
      goToProfile,
      goToFavorites,
      goToFeedback,
      logout
    }
  }
}
</script>

<style scoped>
/* 全局容器 */
.qa-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-attachment: fixed;
  display: flex;
  flex-direction: column;
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
  animation: robot-blink 3s infinite;
}

@keyframes robot-blink {
  0%, 90%, 100% { transform: scale(1); }
  95% { transform: scale(1.1); }
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
  flex: 1;
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  margin: 2rem;
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 欢迎区域 */
.welcome-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.welcome-content {
  text-align: center;
  max-width: 800px;
}

.ai-avatar {
  position: relative;
  width: 120px;
  height: 120px;
  margin: 0 auto 2rem;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 4rem;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.avatar-emoji {
  animation: robot-wave 2s infinite;
}

@keyframes robot-wave {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(-10deg); }
  75% { transform: rotate(10deg); }
}

.avatar-glow {
  position: absolute;
  inset: -20px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  filter: blur(30px);
  opacity: 0.6;
  animation: pulse-glow 3s infinite;
}

@keyframes pulse-glow {
  0%, 100% { transform: scale(0.8); opacity: 0.6; }
  50% { transform: scale(1.2); opacity: 0.9; }
}

.welcome-content h2 {
  font-size: 2.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 1rem;
}

.welcome-content p {
  font-size: 1.2rem;
  color: #666;
  margin: 0 0 3rem;
}

.suggested-questions h3 {
  font-size: 1.5rem;
  color: #333;
  margin: 0 0 1.5rem;
}

.questions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

.question-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.5rem;
  background: rgba(255, 255, 255, 0.8);
  border: 2px solid rgba(102, 126, 234, 0.1);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: left;
}

.question-card:hover {
  background: rgba(102, 126, 234, 0.1);
  border-color: rgba(102, 126, 234, 0.3);
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.2);
}

.question-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.question-text {
  color: #333;
  font-weight: 500;
}

/* 聊天区域 */
.chat-section {
  flex: 1;
  padding: 2rem;
  padding-bottom: 0;
}

.messages-container {
  height: 500px;
  overflow-y: auto;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  scroll-behavior: smooth;
}

.messages-container::-webkit-scrollbar {
  width: 6px;
}

.messages-container::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 3px;
}

.messages-container::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.3);
  border-radius: 3px;
}

.messages-container::-webkit-scrollbar-thumb:hover {
  background: rgba(102, 126, 234, 0.5);
}

.message-wrapper {
  display: flex;
  gap: 1rem;
  animation: messageSlideIn 0.3s ease;
}

@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  flex-shrink: 0;
}

.user-message .message-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.ai-message .message-avatar {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.ai-avatar-small {
  animation: robot-blink 3s infinite;
}

.message-content {
  flex: 1;
  max-width: 70%;
}

.message-bubble {
  padding: 1rem 1.5rem;
  border-radius: 20px;
  position: relative;
  word-wrap: break-word;
}

.message-bubble.user {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 8px;
}

.message-bubble.ai {
  background: rgba(102, 126, 234, 0.1);
  color: #333;
  border-bottom-left-radius: 8px;
}

.message-text {
  line-height: 1.6;
  margin-bottom: 0.5rem;
}

.message-text :deep(strong) {
  font-weight: 600;
}

.message-text :deep(.bullet) {
  color: #667eea;
  font-weight: bold;
  margin-right: 0.5rem;
}

.message-time {
  font-size: 0.75rem;
  opacity: 0.7;
  text-align: right;
}

.user-message .message-time {
  color: rgba(255, 255, 255, 0.8);
}

.ai-message .message-time {
  color: #666;
}

/* 打字指示器 */
.typing-indicator .message-bubble {
  padding: 1rem 1.5rem;
}

.typing-animation {
  display: flex;
  gap: 0.25rem;
}

.typing-animation span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #667eea;
  animation: typing 1.4s infinite;
}

.typing-animation span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-animation span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.5;
  }
  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

/* 输入区域 */
.input-section {
  padding: 2rem;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
}

.input-container {
  max-width: 800px;
  margin: 0 auto;
}

.input-wrapper {
  display: flex;
  gap: 1rem;
  align-items: flex-end;
  margin-bottom: 1rem;
}

.message-input {
  flex: 1;
  min-height: 48px;
  max-height: 120px;
  padding: 1rem 1.5rem;
  border: 2px solid rgba(102, 126, 234, 0.2);
  border-radius: 24px;
  font-size: 1rem;
  resize: none;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
}

.message-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.message-input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.send-btn {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.send-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 快捷操作 */
.quick-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-bottom: 1rem;
}

.quick-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border: none;
  border-radius: 20px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.quick-btn:hover {
  background: rgba(102, 126, 234, 0.2);
  transform: translateY(-1px);
}

.clear-btn:hover {
  background: rgba(255, 107, 107, 0.1);
  color: #ff6b6b;
}

/* 建议面板 */
.suggestions-panel {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 16px;
  padding: 1rem;
  margin-bottom: 1rem;
  animation: panelSlideIn 0.3s ease;
}

@keyframes panelSlideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.suggestions-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.suggestions-header h4 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  font-size: 1.2rem;
  padding: 0.25rem;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.1);
  color: #333;
}

.suggestions-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 0.5rem;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  background: rgba(102, 126, 234, 0.05);
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: left;
}

.suggestion-item:hover {
  background: rgba(102, 126, 234, 0.1);
  transform: translateY(-1px);
}

.suggestion-icon {
  font-size: 1.2rem;
}

.suggestion-text {
  color: #333;
  font-size: 0.9rem;
}

/* API状态指示器 */
.api-status {
  margin-top: 1rem;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  background: rgba(255, 107, 107, 0.1);
  border: 1px solid rgba(255, 107, 107, 0.3);
  border-radius: 12px;
  color: #d63031;
  animation: errorSlideIn 0.3s ease;
}

@keyframes errorSlideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.error-icon {
  font-size: 1.2rem;
  flex-shrink: 0;
}

.error-text {
  flex: 1;
  font-size: 0.9rem;
}

.error-close {
  background: none;
  border: none;
  color: #d63031;
  cursor: pointer;
  font-size: 1.1rem;
  padding: 0.25rem;
  border-radius: 50%;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.error-close:hover {
  background: rgba(214, 48, 49, 0.1);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .content-section {
    margin: 1rem;
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
    border-radius: 16px;
  }
  
  .welcome-content h2 {
    font-size: 2rem;
  }
  
  .welcome-content p {
    font-size: 1rem;
  }
  
  .questions-grid {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }
  
  .question-card {
    padding: 1rem;
  }
  
  .messages-container {
    height: 300px;
    padding: 0.5rem;
  }
  
  .message-content {
    max-width: 85%;
  }
  
  .input-section {
    padding: 1rem;
  }
  
  .input-wrapper {
    gap: 0.75rem;
  }
  
  .message-input {
    padding: 0.875rem 1.25rem;
  }
  
  .send-btn {
    width: 44px;
    height: 44px;
  }
  
  .quick-actions {
    flex-direction: column;
    align-items: center;
    gap: 0.5rem;
  }
  
  .suggestions-list {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .ai-avatar {
    width: 80px;
    height: 80px;
    font-size: 3rem;
  }
  
  .welcome-content h2 {
    font-size: 1.5rem;
  }
  
  .messages-container {
    height: 250px;
  }
  
  .message-bubble {
    padding: 0.875rem 1.25rem;
  }
  
  .input-section {
    padding: 0.75rem;
  }
}
</style>
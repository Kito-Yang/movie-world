<template>
  <div>
    <div class="page-header">
      <h2>反馈管理</h2>
      <div class="header-actions">
        <el-button @click="loadFeedbacks" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>
    
    <el-card>
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部反馈" name="all" />
        <el-tab-pane label="待处理" name="0" />
        <el-tab-pane label="处理中" name="1" />
        <el-tab-pane label="已解决" name="2" />
        <el-tab-pane label="已关闭" name="3" />
      </el-tabs>
      
      <el-table 
        :data="feedbacks" 
        v-loading="loading" 
        stripe 
        empty-text="暂无反馈数据"
        @sort-change="handleSortChange"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="60" sortable />
        <el-table-column prop="username" label="用户" min-width="120" show-overflow-tooltip />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeColor(row.type)">{{ getTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusColor(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" min-width="160" sortable>
          <template #default="{ row }">
            {{ formatDate(row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewFeedback(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button 
              size="small" 
              type="primary" 
              @click="replyFeedback(row)"
              :disabled="row.status === 3"
            >
              <el-icon><ChatLineRound /></el-icon>
              回复
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-if="total > 0"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
        :pager-count="7"
        :total-text="`共 {total} 条`"
        :page-size-text="条/页"
        :goto-text="前往"
        :page-text="页"
        :disabled="loading"
      />
    </el-card>

    <!-- 查看详情对话框 -->
    <el-dialog 
      v-model="viewDialogVisible" 
      title="反馈详情" 
      width="800px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border v-if="currentFeedback">
        <el-descriptions-item label="用户">
          {{ currentFeedback.username || '匿名用户' }}
        </el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="getTypeColor(currentFeedback.type)">
            {{ getTypeText(currentFeedback.type) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusColor(currentFeedback.status)">
            {{ getStatusText(currentFeedback.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ formatDate(currentFeedback.createdTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="标题" span="2">
          {{ currentFeedback.title }}
        </el-descriptions-item>
        <el-descriptions-item label="内容" span="2">
          <div class="feedback-content">{{ currentFeedback.content }}</div>
        </el-descriptions-item>
        <el-descriptions-item 
          label="管理员回复" 
          span="2" 
          v-if="currentFeedback.adminReply"
        >
          <div class="admin-reply">{{ currentFeedback.adminReply }}</div>
        </el-descriptions-item>
        <el-descriptions-item 
          label="回复时间" 
          span="2" 
          v-if="currentFeedback.replyTime"
        >
          {{ formatDate(currentFeedback.replyTime) }}
        </el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
        <el-button 
          type="primary" 
          @click="quickReply"
          :disabled="currentFeedback?.status === 3"
        >
          快速回复
        </el-button>
      </template>
    </el-dialog>

    <!-- 回复对话框 -->
    <el-dialog 
      v-model="replyDialogVisible" 
      title="回复反馈" 
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form 
        ref="replyFormRef"
        :model="replyForm" 
        :rules="replyRules"
        label-width="100px"
      >
        <el-form-item label="反馈标题">
          <el-text>{{ currentFeedback?.title }}</el-text>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="replyForm.status" style="width: 100%" placeholder="请选择状态">
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已解决" :value="2" />
            <el-option label="已关闭" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="回复内容" prop="adminReply">
          <el-input
            v-model="replyForm.adminReply"
            type="textarea"
            :rows="6"
            placeholder="请输入回复内容"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="cancelReply">取消</el-button>
        <el-button type="primary" @click="submitReply" :loading="replyLoading">
          提交回复
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

// 响应式数据
const feedbacks = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const activeTab = ref('all')

// 对话框状态
const viewDialogVisible = ref(false)
const replyDialogVisible = ref(false)
const currentFeedback = ref(null)
const replyLoading = ref(false)
const replyFormRef = ref()

// 排序和搜索
const sortField = ref('')
const sortOrder = ref('')

// 回复表单
const replyForm = reactive({
  status: 0,
  adminReply: ''
})

// 表单验证规则
const replyRules = {
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  adminReply: [
    { required: true, message: '请输入回复内容', trigger: 'blur' },
    { min: 5, message: '回复内容至少5个字符', trigger: 'blur' }
  ]
}

// 计算属性
const statusTabMap = computed(() => ({
  'all': '全部反馈',
  '0': '待处理',
  '1': '处理中', 
  '2': '已解决',
  '3': '已关闭'
}))

// 加载反馈列表
const loadFeedbacks = async () => {
  loading.value = true
  try {
    let url = '/feedback/admin/all'
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }

    // 根据状态选择不同的API端点
    if (activeTab.value !== 'all') {
      url = `/feedback/admin/status/${activeTab.value}`
    }

    // 添加排序参数
    if (sortField.value && sortOrder.value) {
      params.sortField = sortField.value
      params.sortOrder = sortOrder.value
    }
    
    const response = await api.get(url, { params })
    
    // 统一处理响应数据结构
    if (response.data) {
      if (response.data.records !== undefined) {
        // 分页数据结构
        feedbacks.value = response.data.records || []
        total.value = response.data.total || 0
      } else if (Array.isArray(response.data)) {
        // 数组数据结构
        feedbacks.value = response.data
        total.value = response.data.length
      } else {
        // 其他情况
        feedbacks.value = []
        total.value = 0
      }
    } else {
      feedbacks.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载反馈列表失败:', error)
    ElMessage.error('加载反馈列表失败，请稍后重试')
    feedbacks.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 处理标签切换
const handleTabChange = (tabName) => {
  activeTab.value = tabName
  currentPage.value = 1
  loadFeedbacks()
}

// 处理排序变化
const handleSortChange = ({ prop, order }) => {
  sortField.value = prop || ''
  sortOrder.value = order === 'ascending' ? 'asc' : order === 'descending' ? 'desc' : ''
  currentPage.value = 1
  loadFeedbacks()
}

// 处理分页大小变化
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  loadFeedbacks()
}

// 处理当前页变化
const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  loadFeedbacks()
}

// 查看反馈详情
const viewFeedback = (row) => {
  currentFeedback.value = { ...row }
  viewDialogVisible.value = true
}

// 快速回复（从查看对话框跳转到回复对话框）
const quickReply = () => {
  viewDialogVisible.value = false
  replyFeedback(currentFeedback.value)
}

// 回复反馈
const replyFeedback = (row) => {
  currentFeedback.value = { ...row }
  replyForm.status = row.status
  replyForm.adminReply = row.adminReply || ''
  replyDialogVisible.value = true
}

// 取消回复
const cancelReply = () => {
  replyDialogVisible.value = false
  resetReplyForm()
}

// 重置回复表单
const resetReplyForm = () => {
  replyForm.status = 0
  replyForm.adminReply = ''
  if (replyFormRef.value) {
    replyFormRef.value.clearValidate()
  }
}

// 提交回复
const submitReply = async () => {
  if (!replyFormRef.value || !currentFeedback.value) return
  
  try {
    const valid = await replyFormRef.value.validate()
    if (!valid) return
    
    replyLoading.value = true
    
    await api.put(`/feedback/admin/reply/${currentFeedback.value.id}`, {
      status: replyForm.status,
      adminReply: replyForm.adminReply
    })
    
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    resetReplyForm()
    loadFeedbacks()
    
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error(error.response?.data?.message || '回复失败，请稍后重试')
  } finally {
    replyLoading.value = false
  }
}

// 获取类型文本
const getTypeText = (type) => {
  const typeMap = {
    'GENERAL': '一般反馈',
    'BUG': '问题报告', 
    'SUGGESTION': '建议',
    'COMPLAINT': '投诉',
    'OTHER': '其他'
  }
  return typeMap[type] || type
}

// 获取类型颜色
const getTypeColor = (type) => {
  const colorMap = {
    'GENERAL': '',
    'BUG': 'danger',
    'SUGGESTION': 'success',
    'COMPLAINT': 'warning',
    'OTHER': 'info'
  }
  return colorMap[type] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待处理',
    1: '处理中',
    2: '已解决', 
    3: '已关闭'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'info'
  }
  return colorMap[status] || ''
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '暂无'
  try {
    return new Date(dateString).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (error) {
    return '日期格式错误'
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadFeedbacks()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.feedback-content,
.admin-reply {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
  max-height: 200px;
  overflow-y: auto;
  padding: 8px;
  background-color: #f9f9f9;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.admin-reply {
  background-color: #f0f9ff;
  border-color: #b3d8ff;
}

.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 确保表格撑满容器 */
:deep(.el-table) {
  width: 100% !important;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__body-wrapper) {
  width: 100% !important;
}

:deep(.el-table__header-wrapper) {
  background-color: #f8f9fa;
}

:deep(.el-tabs__item) {
  font-weight: 500;
}

:deep(.el-tabs__item.is-active) {
  color: #409eff;
  font-weight: 600;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
  color: #606266;
}

:deep(.el-dialog__header) {
  background-color: #f8f9fa;
  border-bottom: 1px solid #e4e7ed;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  color: #303133;
}

/* 分页器中文样式优化 */
:deep(.el-pagination) {
  --el-pagination-font-size: 14px;
  --el-pagination-bg-color: #f4f4f5;
  --el-pagination-text-color: #606266;
  --el-pagination-border-radius: 4px;
}

:deep(.el-pagination__total) {
  margin-right: 10px;
  font-weight: 400;
}

:deep(.el-pagination__sizes) {
  margin-right: 10px;
}

:deep(.el-pagination__jump) {
  margin-left: 24px;
}

:deep(.el-pagination__jump .el-pagination__goto) {
  margin-right: 8px;
}

:deep(.el-pagination__jump .el-pagination__classifier) {
  margin-left: 8px;
}
</style>
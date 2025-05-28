<template>
  <div>
    <div class="page-header">
      <h2>电影管理</h2>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        添加电影
      </el-button>
    </div>
    
    <el-card>
      <el-table :data="movies" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="电影名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" min-width="100" />
        <el-table-column prop="origin" label="产地" min-width="100" />
        <el-table-column prop="year" label="年份" width="80" />
        <el-table-column prop="rate" label="评分" width="80" />
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="editMovie(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteMovie(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadMovies"
        @current-change="loadMovies"
        style="margin-top: 20px; justify-content: center;"
        :pager-count="7"
        :total-text="`共 {total} 条`"
        :page-size-text="条/页"
        :goto-text="前往"
        :page-text="页"
        :disabled="loading"
      />
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form
        ref="movieFormRef"
        :model="movieForm"
        :rules="movieRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电影ID" prop="movieId">
              <el-input v-model.number="movieForm.movieId" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电影名称" prop="name">
              <el-input v-model="movieForm.name" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="类型" prop="type">
              <el-input v-model="movieForm.type" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产地" prop="origin">
              <el-input v-model="movieForm.origin" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年份" prop="year">
              <el-input v-model.number="movieForm.year" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="时长" prop="duration">
              <el-input v-model.number="movieForm.duration">
                <template #append>分钟</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="评分" prop="rate">
              <el-input v-model.number="movieForm.rate" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="投票人数" prop="turnout">
              <el-input v-model.number="movieForm.turnout">
                <template #append>人</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="首映地点" prop="premiereLocation">
          <el-input v-model="movieForm.premiereLocation" />
        </el-form-item>
        
        <el-form-item label="上映时间" prop="time">
          <el-date-picker
            v-model="movieForm.time"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMovie" :loading="saveLoading">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

const movies = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const editId = ref(null)
const saveLoading = ref(false)
const movieFormRef = ref()

const movieForm = reactive({
  movieId: '',
  name: '',
  type: '',
  origin: '',
  year: new Date().getFullYear(),
  duration: '',
  rate: '',
  turnout: '',
  premiereLocation: '',
  time: new Date()
})

const movieRules = {
  movieId: [{ required: true, message: '请输入电影ID', trigger: 'blur' }],
  name: [{ required: true, message: '请输入电影名称', trigger: 'blur' }],
  type: [{ required: true, message: '请输入电影类型', trigger: 'blur' }],
  origin: [{ required: true, message: '请输入产地', trigger: 'blur' }],
  year: [{ required: true, message: '请输入年份', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入时长', trigger: 'blur' }],
  rate: [{ required: true, message: '请输入评分', trigger: 'blur' }]
}

const loadMovies = async () => {
  loading.value = true
  try {
    const response = await api.get('/movies/page', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    })
    movies.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('加载电影列表失败')
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  dialogTitle.value = '添加电影'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const editMovie = (row) => {
  dialogTitle.value = '编辑电影'
  isEdit.value = true
  editId.value = row.id
  Object.assign(movieForm, row)
  movieForm.time = new Date(row.time)
  dialogVisible.value = true
}

const saveMovie = async () => {
  if (!movieFormRef.value) return
  
  try {
    const valid = await movieFormRef.value.validate()
    if (!valid) return
    
    saveLoading.value = true
    const data = { ...movieForm }
    data.time = data.time.toISOString()
    
    if (isEdit.value) {
      await api.put(`/movies/${editId.value}`, data)
      ElMessage.success('更新成功')
    } else {
      await api.post('/movies', data)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadMovies()
  } catch (error) {
    // Error handled by interceptor
  } finally {
    saveLoading.value = false
  }
}

const deleteMovie = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这部电影吗？', '确认删除', {
      type: 'warning'
    })
    await api.delete(`/movies/${row.id}`)
    ElMessage.success('删除成功')
    loadMovies()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const resetForm = () => {
  Object.assign(movieForm, {
    movieId: '',
    name: '',
    type: '',
    origin: '',
    year: new Date().getFullYear(),
    duration: '',
    rate: '',
    turnout: '',
    premiereLocation: '',
    time: new Date()
  })
}

onMounted(() => {
  loadMovies()
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
}

/* 确保表格撑满容器 */
:deep(.el-table) {
  width: 100% !important;
}

:deep(.el-table__body-wrapper) {
  width: 100% !important;
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
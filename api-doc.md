# 电影世界 API 接口文档

## 📋 目录

- 认证说明
- 响应格式说明
- 用户端接口
- 管理端接口

------

## 认证说明

### JWT Token 认证

所有需要认证的接口都需要在请求头中包含 JWT Token：

```http
Authorization: Bearer your-jwt-token-here
```

### 权限等级

- **公开接口**：无需认证
- **用户接口**：需要登录认证
- **管理员接口**：需要管理员权限

------

## 响应格式说明

### 成功响应

```json
{
  "data": "响应数据",
  "message": "操作成功"
}
```

### 错误响应

```json
{
  "message": "错误信息"
}
```

### 分页响应格式

```json
{
  "records": [],
  "total": 100,
  "size": 10,
  "current": 1,
  "pages": 10
}
```

------

# 用户端接口

## 🔐 用户认证

### 1. 用户注册

**POST** `/api/auth/register`

**权限：** 公开

**请求体：**

```json
{
  "username": "testuser",
  "password": "123456",
  "email": "test@example.com"
}
```

**响应：**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "testuser",
  "role": "USER",
  "message": "注册成功"
}
```

### 2. 用户登录

**POST** `/api/auth/login`

**权限：** 公开

**请求体：**

```json
{
  "username": "testuser",
  "password": "123456"
}
```

**响应：**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "testuser",
  "role": "USER",
  "message": "登录成功"
}
```

### 3. 获取当前用户信息

**GET** `/api/auth/me`

**权限：** 需要登录

**请求头：**

```http
Authorization: Bearer your-jwt-token
```

**响应：**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "testuser",
  "role": "USER",
  "message": "获取用户信息成功"
}
```

------

## 🎬 电影查询（公开接口）

### 1. 获取所有电影

**GET** `/api/movies`

**权限：** 公开

**响应：**

```json
[
  {
    "id": 1,
    "movieId": 1001,
    "name": "阿凡达",
    "turnout": 2000000,
    "type": "科幻",
    "origin": "美国",
    "time": "2023-12-01T00:00:00",
    "duration": 162,
    "year": 2023,
    "rate": 8.5,
    "premiereLocation": "北京"
  }
]
```

### 2. 分页获取电影

**GET** `/api/movies/page`

**权限：** 公开

**查询参数：**

- `pageNum`: 页码（默认1）
- `pageSize`: 每页数量（默认10）

**响应：**

```json
{
  "records": [
    {
      "id": 1,
      "name": "阿凡达",
      "type": "科幻"
    }
  ],
  "total": 100,
  "size": 10,
  "current": 1,
  "pages": 10
}
```

### 3. 根据ID获取电影

**GET** `/api/movies/{id}`

**权限：** 公开

**路径参数：**

- `id`: 电影ID

### 4. 搜索电影

**GET** `/api/movies/search`

**权限：** 公开

**查询参数：**

- `name`: 电影名称（模糊搜索）

### 5. 分页搜索电影

**GET** `/api/movies/search/page`

**权限：** 公开

**查询参数：**

- `name`: 电影名称
- `pageNum`: 页码（默认1）
- `pageSize`: 每页数量（默认10）

### 6. 按年份查询电影

**GET** `/api/movies/year/{year}`

**权限：** 公开

**路径参数：**

- `year`: 年份

### 7. 按类型查询电影

**GET** `/api/movies/type/{type}`

**权限：** 公开

**路径参数：**

- `type`: 电影类型

### 8. 多条件查询电影

**GET** `/api/movies/conditions/page`

**权限：** 公开

**查询参数：**

- `name`: 电影名称（可选）
- `type`: 电影类型（可选）
- `year`: 年份（可选）
- `origin`: 产地（可选）
- `pageNum`: 页码（默认1）
- `pageSize`: 每页数量（默认10）

### 9. 获取所有电影类型

**GET** `/api/movies/types`

**权限：** 公开

**响应：**

```json
["动作", "喜剧", "爱情", "科幻", "恐怖"]
```

------

## 👤 用户信息管理

### 1. 获取用户详细信息

**GET** `/api/user/profile`

**权限：** 需要登录

**响应：**

```json
{
  "id": 1,
  "username": "testuser",
  "email": "test@example.com",
  "role": "USER",
  "createdTime": "2023-01-01T00:00:00",
  "updatedTime": "2023-01-01T00:00:00",
  "status": 1
}
```

### 2. 更新用户信息

**PUT** `/api/user/profile`

**权限：** 需要登录

**请求体：**

```json
{
  "email": "newemail@example.com"
}
```

**响应：**

```json
{
  "message": "用户信息更新成功"
}
```

------

## ❤️ 收藏功能

### 1. 添加收藏

**POST** `/api/favorites/add`

**权限：** 需要登录

**请求体：**

```json
{
  "movieId": 123,
  "movieName": "阿凡达"
}
```

**响应：**

```json
{
  "message": "收藏成功"
}
```

### 2. 取消收藏

**DELETE** `/api/favorites/remove/{movieId}`

**权限：** 需要登录

**路径参数：**

- `movieId`: 电影ID

**响应：**

```json
{
  "message": "取消收藏成功"
}
```

### 3. 获取收藏列表

**GET** `/api/favorites/my`

**权限：** 需要登录

**响应：**

```json
[
  {
    "id": 1,
    "userId": 1,
    "username": "testuser",
    "movieId": 123,
    "movieName": "阿凡达",
    "createdTime": "2023-01-01T00:00:00"
  }
]
```

### 4. 分页获取收藏列表

**GET** `/api/favorites/my/page`

**权限：** 需要登录

**查询参数：**

- `pageNum`: 页码（默认1）
- `pageSize`: 每页数量（默认10）

### 5. 检查收藏状态

**GET** `/api/favorites/check/{movieId}`

**权限：** 需要登录

**路径参数：**

- `movieId`: 电影ID

**响应：**

```json
{
  "isFavorite": true
}
```

------

## 💬 用户反馈

### 1. 提交反馈

**POST** `/api/feedback/submit`

**权限：** 需要登录

**请求体：**

```json
{
  "title": "页面加载缓慢",
  "content": "电影列表页面加载时间过长，希望能优化",
  "type": "BUG"
}
```

**反馈类型说明：**

- `GENERAL`: 一般反馈
- `BUG`: 问题报告
- `SUGGESTION`: 建议

**响应：**

```json
{
  "id": 1,
  "userId": 1,
  "username": "testuser",
  "title": "页面加载缓慢",
  "content": "电影列表页面加载时间过长，希望能优化",
  "type": "BUG",
  "status": 0,
  "createdTime": "2023-01-01T00:00:00"
}
```

### 2. 获取我的反馈列表

**GET** `/api/feedback/my`

**权限：** 需要登录

**响应：**

```json
[
  {
    "id": 1,
    "title": "页面加载缓慢",
    "content": "电影列表页面加载时间过长，希望能优化",
    "type": "BUG",
    "status": 0,
    "adminReply": null,
    "createdTime": "2023-01-01T00:00:00",
    "updatedTime": "2023-01-01T00:00:00"
  }
]
```

**状态说明：**

- `0`: 待处理
- `1`: 处理中
- `2`: 已解决
- `3`: 已关闭

------

# 管理端接口

## 🎬 电影管理

### 1. 添加电影

**POST** `/api/movies`

**权限：** 管理员

**请求体：**

```json
{
  "movieId": 1001,
  "name": "新电影",
  "turnout": 1000000,
  "type": "动作",
  "origin": "中国",
  "time": "2023-12-01T00:00:00",
  "duration": 120,
  "year": 2023,
  "rate": 8.0,
  "premiereLocation": "上海"
}
```

### 2. 更新电影信息

**PUT** `/api/movies/{id}`

**权限：** 管理员

**路径参数：**

- `id`: 电影ID

**请求体：** 同添加电影

### 3. 删除电影

**DELETE** `/api/movies/{id}`

**权限：** 管理员

**路径参数：**

- `id`: 电影ID

### 4. 电影类型统计

**GET** `/api/movies/admin/type-statistics`

**权限：** 管理员

**响应：**

```json
[
  {
    "type": "动作",
    "count": 150
  },
  {
    "type": "喜剧",
    "count": 120
  }
]
```

------

## 💬 反馈管理

### 1. 分页查询所有反馈

**GET** `/api/feedback/admin/all`

**权限：** 管理员

**查询参数：**

- `pageNum`: 页码（默认1）
- `pageSize`: 每页数量（默认10）

**响应：**

```json
{
  "records": [
    {
      "id": 1,
      "userId": 1,
      "username": "testuser",
      "title": "页面加载缓慢",
      "content": "电影列表页面加载时间过长",
      "type": "BUG",
      "status": 0,
      "adminReply": null,
      "createdTime": "2023-01-01T00:00:00"
    }
  ],
  "total": 50,
  "size": 10,
  "current": 1,
  "pages": 5
}
```

### 2. 按状态查询反馈

**GET** `/api/feedback/admin/status/{status}`

**权限：** 管理员

**路径参数：**

- `status`: 反馈状态（0-3）

### 3. 查看反馈详情

**GET** `/api/feedback/admin/{id}`

**权限：** 管理员

**路径参数：**

- `id`: 反馈ID

### 4. 回复反馈

**PUT** `/api/feedback/admin/reply/{id}`

**权限：** 管理员

**路径参数：**

- `id`: 反馈ID

**请求体：**

```json
{
  "adminReply": "我们已经注意到这个问题，将在下个版本中修复",
  "status": 1
}
```

**响应：**

```json
{
  "message": "回复成功"
}
```

------

## 📊 用户活跃度统计

### 1. 获取用户活跃时间分布

**GET** `/api/admin/activity/statistics`

**权限：** 管理员

**响应：**

```json
[
  {
    "hour": 9,
    "count": 150
  },
  {
    "hour": 10,
    "count": 200
  },
  {
    "hour": 14,
    "count": 300
  }
]
```

### 2. 按时间范围获取活跃度统计

**GET** `/api/admin/activity/statistics/range`

**权限：** 管理员

**查询参数：**

- `startTime`: 开始时间（格式：yyyy-MM-dd HH:mm:ss）
- `endTime`: 结束时间（格式：yyyy-MM-dd HH:mm:ss）

**示例：**

```
GET /api/admin/activity/statistics/range?startTime=2023-01-01 00:00:00&endTime=2023-01-31 23:59:59
```

### 3. 获取所有登录日志

**GET** `/api/admin/activity/logs`

**权限：** 管理员

**响应：**

```json
[
  {
    "id": 1,
    "userId": 1,
    "username": "testuser",
    "loginTime": "2023-01-01T09:00:00",
    "ipAddress": "192.168.1.100",
    "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
  }
]
```

------

## 🚫 错误码说明

| HTTP状态码 | 说明              |
| ---------- | ----------------- |
| 200        | 请求成功          |
| 400        | 请求参数错误      |
| 401        | 未认证或Token无效 |
| 403        | 权限不足          |
| 404        | 资源不存在        |
| 500        | 服务器内部错误    |

------

## 📝 接口调用示例

### JavaScript 示例

```javascript
// 登录
const login = async (username, password) => {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username, password })
  });
  
  const data = await response.json();
  if (data.token) {
    localStorage.setItem('token', data.token);
  }
  return data;
};

// 带Token的请求
const getMyFavorites = async () => {
  const token = localStorage.getItem('token');
  const response = await fetch('/api/favorites/my', {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  
  return await response.json();
};

// 添加收藏
const addFavorite = async (movieId, movieName) => {
  const token = localStorage.getItem('token');
  const response = await fetch('/api/favorites/add', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ movieId, movieName })
  });
  
  return await response.json();
};
```

### cURL 示例

```bash
# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'

# 获取收藏列表（需要Token）
curl -X GET http://localhost:8080/api/favorites/my \
  -H "Authorization: Bearer your-jwt-token"

# 提交反馈
curl -X POST http://localhost:8080/api/feedback/submit \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{"title":"Bug报告","content":"发现了一个问题","type":"BUG"}'
```

------

## 📞 技术支持

如有接口使用问题，请联系我或查看详细的错误日志。所有接口都支持跨域请求（CORS），可直接在前端项目中调用。
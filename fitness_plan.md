# 健身打卡小程序 — 项目计划书

## 1. 项目概述

### 1.1 项目背景
开发一款轻量级健身打卡小程序，帮助用户记录每日健身情况，同时提供健身教学视频和食物热量查询等辅助功能。管理端可对用户数据进行统一管理。

### 1.2 项目目标
- 实现用户每日健身打卡核心功能
- 提供健身动作教学视频库
- 提供常见食物热量数据查询
- 实现管理端对用户、内容、数据的全面管理
- 建立超级管理员体系，保障系统安全

### 1.3 目标用户
- **普通用户**：有健身打卡需求的个人用户
- **管理员**：负责内容维护和用户管理的运营人员
- **超级管理员**：拥有系统最高权限，管理管理员账号

---

## 2. 技术选型

| 层面 | 技术 | 说明 |
|------|------|------|
| 前端框架 | uni-app | 一套代码编译到微信小程序 + H5 + App |
| 状态管理 | Vuex / Pinia | 全局状态管理 |
| UI 组件 | uni-ui / uView | 加快开发效率 |
| 后端框架 | Node.js + Express / Koa | 轻量级 RESTful API |
| 数据库 | MySQL | 关系型数据存储 |
| ORM | Sequelize / TypeORM | 数据库操作 |
| 缓存 | Redis（可选） | 热点数据缓存 |
| 文件存储 | 七牛云 / 阿里云 OSS | 视频文件存储 |
| 部署 | 云服务器（CentOS） | 服务端部署 |

---

## 3. 系统架构

```
┌─────────────────────────────────────────────┐
│              客户端（uni-app）                 │
│  微信小程序 / H5 / App                       │
│  ┌─────────┐ ┌─────────┐ ┌──────────────┐   │
│  │ 用户端    │ │ 管理端    │ │ 超级管理端    │   │
│  └─────────┘ └─────────┘ └──────────────┘   │
└───────────────────┬─────────────────────────┘
                    │ HTTP/HTTPS
┌───────────────────▼─────────────────────────┐
│           API 网关层（Nginx）                  │
└───────────────────┬─────────────────────────┘
                    │
┌───────────────────▼─────────────────────────┐
│           后端服务（Node.js）                  │
│  ┌──────────┐ ┌──────────┐ ┌──────────────┐  │
│  │ 用户模块   │ │ 打卡模块   │ │ 内容管理模块  │  │
│  └──────────┘ └──────────┘ └──────────────┘  │
│  ┌──────────┐ ┌──────────┐                   │
│  │ 食物模块   │ │ 视频模块   │                   │
│  └──────────┘ └──────────┘                   │
└───────────────────┬─────────────────────────┘
                    │
┌───────────────────▼─────────────────────────┐
│              数据库（MySQL）                   │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌────────────┐  │
│  │ 用户表 │ │ 打卡表 │ │ 视频表 │ │ 食物热量表  │  │
│  └──────┘ └──────┘ └──────┘ └────────────┘  │
│  ┌──────────┐ ┌────────────┐                 │
│  │ 管理员表   │ │ 打卡记录详情 │                 │
│  └──────────┘ └────────────┘                 │
└─────────────────────────────────────────────┘
```

---

## 4. 数据库设计

### 4.1 用户表（users）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INT (PK, AUTO) | 用户ID |
| username | VARCHAR(50) | 用户名（唯一） |
| password | VARCHAR(255) | 密码（bcrypt加密） |
| nickname | VARCHAR(50) | 昵称 |
| avatar | VARCHAR(255) | 头像URL |
| height | DECIMAL(5,2) | 身高（cm） |
| weight | DECIMAL(5,2) | 体重（kg） |
| gender | TINYINT | 性别（0-未知，1-男，2-女） |
| birth_date | DATE | 出生日期 |
| phone | VARCHAR(20) | 手机号 |
| created_at | DATETIME | 注册时间 |
| updated_at | DATETIME | 更新时间 |
| status | TINYINT | 状态（0-禁用，1-正常） |

### 4.2 管理员表（admins）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INT (PK, AUTO) | 管理员ID |
| username | VARCHAR(50) | 管理员账号（唯一） |
| password | VARCHAR(255) | 密码（bcrypt加密） |
| role | ENUM('super', 'admin') | 角色：super-超级管理员，admin-普通管理员 |
| real_name | VARCHAR(50) | 真实姓名 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| status | TINYINT | 状态（0-禁用，1-正常） |

### 4.3 打卡记录表（checkins）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INT (PK, AUTO) | 记录ID |
| user_id | INT (FK) | 用户ID |
| checkin_date | DATE | 打卡日期（同一天只允许一条） |
| checkin_time | TIME | 打卡时间 |
| duration | INT | 运动时长（分钟） |
| type | VARCHAR(50) | 运动类型（跑步/力量/瑜伽等） |
| intensity | ENUM('low','medium','high') | 运动强度 |
| content | TEXT | 打卡备注 |
| calories_burned | INT | 消耗卡路里（kcal） |
| created_at | DATETIME | 创建时间 |

### 4.4 健身视频表（videos）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INT (PK, AUTO) | 视频ID |
| title | VARCHAR(200) | 视频标题 |
| description | TEXT | 视频描述 |
| cover_url | VARCHAR(255) | 封面图URL |
| video_url | VARCHAR(255) | 视频播放URL |
| category | VARCHAR(50) | 分类（胸部/腿部/核心/全身等） |
| duration | INT | 视频时长（秒） |
| difficulty | ENUM('beginner','intermediate','advanced') | 难度等级 |
| coach | VARCHAR(50) | 教练/演示者 |
| sort_order | INT | 排序权重 |
| status | TINYINT | 状态（0-下架，1-上架） |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 4.5 食物热量表（foods）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INT (PK, AUTO) | 食物ID |
| name | VARCHAR(100) | 食物名称 |
| category | VARCHAR(50) | 分类（主食/水果/肉类/蔬菜等） |
| calories | DECIMAL(8,2) | 每100g热量（kcal） |
| protein | DECIMAL(8,2) | 蛋白质含量（g/100g） |
| fat | DECIMAL(8,2) | 脂肪含量（g/100g） |
| carbohydrate | DECIMAL(8,2) | 碳水化合物含量（g/100g） |
| fiber | DECIMAL(8,2) | 膳食纤维含量（g/100g） |
| unit | VARCHAR(20) | 单位（g/份/个） |
| image_url | VARCHAR(255) | 食物图片URL |
| status | TINYINT | 状态（0-下架，1-上架） |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 4.6 打卡点赞表（checkin_likes）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INT (PK, AUTO) | 记录ID |
| checkin_id | INT (FK) | 打卡记录ID |
| user_id | INT (FK) | 点赞用户ID |
| created_at | DATETIME | 点赞时间 |

### 4.7 打卡评论表（checkin_comments）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INT (PK, AUTO) | 记录ID |
| checkin_id | INT (FK) | 打卡记录ID |
| user_id | INT (FK) | 评论用户ID |
| content | TEXT | 评论内容 |
| created_at | DATETIME | 评论时间 |

### 4.8 用户目标表（user_goals）

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INT (PK, AUTO) | 目标ID |
| user_id | INT (FK) | 用户ID |
| goal_type | VARCHAR(50) | 目标类型（减脂/增肌/塑形/保持健康） |
| target_weight | DECIMAL(5,2) | 目标体重（kg） |
| weekly_frequency | INT | 每周运动次数 |
| reminder_time | TIME | 提醒时间 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

---

## 5. 功能模块详情

### 5.1 用户端功能（User Side）

| 模块 | 功能 | 说明 |
|------|------|------|
| **账号系统** | 用户注册 | 手机号/邮箱注册 |
| | 用户登录 | 密码登录 + 验证码登录 |
| | 找回密码 | 手机/邮箱验证重置 |
| | 退出登录 | 清除登录态 |
| **个人中心** | 个人信息编辑 | 头像、昵称、身高体重等 |
| | 我的目标 | 设置健身目标（减脂/增肌/塑形） |
| | 打卡统计 | 查看累计打卡天数、连续打卡、本月打卡率 |
| | 运动周报/月报 | 可视化图表展示运动数据趋势 |
| **每日打卡** | 一键打卡 | 选择运动类型、填写时长、强度、备注 |
| | 打卡日历 | 日历视图查看打卡记录 |
| | 打卡提醒 | 每日定时提醒打卡（可设置） |
| | 消耗计算 | 根据运动类型和时长估算消耗卡路里 |
| **健身视频** | 视频列表 | 按分类浏览健身教学视频 |
| | 视频详情 | 播放、查看描述、难度、时长 |
| | 视频搜索 | 按关键字搜索视频 |
| | 收藏视频 | 收藏喜欢的视频方便查看 |
| **食物热量** | 食物查询 | 按名称搜索食物热量数据 |
| | 分类浏览 | 按分类查看食物（主食/水果/肉蛋/蔬菜等） |
| | 热量计算 | 输入份量自动计算摄入热量 |
| **社交互动** | 打卡广场 | 查看所有用户的公开打卡动态 |
| | 点赞评论 | 为好友/他人的打卡记录点赞评论 |
| | 关注用户 | 关注其他用户，查看其动态 |
| **通知提醒** | 打卡提醒推送 | 每日定时推送打卡通知 |
| | 互动通知 | 收到点赞/评论/关注时通知 |

### 5.2 管理端功能（Admin Side）

| 模块 | 功能 | 说明 |
|------|------|------|
| **仪表盘** | 数据概览 | 用户总数、今日打卡数、新增用户等 |
| | 趋势图表 | 打卡趋势、用户增长趋势 |
| **用户管理** | 用户列表 | 查看所有用户信息（账号/时间/身高/体重） |
| | 用户搜索 | 按用户名/手机号搜索 |
| | 编辑用户 | 修改用户信息 |
| | 禁用/启用 | 管理用户账号状态 |
| | 删除用户 | **删除用户及其所有打卡数据** |
| **视频管理** | 视频列表 | 查看所有健身视频 |
| | 添加视频 | 上传视频、填写信息 |
| | 编辑视频 | 修改视频信息 |
| | 删除视频 | 下架/删除视频 |
| **食物管理** | 食物列表 | 查看所有食物数据 |
| | 添加食物 | 添加食物名称、热量、营养数据 |
| | 编辑食物 | 修改食物信息 |
| | 删除食物 | 删除食物条目 |
| | 批量导入 | Excel/CSV批量导入食物数据 |

### 5.3 超级管理员功能（Super Admin Side）

| 模块 | 功能 | 说明 |
|------|------|------|
| **管理员管理** | 管理员列表 | 查看所有管理员账号 |
| | 添加管理员 | 创建新的管理员账号 |
| | 删除管理员 | 删除管理员账号 |
| | 修改权限 | 修改管理员角色（super/admin） |
| **系统设置** | 基本配置 | 系统参数配置 |
| | 操作日志 | 查看管理员操作日志 |
| **数据管理** | 数据备份 | 数据库备份功能 |
| | 数据导出 | 导出用户数据/打卡数据 |

---

## 6. API 接口设计

### 6.1 用户端接口

```
# 认证模块
POST   /api/user/register          # 用户注册
POST   /api/user/login             # 用户登录
POST   /api/user/logout            # 退出登录
POST   /api/user/reset-password    # 重置密码

# 个人信息
GET    /api/user/profile           # 获取个人信息
PUT    /api/user/profile           # 更新个人信息
PUT    /api/user/goal              # 设置健身目标
GET    /api/user/goal              # 获取健身目标

# 打卡模块
POST   /api/checkin                # 打卡
GET    /api/checkin/today          # 获取今日打卡
GET    /api/checkin/calendar       # 获取打卡日历（月）
GET    /api/checkin/stats          # 获取打卡统计
DELETE /api/checkin/:id            # 删除打卡记录

# 视频模块
GET    /api/videos                 # 视频列表（分页）
GET    /api/videos/:id             # 视频详情
GET    /api/videos/search          # 搜索视频
POST   /api/videos/:id/favorite    # 收藏/取消收藏

# 食物模块
GET    /api/foods                  # 食物列表（分页）
GET    /api/foods/search           # 搜索食物
GET    /api/foods/:id              # 食物详情
POST   /api/foods/calculate        # 计算热量

# 社交模块
GET    /api/feed                   # 打卡广场动态
POST   /api/checkin/:id/like       # 点赞
POST   /api/checkin/:id/comment    # 评论
GET    /api/checkin/:id/comments   # 获取评论列表
```

### 6.2 管理端接口

```
# 认证模块
POST   /api/admin/login            # 管理员登录
POST   /api/admin/logout           # 退出登录

# 仪表盘
GET    /api/admin/dashboard        # 数据概览

# 用户管理
GET    /api/admin/users            # 用户列表
GET    /api/admin/users/:id        # 用户详情
PUT    /api/admin/users/:id        # 编辑用户
DELETE /api/admin/users/:id        # 删除用户（含打卡数据）
PUT    /api/admin/users/:id/status # 禁用/启用用户

# 视频管理
GET    /api/admin/videos           # 视频列表
POST   /api/admin/videos           # 添加视频
PUT    /api/admin/videos/:id       # 编辑视频
DELETE /api/admin/videos/:id       # 删除视频

# 食物管理
GET    /api/admin/foods            # 食物列表
POST   /api/admin/foods            # 添加食物
PUT    /api/admin/foods/:id        # 编辑食物
DELETE /api/admin/foods/:id        # 删除食物
POST   /api/admin/foods/import     # 批量导入

# 超级管理员专属
GET    /api/admin/admins           # 管理员列表
POST   /api/admin/admins           # 添加管理员
DELETE /api/admin/admins/:id       # 删除管理员
PUT    /api/admin/admins/:id       # 修改管理员信息/角色
GET    /api/admin/logs             # 操作日志
```

---

## 7. 页面结构

### 7.1 用户端页面

```
pages/
├── index/                    # 首页
│   └── index.vue             # 首页（打卡入口 + 数据概览）
├── checkin/                  # 打卡
│   ├── checkin.vue           # 打卡页面
│   ├── calendar.vue          # 打卡日历
│   └── stats.vue             # 打卡统计/图表
├── video/                    # 视频
│   ├── list.vue              # 视频列表
│   ├── detail.vue            # 视频播放详情
│   └── favorite.vue          # 我的收藏
├── food/                     # 食物
│   ├── list.vue              # 食物热量列表
│   ├── detail.vue            # 食物详情
│   ├── search.vue            # 食物搜索
│   └── calculator.vue        # 热量计算器
├── social/                   # 社交
│   ├── feed.vue              # 打卡广场
│   ├── follow.vue            # 关注/粉丝
│   └── user-home.vue         # 他人主页
├── user/                     # 个人中心
│   ├── profile.vue           # 个人信息
│   ├── goal.vue              # 健身目标设置
│   ├── settings.vue          # 设置
│   └── about.vue             # 关于
├── auth/                     # 认证
│   ├── login.vue             # 登录
│   ├── register.vue          # 注册
│   └── reset-password.vue    # 重置密码
└── components/               # 通用组件
    ├── CheckinCard.vue       # 打卡卡片
    ├── VideoCard.vue         # 视频卡片
    ├── FoodCard.vue          # 食物卡片
    ├── Calendar.vue          # 日历组件
    ├── StatsChart.vue        # 统计图表
    └── ...
```

### 7.2 管理端页面

```
admin/
├── dashboard.vue             # 管理首页 - 数据仪表盘
├── login.vue                 # 管理端登录
├── users/                    # 用户管理
│   ├── list.vue              # 用户列表
│   └── detail.vue            # 用户详情
├── videos/                   # 视频管理
│   ├── list.vue              # 视频列表
│   └── edit.vue              # 视频编辑（添加/编辑）
├── foods/                    # 食物管理
│   ├── list.vue              # 食物列表
│   ├── edit.vue              # 食物编辑（添加/编辑）
│   └── import.vue            # 批量导入
├── admins/                   # 管理员管理（超级管理员）
│   ├── list.vue              # 管理员列表
│   └── edit.vue              # 添加/编辑管理员
└── system/                   # 系统设置
    ├── settings.vue          # 系统配置
    └── logs.vue              # 操作日志
```

---

## 8. 权限体系

### 8.1 角色定义

| 角色 | 权限级别 | 说明 |
|------|---------|------|
| super_admin | 3级（最高） | 拥有所有权限，可管理管理员账号 |
| admin | 2级 | 可管理用户、视频、食物内容 |
| user | 1级 | 普通用户，仅可操作自己的数据 |

### 8.2 权限矩阵

| 操作 | user | admin | super_admin |
|------|------|-------|-------------|
| 用户打卡 | ✔ | — | — |
| 查看自己打卡记录 | ✔ | — | — |
| 查看/搜索视频 | ✔ | ✔ | ✔ |
| 查看/搜索食物 | ✔ | ✔ | ✔ |
| 查看用户列表 | — | ✔ | ✔ |
| 编辑用户 | — | ✔ | ✔ |
| 删除用户 | — | ✔ | ✔ |
| 管理视频内容 | — | ✔ | ✔ |
| 管理食物数据 | — | ✔ | ✔ |
| 查看管理员列表 | — | — | ✔ |
| 添加/删除管理员 | — | — | ✔ |
| 修改管理员角色 | — | — | ✔ |
| 查看操作日志 | — | — | ✔ |

---

## 9. 开发阶段与时间规划

### 第一阶段：基础架构搭建（第1-2周）
- 项目初始化，搭建前后端框架
- 数据库设计与建表
- 基础组件开发
- 用户认证模块（注册/登录）

### 第二阶段：核心功能开发（第3-5周）
- 打卡功能开发（打卡、日历、统计）
- 用户个人中心
- 管理端用户管理

### 第三阶段：内容管理（第6-7周）
- 视频模块（浏览 + 管理）
- 食物热量模块（查询 + 管理）
- 管理端仪表盘

### 第四阶段：社交与增强功能（第8-9周）
- 打卡广场、点赞评论
- 关注功能
- 运动周报/月报
- 打卡提醒推送

### 第五阶段：超级管理端与权限（第10周）
- 超级管理员管理功能
- 操作日志系统
- 权限校验完善

### 第六阶段：测试与发布（第11-12周）
- 功能测试
- 性能测试
- Bug修复
- 上线发布

---

## 10. 项目目录结构

```
fitness-app/
├── client/                    # 前端（uni-app）
│   ├── pages/                 # 用户端页面
│   │   ├── index/
│   │   ├── checkin/
│   │   ├── video/
│   │   ├── food/
│   │   ├── social/
│   │   ├── user/
│   │   └── auth/
│   ├── admin/                 # 管理端页面
│   │   ├── dashboard.vue
│   │   ├── login.vue
│   │   ├── users/
│   │   ├── videos/
│   │   ├── foods/
│   │   ├── admins/
│   │   └── system/
│   ├── components/            # 公共组件
│   ├── api/                   # API请求封装
│   ├── store/                 # 状态管理
│   ├── utils/                 # 工具函数
│   ├── static/                # 静态资源
│   ├── App.vue
│   ├── main.js
│   ├── manifest.json
│   └── pages.json
│
├── server/                    # 后端（Node.js）
│   ├── src/
│   │   ├── controllers/       # 控制器
│   │   ├── models/            # 数据模型
│   │   ├── routes/            # 路由
│   │   ├── middleware/        # 中间件（认证/权限）
│   │   ├── services/          # 业务逻辑层
│   │   ├── utils/             # 工具函数
│   │   └── config/            # 配置文件
│   ├── app.js                 # 入口文件
│   └── package.json
│
├── database/                  # 数据库脚本
│   ├── init.sql               # 初始化建表脚本
│   └── seed.sql               # 初始数据（超级管理员、示例数据）
│
└── docs/                      # 文档
    └── api.md                 # API文档
```

---

## 11. 初始默认数据

### 11.1 超级管理员初始账号

| 字段 | 值 |
|------|-----|
| 用户名 | superadmin |
| 密码 | admin888（首次登录强制修改） |
| 角色 | super |

### 11.2 运动类型分类

- 跑步 / 快走 / 骑行（有氧类）
- 力量训练 / 器械（力量类）
- 瑜伽 / 普拉提（柔韧类）
- HIIT / Tabata（燃脂类）
- 游泳 / 球类（综合类）
- 其他

### 11.3 视频分类

- 胸部训练
- 背部训练
- 腿部训练
- 肩部训练
- 手臂训练
- 核心腹部
- 全身燃脂
- 瑜伽拉伸
- 热身放松

### 11.4 食物分类

- 主食谷物
- 肉类蛋类
- 蔬菜菌藻
- 水果
- 奶制品
- 豆类坚果
- 饮品
- 零食甜点
- 调味品

---

## 12. 安全方案

| 措施 | 说明 |
|------|------|
| 密码加密 | 使用 bcrypt 进行密码哈希存储 |
| Token认证 | 使用 JWT（JSON Web Token）进行身份验证 |
| 接口鉴权 | 所有接口需携带 Token，中间件统一校验 |
| 权限校验 | 基于角色（RBAC）进行接口访问控制 |
| SQL注入防护 | 使用 ORM 参数化查询 |
| XSS防护 | 对用户输入进行转义处理 |
| HTTPS | 生产环境强制启用 HTTPS |
| 敏感信息脱敏 | 返回前端时隐藏密码等敏感字段 |

---

## 13. 数据流转示例

### 打卡流程

```
用户打开小程序 → 进入首页
  → 点击"打卡"按钮
  → 选择运动类型（跑步）
  → 填写运动时长（30分钟）
  → 选择强度（中等）
  → 填写备注（可选）
  → 提交打卡
    → 前端发送 POST /api/checkin
    → 后端校验 Token → 校验参数
    → 查询今日是否已打卡（防重复）
    → 写入 checkins 表
    → 返回打卡成功信息
  → 前端显示打卡成功提示
  → 自动刷新首页打卡统计数据
```

### 管理端删除用户流程

```
管理员登录管理端 → 进入用户管理
  → 搜索/查找目标用户
  → 点击"删除用户"
  → 二次确认弹窗
  → 确认删除
    → 前端发送 DELETE /api/admin/users/:id
    → 后端校验管理员 Token + 权限
    → 开启事务
    → 删除该用户所有打卡记录（checkins）
    → 删除该用户所有评论/点赞（comments/likes）
    → 删除用户本身（users）
    → 提交事务
    → 返回删除成功
  → 前端刷新用户列表
```

---

## 14. 扩展性考虑

- **大屏适配**：管理端可扩展为 PC Web 管理后台
- **多端兼容**：uni-app 一套代码可编译到微信小程序、H5、App
- **国际化**：预留 i18n 多语言支持
- **第三方登录**：预留微信登录、QQ登录接口
- **数据导出**：支持 Excel/PDF 导出打卡报告
- **推送服务**：预留微信模板消息 / 小程序订阅消息

---

> **文档版本**：v1.0  
> **最后更新**：2026-04-27  
> **状态**：计划阶段

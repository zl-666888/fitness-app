# 🏋️ 健身打卡全栈项目

> 微信小程序 + Vue3 管理后台 + Spring Boot 后端，一套完整的前后端分离健身打卡解决方案。

---

## 🎯 功能特性

### 用户端（微信小程序）
- **每日打卡** — 记录运动类型、时长、强度、备注
- **打卡日历** — 日历视图查看每月打卡记录
- **打卡统计** — 累计天数、连续打卡天数、本月打卡次数
- **社交广场** — 浏览/点赞/评论他人的打卡动态
- **关注体系** — 关注/取关其他用户
- **健身视频** — 分类浏览视频，支持收藏
- **食物热量** — 查询食物热量，计算每日摄入
- **个人中心** — 个人信息、健身目标、收藏管理

### 管理后台（Vue 3）
- **数据仪表盘** — 用户总数、打卡数、今日新增等
- **用户/管理员管理** — 增删改查，禁用启用
- **视频管理** — 添加/编辑/删除健身视频
- **食物管理** — 支持批量导入
- **操作日志** — 记录管理员所有操作

---

## 🛠 技术栈

| 模块 | 技术 |
|------|------|
| 用户端 | 微信小程序原生（WXML + WXSS + JS） |
| 管理后台 | Vue 3 + Vite 5 + Element Plus + Pinia + ECharts |
| 后端 | Java 17 + Spring Boot 3.2 + MyBatis-Plus 3.5 |
| 数据库 | MySQL 8 + Redis |
| 认证 | JWT（双令牌：Access Token 2小时 / Refresh Token 7天） |
| API文档 | Knife4j（启动后访问 `/doc.html`） |
| 视频处理 | FFmpeg |

---

## 🚀 快速开始

### 环境要求
- JDK 17+ / Maven 3.8+
- Node.js 18+ / npm
- MySQL 8.0+ / Redis 6.0+
- FFmpeg（视频转码用）
- 微信开发者工具

### 1. 克隆项目
```bash
git clone https://github.com/zl-666888/fitness-app.git
cd fitness-app
```

### 2. 初始化数据库
```sql
-- 登录 MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE sport CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入数据
USE sport;
SOURCE database/sport.sql;
SOURCE database/sport_test_data.sql;
```

### 3. 配置后端
修改 `server/src/main/resources/application-dev.yml` 中的数据库和 Redis 密码配置。

### 4. 启动后端
```bash
cd server
mvn spring-boot:run
# 运行在 http://localhost:8080
```

### 5. 启动管理后台
```bash
cd admin-panel
npm install
npm run dev
# 运行在 http://localhost:3000
```

### 6. 导入微信小程序
用微信开发者工具导入 `fitness_app` 目录，修改 `api/request.js` 中的 `LAN_IP` 为你的电脑 IP。

---

## 📁 项目结构

```
fitness-app/
├── fitness_app/          # 微信小程序（用户端）
│   ├── api/              # API 请求封装
│   ├── components/       # 公共组件
│   └── pages/            # 页面文件
├── admin-panel/          # Vue 3 管理后台
│   └── src/views/        # 页面视图
├── server/               # Spring Boot 后端
│   └── src/main/java/com/fitness/
│       ├── controller/   # 14个控制器
│       ├── entity/       # 11个实体类
│       ├── mapper/       # 11个Mapper
│       └── service/      # 业务层
└── database/             # SQL 脚本
    ├── sport.sql         # 表结构
    └── sport_test_data.sql  # 测试数据
```

---

## 🔌 API 文档

后端启动后访问：`http://localhost:8080/doc.html`

---

## 👤 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 超级管理员 | admin | 123456 |
| 普通用户 | testuser | 123456 |

> ⚠️ 生产环境请务必修改默认密码！

---

## 📄 License

MIT License — 自由使用、修改和分发。

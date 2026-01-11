# YIR-ApprovalHub - 动态审批流程管理系统

用于上海电力大学信管专业大三上期末大型作业——审批系统的开发

## 项目简介

YIR-ApprovalHub 是一个功能完善的企业级审批流程管理系统，支持多种类型的申请流程管理，包括请假申请、报销申请等。系统采用前后端分离架构，提供完整的用户权限管理、审批流程配置和历史记录追踪功能。

## 技术栈

### 后端
- **框架**: Spring Boot 2.7.14
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA (Hibernate)
- **安全**: Spring Security + JWT
- **构建工具**: Maven

### 前端
- **框架**: Vue 3
- **UI组件**: Element Plus
- **路由**: Vue Router 4
- **HTTP客户端**: Axios
- **构建工具**: Vue CLI

## 核心功能

### 1. 用户与权限管理
- ✅ 三种角色：员工（EMPLOYEE）、审批人（APPROVER）、管理员（ADMIN）
- ✅ 基于JWT的身份认证
- ✅ 用户、部门、岗位管理
- ✅ 用户-岗位关联表

### 2. 基础申请表单
- ✅ **请假申请**: 支持病假、年假、事假、产假、陪产假等多种请假类型
- ✅ **报销申请**: 支持差旅、招待、办公用品、培训、通讯等报销类型
- ✅ 固定审批流程（部门经理 -> HR经理）

### 3. 审批任务管理
- ✅ 待审批任务列表
- ✅ 审批同意/拒绝功能
- ✅ 审批意见记录
- ✅ 完整的审批历史追踪

### 4. 我的申请管理
- ✅ 新建申请（草稿保存）
- ✅ 查看申请详情
- ✅ 提交申请
- ✅ 撤回申请
- ✅ 申请状态跟踪

### 5. 审批流程记录
- ✅ 完整的审批历史记录
- ✅ 时间线展示
- ✅ 审批人意见记录

## 数据库设计

### 核心表结构
1. **users** - 用户表
2. **departments** - 部门表
3. **positions** - 岗位表
4. **user_positions** - 用户岗位关联表
5. **applications** - 申请基础表
6. **leave_applications** - 请假申请表
7. **reimbursement_applications** - 报销申请表
8. **approval_tasks** - 审批任务表
9. **approval_history** - 审批历史表

详细的数据库Schema请查看: `database/schema.sql`

## 项目结构

```
YIR-ApprovalHub-/
├── src/main/java/com/yir/approvalhub/
│   ├── entity/          # 实体类
│   ├── repository/      # 数据访问层
│   ├── service/         # 业务逻辑层
│   ├── controller/      # 控制器层
│   ├── dto/            # 数据传输对象
│   ├── security/       # 安全配置
│   └── config/         # 配置类
├── frontend/
│   ├── src/
│   │   ├── views/      # 页面组件
│   │   ├── services/   # API服务
│   │   └── router/     # 路由配置
│   └── public/         # 静态资源
├── database/
│   └── schema.sql      # 数据库Schema
└── pom.xml             # Maven配置文件
```

## 快速开始

### 环境要求
- JDK 11+
- MySQL 8.0+
- Node.js 14+
- Maven 3.6+

### 后端启动步骤

1. **配置数据库**
```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
source database/schema.sql
```

2. **修改配置文件**
编辑 `src/main/resources/application.properties`，修改数据库连接信息：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/approval_hub?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

3. **启动后端服务**
```bash
# 使用Maven启动
mvn spring-boot:run

# 或先编译再运行
mvn clean package
java -jar target/approval-hub-1.0.0.jar
```

后端服务默认运行在 `http://localhost:8080`

### 前端启动步骤

1. **安装依赖**
```bash
cd frontend
npm install
```

2. **启动开发服务器**
```bash
npm run serve
```

前端服务默认运行在 `http://localhost:8081`

### 默认测试账号

系统启动后会自动初始化以下测试账号：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | 管理员 | 系统管理员 |
| manager1 | manager123 | 审批人 | 部门经理 |
| hrmanager | hr123 | 审批人 | HR经理 |
| employee1 | emp123 | 员工 | 普通员工 |
| employee2 | emp123 | 员工 | 普通员工 |

## API文档

### 认证接口
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/me` - 获取当前用户信息

### 申请管理接口
- `POST /api/applications/leave` - 创建请假申请
- `POST /api/applications/reimbursement` - 创建报销申请
- `POST /api/applications/{id}/submit` - 提交申请
- `POST /api/applications/{id}/withdraw` - 撤回申请
- `GET /api/applications/my` - 获取我的申请列表
- `GET /api/applications/{id}` - 获取申请详情
- `GET /api/applications/{id}/history` - 获取申请历史

### 审批管理接口
- `GET /api/approvals/pending` - 获取待审批任务
- `GET /api/approvals/application/{applicationId}` - 获取申请的审批任务
- `POST /api/approvals/{taskId}/process` - 处理审批任务

## 使用说明

### 员工操作流程
1. 使用员工账号登录系统
2. 点击"新建请假申请"或"新建报销申请"
3. 填写申请表单
4. 保存草稿或直接提交
5. 在"我的申请"中查看申请状态
6. 可以撤回待审批的申请

### 审批人操作流程
1. 使用审批人账号登录系统
2. 点击"待审批任务"查看待处理的任务
3. 点击"查看详情"查看申请的完整信息
4. 点击"同意"或"拒绝"进行审批
5. 可以填写审批意见

## 高级功能扩展（可选）

系统预留了以下高级功能的扩展接口：

1. **动态表单设计器** - 支持自定义申请表单
2. **动态流程设计器** - 支持自定义审批流程
3. **自定义审批人指派** - 支持按岗位、组织架构指派审批人
4. **会签/或签功能** - 支持多人会签或或签
5. **文件上传/下载** - 支持附件管理
6. **统计报表** - 支持审批数据统计和分析

## 开发指南

### 添加新的申请类型

1. 在 `entity` 包中创建新的实体类，继承 `Application`
2. 在 `repository` 包中创建对应的Repository接口
3. 在 `dto` 包中创建请求和响应DTO
4. 在 `service` 包中添加业务逻辑
5. 在 `controller` 包中添加API接口
6. 在前端添加对应的表单和视图

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查MySQL服务是否启动
   - 验证数据库用户名和密码
   - 确认数据库已创建

2. **JWT认证失败**
   - 检查JWT密钥配置
   - 清除浏览器缓存和LocalStorage

3. **跨域问题**
   - 检查CORS配置
   - 确认前端代理配置正确

## 贡献指南

欢迎提交Issue和Pull Request来帮助改进项目！

## 许可证

本项目仅用于学习和教学目的。

## 联系方式

如有问题，请通过以下方式联系：
- 提交GitHub Issue
- 发送邮件至项目维护者

---

**上海电力大学 信息管理专业 大三上期末大型作业**

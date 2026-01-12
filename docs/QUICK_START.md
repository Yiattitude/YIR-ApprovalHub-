# YIR Approval Hub 快速开始指南

## 5分钟快速部署

本指南帮助您在5分钟内快速启动YIR审批系统。

---

## 前提条件检查

在开始之前，确保您已安装：

```bash
# 检查 Java 版本（需要 11+）
java -version

# 检查 Maven 版本（需要 3.6+）
mvn -version

# 检查 MySQL 版本（需要 8.0+）
mysql --version

# 检查 Node.js 版本（需要 14+）
node --version

# 检查 npm 版本
npm --version
```

如果没有安装，请先安装必要的软件。

---

## 步骤 1: 获取代码

```bash
# 克隆仓库
git clone https://github.com/Yiattitude/YIR-ApprovalHub-.git

# 进入项目目录
cd YIR-ApprovalHub-
```

---

## 步骤 2: 设置数据库（2分钟）

### 方式一：使用自动创建（推荐）

配置文件已设置自动创建数据库，只需：

```bash
# 编辑配置文件
vim src/main/resources/application.properties

# 修改数据库密码为您的MySQL root密码
spring.datasource.password=your_password
```

### 方式二：手动创建数据库

```bash
# 登录 MySQL
mysql -u root -p

# 执行数据库脚本
source database/schema.sql

# 退出
exit
```

---

## 步骤 3: 启动后端（1分钟）

```bash
# 直接运行（开发模式）
mvn spring-boot:run
```

等待看到以下输出：
```
===================================
Sample data initialized successfully!
Admin: admin/admin123
Manager1: manager1/manager123
HR Manager: hrmanager/hr123
Employee1: employee1/emp123
Employee2: employee2/emp123
===================================
```

后端服务现在运行在 `http://localhost:8080`

**保持这个终端窗口运行！**

---

## 步骤 4: 启动前端（2分钟）

打开**新的终端窗口**：

```bash
# 进入前端目录
cd frontend

# 安装依赖（首次运行需要）
npm install

# 启动前端服务
npm run serve
```

等待看到：
```
App running at:
- Local:   http://localhost:8081/
```

前端服务现在运行在 `http://localhost:8081`

---

## 步骤 5: 访问系统

1. 打开浏览器
2. 访问 `http://localhost:8081`
3. 使用测试账号登录：
   - 用户名: `employee1`
   - 密码: `emp123`

---

## 快速测试流程

### 场景：员工提交请假申请并审批

#### 1. 员工创建申请（employee1）

```
1. 登录账号: employee1 / emp123
2. 点击"新建请假申请"
3. 填写表单:
   - 标题: 测试请假
   - 类型: 病假
   - 开始: 明天的日期
   - 结束: 后天的日期
   - 天数: 2
   - 原因: 身体不适
4. 点击"保存并提交"
5. 在"我的申请"中查看状态：应该是"审批中"
```

#### 2. 第一级审批（manager1）

```
1. 退出登录
2. 使用 manager1 / manager123 登录
3. 点击"待审批任务"
4. 应该能看到刚才的请假申请
5. 点击"同意"
6. 输入意见："同意该请假申请"
7. 点击"确定"
```

#### 3. 第二级审批（hrmanager）

```
1. 退出登录
2. 使用 hrmanager / hr123 登录
3. 点击"待审批任务"
4. 应该能看到该请假申请
5. 点击"同意"
6. 输入意见："已核实，准予批准"
7. 点击"确定"
```

#### 4. 查看最终结果（employee1）

```
1. 退出登录
2. 重新使用 employee1 / emp123 登录
3. 在"我的申请"中查看
4. 状态应该变成"已通过"
5. 点击"查看"可以看到完整的审批流程
```

---

## 测试账号说明

| 账号 | 密码 | 角色 | 用途 |
|------|------|------|------|
| employee1 | emp123 | 员工 | 测试提交申请 |
| employee2 | emp123 | 员工 | 测试提交申请 |
| manager1 | manager123 | 审批人 | 第一级审批 |
| hrmanager | hr123 | 审批人 | 第二级审批 |
| admin | admin123 | 管理员 | 系统管理 |

---

## 常见问题

### Q: 后端启动失败，显示数据库连接错误

**A**: 检查：
1. MySQL服务是否运行：`sudo systemctl status mysql`
2. 数据库密码是否正确
3. MySQL端口3306是否被占用

### Q: 前端启动失败，显示端口被占用

**A**: 修改端口：
```bash
# 编辑 frontend/vue.config.js
# 修改 port: 8081 为其他端口，如 8082
```

### Q: 登录后显示401错误

**A**: 
1. 清除浏览器缓存和LocalStorage
2. 重新登录

### Q: 前端页面空白

**A**: 
1. 打开浏览器开发者工具（F12）
2. 查看Console错误信息
3. 确保后端服务正常运行

---

## 停止服务

### 停止后端
在后端终端按 `Ctrl + C`

### 停止前端
在前端终端按 `Ctrl + C`

---

## 重新启动

### 后端
```bash
mvn spring-boot:run
```

### 前端
```bash
cd frontend
npm run serve
```

---

## 下一步

系统运行成功后，您可以：

1. 📖 阅读[用户指南](USER_GUIDE.md)了解详细功能
2. 🔧 查看[部署指南](DEPLOYMENT.md)进行生产环境部署
3. 📡 阅读[API文档](API.md)了解接口详情
4. 💡 探索源代码，理解系统架构

---

## 获取帮助

如遇到问题：

1. 查看日志文件
2. 阅读完整文档
3. 提交GitHub Issue
4. 联系技术支持

---

**恭喜！您已成功启动YIR审批系统！**

*Happy Coding! 🚀*

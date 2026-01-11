# YIR Approval Hub 部署指南

## 目录
1. [系统要求](#系统要求)
2. [本地开发环境部署](#本地开发环境部署)
3. [生产环境部署](#生产环境部署)
4. [常见问题](#常见问题)

---

## 系统要求

### 必需软件
- **JDK**: 11 或更高版本
- **MySQL**: 8.0 或更高版本
- **Maven**: 3.6 或更高版本
- **Node.js**: 14 或更高版本
- **npm**: 6 或更高版本

### 推荐配置
- **CPU**: 2核及以上
- **内存**: 4GB及以上
- **磁盘**: 10GB及以上可用空间

---

## 本地开发环境部署

### 步骤1: 克隆项目
```bash
git clone https://github.com/Yiattitude/YIR-ApprovalHub-.git
cd YIR-ApprovalHub-
```

### 步骤2: 配置数据库

#### 2.1 安装MySQL
确保MySQL服务已安装并运行。

#### 2.2 创建数据库
```bash
# 登录MySQL
mysql -u root -p

# 执行数据库脚本
source database/schema.sql

# 或手动创建数据库
CREATE DATABASE approval_hub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2.3 配置数据库连接
编辑 `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/approval_hub?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password
```

### 步骤3: 启动后端服务

#### 3.1 使用Maven启动
```bash
# 清理并启动
mvn clean spring-boot:run
```

#### 3.2 或编译后运行
```bash
# 编译项目
mvn clean package -DskipTests

# 运行jar文件
java -jar target/approval-hub-1.0.0.jar
```

后端服务将在 `http://localhost:8080` 启动

**验证后端服务:**
```bash
curl http://localhost:8080/api/auth/login
```

### 步骤4: 启动前端服务

#### 4.1 安装依赖
```bash
cd frontend
npm install
```

#### 4.2 启动开发服务器
```bash
npm run serve
```

前端服务将在 `http://localhost:8081` 启动

#### 4.3 访问系统
在浏览器中打开: `http://localhost:8081`

使用测试账号登录:
- 用户名: `employee1`
- 密码: `emp123`

---

## 生产环境部署

### 方案1: 传统部署（推荐）

#### 后端部署

1. **编译项目**
```bash
mvn clean package -DskipTests
```

2. **配置生产环境属性**

创建 `application-prod.properties`:
```properties
# 服务器配置
server.port=8080

# 数据库配置
spring.datasource.url=jdbc:mysql://your-db-host:3306/approval_hub?useSSL=true&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password

# JPA配置
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# JWT配置
jwt.secret=your-production-secret-key-minimum-256-bits
jwt.expiration=86400000

# 日志配置
logging.level.root=WARN
logging.level.com.yir.approvalhub=INFO
```

3. **启动服务**
```bash
# 使用生产配置启动
java -jar target/approval-hub-1.0.0.jar --spring.profiles.active=prod

# 或使用nohup后台运行
nohup java -jar target/approval-hub-1.0.0.jar --spring.profiles.active=prod > app.log 2>&1 &
```

4. **配置为系统服务**

创建 `/etc/systemd/system/approval-hub.service`:
```ini
[Unit]
Description=YIR Approval Hub
After=syslog.target network.target

[Service]
User=appuser
ExecStart=/usr/bin/java -jar /opt/approval-hub/approval-hub-1.0.0.jar --spring.profiles.active=prod
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务:
```bash
sudo systemctl daemon-reload
sudo systemctl enable approval-hub
sudo systemctl start approval-hub
sudo systemctl status approval-hub
```

#### 前端部署

1. **构建生产版本**
```bash
cd frontend
npm run build
```

2. **部署到Web服务器**

**使用Nginx:**

安装Nginx:
```bash
sudo apt-get update
sudo apt-get install nginx
```

配置Nginx (`/etc/nginx/sites-available/approval-hub`):
```nginx
server {
    listen 80;
    server_name your-domain.com;

    root /var/www/approval-hub;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 安全头
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
}
```

部署前端文件:
```bash
sudo cp -r frontend/dist/* /var/www/approval-hub/
sudo ln -s /etc/nginx/sites-available/approval-hub /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 方案2: Docker部署

#### 创建Dockerfile（后端）
创建 `Dockerfile`:
```dockerfile
FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/approval-hub-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 创建Dockerfile（前端）
创建 `frontend/Dockerfile`:
```dockerfile
FROM node:14 as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### 创建docker-compose.yml
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: approval_hub
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./database/schema.sql:/docker-entrypoint-initdb.d/schema.sql

  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/approval_hub?useSSL=false&serverTimezone=UTC
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: rootpassword
    depends_on:
      - mysql

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql-data:
```

#### 部署
```bash
# 构建并启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

---

## 数据库备份与恢复

### 备份数据库
```bash
mysqldump -u root -p approval_hub > approval_hub_backup_$(date +%Y%m%d).sql
```

### 恢复数据库
```bash
mysql -u root -p approval_hub < approval_hub_backup_20240112.sql
```

---

## 监控和日志

### 应用日志
日志位置: `logs/approval-hub.log`

查看实时日志:
```bash
tail -f logs/approval-hub.log
```

### 系统监控
可以使用以下工具进行监控:
- **Spring Boot Actuator**: 添加依赖后访问 `/actuator`
- **Prometheus + Grafana**: 用于指标收集和可视化
- **ELK Stack**: 用于日志聚合和分析

---

## 安全加固

### 1. 修改默认密码
登录后立即修改所有测试账号的密码。

### 2. 使用HTTPS
配置SSL证书，强制使用HTTPS访问。

### 3. 使用环境变量管理敏感配置
**永远不要在配置文件中硬编码敏感信息！**

```bash
# 在生产环境中设置环境变量
export DB_USERNAME=your_secure_username
export DB_PASSWORD=your_secure_password
export JWT_SECRET=$(openssl rand -base64 64)

# 或者使用配置管理工具（如Kubernetes Secrets）
kubectl create secret generic app-secrets \
  --from-literal=db-username=your_username \
  --from-literal=db-password=your_password \
  --from-literal=jwt-secret=your_jwt_secret
```

### 4. 配置防火墙
```bash
# 只开放必要的端口
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

### 4. 数据库安全
- 使用强密码
- 限制数据库访问IP
- 定期备份数据
- **重要**: 不要在配置文件中使用默认的root密码
- 使用环境变量存储数据库凭证

### 5. 应用安全
- 更新JWT密钥为强随机字符串（至少64字符）
- 使用环境变量存储JWT密钥，不要硬编码
- 启用CORS限制
- 配置合适的会话超时时间

---

## 性能优化

### 1. 数据库优化
- 添加必要的索引
- 定期优化表
- 配置连接池参数

### 2. 应用优化
```properties
# 连接池配置
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# JPA缓存
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
```

### 3. 前端优化
- 启用Gzip压缩
- 配置浏览器缓存
- 使用CDN加速静态资源

---

## 常见问题

### Q1: 数据库连接失败
**问题**: `Communications link failure`

**解决方案**:
1. 检查MySQL服务是否运行
2. 验证数据库连接配置
3. 确认防火墙未阻止3306端口

### Q2: 前端无法访问后端API
**问题**: CORS错误或404

**解决方案**:
1. 检查后端服务是否运行
2. 验证CORS配置
3. 检查前端API基础URL配置

### Q3: JWT认证失败
**问题**: 401 Unauthorized

**解决方案**:
1. 清除浏览器LocalStorage
2. 重新登录获取新token
3. 检查JWT密钥配置

### Q4: 构建失败
**问题**: Maven或npm构建错误

**解决方案**:
1. 清理缓存: `mvn clean` 或 `npm cache clean --force`
2. 删除依赖重新下载
3. 检查网络连接和镜像源配置

---

## 技术支持

如遇到其他问题，请:
1. 查看日志文件
2. 搜索GitHub Issues
3. 提交新的Issue

---

**部署完成后，记得测试所有核心功能！**

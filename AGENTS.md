# 在线考试系统 - 项目上下文文档

## 项目概述

这是一个基于微服务架构的在线考试系统，采用前后端分离的设计模式。项目包含完整的考试管理、用户管理、课程管理、题库管理等功能模块。

### 技术栈

**后端技术栈：**
- **框架**: Spring Boot 2.7.8 + Spring Cloud 2021.0.5 + Spring Cloud Alibaba 2021.1
- **数据库**: MySQL 8.0.30 + Druid 连接池
- **ORM**: MyBatis-Plus 3.5.2
- **缓存**: Redis
- **认证授权**: Spring Security OAuth2 + JWT
- **API文档**: SpringDoc OpenAPI + Knife4j
- **消息队列**: RabbitMQ
- **服务治理**: Nacos (配置中心与服务发现)
- **分布式事务**: Seata 1.5.2
- **流量控制**: Sentinel 1.8.6
- **链路追踪**: Zipkin
- **其他工具**: Hutool 5.8.8, EasyExcel 3.1.4, FastJSON2 2.0.7

**前端技术栈：**
- **框架**: Vue 3.4.0 + Vue Router 4.2.5
- **UI组件库**: Element Plus 2.4.4
- **HTTP客户端**: Axios 1.6.2
- **图表库**: ECharts 5.4.3
- **构建工具**: Vite 5.0.0

## 项目结构

```
exam/
├── fte-common/                    # 通用模块
│   ├── fte-common-core/          # 核心通用组件
│   ├── fte-common-redis/         # Redis 通用配置
│   └── fte-common-web/           # Web 通用配置
├── fte-service/                   # 业务服务模块
│   ├── fte-exam-service/         # 考试服务 (端口: 8087)
│   ├── fte-file-service/         # 文件服务
│   ├── fte-mails-service/        # 邮件服务
│   ├── fte-message-service/      # 消息服务 (端口: 8083)
│   └── fte-user-service/         # 用户服务 (端口: 8079)
├── fte-api/                      # API接口模块
├── fte-gateway/                  # API网关 (端口: 10002)
├── fte-auth/                     # 认证服务 (端口: 10003)
├── fte-mbg/                      # MyBatis Generator 代码生成
├── exam-admin-frontend/          # 前端管理后台
├── docs/                         # 项目文档
├── images/                       # 项目图片资源
├── sql/                          # 数据库脚本
└── pom.xml                       # Maven 父项目配置
```

## 服务端口配置

| 服务名称 | 端口 | 说明 |
|---------|------|------|
| Gateway | 10002 | API网关，统一入口 |
| Auth | 10003 | 认证授权服务 |
| User Service | 8079 | 用户服务 |
| Exam Service | 8087 | 考试服务 |
| Message Service | 8083 | 消息服务 |

## 数据库配置

- **数据库名**: exam_system
- **连接URL**: `jdbc:mysql://localhost:3306/exam_system?allowMultiQueries=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false`
- **用户名**: root
- **密码**: 123456
- **连接池**: Druid

## Redis配置

- **主机**: localhost
- **端口**: 6379
- **密码**: 123456
- **数据库**: 0

## 构建与运行

### 后端服务构建

```bash
# 1. 安装依赖并构建整个项目
mvn clean install

# 2. 构建单个模块（例如考试服务）
cd fte-service/fte-exam-service
mvn clean package

# 3. 运行服务（使用Java 17兼容性设置）
java --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     --add-opens java.base/java.io=ALL-UNNAMED \
     --add-opens java.base/java.net=ALL-UNNAMED \
     --add-opens java.base/java.nio=ALL-UNNAMED \
     --add-opens java.base/sun.nio.ch=ALL-UNNAMED \
     --add-opens java.base/sun.security.ssl=ALL-UNNAMED \
     --add-opens java.base/sun.security.util=ALL-UNNAMED \
     --add-opens java.base/java.security=ALL-UNNAMED \
     --add-opens java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED \
     --add-opens java.base/java.util.concurrent=ALL-UNNAMED \
     -jar target/fte-exam-service-1.0-SNAPSHOT.jar

# 或者使用提供的启动脚本
startup.bat target/fte-exam-service-1.0-SNAPSHOT.jar
```

### 前端开发

```bash
# 进入前端目录
cd exam-admin-frontend

# 安装依赖
npm install

# 开发模式运行
npm run dev

# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

### 服务启动顺序建议

1. **基础设施服务**:
   - MySQL 数据库
   - Redis 缓存
   - RabbitMQ 消息队列
   - Nacos 服务注册中心 (可选，当前配置为禁用状态)

2. **业务服务** (顺序无关):
   - fte-auth (认证服务)
   - fte-user-service (用户服务)
   - fte-exam-service (考试服务)
   - fte-message-service (消息服务)
   - fte-file-service (文件服务)
   - fte-mails-service (邮件服务)

3. **网关服务**:
   - fte-gateway (最后启动，依赖其他服务)

## 开发约定

### 后端开发
1. **包结构**: 按照功能模块划分，使用 `com.baymax.exam` 作为基础包名
2. **API设计**: RESTful 风格，使用统一的响应格式
3. **数据库操作**: 使用 MyBatis-Plus，避免手写 SQL
4. **日志记录**: 使用 SLF4J + Logback，关键操作需要记录日志
5. **异常处理**: 使用统一的全局异常处理器
6. **参数校验**: 使用 JSR-303 注解进行参数校验

### 前端开发
1. **组件规范**: 使用 Composition API，按功能组织组件
2. **状态管理**: 使用 Pinia 或 Vuex 进行状态管理
3. **API调用**: 使用封装的 Axios 实例，统一错误处理
4. **路由管理**: 使用 Vue Router，按模块组织路由
5. **样式规范**: 使用 CSS 预处理器，遵循 BEM 命名规范

## 测试

### 单元测试
```bash
# 运行所有测试
mvn test

# 运行特定模块的测试
cd fte-service/fte-exam-service
mvn test
```

### API测试
- **Swagger UI**: `http://localhost:{port}/swagger-ui.html`
- **Knife4j UI**: `http://localhost:{port}/doc.html`
- **OpenAPI文档**: `http://localhost:{port}/v3/api-docs`

## 部署说明

### 环境要求
- **Java**: JDK 11 或更高版本 (推荐 JDK 17)
- **Node.js**: 16.x 或更高版本
- **MySQL**: 8.0 或更高版本
- **Redis**: 6.x 或更高版本
- **Maven**: 3.6.x 或更高版本

### 生产环境配置
1. 修改各服务的 `application.yml` 配置文件
2. 更新数据库连接信息为生产环境
3. 配置正确的 Redis 连接信息
4. 启用 Nacos 服务发现和配置中心（如需要）
5. 配置 SSL/TLS 证书（如需要 HTTPS）

## 常见问题

### Java 17 兼容性问题
项目已配置 Java 17 的模块访问权限，如果遇到模块访问错误，请使用提供的 `startup.bat` 脚本或相应的 JVM 参数启动服务。

### 服务无法启动
1. 检查端口是否被占用
2. 检查数据库连接配置
3. 检查 Redis 连接配置
4. 查看服务日志获取详细错误信息

### 前端构建失败
1. 检查 Node.js 版本是否符合要求
2. 清除 node_modules 重新安装依赖
3. 检查网络连接，确保能访问 npm 仓库

## 相关资源

- **数据库脚本**: `sql/exam_system.sql`
- **项目文档**: `docs/` 目录
- **系统图片**: `images/` 目录
- **API网关路由配置**: `fte-gateway/src/main/resources/application.yml`

## 注意事项

1. 本项目使用 Spring Cloud Alibaba 生态，部分功能依赖 Nacos，当前配置中 Nacos 相关功能默认禁用
2. 认证服务使用 JWT + RSA 非对称加密，公钥通过 `/rsa/publicKey` 端点提供
3. 网关配置了白名单路径，部分接口无需认证即可访问
4. 前端管理后台需要先启动后端服务才能正常使用
# ssm-crud-demo 项目说明

这是一份面向 Spring/SSM 初学者的项目说明。当前项目是一个传统 Java Web 项目：

- Java 21 编译
- Maven 构建
- 打包成 WAR
- 部署到外部 Tomcat
- Spring Framework / Spring MVC 负责 Web 和依赖管理
- Spring JDBC 负责数据源和事务
- MyBatis 负责 SQL 映射
- H2 内存数据库用于本地演示

## 1. 一句话理解 SSM

SSM 通常指：

- Spring：管理对象、装配依赖、管理事务
- Spring MVC：处理 HTTP 请求，提供 Controller
- MyBatis：把 Java 方法和 SQL 语句对应起来

在这个项目里，一个接口请求大概这样流动：

```text
浏览器/curl
  -> Tomcat
  -> Spring DispatcherServlet
  -> BookController
  -> BookService
  -> BookMapper
  -> BookMapper.xml 中的 SQL
  -> H2 数据库 books 表
```

## 2. 项目目录结构

```text
ssm-crud-demo
├── pom.xml
├── README.md
├── doc
│   └── project-guide.md
├── src
│   └── main
│       ├── java
│       │   └── com/example/ssm
│       │       ├── config
│       │       ├── controller
│       │       ├── domain
│       │       ├── mapper
│       │       └── service
│       ├── resources
│       │   ├── data.sql
│       │   ├── schema.sql
│       │   └── mappers/BookMapper.xml
│       └── webapp/WEB-INF/web.xml
└── target
    └── ssm-crud-demo.war
```

`target` 是 Maven 构建产物目录，可以删除后重新生成，不需要手写维护。

## 3. 关键文件说明

### `pom.xml`

Maven 项目配置文件，负责说明：

- 项目坐标：`groupId`、`artifactId`、`version`
- 打包方式：`war`
- Java 编译版本：`21`
- 项目依赖：Spring MVC、Spring JDBC、MyBatis、Jackson、H2
- 构建插件：编译插件和 WAR 打包插件

这里的 `<finalName>ssm-crud-demo</finalName>` 决定生成的 WAR 文件名：

```text
target/ssm-crud-demo.war
```

Tomcat 部署后，访问路径也会默认变成：

```text
http://localhost:8080/ssm-crud-demo/
```

### `src/main/webapp/WEB-INF/web.xml`

传统 Java Web 项目的描述文件。

这个项目里的 `web.xml` 很简洁，因为 Spring MVC 的核心注册已经交给 `AppInitializer.java` 完成。

### `config/AppInitializer.java`

这是 Spring MVC 的启动入口，替代老式 `web.xml` 里手写 `DispatcherServlet` 的方式。

它做三件事：

- 指定根容器配置：`RootConfig`
- 指定 Web MVC 配置：`WebConfig`
- 指定 DispatcherServlet 拦截路径：`/`

`DispatcherServlet` 是 Spring MVC 的前端控制器，所有进入 Spring MVC 的请求都会先到它这里。

### `config/RootConfig.java`

这是应用级配置，负责 Web 之外的核心 Bean：

- `DataSource`：数据库连接来源
- `SqlSessionFactory`：MyBatis 核心工厂
- `PlatformTransactionManager`：Spring 事务管理器
- `@ComponentScan`：扫描 Service
- `@MapperScan`：扫描 MyBatis Mapper 接口

当前项目使用 H2 内存数据库。Tomcat 启动应用时会自动执行：

```text
schema.sql
data.sql
```

所以每次重启应用，数据都会回到初始示例数据。

### `config/WebConfig.java`

这是 Spring MVC 配置，负责 Web 层：

- 开启 Spring MVC 注解能力
- 扫描 Controller
- 配置 Jackson JSON 转换器

这里注册了 `JavaTimeModule`，是为了让 `LocalDateTime` 能正常转成 JSON。

### `controller/HomeController.java`

首页说明接口。

访问：

```text
http://localhost:8080/ssm-crud-demo/
```

会返回一个 JSON，告诉你项目正在运行，以及真正的书籍接口地址。

### `controller/BookController.java`

REST 接口层。

它负责接收 HTTP 请求，并调用 `BookService`：

| 方法 | 路径 | 作用 |
|---|---|---|
| GET | `/api/books` | 查询所有书籍 |
| GET | `/api/books/{id}` | 查询单本书 |
| POST | `/api/books` | 新增书籍 |
| PUT | `/api/books/{id}` | 修改书籍 |
| DELETE | `/api/books/{id}` | 删除书籍 |

Controller 不直接写 SQL，也不直接处理数据库连接，这是分层设计的重点。

### `service/BookService.java`

业务层。

它负责：

- 调用 Mapper 完成数据操作
- 做简单参数校验
- 使用 `@Transactional` 管理事务
- 找不到数据时抛出异常，让 Controller 转成 404

写接口时，一般不要把业务规则都放在 Controller 里。Controller 主要处理 HTTP，Service 主要处理业务。

### `mapper/BookMapper.java`

MyBatis 的 Java Mapper 接口。

它只定义方法，不写实现类，例如：

```java
List<Book> findAll();
Book findById(Long id);
int insert(Book book);
```

真正执行的 SQL 写在 `BookMapper.xml` 中。MyBatis 会在运行时创建代理对象，把接口方法和 XML SQL 绑定起来。

### `resources/mappers/BookMapper.xml`

MyBatis SQL 映射文件。

这里写了真正的 SQL：

- `findAll` 对应查询全部
- `findById` 对应按主键查询
- `insert` 对应新增
- `update` 对应修改
- `deleteById` 对应删除

`namespace` 必须等于 Java Mapper 接口的完整类名：

```text
com.example.ssm.mapper.BookMapper
```

`resultMap` 负责把数据库列名映射到 Java 字段名，例如：

```text
created_at -> createdAt
updated_at -> updatedAt
```

### `domain/Book.java`

书籍实体类。

它对应数据库中的 `books` 表，也会作为 REST API 的 JSON 请求/响应对象。

例如接口返回的 JSON：

```json
{
  "id": 1,
  "title": "Spring MVC Guide",
  "author": "Spring Team",
  "price": 59.90,
  "createdAt": "2026-05-03T15:24:08.070652",
  "updatedAt": "2026-05-03T15:24:08.070652"
}
```

### `resources/schema.sql`

建表脚本。

应用启动时，Spring 会执行这个文件创建 `books` 表。

### `resources/data.sql`

初始化数据脚本。

应用启动时，Spring 会插入两条示例书籍数据。

## 4. REST 接口测试

基础地址：

```text
http://localhost:8080/ssm-crud-demo
```

查询全部：

```bash
curl http://localhost:8080/ssm-crud-demo/api/books
```

查询单条：

```bash
curl http://localhost:8080/ssm-crud-demo/api/books/1
```

新增：

```bash
curl -X POST http://localhost:8080/ssm-crud-demo/api/books \
  -H 'Content-Type: application/json' \
  -d '{"title":"Java 21 Notes","author":"Sangyu","price":39.90}'
```

修改：

```bash
curl -X PUT http://localhost:8080/ssm-crud-demo/api/books/1 \
  -H 'Content-Type: application/json' \
  -d '{"title":"Spring MVC Updated","author":"Spring Team","price":69.90}'
```

删除：

```bash
curl -X DELETE http://localhost:8080/ssm-crud-demo/api/books/1
```

## 5. 构建和部署

在项目目录下执行：

```bash
mvn clean package
```

生成：

```text
target/ssm-crud-demo.war
```

部署到 Tomcat：

```bash
cp target/ssm-crud-demo.war /opt/homebrew/opt/tomcat/libexec/webapps/
brew services restart tomcat
```

访问：

```text
http://localhost:8080/ssm-crud-demo/api/books
```

## 6. 为什么用 H2，而不是 MySQL

这个项目为了让你能立刻本地运行，使用了 H2 内存数据库。

优点：

- 不需要配置 MySQL 用户名和密码
- 启动应用自动建表
- 适合学习 SSM 的整体结构

缺点：

- 数据在应用重启后会重置
- 不适合正式保存业务数据

以后要换成 MySQL，主要修改 `RootConfig.dataSource()`，把 H2 数据源换成 MySQL 数据源，并添加 MySQL JDBC 驱动依赖。

## 7. 常见 404 原因

### 访问了错误路径

错误示例：

```text
http://localhost:8080/你的项目名/
```

`你的项目名` 只是说明文档里的占位符，不是真的项目名。

正确路径：

```text
http://localhost:8080/ssm-crud-demo/
http://localhost:8080/ssm-crud-demo/api/books
```

### WAR 名和访问路径不一致

Tomcat 默认使用 WAR 文件名作为访问路径。

如果 WAR 是：

```text
ssm-crud-demo.war
```

访问路径就是：

```text
/ssm-crud-demo
```

如果把 WAR 改名成：

```text
ROOT.war
```

访问路径才会变成根路径：

```text
/
```

## 8. 学习顺序建议

建议按这个顺序看代码：

1. `pom.xml`
2. `AppInitializer.java`
3. `WebConfig.java`
4. `RootConfig.java`
5. `BookController.java`
6. `BookService.java`
7. `BookMapper.java`
8. `BookMapper.xml`
9. `Book.java`
10. `schema.sql` 和 `data.sql`

这条路线就是从“项目如何启动”看到“请求如何进入”，再看到“业务如何调用数据库”。

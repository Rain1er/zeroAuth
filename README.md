# zeroAuth

## 简介
`zeroAuth` 是一个基于 montoya-api 开发的Burp Suite 的插件，旨在帮助安全研究人员和开发者快速发现和绕过身份验证机制的漏洞。
参考项目：  

https://github.com/0x727/BypassPro?tab=readme-ov-file
https://github.com/p0desta/AutoBypass403-BurpSuite

## 功能特性
- **主动扫描**：支持通过上下文菜单发送请求进行主动扫描。
- **被动扫描**：支持拦截代理请求并自动检测潜在的身份验证绕过漏洞。
- **灵活配置**：支持通过 YAML 文件自定义扫描规则，包括路径前缀、后缀和特殊头部。
- **多线程支持**：用户可自定义扫描线程数以提高效率。
- **结果展示**：通过图形界面展示扫描结果，支持查看请求和响应详情。

## 安装
1. 克隆项目到本地：
   ```bash
   git clone https://github.com/Rain1er/zeroAuth
   cd zeroAuth
   ```
2. 使用 Maven 构建项目：
   ```bash
   mvn clean package
   ```
3. 在 Burp Suite 中加载生成的插件 JAR 文件：
   - 进入 Burp Suite 的 `Extensions` 标签页。
   - 点击 `Add` 按钮，选择生成的 `zeroAuth.jar` 文件。

## 使用方法
1. **主动扫描**：
   - 右键点击 HTTP 请求，选择 `Send to zeroAuth`。
   - 插件将自动生成 payload 并发送请求，结果会显示在插件的界面中。

2. **被动扫描**：
   - 启用 `Passive Scan` 选项。
   - 插件会自动拦截代理请求并检测潜在漏洞。

3. **配置文件**：
   - 配置文件位于 `src/main/resources/config.yaml`。
   - 可自定义路径前缀、后缀、头部等规则。

## 配置示例
参考基于路由、ip的认证绕过方式，以下是 `config.yaml` 的部分示例：
```yaml
prefix:
# 白名单 + CVE-2020-1957，/demo/..;/admin/index其中demo为授权路径，admin/index为鉴权路径
  - "images/..;/"
# 白名单 + CVE-2016-6802，在访问路径前加上/任意目录名/../，即可绕过访问
  - "images/../"
# 模仿weaver的空格绕过，增加前缀fuzz /services%20/WorkflowServiceXml
  - "%20"
suffix:
# 静态文件后缀绕过
  - ".js"
  - ".css"
# 矩阵参数绕过
  - ";.js"
  - ";.css"
headers:
  - X-Custom-IP-Authorization: 127.0.0.1
```

## 开发者信息
- **作者**: raindrop
- **版本**: 1.0
- **许可证**: MIT

欢迎提交问题和贡献代码！如需帮助，请联系 [rain.xinc@gmail.com]。

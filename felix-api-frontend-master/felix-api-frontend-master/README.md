# Felix-API-Frontend

## 项目介绍
Felix-API 接口开放平台前端代码仓库。 这是一个提供API接口供开发者调用的平台
用户可以登录、注册，开通接口调用权限。接口的调用会被统计，后续可能收费。
管理员可以发布接口、下线接口、接入接口，以及在线调试接口。


## 项目背景

1. 前端开发都需要使用后端接口来获取数据。
2. 网上有很多现成的API接口调用平台

自己动手做一个API接口平台：
1. 用户可以访问前台，登录、注册，开通、关闭接口调用权限。
2. 管理员可以对接口进行增删改查

要求：
1. 防止攻击（安全性）
2. 不能随意调用（限制、开通）
3. 统计调用次数
4. 流量保护
5. API接入

## 技术选型
### 前端
- Ant Design Pro
- React
- Ant Design Procomponents
- Umi
- Umi Request（Axios的封装）

### 后端
- Spring Boot
- Spring Cloud Gateway
- Dobbo
- Nacos
- Spring Boot Starter（SDK开发）

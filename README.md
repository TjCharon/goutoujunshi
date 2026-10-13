# 狗头军师 Android 浮窗

一个个人侧载的 Android 对话副驾：在用户主动开启辅助功能与悬浮窗权限后，读取当前屏幕上可访问的聊天消息，由 Jev 做快速结构化判断，再通过用户自行配置的 OpenAI Chat Completions 兼容接口生成 3 条候选回复。

本项目基于 [Jev 聊天助手](https://github.com/jev-chat/jev-chat-jarvis) 二次开发，并保留其 `LICENSE` 与 `NOTICE`。

## 已实现

- 微信、QQ、X、飞书适配器；其它 App 可手动截屏识别一次。
- 正常 Android 辅助功能采集；没有可访问文本时使用本机 ML Kit 中文 OCR。
- Jev 判断意图、风险、需求、动作和是否应立即回复。
- OpenAI 兼容回复接口完全自定义：`Base URL`、`API Key`、`Model Name`。
- 半透明浮窗展示判断和 3 条候选；可复制或填入输入框，但绝不自动发送。
- API Key 使用 Android Keystore 加密保存。
- 聊天历史默认不落盘；关联上下文需用户主动开启。

## 权限与边界

- 不 Hook、不使用 Xposed、不修改聊天 App、不读取聊天数据库。
- 不伪装系统辅助功能服务，不绕过目标 App 的访问控制。
- 只处理用户本人设备上、本人有权查看的内容。
- 受保护窗口、图片消息、自绘界面或未向辅助功能暴露的文本可能读不到。
- 本地 OCR 截图不会上传；只有触发分析时，提取出的文字才会发送给用户配置的 Jev 与回复接口。
- 转账、红包、收款和发送按钮永远不触碰。

## 手机端配置

1. 安装 APK 并打开“狗头军师”。
2. 在设置中填写 Jev 判断接口。推荐选择 `TypeSafe 直连`，填写 Jev API Key。
3. 在“回复接口（OpenAI 兼容）”中填写 Base URL、API Key、Model Name。
4. 开启辅助功能、悬浮窗权限；小米/HyperOS 还需允许自启动并关闭电池限制。
5. 在聊天页面点击浮窗分析；选择候选后可填入输入框，确认无误后由本人手动发送。

## 构建

需要 JDK 17、Android SDK Platform 35 / Build Tools 35：

```bash
./gradlew test assembleDebug
```

APK 输出到：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 许可证与来源

本项目沿用上游 MIT License。分发时必须同时保留 [LICENSE](LICENSE) 与 [NOTICE](NOTICE)，不得暗示由上游作者背书。

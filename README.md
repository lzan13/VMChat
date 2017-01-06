MLChat
======
此项目是使用`AndroidStudio`通过集成环信`SDK3.x`集成的一个聊天项目，此项目旨在帮助开发者更好的集成聊天，
并且项目遵循`Android Medital Design`设计模式，方便开发者参考做出更符合`Android`自身风格的应用；
最主要的是此项目注释详细，每个类、方法都有进行说明 ^_^

### 开发环境
如果大家的开发环境版本过低建议及时进行更新，因为最新的`Android SDK`已经不需要翻墙就可以更新了，开发工具就像我们的武器，
可以免费更换好武器，你还一直抱着你的老三八大盖不放，这样干不过别人啊

>这边使用最新版 studio 进行开发，sdk 环境也基本都更新到最新
    AndroidStudio 2.2.2
    Build-Tools 25.0.2
    Gradle 2.14.1

已实现模块儿
-----------------
#### #基础模块

这些都是作为一个聊天APP所必须的一些功能，都已经实现，如果你发现什么bug，可以`fork`本项目自己修改并提交`pull request`，感谢你的贡献

| 实现功能            | 详细介绍        | 官方介绍            |
|:-------------------|:--------------:|:------------------:|
| 文本消息的收发与显示  |         | [Easemob 消息](http://docs.easemob.com/start/200androidcleintintegration/50singlechat)
| 图片消息的收发       |  |
| 查看大图            |  | 
| 文件消息收发         |  | 
| 语音消息收发         |  | 
| 消息的重发           |  | 
| 消息回执状态的展示    |  | 
| 消息监听与聊天界面刷新 |  | 
| 消息的下拉平滑加载    |  |
| 好友申请监听与处理    |  | 
| 音视频通话           | [Video and Voice](/wiki/VideoAndVoice) | [实时通话](http://docs.easemob.com/start/200androidcleintintegration/90realtimeaudio)


#### #扩展模块

关于扩展模块，这些都是通过环信SDK原有的一些功能扩展而来

- 会话置顶
- 会话扩展保存时间
- 消息回撤
- 消息草稿
- 输入状态提示


待实现模块儿
------
这些功能都是暂时还没有实现的一些功能，不过后期会慢慢加上，尽请期待

#### #基本模块
- 联系人获取与展示
- 位置消息收发
- 视频消息收发
- 群组创建与加入
- 群变化的监听
- 群组的展示
- 聊天室的加入

#### #扩展模块
- 群组@功能
- 阅后即焚
- 发送名片

License
---------

Copyright (c) 2015 lzan13

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

MLChat
======
此项目是使用`AndroidStudio`通过集成环信`SDK3.x`集成的一个聊天项目，此项目旨在帮助开发者更好的集成聊天，
并且项目遵循`Android Medital Design`设计模式，方便开发者参考做出更符合`Android`自身风格的应用；
最主要的是此项目注释详细，每个类、方法都有进行说明 ^_^

目录
-------
- [开发环境](#开发环境)
- [功能模块](#功能模块)
    - [基础模块](#基础模块)
    - [扩展模块](#扩展模块)
- [版权信息](#版权信息)

开发环境
----
这边使用最新版 `AndroidStudio` 进行开发，SDK 环境也基本都更新到最新，如果大家的开发环境版本过低建议及时进行更新，
因为最新的`Android SDK`已经不需要翻墙就可以更新了，开发工具就像我们的武器，可以免费更换好武器，
你还一直抱着你的老三八大盖不放，这样干不过别人啊！因此建议大家都更新下自己的环境！

>注意：因为国内从 github 有时 clone 项目比较慢，为了减小项目体积，因此没有将 libs 目录相关内容提交到仓库，
需开发者 clone 项目之后自己添加相关库，需要添加的库为：

>- Easemob 环信(3.2.3+)
>- 小米推送(3.0.3)
>- 华为推送(2705)
>- TalkingData 统计(2.2.30+)


功能模块
----
一些聊天App所必须的功能模块已在第一实现实现，如发现什么bug，可以`fork`本项目自己修改并提交`pull request`，感谢你的贡献

### 基础模块
| Base Module        | Project Wiki            | Easemob API           |    Remarks    |
|--------------------|:-----------------------:|:---------------------:|:-------------:|
| Text Message       | Null                    | [Message Doc][]       |               |
| Image Message      | Null                    | [Message Doc][]       |               |
| View Larger Image  | Null                    | Null                  |               |
| File Message       | Null                    | [Message Doc][]       |               |
| Voice Message      | Null                    | [Message Doc][]       |               |
| Resend Message     | Null                    | [Message Doc][]       |               |
| Message Ack        | Null                    | [Message API][]       |               |
| Message Listener   | Null                    | [Message Doc][]       |               |
| Load More Message  | Null                    | [Message Doc][]       |               |
| Contacts Apply For | Null                    | Null                  |               |
| Video And Voice    | [Video & Voice Wiki]    | [Video & Voice API][] |               |
| Contacts Manager   | Null                    | [Contacts Manager][]  |               |
| Location Message   | Null                    | [Message Doc][]       | Unimplemented |
| Video Message      | Null                    | [Message Doc][]       | Unimplemented |
| Group Manger       | Null                    | [Group Manager][]     | Unimplemented |
| Group Listener     | Null                    | [Group Manager][]     | Unimplemented |
| Chat Room Manager  | Null                    | [ChatRoom Manager][]  | Unimplemented |

### 扩展模块
| Expansion Module          | Project Wiki         | Easemob API         |        Remarks         |
|:--------------------------|:--------------------:|:-------------------:|:----------------------:|
| Conversation Top          | null                 | [Conversation Top]  | Use Conversation Ext   |
| Conversation Last Time    | null                 | [Conversation Top]  | Use Conversation Ext   |
| Conversation Draft        | null                 | [Conversation Top]  | Use Conversation Ext   |
| Recall Message            | null                 | [Message Doc]       | Use CMD & Message Ext  |
| Input Status              | null                 | [Message Doc]       | Use CMD Message        |
| Group At                  | null                 | null                | Unimplemented          |
| Read after the burning    | null                 | null                | Unimplemented          |
| Share Contacts Card       | null                 | null                | Unimplemented          |


版权信息
----

Copyright (c) 2015 lzan13

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


[Message API]: http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1chat_1_1_e_m_message.html 'Message API'
[Message Doc]: http://docs.easemob.com/im/200androidclientintegration/50singlechat 'Message '
[Call Wiki]: https://github.com/lzan13/MLChat/wiki/VideoAndVoice 'Call Wiki'
[Call API]: http://docs.easemob.com/im/200androidclientintegration/80audiovideo 'Call API'
[Contacts Manager]: http://docs.easemob.com/im/200androidclientintegration/60buddymgmt 'Contacts Manager'
[Group Manager]: http://docs.easemob.com/im/200androidclientintegration/70groupchat 'Group Manager'
[ChatRoom Manager]: http://docs.easemob.com/im/200androidclientintegration/75chatroom 'ChatRoom Manager'

[Conversation Top]: http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1chat_1_1_e_m_conversation.html 'Conversation Top'

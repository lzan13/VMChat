MLChatDemo
================
此项目是使用`AndroidStudio`通过集成环信`3.xSDK`集成的一个聊天项目，此项目旨在帮助开发者更好的集成聊天，
并且项目遵循`Android` `Medital Design`设计模式，方便开发者参考做出更符合`Android`设计模式的应用；
最主要的是此项目注视详细，每个类、方法都有进行说明，

### 首先说下此Demo开发环境
如果大家的开发环境版本过低建议及时进行更新，因为最新的sdk已经不需要翻墙就可以更新了，开发工具就像我们的武器，
可以免费更换好武器，你还一直抱着你的老三八大盖不放，这样干不过别人啊
>AndroidStudio 1.5.1  
Android SDK Tools 24.4.1  
Android SDK Build-tools 23.0.2  
Android SDK compileSdkVersion 23  
Android SDK targetSdkVersion 22  
Android SDK minSdkVersion 15  
Gradle 2.8
    
### 所使用库的版本
>compile fileTree(include: ['*.jar'], dir: 'libs')  
// Android V7库，因为已经包含了V4库，所以这里不需要再引入V4库了  
compile 'com.android.support:appcompat-v7:23.2.1'  
compile 'com.android.support:design:23.2.1'  
// Android 官方提供的替代 ListView  
compile 'com.android.support:recyclerview-v7:23.2.1'  
// 第三方图片加载库  
compile 'com.github.bumptech.glide:glide:3.7.0'  
// 环信sdk  
compile files('libs/hyphenatechat_3.1.0.jar')  

已实现模块儿
-----------------
##### 基础模块
- 文本消息的收发与显示
- 消息监听与聊天界面刷新
- 消息的下拉平滑加载
- 好友申请监听与更新


##### 扩展模块
- 会话置顶
- 记录会话最后时间

待实现模块儿
---------------------
##### 基本模块
- 群组创建与加入
- 群组的展示
- 聊天室的加入

##### 扩展模块
- 群组@功能
- 阅后即焚
- 消息回撤

更新记录
-------------------
更改`ListView`为`RecyclerView` 2016-3-17  
实现消息重发，修改扩展菜单为`GridView` 2016-3-21 



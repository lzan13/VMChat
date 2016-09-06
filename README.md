MLEaseChat
================
此项目是使用`AndroidStudio`通过集成环信`SDK3.x`集成的一个聊天项目，此项目旨在帮助开发者更好的集成聊天，
并且项目遵循`Android Medital Design`设计模式，方便开发者参考做出更符合`Android`自身风格的应用；
最主要的是此项目注释详细，每个类、方法都有进行说明 ^_^

### 首先说下此项目开发环境
如果大家的开发环境版本过低建议及时进行更新，因为最新的`SDK-Tools`已经不需要翻墙就可以更新了，开发工具就像我们的武器，
可以免费更换好武器，你还一直抱着你的老三八大盖不放，这样干不过别人啊
```gradle
AndroidStudio 2.1.2
Android SDK Tools 25.1.7
Android SDK Build-tools 24.0.2
Android SDK compileSdkVersion 24
Android SDK targetSdkVersion 22
Android SDK minSdkVersion 15
Gradle 2.10
Genymotion 2.7.2
```

### 所使用库的版本
```gradle
    compile fileTree(include: ['*.jar'], dir: 'libs')
    /**
     *     新的遵循 Android  Material design 设计风格库，此扩展库已经包含了一下三个扩展库，如果引入了design，
     *     就不需要再单独引入其他库
     *     support-v4
     *     appcomat-v7
     *     recyclerView库
     */
    compile 'com.android.support:design:24.2.0'
    // 解决方法数超过65536问题扩展库
    compile 'com.android.support:multidex:1.0.0'
    // Google Play Service 库，使用GCM推送需要
    compile 'com.google.android.gms:play-services-gcm:9.4.0'
    // LeakCanary Debug库，开源捕获内存溢出的库
    debugCompile 'com.squareup.leakcanary:leakcanary-android:1.4-beta2'
    // LeakCanary 发版的库
    releaseCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.4-beta2'
    // 第三方图片加载库
    compile 'com.github.bumptech.glide:glide:3.7.0'
    // 观察者模式解耦库
    compile 'org.greenrobot:eventbus:3.0.0'
    // 支持手势缩放的ImageView
    compile 'com.bm.photoview:library:1.4.1'
    /**
     *     引入各种第三方 SDK jar包，在上边 fileTree 里已经包含了libs，这里可以不用再单独添加
     */
    compile files('libs/xiaomi_push_v303.jar')
    compile files('libs/huanxin_chat_v315.jar')
    compile files('libs/huawei_push_v2705.jar')
    compile files('libs/talkingdata_analytics_v2230.jar')
```

已实现模块儿
-----------------
#### 基础模块

这些都是作为一个聊天`APP`做必须的一些功能，都已经实现，如果你发现什么`bug`，可以`fork`本项目自己修改并提交`pull request`，感谢你的贡献

- 文本消息的收发与显示
- 图片消息的收发
- 查看大图
- 文件消息收发
- 语音消息的收发，以及录制语音控件的自定义实现
- 消息的重发
- 消息回执状态的展示
- 消息监听与聊天界面刷新
- 消息的下拉平滑加载
- 好友申请监听与处理
- 语音通话以及通话界面的最小化与恢复
- 视频通话以及通话界面最小化，恢复后有bug，看不了对方图像，待解决



#### 扩展模块

关于扩展模块，这些都是通过环信SDK原有的一些功能扩展而来，比如会话置顶是给`Conversation.setExtFiled()`的方式实现

- 会话置顶
- 记录会话最后时间
- 消息回撤
- 消息草稿


待实现模块儿
------
这些功能都是暂时还没有实现的一些功能，不过后期会慢慢加上，尽请期待

#### 基本模块
- 联系人获取与展示
- 位置消息收发
- 视频消息收发
- 群组创建与加入
- 群变化的监听
- 群组的展示
- 聊天室的加入

#### 扩展模块
- 群组@功能
- 阅后即焚
- 发送名片




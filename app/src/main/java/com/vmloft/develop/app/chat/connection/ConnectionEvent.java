package com.vmloft.develop.app.chat.connection;

/**
 * Created by lzan13 on 2016/5/30.
 * 自定义连接监听 EventBus post 事件，传递链接变化状态
 */
public class ConnectionEvent {
    // 链接状态
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ConnectionEvent() {

    }
}

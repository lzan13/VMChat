package net.melove.app.chat.application.eventbus;

/**
 * Created by lzan13 on 2016/5/30.
 * 自定义申请与通知 EventBus post 事件，传递事件与通知信息
 */
public class MLInvitedEvent {

    // 通知类型 0（联系人），1（群组）
    private int type;

    public MLInvitedEvent() {

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

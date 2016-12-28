package net.melove.app.chat.apply;

import com.hyphenate.chat.EMMessage;

/**
 * Created by lzan13 on 2016/5/30.
 * 自定义申请与通知 EventBus post 事件，传递事件与通知信息
 */
public class MLApplyEvent {


    // 保存申请与通知的消息对象
    private EMMessage message;


    public MLApplyEvent() {

    }

    public EMMessage getMessage() {
        return message;
    }

    public void setMessage(EMMessage message) {
        this.message = message;
    }

}

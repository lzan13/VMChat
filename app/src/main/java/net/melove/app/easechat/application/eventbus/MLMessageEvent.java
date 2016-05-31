package net.melove.app.easechat.application.eventbus;

import com.hyphenate.chat.EMMessage;

/**
 * Created by lzan13 on 2016/5/30.
 * 自定义EventBus post 的事件
 */
public class MLMessageEvent {

    private EMMessage message;
    private int progress;

    public MLMessageEvent() {

    }

    public EMMessage getMessage() {
        return message;
    }

    public void setMessage(EMMessage message) {
        this.message = message;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}

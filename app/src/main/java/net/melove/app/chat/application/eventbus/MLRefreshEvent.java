package net.melove.app.chat.application.eventbus;

/**
 * Created by lzan13 on 2016/6/30.
 * 刷新事件
 */
public class MLRefreshEvent {
    // 刷新的数量
    private int count;
    // 刷新的位置
    private int position;
    // 刷新方式类型
    private int type;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

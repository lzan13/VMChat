package com.vmloft.develop.app.chat.room;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hyphenate.chat.EMChatRoom;
import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.interfaces.ItemCallBack;
import com.vmloft.develop.library.tools.widget.VMImageView;
import java.util.List;

/**
 * Created by lzan13 on 2015/8/26.
 * 联系人列表适配器
 */
public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomsViewHolder> {

    private Context context;
    private ItemCallBack callBack;

    private LayoutInflater inflater;
    private List<EMChatRoom> roomsList;

    public RoomsAdapter(Context context, List<EMChatRoom> list) {
        this.context = context;
        roomsList = list;
        inflater = LayoutInflater.from(this.context);
    }

    /**
     * 获取当前数据源大小
     *
     * @return 返回当前适配器数据源 Item 个数
     */
    @Override public int getItemCount() {
        return roomsList.size();
    }

    @Override public RoomsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_contacts, parent, false);
        return new RoomsViewHolder(itemView);
    }

    /**
     * @param holder
     * @param position
     */
    @Override public void onBindViewHolder(final RoomsViewHolder holder, final int position) {

        // 设置用户昵称
        //holder.nickNameView.setText(
        //        TextUtils.isEmpty(user.getNickName()) ? user.getUserName() : user.getNickName());
        //
        //// 设置联系人头像点击监听
        //holder.avatarView.setOnClickListener(new View.OnClickListener() {
        //    @Override public void onClick(View view) {
        //        callBack.onAction(holder.avatarView.getId(), position);
        //    }
        //});
        //// 设置 itemView 点击监听事件
        //holder.itemView.setOnClickListener(new View.OnClickListener() {
        //    @Override public void onClick(View v) {
        //        callBack.onAction(Constants.ACTION_CLICK, position);
        //    }
        //});
        //// 设置 itemView 长按监听事件
        //holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
        //    @Override public boolean onLongClick(View view) {
        //        callBack.onAction(Constants.ACTION_LONG_CLICK, position);
        //        return false;
        //    }
        //});
    }

    /**
     * 设置 Item 项回调接口
     */
    public void setItemCallBack(ItemCallBack callback) {
        callBack = callback;
    }

    /**
     * 自定义联系人 ViewHolder 用来展示联系人数据
     */
    public static class RoomsViewHolder extends RecyclerView.ViewHolder {
        VMImageView avatarView;
        TextView nickNameView;

        /**
         * 构造方法
         *
         * @param itemView 显示联系人数据的 ItemView
         */
        public RoomsViewHolder(View itemView) {
            super(itemView);
            avatarView = (VMImageView) itemView.findViewById(R.id.img_avatar);
            nickNameView = (TextView) itemView.findViewById(R.id.text_nickname);
        }
    }
}

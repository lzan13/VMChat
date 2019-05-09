package com.vmloft.develop.app.chat.ui.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vmloft.develop.app.chat.common.AConstants;
import com.vmloft.develop.app.chat.R;
import com.vmloft.develop.app.chat.interfaces.ItemCallBack;

import com.vmloft.develop.library.tools.widget.VMImageView;
import java.util.List;

/**
 * Created by lzan13 on 2015/8/26.
 * 联系人列表适配器
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    private Context mContext;
    private ItemCallBack mCallBack;

    private LayoutInflater mInflater;
    private List<UserEntity> mContactsList;

    public ContactsAdapter(Context context, List<UserEntity> list) {
        mContext = context;
        mContactsList = list;
        mInflater = LayoutInflater.from(mContext);
    }

    /**
     * 获取当前数据源大小
     *
     * @return 返回当前适配器数据源 Item 个数
     */
    @Override public int getItemCount() {
        return mContactsList.size();
    }

    @Override public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_contacts, parent, false);
        return new ContactsViewHolder(itemView);
    }

    /**
     * @param holder
     * @param position
     */
    @Override public void onBindViewHolder(final ContactsViewHolder holder, final int position) {
        UserEntity user = mContactsList.get(position);

        // 设置用户昵称
        holder.nickNameView.setText(
                TextUtils.isEmpty(user.getNickName()) ? user.getUserName() : user.getNickName());

        // 设置联系人头像点击监听
        holder.avatarView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mCallBack.onAction(holder.avatarView.getId(), position);
            }
        });
        // 设置 itemView 点击监听事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mCallBack.onAction(AConstants.ACTION_CLICK, position);
            }
        });
        // 设置 itemView 长按监听事件
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View view) {
                mCallBack.onAction(AConstants.ACTION_LONG_CLICK, position);
                return false;
            }
        });
    }

    /**
     * 设置 Item 项回调接口
     */
    public void setItemCallBack(ItemCallBack callback) {
        mCallBack = callback;
    }

    /**
     * 自定义联系人 ViewHolder 用来展示联系人数据
     */
    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        VMImageView avatarView;
        TextView nickNameView;

        /**
         * 构造方法
         *
         * @param itemView 显示联系人数据的 ItemView
         */
        public ContactsViewHolder(View itemView) {
            super(itemView);
            avatarView = (VMImageView) itemView.findViewById(R.id.img_avatar);
            nickNameView = (TextView) itemView.findViewById(R.id.text_nickname);
        }
    }
}

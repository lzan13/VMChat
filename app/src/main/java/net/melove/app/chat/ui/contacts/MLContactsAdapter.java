package net.melove.app.chat.ui.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.melove.app.chat.MLConstants;
import net.melove.app.chat.R;
import net.melove.app.chat.module.listener.MLItemCallBack;
import net.melove.app.chat.ui.widget.MLImageView;

import java.util.List;

/**
 * Created by lzan13 on 2015/8/26.
 * 联系人列表适配器
 */
public class MLContactsAdapter extends RecyclerView.Adapter<MLContactsAdapter.ContactsViewHolder> {

    private Context mContext;
    private MLItemCallBack mCallBack;

    private LayoutInflater mInflater;
    private List<MLUserEntity> mContactsList;

    public MLContactsAdapter(Context context, List<MLUserEntity> list) {
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
        View itemView = mInflater.inflate(R.layout.item_user, parent, false);
        return new ContactsViewHolder(itemView);
    }

    /**
     * @param holder
     * @param position
     */
    @Override public void onBindViewHolder(ContactsViewHolder holder, final int position) {
        MLUserEntity user = mContactsList.get(position);

        // 设置用户昵称
        holder.nickNameView.setText(
                TextUtils.isEmpty(user.getNickName()) ? user.getNickName() : user.getUserName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mCallBack.onAction(MLConstants.ML_ACTION_CLICK, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View view) {
                mCallBack.onAction(MLConstants.ML_ACTION_LONG_CLICK, position);
                return false;
            }
        });
    }

    public void setItemCallBack(MLItemCallBack callback) {
        mCallBack = callback;
    }

    /**
     * 自定义联系人 ViewHolder 用来展示联系人数据
     */
    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        MLImageView avatarView;
        TextView nickNameView;

        /**
         * 构造方法
         *
         * @param itemView 显示联系人数据的 ItemView
         */
        public ContactsViewHolder(View itemView) {
            super(itemView);
            avatarView = (MLImageView) itemView.findViewById(R.id.img_avatar);
            nickNameView = (TextView) itemView.findViewById(R.id.text_nickname);
        }
    }
}

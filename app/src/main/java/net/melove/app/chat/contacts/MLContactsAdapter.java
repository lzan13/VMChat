package net.melove.app.chat.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.melove.app.chat.R;
import net.melove.app.chat.communal.widget.MLImageView;

import java.util.List;

/**
 * Created by lzan13 on 2015/8/26.
 * 联系人列表适配器
 */
public class MLContactsAdapter extends RecyclerView.Adapter<MLContactsAdapter.ContactsViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<MLContactsEntity> mContactsList;


    public MLContactsAdapter(Context context, List<MLContactsEntity> list) {
        mContext = context;
        mContactsList = list;
    }

    /**
     * 获取当前数据源大小
     *
     * @return 返回当前适配器数据源 Item 个数
     */
    @Override
    public int getItemCount() {
        return mContactsList.size();
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_contacts, parent, false);
        return new ContactsViewHolder(itemView);
    }

    /**
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 自定义联系人 ViewHolder 用来展示联系人数据
     */
    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        MLImageView imageViewAvatar;
        TextView textViewUsername;

        /**
         * 构造方法
         *
         * @param itemView 显示联系人数据的 ItemView
         */
        public ContactsViewHolder(View itemView) {
            super(itemView);
            imageViewAvatar = (MLImageView) itemView.findViewById(R.id.ml_img_invited_avatar);
            textViewUsername = (TextView) itemView.findViewById(R.id.ml_text_invited_username);

        }
    }
}

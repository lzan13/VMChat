package net.melove.demo.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;

import java.util.Map;

/**
 * Created by lz on 2015/12/13.
 */
public class MLConversationAdapter extends BaseAdapter {

    private Context mContext;

    private Map<String, EMConversation> mConversationMap;

    public MLConversationAdapter(Context context) {
        mContext = context;
        mConversationMap = EMChatManager.getInstance().getAllConversations();

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}

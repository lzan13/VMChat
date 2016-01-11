package net.melove.demo.chat.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import net.melove.demo.chat.R;

/**
 * Created by lzan13 on 2016/1/11.
 */
public class MLSmiliesAdapter extends BaseAdapter {

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

    static class ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
//            imageView = view.findViewById(R.id.ml_img_)
        }

    }
}

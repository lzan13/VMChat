package net.melove.demo.chat.contacts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.melove.demo.chat.R;
import net.melove.demo.chat.database.MLInvitedDao;
import net.melove.demo.chat.common.widget.MLToast;
import net.melove.demo.chat.common.base.MLBaseFragment;

import java.util.List;

/**
 * Created by lzan13 on 2015/3/28.
 */
public class MLInvitedFragment extends MLBaseFragment {


    private MLInvitedDao mInvitedDao;
    private List<MLInvitedEntity> mList;
    private MLInvitedAdapter mInvitedAdapter;
    private ListView mListView;

    public MLInvitedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invited, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initView();
    }

    private void init() {
        mActivity = getActivity();
        mInvitedDao = new MLInvitedDao(mActivity);
    }


    private void initView() {

        mList = mInvitedDao.getInvitedList();
        mInvitedAdapter = new MLInvitedAdapter(mActivity, mList);
        // 初始化ListView
        mListView = (ListView) getView().findViewById(R.id.ml_listview_invited);
        mListView.setAdapter(mInvitedAdapter);
        setItemClickListener();
        mListView.setEmptyView(getView().findViewById(R.id.ml_layout_empty));
    }

    /**
     * by lzan13 2015-11-2 11:25:04
     * 列表项点击事件
     */
    private void setItemClickListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MLToast.makeToast("item " + position).show();
            }
        });
    }

    private void setItemLongClickListener(){
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
                dialog.setTitle(mActivity.getResources().getString(R.string.ml_dialog_title_apply_for));
                dialog.setMessage(mActivity.getResources().getString(R.string.ml_dialog_content_add_contact));
                dialog.setPositiveButton(R.string.ml_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setNegativeButton(R.string.ml_btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                return false;
            }
        });
    }

}

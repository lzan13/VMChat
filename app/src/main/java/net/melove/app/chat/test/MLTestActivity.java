package net.melove.app.chat.test;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.view.View;
import net.melove.app.chat.R;
import net.melove.app.chat.ui.MLBaseActivity;

public class MLTestActivity extends MLBaseActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        setSupportActionBar(getToolbar());
        getToolbar().setTitle("Test Activity");
        getToolbar().setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onFinish();
            }
        });
    }
}

package net.melove.app.chat.test;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import net.melove.app.chat.R;
import net.melove.app.chat.ui.MLBaseActivity;

public class MLTestActivity extends MLBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

}

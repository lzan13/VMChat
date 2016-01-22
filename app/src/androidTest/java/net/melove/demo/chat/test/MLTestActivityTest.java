package net.melove.demo.chat.test;

import android.test.ActivityInstrumentationTestCase2;

import junit.framework.TestCase;

import net.melove.demo.chat.util.MLLog;

/**
 * Created by lzan13 on 2016/1/15.
 */
public class MLTestActivityTest extends ActivityInstrumentationTestCase2<MLTestActivity> {

    private MLTestActivity mTestActivity;

    public MLTestActivityTest(Class<MLTestActivity> activityClass) {
        super(activityClass);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mTestActivity = getActivity();
        MLLog.d("MLTestActivityTest setUp!");

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
package net.melove.demo.chat;

import android.content.Context;
import android.test.InstrumentationTestCase;

import net.melove.demo.chat.util.MLLog;

/**
 * Created by lzan13 on 2016/1/15.
 */
public class MLHelperTest extends InstrumentationTestCase {

    private Context mContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLog() {
        MLLog.d("test log!");
    }

}

package com.jaeckel.wipe.test;

import android.test.ActivityInstrumentationTestCase2;
import com.jaeckel.wipe.*;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<WipeConfigActivity> {

    public HelloAndroidActivityTest() {
        super(WipeConfigActivity.class);
    }

    public void testActivity() {
        WipeConfigActivity activity = getActivity();
        assertNotNull(activity);
    }
}


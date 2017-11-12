package org.ros2.android.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class RosManagerTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        this.context = InstrumentationRegistry.getContext();
    }

    @After
    public void tearDown() throws Exception {
        this.context = null;
    }

    RosConfig  config;
    RosManager manager;

    @Test
    public void testConnect() {

        this.config = new RosConfig();

        // Initialize the Ros Service as a normal Android Service.
        // Since we call manager.disconnect() in onPause, this will unbind the
        // Ros Service, so every time onResume is called, we should
        // create a new RosManager object.
        this.manager = new RosManager(this.context, new Runnable() {

            @Override
            public void run() {
                synchronized (RosManagerTest.this) {
                    try {
                        config = setupRosConfig(manager);
                        manager.connect(config);
                    } catch (Exception e) {

                    }
                }
            }
        });

        assertNotNull(this.manager);

    }

    private RosConfig setupRosConfig(RosManager ros) {
        RosConfig config = ros.getConfig(RosConfig.CONFIG_TYPE_DEFAULT);
        // Configure your instance...
        return config;
    }

}

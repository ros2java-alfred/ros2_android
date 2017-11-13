package org.ros2.android.core;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.ros2.android.core.node.AndroidNativeNode;
import org.ros2.android.core.node.AndroidNode;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class RosManagerTest {

    private Context context;

    private RosConfig config;
    private RosManager manager;

    @Before
    public void setUp() throws Exception {
        this.context = InstrumentationRegistry.getContext();
    }

    @After
    public void tearDown() throws Exception {
        this.context = null;
    }

    @Test
    public void testConnect() {
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

        assertNotNull(this.config);
        assertNotNull(this.manager);

        if (this.manager != null) {
            this.manager.disconnect();
        }
    }

    @Test
    public void testAddNode() {
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

        if (this.manager != null) {
            AndroidNode node = this.setupRosNode();
            this.manager.addNode(node);

            assertEquals(1, this.manager.getNodes().size());

            this.manager.removeNode(node);
            this.manager.disconnect();
        }
    }

    @Test
    public void testRemoveNode() {
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

        if (this.manager != null) {
            AndroidNode node = this.setupRosNode();
            this.manager.addNode(node);
            this.manager.removeNode(node);

            assertEquals(0, this.manager.getNodes().size());

            this.manager.disconnect();
        }
    }

    @Test
    public void testGetVersion() {
        int currentVersion = RosManager.getVersion(this.context);
        assertEquals(Build.VERSION.SDK_INT*10000000 + 1000, currentVersion);
    }

    @Test
    public void testGetRequestPermission() {
        Intent intent = RosManager.getRequestPermissionIntent("");

        assertNotNull(intent);
    }

    @Test
    public void testHasPermission() {
        boolean isPermit = RosManager.hasPermission(this.context, "");

        assertTrue(isPermit);
    }

    private RosConfig setupRosConfig(RosManager ros) {
        RosConfig config = ros.getConfig(RosConfig.CONFIG_TYPE_DEFAULT);
        // Configure your instance...
        return config;
    }

    private AndroidNode setupRosNode() {
        AndroidNode node = new AndroidNativeNode("test", this.context);
        // Custom node components...
        return node;
    }
}

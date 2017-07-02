package org.ros2.android.core;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ros2.android.core.node.NodeAndroid;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NodeMainExecutorServiceTest {

    private Context context;

    @Rule
    public final ServiceTestRule mServiceRule = ServiceTestRule.withTimeout(60L, TimeUnit.SECONDS);

    @Before
    public void setUp() throws Exception {
        this.context = InstrumentationRegistry.getContext();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void onStartCommand() throws Exception {
    }

    @Test
    public void onDestroy() throws Exception {
    }

    @Test
    public void shutdown() throws Exception {
    }

    @Test
    public void execute() throws Exception {
    }

    @Test
    public void onBind() throws Exception {
    }

    @Test
    public void testWithBoundService() throws TimeoutException {
        // Create the service Intent.
        Intent serviceIntent = new Intent(this.context, NodeMainExecutorService.class);

        // Data can be passed to the service via the Intent.
        //serviceIntent.putExtra(NodeMainExecutorService.SEED_KEY, 42L);

        // Bind the service and grab a reference to the binder.
        IBinder binder = mServiceRule.bindService(serviceIntent);

        // Get the reference to the service, or you can call
        // public methods on the binder directly.
        NodeMainExecutorService service =
                ((NodeMainExecutorService.LocalBinder) binder).getService();

        // Verify that the service is working correctly.
        NodeAndroid node = new NodeAndroid(this.context);
        service.execute(node);
        //assertThat(service.getRandomInt(), is(any(Integer.class)));

        int i = 0;
        i += 1;
    }
}
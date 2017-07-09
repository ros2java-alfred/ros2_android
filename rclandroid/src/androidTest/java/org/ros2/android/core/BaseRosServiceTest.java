/* Copyright 2017 Mickael Gaillard <mick.gaillard@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import org.ros2.android.core.mock.MockRosService;
import org.ros2.android.core.node.AndroidNativeNode;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BaseRosServiceTest {

    private Context context;

    @Rule
    public final ServiceTestRule mServiceRule = ServiceTestRule.withTimeout(60L, TimeUnit.SECONDS);

    @Before
    public void setUp() throws Exception {
        this.context = InstrumentationRegistry.getContext();
    }

    @After
    public void tearDown() throws Exception {
        this.context = null;
    }

    @Test
    public void onBind() throws Exception {
    }

    @Test
    public void testWithBoundService() throws TimeoutException, InterruptedException {
        // Create the service Intent.
        Intent serviceIntent = new Intent(this.context, MockRosService.class);

        // Data can be passed to the service via the Intent.
        //serviceIntent.putExtra(NodeMainExecutorService.SEED_KEY, 42L);
        this.context.startService(serviceIntent);

        // Bind the service and grab a reference to the binder.
        IBinder binder = mServiceRule.bindService(serviceIntent);

        // Get the reference to the service, or you can call
        // public methods on the binder directly.
        MockRosService serviceExecutor =
                (MockRosService) ((MockRosService.RosBinder) binder).getService();

        // Verify that the service is working correctly.
        AndroidNativeNode node = new AndroidNativeNode("_test", this.context);
        serviceExecutor.addNode(node);
        //assertThat(service.getRandomInt(), is(any(Integer.class)));

        Thread.sleep(1000);

        serviceExecutor.removeNode(node);
        this.context.stopService(serviceIntent);

        int i = 0;
        i += 1;
    }
}
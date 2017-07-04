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

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.ros2.android.core.node.AndroidNativeNode;
import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.executor.MultiThreadedExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * Ros2 on Service.
 */
public abstract class BaseRosService extends Service {
    static final String TAG = "NodeMainExecutorService";

    /** Binder given to client. */
    private final IBinder mBinder = new RosBinder();

    /** Service Executor */
    private MultiThreadedExecutor executor;

    protected class RosBinder extends Binder {
        public BaseRosService getService() {
            return BaseRosService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStart");
        int result = super.onStartCommand(intent, flags, startId);

        if (!RCLJava.isInitialized()) {
            RCLJava.rclJavaInit();
        }

        if (this.executor == null) {
            this.executor = new MultiThreadedExecutor();
            this.executor.spin();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override
    public boolean stopService (Intent service) {
        if (this.executor != null) {
            this.executor.cancel();
            this.executor = null;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        if (RCLJava.isInitialized()) {
            RCLJava.shutdown();
        }

        super.onDestroy();
    }

    public void addNode(AndroidNativeNode node) {
        this.executor.addNode(node);
    }

    public void removeNode(AndroidNativeNode node) {
        this.executor.removeNode(node);
    }

    public List<AndroidNativeNode> getNodes() {
        return new ArrayList<>();
    }
}

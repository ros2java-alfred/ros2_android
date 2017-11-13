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
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.ros2.android.core.node.AndroidNode;
import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.executor.MultiThreadedExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * Ros2 on Service.
 */
public class RosService extends Service {
    private static final String TAG = "RosService";

    /** Binder given to client. */
    private final IBinder mBinder = new RosBinder();

    /** Service Executor */
    private MultiThreadedExecutor executor;

    /** Main Service thread */
    private volatile HandlerThread handlerThread;
    private ServiceHandler serviceHandler;

    /** Binder */
    protected class RosBinder extends Binder {
        public RosService getService() {
            return RosService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Start RosService");

        // An Android handler thread internally operates on a looper.
        this.handlerThread = new HandlerThread("Service Main Thread");
        this.handlerThread.start();

        // An Android service handler is a handler running on a specific background thread.
        this.serviceHandler = new ServiceHandler(this.handlerThread.getLooper());

        if (!RCLJava.isInitialized()) {
            RCLJava.rclJavaInit();
        }
    }

    //    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStart");
        int result = super.onStartCommand(intent, flags, startId);

        this.serviceHandler.sendEmptyMessage(0);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        this.serviceHandler.sendEmptyMessage(1);

        return this.mBinder;
    }

    @Override
    public boolean stopService (Intent service) {
        if (RosService.this.executor != null) {
            RosService.this.executor.cancel();
            RosService.this.executor = null;
        }

        return true;
    }

    @Override
    public void onDestroy() {
        // Cleanup service before destruction
        this.handlerThread.quit();

        if (RCLJava.isInitialized()) {
            RCLJava.shutdown();
        }

        super.onDestroy();
    }

    // Define how the handler will process messages
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        // Define how to handle any incoming messages here
        @Override
        public void handleMessage(Message message) {
            if (RosService.this.executor == null) {
                RosService.this.executor = new MultiThreadedExecutor();
                RosService.this.executor.spin();
            }

            stopSelf();
        }
    }

    public void addNode(AndroidNode node) {
        this.executor.addNode(node);
    }

    public void removeNode(AndroidNode node) {
        this.executor.removeNode(node);
    }

    public List<AndroidNode> getNodes() {
        return new ArrayList<>();
    }
}

/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros2.android.core;

import com.google.common.base.Preconditions;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.ros2.android.core.node.NodeAndroid;

public abstract class AbstractRosApplication extends Application {

    private NodeMainExecutorService nodeMainExecutorService;

    private final NodeMainExecutorServiceConnection nodeMainExecutorServiceConnection;
    private final class NodeMainExecutorServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            nodeMainExecutorService = ((NodeMainExecutorService.LocalBinder) binder).getService();
            startMasterChooser();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            nodeMainExecutorService = null;
        }
    };

    protected AbstractRosApplication() {
        super();
        this.nodeMainExecutorServiceConnection = new NodeMainExecutorServiceConnection();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.startNodeMainExecutorService();
    }

    protected void startNodeMainExecutorService() {
        Log.d("RosApplication", "Start NodeMainExecutorService");
        
        if (this.nodeMainExecutorService == null) {
            Intent intent = new Intent(this, NodeMainExecutorService.class);
            this.startService(intent);
            Preconditions.checkState(
                    this.bindService(intent, this.nodeMainExecutorServiceConnection, BIND_AUTO_CREATE),
                    "Failed to bind NodeMainExecutorService.");
        }
    }

    public void restartNodeMainExecutorService() {
        Log.d("RosApplication", "Restart NodeMainExecutorService");
        
        this.stopNodeMainExecutorService();
        this.startNodeMainExecutorService();
    }
    
    protected void stopNodeMainExecutorService() {
        Log.d("RosApplication", "Stop NodeMainExecutorService");
        
        if (this.nodeMainExecutorService != null) {
            this.nodeMainExecutorService.shutdown();
            this.unbindService(this.nodeMainExecutorServiceConnection);
            this.nodeMainExecutorService = null;
        }
    }
    
    /**
     * This method is called in a background thread once this {@link Activity} has
     * been initialized with a {@link NodeMainExecutorService} has started. Your {@link NodeAndroid}s
     * should be started here using the provided {@link NodeMainExecutor}.
     * 
     * @param nodeMainExecutor
     *          the {@link NodeMainExecutor} created for this {@link Activity}
     */
    protected abstract void init(NodeMainExecutor nodeMainExecutor);

    public void startMasterChooser() {
        //Preconditions.checkState(getMasterUri() == null);
        // Call this method on super to avoid triggering our precondition in the
        // overridden startActivityForResult().
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                AbstractRosApplication.this.init(nodeMainExecutorService);
                return null;
            }
        }.execute();
        //super.startActivityForResult(new Intent(this, MasterChooser.class), 0);
    }
}

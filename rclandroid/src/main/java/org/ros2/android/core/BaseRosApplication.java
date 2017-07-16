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

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.google.common.base.Preconditions;

public class BaseRosApplication extends Application {

    private BaseRosService service;

    private final RosServiceConnection serviceConnection;
    private final class RosServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((BaseRosService.RosBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    };

    protected BaseRosApplication() {
        super();
        this.serviceConnection = new RosServiceConnection();
    }

    public BaseRosService getRosService() {
        return this.service;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.startNodeMainExecutorService();
    }

    protected void startNodeMainExecutorService() {
        Log.d("RosApplication", "Start RosService");
        
        if (this.service == null) {
            Intent intent = new Intent(this, BaseRosService.class);
            this.startService(intent);
            boolean isBinding = this.bindService(intent, this.serviceConnection, BIND_AUTO_CREATE);
            Preconditions.checkState(isBinding, "Failed to bind RosService.");
        }
    }

    public void restartNodeMainExecutorService() {
        Log.d("RosApplication", "Restart NodeMainExecutorService");
        
        this.stopNodeMainExecutorService();
        this.startNodeMainExecutorService();
    }
    
    protected void stopNodeMainExecutorService() {
        Log.d("RosApplication", "Stop NodeMainExecutorService");
        
        if (this.service != null) {
//            this.service.shutdown();
            this.unbindService(this.serviceConnection);
            this.service = null;
        }
    }
}

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

import org.ros2.rcljava.RCLJava;

public abstract class BaseRosApplication extends Application {


    protected BaseRosApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!RCLJava.isInitialized()) {
            RCLJava.rclJavaInit();
        }
    }

    protected void startNodeMainExecutorService() {

    }

    public void restartNodeMainExecutorService() {
        Log.d("RosApplication", "Restart NodeMainExecutorService");
        
        this.stopNodeMainExecutorService();
        this.startNodeMainExecutorService();
    }
    
    protected void stopNodeMainExecutorService() {
        Log.d("RosApplication", "Stop NodeMainExecutorService");
    }
}

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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.google.common.base.Preconditions;

import org.ros2.android.core.node.AndroidNode;
import org.ros2.rcljava.exception.NotInitializedException;

import static android.content.Context.BIND_AUTO_CREATE;

public class RosManager {

    private Context context;
    private RosConfig config;
    private RosService service;

    private class RosServiceConnection implements ServiceConnection {

        private Runnable runOnRosReady;

        public RosServiceConnection(Runnable runOnRosReady) {
            this.runOnRosReady = runOnRosReady;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            RosManager.this.service = ((RosService.RosBinder) binder).getService();

            Thread thread = new Thread(this.runOnRosReady);
            thread.run();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            RosManager.this.service = null;
        }

        @Override
        public void onBindingDied(ComponentName name) {
            throw new NotInitializedException("RSO2 service not binding.");
        }
    };

    /**
     * Constructs an interface to the ROS2 service.
     * Calling this is the first step to enabling an application to use the ROS2 service.
     * Once ROS2 is ready, if runOnTangoReady is provided, it will be run on a new Thread.
     * @param context The Android application context the ROS2 interface should be associated with.
     * @param runOnRosReady
     */
    public RosManager(Context context, Runnable runOnRosReady) {
        this.context = context;

        RosServiceConnection serviceConnection = new RosServiceConnection(runOnRosReady);

        Intent intent = new Intent(this.context, RosService.class);
        boolean isBinding = context.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        Preconditions.checkState(isBinding, "Failed to bind RosService.");
    }

    /**
     *Deprecated. Use the other constructor.
     * @param context The Android application context the ROS2 interface should be associated with.
     */
    @Deprecated
    public RosManager(Context context) {
        this(context, null);
    }

    /**
     * Starts the RosService.
     * After calling this function, the service will begin to supply callbacks subscriber/publisher events.
     * The calling application must have internet permissions enabled for connect to be successful.
     * @param config
     */
    public void connect(RosConfig config) {
        this.config = config;
    }

    /**
     * Disconnect a client from the RosService. If a RosConfig was locked, disconnect will also unlock it.
     */
    public void disconnect() {
        //this.service.shutdown();
        //this.unbindService(this.serviceConnection);
        this.service = null;
        this.config = null;
        // TODO Unload...
    }

    /**
     * Given a RosConfig object getConfig assigns the configuration that returns from RosService to config.
     * This should be used to initialize a RosConfig object before setting custom settings and locking that configuration.
     * This function can also be used to find the current configuration of the service.
     * ROS2_CONFIG_DEFAULT = Default configuration of the service.
     * @param configType The requested configuration type.
     * @return RosConfig object which is either used to initialize or find the configuration of the service.
     */
    public RosConfig getConfig(int configType) {
        RosConfig result = this.config;

        if (configType == RosConfig.CONFIG_TYPE_DEFAULT) {
            result = new RosConfig();
        }

        return result;
    }

    public void addNode(AndroidNode node) {
        if (this.service != null) {
            this.service.addNode(node);
        } else {
            throw new NotInitializedException("ROS2 service not initialize.");
        }
    }

    public void removeNode(AndroidNode node) {
        if (this.service != null) {
            this.service.removeNode(node);
        } else {
            throw new NotInitializedException("ROS2 service not initialize.");
        }
    }
}

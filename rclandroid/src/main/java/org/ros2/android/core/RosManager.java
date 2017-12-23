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
import android.os.Build;
import android.os.IBinder;

import com.google.common.base.Preconditions;

import org.ros2.android.core.node.AndroidNode;
import org.ros2.rcljava.exception.NotInitializedException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Ros service manager.
 */
public class RosManager {
    private static final int OS_POSITION = 10000000;

    private final Lock mutex = new ReentrantLock();

    private final Context context;

    // Runnable from client.
    private final Runnable runOnRosReady;

    private RosConfig config;
    private RosService service;

    private boolean isBinding = false;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            RosManager.this.service = ((RosService.RosBinder) binder).getService();

            if (RosManager.this.runOnRosReady != null) {
                Thread thread = new Thread(RosManager.this.runOnRosReady);
                thread.run();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            RosManager.this.service = null;
        }

        @Override
        public void onBindingDied(ComponentName name) {
            RosManager.this.service = null;
            throw new NotInitializedException("RSO2 service not binding.");
        }
    };

    /**
     * Constructs an interface to the ROS2 service.
     * Calling this is the first step to enabling an application to use the ROS2 service.
     * Once ROS2 is ready, if runOnRosReady is provided, it will be run on a new Thread.
     * @param context The Android application context the ROS2 interface should be associated with.
     * @param runOnRosReady
     */
    public RosManager(Context context, Runnable runOnRosReady) {
        this.context = context;
        this.runOnRosReady = runOnRosReady;

        Intent intent = new Intent(this.context, RosService.class);
        boolean isBinding = this.context.bindService(intent, this.serviceConnection, BIND_AUTO_CREATE);
        Preconditions.checkState(isBinding, "Failed to bind RosService.");
    }

    /**
     *Deprecated. Use the other constructor.
     * @param context The Android application context the ROS2 interface should be associated with.
     */
    @Deprecated
    public RosManager(Context context) { this(context, null); }

    /**
     * Starts the RosService.
     * After calling this function, the service will begin to supply callbacks subscriber/publisher events.
     * The calling application must have internet permissions enabled for connect to be successful.
     * @param config Optional RosConfig object. If not null, RosService will lock its configuration to this config.


     */
    public void connect(RosConfig config) {
        if (this.mutex.tryLock() && config != null) {
            this.config = config;
        }
    }

    /**
     * Disconnect a client from the RosService. If a RosConfig was locked, disconnect will also unlock it.
     */
    public void disconnect() {
        this.context.unbindService(this.serviceConnection);
        this.mutex.unlock();
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

        if (this.mutex.tryLock()) {
            switch (configType) {
                case RosConfig.CONFIG_TYPE_DEFAULT:
                    result = new RosConfig();
                    break;
                default:
                    break;
            }
        }

        return result;
    }

    /**
     * Generates an Intent for requesting ROS permissions. Example usage:
     * <code>startActivityForResult(RosManager.getRequestPermissionIntent(RosManager.PERMISSIONTYPE_MULTI_EXECUTOR), 42);</code>
     * @param permissionType The type of permission to request; either PERMISSIONTYPE_MULTI_EXECUTOR or PERMISSIONTYPE_SINGLE_EXECUTOR.
     * @return Intent An Intent that can be used to request permission.
     */
    public static Intent getRequestPermissionIntent(String permissionType) {
        Intent result = new Intent();
        result.setAction("");
        return result;
    }

    /**
     * Gets the version number for RosService.
     * Note that this will correctly drop the first two digits of the versionCode since those are used
     * to identify the Android platform version as per https://developer.android.com/training/multiple-apks/api.html
     * and are not actually the true version number. For example, "190010362" is for KitKat and "230010362" is for Marshmallow, but the true version number is "10362".
     * @param context The context of the calling app.
     * @return The version number of RosService.
     */
    public static int getVersion(Context context) {
        return (Build.VERSION.SDK_INT * OS_POSITION)  + BuildConfig.VERSION_CODE;
    }

    /**
     * Checks if the calling app has the specified permission.
     * It is recommended that an app check to see if it has a permission before trying to request it;
     * this will save time by avoiding re-requesting permissions that have already been granted.
     * @param context The context of the calling app.
     * @param permissionType The type of permission to request; either PERMISSIONTYPE_MULTI_EXECUTOR or PERMISSIONTYPE_SINGLE_EXECUTOR.
     * @return True if the permission was already granted; false if otherwise.
     */
    public static boolean hasPermission(Context context, java.lang.String permissionType) {
        return true;
    }

    /**
     *
     * @param node
     */
    public void addNode(AndroidNode node) {
        if (this.service != null) {
            this.service.addNode(node);
        } else {
            throw new NotInitializedException("ROS2 service not initialize.");
        }
    }

    /**
     *
     * @param node
     */
    public void removeNode(AndroidNode node) {
        if (this.service != null) {
            this.service.removeNode(node);
        } else {
            throw new NotInitializedException("ROS2 service not initialize.");
        }
    }

    public List<AndroidNode> getNodes() {
        List<AndroidNode> nodes = new ArrayList<>();

        if (this.service != null) {
            nodes.addAll(this.service.getNodes());
        } else {
            throw new NotInitializedException("ROS2 service not initialize.");
        }

        return nodes;
    }
}

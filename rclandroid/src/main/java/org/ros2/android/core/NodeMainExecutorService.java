package org.ros2.android.core;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.node.Node;

import org.ros2.android.core.node.NodeAndroid;

/**
 * Node on Service.
 */
public class NodeMainExecutorService extends Service implements NodeMainExecutor {
    static final String TAG = "NodeMainExecutorService";

    /** Binder given to client. */
    private final IBinder mBinder = new LocalBinder();

    /** Android Node. */
    private NodeAndroid nodeAndroid;

    /** Ros2 node. */
    private Node node;

    /** Service Thread */
    private HandlerThread thread;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStart");
        int result = super.onStartCommand(intent, flags, startId);

        if (!RCLJava.isInitialized()) {
            RCLJava.rclJavaInit();
        }

        if (this.thread == null) {
            this.thread = new HandlerThread("Spin loop");
        }

        if (!this.thread.isAlive()) {
            this.thread.start();
            result = START_STICKY;
        }

        return result;
    }

    @Override
    public void onDestroy() {
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }

        if (RCLJava.isInitialized()) {
            RCLJava.shutdown();
        }

        super.onDestroy();
    }

    @Override
    public void shutdown() {
        if (this.nodeAndroid != null) {
            this.nodeAndroid.onShutdown(this.node);
            this.nodeAndroid.onShutdownComplete(this.node);
        }

        if (this.node != null) {
            this.node.dispose();
        }
    }


    @Override
    public void execute(NodeAndroid nodeAndroid) {
        this.nodeAndroid = nodeAndroid;

        this.node = RCLJava.createNode(this.nodeAndroid.getDefaultNodeName());
        this.nodeAndroid.onStart(this.node);

        Handler handler = new Handler(this.thread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                spin(node);
            }
        });
    }

    private void spin(Node node) {
        if (this.node != null) {
            RCLJava.spin(this.node);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    protected class LocalBinder extends Binder {

        public NodeMainExecutorService getService() {
            return NodeMainExecutorService.this;
        }
    }
}

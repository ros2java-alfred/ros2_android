package org.ros2.android.core;

import org.ros2.android.core.node.NodeAndroid;

public interface NodeMainExecutor {

    /** Start new node. */
    void execute(NodeAndroid nodeAndroid);

    /** Shutdown node. */
    void shutdown() ;
}

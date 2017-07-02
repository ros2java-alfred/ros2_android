package org.ros2.android.core.node;

import org.ros2.rcljava.node.Node;

public interface NodeManager {

    void onStart(Node node);

    void onError(Node node, Throwable throwable);

    void onShutdown(Node node);

    void onShutdownComplete(Node node);

    String getDefaultNodeName();
}
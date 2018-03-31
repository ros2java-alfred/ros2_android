package org.ros2.android.hardware.sensor;

import android.hardware.SensorEvent;

import org.ros2.android.core.node.AndroidNode;
import org.ros2.rcljava.node.topic.Publisher;

import sensor_msgs.msg.CameraInfo;
import sensor_msgs.msg.Image;

public class CameraSensorAdapter extends AbstractSensorAdapter<Image> {

    private Publisher<CameraInfo> cameraInfoPublisher;
    private CameraInfo cameraInfo;

    public CameraSensorAdapter(AndroidNode node, Image message, String topicName) {
        super(node, message, topicName);

        this.cameraInfo = new CameraInfo();

        this.cameraInfoPublisher = this.node.createPublisher(CameraInfo.class, this.topicName + "_camera_info");
    }

    @Override
    public void publishSensorState() {

        this.pub.publish(this.msg);
        this.cameraInfoPublisher.publish(this.cameraInfo);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        this.cameraInfo.getHeader().setStamp(this.node.now());
        this.cameraInfo.getHeader().setFrameId("camera");


    }
}

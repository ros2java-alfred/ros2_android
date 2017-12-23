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

package org.ros2.android.hardware.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import org.ros2.android.core.node.AndroidNode;

import java.util.Arrays;
import java.util.Collection;

import sensor_msgs.msg.Imu;

/**
 * Android Proximity Adapter for ROS2.
 *
 * need to add to manifest :
 * <uses-feature android:name="android.hardware.sensor.proximity"/>
 */
public class RotationVectorSensorAdapter extends AbstractSensorAdapter<Imu> {

    private volatile Imu imu = new Imu();
    private Object quatTime;

    public RotationVectorSensorAdapter(AndroidNode node, Imu message, String topicName) {
        super(node, message, topicName);
    }

    @Override
    public void publishSensorState() {
        synchronized (this.mutex) {
            this.msg.setOrientation(this.imu.getOrientation());
            this.msg.setOrientationCovariance(this.imu.getOrientationCovariance());

            this.msg.getHeader().setStamp(this.node.getCurrentTime());
            this.msg.getHeader().setFrameId("imu");

//            logger.debug("Publish rotate value : " + this.imu);
//            System.out.println("Publish rotate value : " + this.imu);
        }
        this.pub.publish(this.msg);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            synchronized (this.mutex) {
                float[] quaternion = new float[4];
                SensorManager.getQuaternionFromVector(quaternion, sensorEvent.values);
                this.imu.getOrientation().setW(quaternion[0]);
                this.imu.getOrientation().setX(quaternion[1]);
                this.imu.getOrientation().setY(quaternion[2]);
                this.imu.getOrientation().setZ(quaternion[3]);
                Collection<Double> tmpCov = Arrays.asList(0.001d, 0d, 0d, 0d, 0.001d, 0d, 0d, 0d, 0.001d);// TODO Make Parameter
                this.imu.setOrientationCovariance(tmpCov);
                this.quatTime = sensorEvent.timestamp;

//                logger.debug("Sensor rotate value : " + this.imu);
//                System.out.println("Sensor rotate value : " + this.imu);
            }
        }
    }
}

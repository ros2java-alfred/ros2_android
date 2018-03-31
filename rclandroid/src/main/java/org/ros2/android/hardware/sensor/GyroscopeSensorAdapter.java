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

import org.ros2.android.core.node.AndroidNode;

import java.util.Arrays;
import java.util.Collection;

import sensor_msgs.msg.Imu;

/**
 * Android Gyroscope Adapter for ROS2.
 *
 * need to add to manifest :
 * <uses-feature android:name="android.hardware.sensor.gyroscope"/>
 */
public class GyroscopeSensorAdapter extends AbstractSensorAdapter<Imu> {

    private volatile Imu imu = new Imu();
    private long gyroTime;

    public GyroscopeSensorAdapter(AndroidNode node, Imu message, String topicName) {
        super(node, message, topicName);
    }

    @Override
    public void publishSensorState() {
        synchronized (this.mutex) {
            this.msg.setAngularVelocity(this.imu.getAngularVelocity());
            this.msg.setAngularVelocityCovariance(this.imu.getAngularVelocityCovariance());

            this.msg.getHeader().setStamp(this.node.now());
            this.msg.getHeader().setFrameId("imu");

//            logger.debug("Publish gyro value : " + this.imu);
//            System.out.println("Publish gyro value : " + this.imu);
        }
        this.pub.publish(this.msg);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            synchronized (this.mutex) {
                this.imu.getAngularVelocity().setX(sensorEvent.values[0]);
                this.imu.getAngularVelocity().setY(sensorEvent.values[1]);
                this.imu.getAngularVelocity().setZ(sensorEvent.values[2]);

                Collection<Double> tmpCov = Arrays.asList(0.0025d,0d,0d, 0d,0.0025d,0d, 0d,0d,0.0025d);// TODO Make Parameter
                this.imu.setAngularVelocityCovariance(tmpCov);
                this.gyroTime = sensorEvent.timestamp;

//                logger.debug("Sensor gyro value : " + this.imu);
//                System.out.println("Sensor gyro value : " + this.imu);
            }
        }
    }
}

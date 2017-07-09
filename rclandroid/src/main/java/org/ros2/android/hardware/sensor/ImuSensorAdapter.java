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
import org.ros2.rcljava.node.topic.Publisher;

import sensor_msgs.msg.Imu;
//import geometry_msgs.msg.Vector3; //TODO for test to FIX

public class ImuSensorAdapter implements SensorAdapter<Imu> {

    private boolean withSubTopics = true;

    protected AccelerometerSensorAdapter accelAdapter;
    protected GyroscopeSensorAdapter gyroAdapter;

    protected Imu msg;
    protected String topicName = "_undefine!";//TODO TBD if keep
    protected final AndroidNode node;
    protected final Publisher<Imu> pub;

    public ImuSensorAdapter(AndroidNode node, Imu imu, String topicName) {
        this.topicName = topicName;
        this.node = node;

        this.msg = imu;
        this.pub = this.node.createPublisher(
                Imu.class,
                this.topicName);

        String accelTopic = null;
        String gyroTopic  = null;
        if (withSubTopics) {
            accelTopic = topicName + "/accel";
            gyroTopic  = topicName + "/gyro";
        }

        this.accelAdapter = new AccelerometerSensorAdapter(this.node, this.msg, accelTopic);
        this.gyroAdapter  = new GyroscopeSensorAdapter(this.node, this.msg, gyroTopic);
    }

    @Override
    public void publishSensorState() {
        if (this.withSubTopics) {
            if (this.accelAdapter != null) {
                this.accelAdapter.publishSensorState();
            }

            if (this.gyroAdapter != null) {
                this.gyroAdapter.publishSensorState();
            }
        }

        this.pub.publish(this.msg);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (this.accelAdapter != null && sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            this.accelAdapter.onSensorChanged(sensorEvent);
        }

        if (this.gyroAdapter != null && sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            this.gyroAdapter.onSensorChanged(sensorEvent);
        }

    }

    @Override
    public Imu getMessage() {
        return this.msg;
    }
}

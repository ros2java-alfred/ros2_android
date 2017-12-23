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

import sensor_msgs.msg.Temperature;

/**
 * Android Barometer Adapter for ROS2.
 *
 * need to add to manifest :
 * <uses-feature android:name="android.hardware.sensor.ambient_temperature"/>
 */
public class AmbientTemperatureSensorAdapter extends AbstractSensorAdapter<Temperature> {

    private volatile float temperature = 0f;

    public AmbientTemperatureSensorAdapter(AndroidNode node, Temperature message, String topicName) {
        super(node, message, topicName);
    }

    @Override
    public void publishSensorState() {
        synchronized (this.mutex) {
            this.msg.setTemperature(this.temperature);

            this.msg.getHeader().setStamp(this.node.getCurrentTime());
            this.msg.getHeader().setFrameId("temp");

            logger.debug("Publish ambient temperature value : " + this.temperature);
            System.out.println("Publish ambient temperature value : " + this.temperature);
        }
        this.pub.publish(msg);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            synchronized (this.mutex) {
                this.temperature = sensorEvent.values[0];

                logger.debug("Sensor ambient temperature value : " + this.temperature);
                System.out.println("Sensor ambient temperature value : " + this.temperature);
            }
        }
    }
}

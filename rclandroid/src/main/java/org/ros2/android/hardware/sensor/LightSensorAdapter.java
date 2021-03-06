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

import sensor_msgs.msg.Illuminance;

/**
 * Android Light Adapter for ROS2.
 *
 * need to add to manifest :
 * <uses-feature android:name="android.hardware.sensor.light"/>
 */
public final class LightSensorAdapter extends AbstractSensorAdapter<Illuminance> {

    private volatile float lux = 0f;

    public LightSensorAdapter(AndroidNode node, Illuminance message, String topicName) {
        super(node, message, topicName);
    }

    @Override
    public void publishSensorState() {
        synchronized (this.mutex) {
            this.msg.setIlluminance(this.lux);

            this.msg.getHeader().setStamp(this.node.now());
            this.msg.getHeader().setFrameId("light");

            logger.debug("Publish light value : " + this.lux);
            System.out.println("Publish light value : " + this.lux);
        }
        this.pub.publish(this.msg);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            synchronized (this.mutex) {
                this.lux = sensorEvent.values[0];
                logger.debug("Sensor light value : " + this.lux);
                System.out.println("Sensor light value : " + this.lux);
            }
        }
    }
}

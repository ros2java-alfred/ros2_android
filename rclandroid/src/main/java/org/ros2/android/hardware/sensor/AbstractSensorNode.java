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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import org.ros2.android.core.node.AndroidNativeNode;
import org.ros2.rcljava.internal.message.Message;
import org.ros2.rcljava.time.WallTimer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public abstract class AbstractSensorNode<T extends Message> extends AndroidNativeNode
        implements SensorNode {

    private final static String TAG = "AbstractSensorNode";
    private static final Logger logger = LoggerFactory.getLogger(AbstractSensorNode.class);

    private int typeSensor = 0;
    private final SensorManager sensorManager;
    private final Sensor sensor;

    private final WallTimer timer;
    protected SensorAdapter sensorAdapter;

    public AbstractSensorNode(final Context context, final String name, final int typeSensor, final long time, final TimeUnit timeUnit) {
        super(name, context);
        this.typeSensor = typeSensor;

        // Load sensor
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.sensor = this.sensorManager.getDefaultSensor(this.typeSensor);

        // Register sensor to listener
        this.sensorManager.registerListener(this, this.sensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        this.timer = this.createWallTimer(time, timeUnit, this);
    }

    @Override
    public void dispose() {
        if (this.sensor != null) { //TODO remove after add throw constructor
            this.sensorManager.unregisterListener(this);
        }
        super.dispose();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (this.sensorAdapter != null && sensorEvent.sensor.getType() == this.typeSensor) {
            this.sensorAdapter.onSensorChanged(sensorEvent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void tick() {
        if (this.sensorAdapter != null) {
            this.sensorAdapter.publishSensorState();
        }
    }
}

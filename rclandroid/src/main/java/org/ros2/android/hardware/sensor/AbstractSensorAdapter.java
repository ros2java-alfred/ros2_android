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

import org.ros2.android.core.node.AndroidNode;
import org.ros2.rcljava.internal.message.Message;
import org.ros2.rcljava.node.topic.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSensorAdapter<T extends Message> implements SensorAdapter<T> {

    private static final String TAG = "SensorAdapter";
    protected static final Logger logger = LoggerFactory.getLogger(AbstractSensorAdapter.class);

    protected volatile String topicName = "_undefine!";
    protected final T msg;
    protected final AndroidNode node;
    protected volatile Publisher<T> pub;

    protected final Object mutex = new Object();

    public AbstractSensorAdapter(final AndroidNode node, final T message, final String topicName) {
        logger.debug("Init Sensor adapter");

        this.node = node;
        this.msg = message;
        this.topicName = topicName;

        if (this.topicName != null) {
            this.pub = this.node.createPublisher( (Class<T>)this.msg.getClass(), this.topicName);
        }
    }

    public AndroidNode getNode() {
        return this.node;
    }

    @Override
    public T getMessage() {
        return this.msg;
    }
}

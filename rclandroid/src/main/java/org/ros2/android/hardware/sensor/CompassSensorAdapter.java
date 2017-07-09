package org.ros2.android.hardware.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import org.ros2.android.core.node.AndroidNode;

import java.util.Arrays;
import java.util.Collection;

import sensor_msgs.msg.MagneticField;

public class CompassSensorAdapter extends AbstractSensorAdapter<MagneticField> {

    private MagneticField magneticField = new MagneticField();
    private long magneTime;

    public CompassSensorAdapter(AndroidNode node, MagneticField message, String topicName) {
        super(node, message, topicName);
    }

    @Override
    public void publishSensorState() {
        synchronized (this.mutex) {
            this.msg.setMagneticField(this.magneticField.getMagneticField());
            this.msg.setMagneticFieldCovariance(this.magneticField.getMagneticFieldCovariance());

//            logger.debug("Publish compass value : " + this.magneticField);
//            System.out.println("Publish compass value : " + this.magneticField);
        }
        this.pub.publish(this.msg);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            synchronized (this.mutex) {
                this.magneticField.getMagneticField().setX(sensorEvent.values[0]);
                this.magneticField.getMagneticField().setY(sensorEvent.values[1]);
                this.magneticField.getMagneticField().setZ(sensorEvent.values[2]);

                Collection<Double> tmpCov = Arrays.asList(); //TODO
                this.magneticField.setMagneticFieldCovariance(tmpCov);
                this.magneTime = sensorEvent.timestamp;

//                logger.debug("Sensor compass value : " + this.magneticField);
//                System.out.println("Sensor compass value : " + this.magneticField);
            }
        }
    }
}

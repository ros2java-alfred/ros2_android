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
package org.ros2.android.core;

/** Ros configuration */
public class RosConfig {

    public static final int CONFIG_TYPE_DEFAULT = 0;

    public static final int CONFIG_TYPE_CONNEXT = 1;

    /** DDS Implementation */
    private DDSVendor ddsVendor = null;

    protected void setDDSVendor(DDSVendor vendor) {
        this.ddsVendor = vendor;
    }

    /** DDS vendor available on Android */
    public static enum DDSVendor {
        FAST_RTPS   ("rmw_fastrtps_cpp"),
        CONNEXT     ("rmw_connext_cpp"),
        OPENSPLICE  ("rmw_opensplice_cpp");

        private String name;

        DDSVendor(String name) {
            this.name = name;
        }

        public String getRmwImplementation() {
            return this.name;
        }
    }
}

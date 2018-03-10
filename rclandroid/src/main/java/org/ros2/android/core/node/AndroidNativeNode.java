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
package org.ros2.android.core.node;

import android.content.Context;

import org.ros2.rcljava.node.NativeNode;

/**
 * Android Native Node.
 */
public class AndroidNativeNode extends NativeNode implements AndroidNode {

	private static final String TAG = "AndroidNativeNode";

	private final Context context;

	public AndroidNativeNode(Context context, String name) {
		super(name);
		this.context = context;
	}

	@Deprecated
	public AndroidNativeNode(String name, Context context) {
		this(context, name);
	}

	/**
	 * Get ROS Node name.
	 */
	public String getDefaultNodeName() {
		return "android_" + ""; //ApplicationBase.getUDID();
	}

}

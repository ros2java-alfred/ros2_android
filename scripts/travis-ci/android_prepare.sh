# android_prepare.sh
#!/bin/sh

set -e

source /scripts/travis-ci/android_common.source

if [ $DEBUG -eq 1 ]
then
  set -ev
  df -h
fi

DOCKER_IMG="$DOCKER_REPO:$DOCKER_DIST"

# Docker Hub.
get_docker_image

display_debug

# Make shared environment variables.
make_environement_file

# Check container variables.
if [ $DEBUG -eq 1 ]
then
  echo -e "\n\e[33;1mCheck container variables.\e[0m"
  cat $HOME_ENV
  docker run -u "$UID" -it --rm -v $(pwd):$(pwd) --env-file $HOME_ENV -w $(pwd) $DOCKER_IMG sh -c "locale && env | grep -E '^TRAVIS_' && env | grep -E '^CI_' && env | grep -E '^ROS' && env | grep -E '^ANDROID' && df -h"
  display_debug
fi

# Install NDK.
get_ndk_android

# Install SDK.
get_sdk_android

# Add licenses SDK.
set_licenses_android

# Add repositories SDK.
set_repositories_android

# Install SDK components.
get_sdk_components

display_debug

# INSTALL/BUILD ROS2 AMENT...
echo -e "\n\e[33;1mINSTALL/BUILD ROS2 AMENT...\e[0m"
mkdir -p $HOME_BUILD/ament_ws/src
cd $HOME_BUILD/ament_ws
docker run -u "$UID" -it --rm -v $(pwd):$(pwd) -w $(pwd) $DOCKER_IMG sh -c "/usr/bin/wget https://gist.githubusercontent.com/Theosakamg/e6084cfafa6b7ea690104424cef970a2/raw/ament_java.repos -nv"
docker run -u "$UID" -it --rm -v $(pwd):$(pwd) -w $(pwd) $DOCKER_IMG sh -c "/usr/bin/vcs import src < ament_java.repos"
docker run -u "$UID" -it --rm -v $(pwd):$(pwd) -w $(pwd) $DOCKER_IMG sh -c "src/ament/ament_tools/scripts/ament.py build --symlink-install --isolated"
display_debug

# INSTALL ROS2 WS...
echo -e "\n\e[33;1mINSTALL ROS2 WS...\e[0m"
mkdir -p $ROS2WS/src
cd $ROS2WS
docker run -u "$UID" -it --rm -v $(pwd):$(pwd) -w $(pwd) $DOCKER_IMG sh -c "/usr/bin/wget https://gist.githubusercontent.com/Theosakamg/617cd893813163cdcb9943a08d667964/raw/ros2_java_android.repos -nv"
docker run -u "$UID" -it --rm -v $(pwd):$(pwd) -w $(pwd) $DOCKER_IMG sh -c "/usr/bin/vcs import src < ros2_java_android.repos"
display_debug

# Patch for Java support.
echo "Patch for Java support."
cd $ROS2WS/src/ros2/rosidl_typesupport && patch -p1 < ../../ros2_java/ros2_java/rosidl_ros2_android.diff

# Sync with git trigger
rm -rf $ROS2WS/src/ros2_java/ros2_android && ln -s $HOME_BUILD/ros2java-alfred/ros2_android $ROS2WS/src/ros2_java/ros2_android

# Disable many package (not needed for android)
echo "Disable packages."
touch $ROS2WS/src/ros2/rosidl/python_cmake_module/AMENT_IGNORE
touch $ROS2WS/src/ros2/rosidl/rosidl_generator_py/AMENT_IGNORE
#touch $ROS2WS/src/ros2/rosidl/rosidl_generator_cpp/AMENT_IGNORE
#touch $ROS2WS/src/ros2/rosidl/rosidl_typesupport_introspection_cpp/AMENT_IGNORE
#touch $ROS2WS/src/ros2/rosidl_typesupport/rosidl_typesupport_cpp/AMENT_IGNORE
touch $ROS2WS/src/ros2_java/ros2_android_examples/ros2_android_listener/AMENT_IGNORE
touch $ROS2WS/src/ros2_java/ros2_android_examples/ros2_android_talker/AMENT_IGNORE
touch $ROS2WS/src/ros2_java/ros2_android_examples/ros2_listener_android/AMENT_IGNORE
touch $ROS2WS/src/ros2_java/ros2_android_examples/ros2_talker_android/AMENT_IGNORE
# touch $ROS2WS/src/ros2_java/ros2_android_tango_examples/


# DEBUG
if [ $DEBUG -eq 1 ]
then
  echo "List $HOME_BUILD path"
  cd $HOME_BUILD
  find . -maxdepth 3 -type d -not \( -path "./.git" -prune \)
  echo "List $ROS2WS/src path"
  cd $ROS2WS/src
  find . -maxdepth 3 -type d -not \( -path "./.git" -prune \)
  display_debug
fi

echo -e "\n\e[33;1mBUILD ROS2 WS...\e[0m"
cd $HOME_BUILD
docker run -u "$UID" -it --rm -v $(pwd):$(pwd) --env-file "$HOME_ENV" -w $(pwd) "$DOCKER_IMG" sh -c ". $HOME_BUILD/ament_ws/install_isolated/local_setup.sh && cd $ROS2WS && ament build --isolated --cmake-args -DPYTHON_EXECUTABLE=$PYTHON_PATH -DTHIRDPARTY=ON -DCMAKE_FIND_ROOT_PATH=\"$ROOT_PATH\" -DANDROID_FUNCTION_LEVEL_LINKING=OFF -DANDROID_TOOLCHAIN_NAME=\"$ANDROID_GCC\" -DANDROID_STL=gnustl_shared -DANDROID_ABI=\"$ANDROID_ABI\" -DANDROID_NDK=\"$ANDROID_NDK_HOME\" -DANDROID_NATIVE_API_LEVEL=\"$ANDROID_VER\" -DCMAKE_TOOLCHAIN_FILE=\"$ROS2JAVA_PATH\" -DANDROID_HOME=\"$ANDROID_SDK_ROOT\" -- --ament-gradle-args -g $HOME_BUILD/.gradle -Pament.android_stl=gnustl_shared -Pament.android_abi=\"$ANDROID_ABI\" -Pament.android_ndk=\"$ANDROID_NDK_HOME\" --stacktrace -- "
display_debug

echo -e "\n\e[33;1mClean ROS2 build WS...\e[0m"
rm -rf $HOME_BUILD/ros2_java_ws/build_isolated

start_emulator_android

exit


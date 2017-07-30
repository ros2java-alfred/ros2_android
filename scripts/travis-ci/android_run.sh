# scripts/travis-ci/linux_run.sh
#!/bin/sh

set -e

DEBUG=0
EMULATOR=0

if [ $DEBUG -eq 1 ]
then
  set -ev
fi

cd $HOME_BUILD
docker run -u "$UID" -it --rm -v $(pwd):$(pwd) --env-file "$HOME_ENV" -w $(pwd) "$DOCKER_REPO:$DOCKER_DIST" sh -c ". $HOME_BUILD/ament_ws/install_isolated/local_setup.sh && cd $ROS2WS && ament build --isolated --only-packages rclandroid --cmake-args -DPYTHON_EXECUTABLE=\"$PYTHON_PATH\" -DTHIRDPARTY=ON -DCMAKE_FIND_ROOT_PATH=\"$ROOT_PATH\" -DANDROID_FUNCTION_LEVEL_LINKING=OFF -DANDROID_TOOLCHAIN_NAME=\"$ANDROID_GCC\" -DANDROID_STL=gnustl_shared -DANDROID_ABI=\"$ANDROID_ABI\" -DANDROID_NDK=\"$ANDROID_NDK_HOME\" -DANDROID_NATIVE_API_LEVEL=\"$ANDROID_VER\" -DCMAKE_TOOLCHAIN_FILE=\"$ROS2JAVA_PATH\" -DANDROID_HOME=\"$ANDROID_SDK_ROOT\" -- --ament-gradle-args -g $HOME_BUILD/.gradle -Pament.android_stl=gnustl_shared -Pament.android_abi=\"$ANDROID_ABI\" -Pament.android_ndk=\"$ANDROID_NDK_HOME\" --stacktrace -- "

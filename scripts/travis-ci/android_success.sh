# scripts/travis-ci/linux_success.sh
#!/bin/sh

set -e

DEBUG=1
EMULATOR=0

if [ $DEBUG -eq 1 ]
then
  set -ev
fi

if [ ${TRAVIS_BRANCH} == "master" ];
then
    # DEBUG
    if [ $DEBUG -eq 1 ]
    then
      df -h
    fi

    echo "Create new docker container"
    docker run -u "$UID" -it --env-file "$HOME_ENV" -w $(pwd) --name="artefact" $DOCKER_REPO:$DOCKER_DIST sh -c "echo ok"
    
    # DEBUG
    if [ $DEBUG -eq 1 ]
    then
      df -h
    fi
    
    echo "Copy file into container"
    docker cp -L $HOME_BUILD/. artefact:$HOME_BUILD
    
        # DEBUG
    if [ $DEBUG -eq 1 ]
    then
      df -h
    fi
    
    echo "Commit new content."
    docker commit -m "Build $TRAVIS_BUILD_NUMBER" artefact $DOCKER_REPO:$DOCKER_DIST-ros2android
    
    echo "Push to Docker hub."
    docker push $DOCKER_REPO:$DOCKER_DIST-ros2android

    if [ ${TRAVIS_EVENT_TYPE} != "cron" ]; then
        docker tag $DOCKER_REPO:$DOCKER_DIST-ros2android $DOCKER_REPO:$DOCKER_DIST-ros2android_$TRAVIS_BUILD_NUMBER ;
    fi

    if [ ${TRAVIS_EVENT_TYPE} != "cron" ]; then
        docker push $DOCKER_REPO:$DOCKER_DIST-ros2android_$TRAVIS_BUILD_NUMBER ;
    fi
fi

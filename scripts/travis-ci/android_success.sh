# scripts/travis-ci/linux_success.sh
#!/bin/sh

set -ev

if [ ${TRAVIS_BRANCH} == "master" ]; then
    docker run -u "$UID" -it --env-file "$HOME_ENV" -w $(pwd) --name="artefact" $DOCKER_REPO:$DOCKER_DIST sh -c "echo ok"
    docker cp -L $HOME_BUILD/. artefact:$HOME_BUILD
    docker commit -m "Build $TRAVIS_BUILD_NUMBER" artefact $DOCKER_REPO:$DOCKER_DIST-ros2android
    docker push $DOCKER_REPO:$DOCKER_DIST-ros2android

    if [ ${TRAVIS_EVENT_TYPE} != "cron" ]; then
        docker tag $DOCKER_REPO:$DOCKER_DIST-ros2android $DOCKER_REPO:$DOCKER_DIST-ros2android_$TRAVIS_BUILD_NUMBER ;
    fi

    if [ ${TRAVIS_EVENT_TYPE} != "cron" ]; then
        docker push $DOCKER_REPO:$DOCKER_DIST-ros2android_$TRAVIS_BUILD_NUMBER ;
    fi
fi

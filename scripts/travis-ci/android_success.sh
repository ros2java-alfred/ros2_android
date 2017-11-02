# scripts/travis-ci/linux_success.sh
#!/bin/sh

set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source "$DIR/android_common.source"

DOCKER_SUFFIX="ros2android"

if [ $DEBUG -eq 1 ]
then
  set -ev
fi

if [ ${TRAVIS_BRANCH} == "master" ];
then
    DOCKER_NAME="artefact"

    echo "Create new docker container"
    docker run -u "$UID" -it --env-file "$HOME_ENV" -w $(pwd) --name=$DOCKER_NAME $DOCKER_REPO:$DOCKER_DIST sh -c "echo ok"

    echo "Copy file into container"
    docker cp -L $HOME_BUILD/. $DOCKER_NAME:$HOME_BUILD

    echo "Commit new content."
    docker commit -m "Build $TRAVIS_BUILD_NUMBER" $DOCKER_NAME $DOCKER_REPO:$DOCKER_DIST-$DOCKER_SUFFIX

    echo "Push to Docker hub."
    docker push $DOCKER_REPO:$DOCKER_DIST-$DOCKER_SUFFIX

    if [ ${TRAVIS_EVENT_TYPE} != "cron" ]; then
        docker tag  $DOCKER_REPO:$DOCKER_DIST-$DOCKER_SUFFIX $DOCKER_REPO:$DOCKER_DIST-$DOCKER_SUFFIX_$TRAVIS_BUILD_NUMBER ;
        docker push $DOCKER_REPO:$DOCKER_DIST-$DOCKER_SUFFIX_$TRAVIS_BUILD_NUMBER ;
    fi

    # DEBUG
    if [ $DEBUG -eq 1 ]
    then
      df -h
    fi
fi

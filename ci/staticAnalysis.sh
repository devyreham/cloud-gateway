#!/bin/bash

# load dev credentials locally
for elem in $(cat /etc/environment.d/dev-env.conf)
do
  if [[ ! $elem == \#* ]]
  then
    export $elem
  fi
done

./gradlew clean build test jacocoTestReport || true

/opt/sonar-scanner/bin/sonar-scanner -Dsonar.login=${SONAR_TOKEN}

#!/bin/bash

# load dev credentials locally
for elem in $(cat /etc/environment.d/dev-env.conf)
do
  if [[ ! $elem == \#* ]]
  then
    export $elem
  fi
done

gradle test jacocoTestReport



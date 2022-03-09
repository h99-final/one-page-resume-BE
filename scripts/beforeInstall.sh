#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/

if [ -d $REPOSITORY/myapp ]; then
    rm -rf $REPOSITORY/myapp
fi
mkdir -vp $REPOSITORY/myapp
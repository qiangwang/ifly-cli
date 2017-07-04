#!/bin/bash

docker run -it --rm -v "$PWD":/ifly-sdk -w /ifly-sdk gradle:3.4 bash

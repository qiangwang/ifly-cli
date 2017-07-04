#!/bin/bash

docker run -it --rm -v "$PWD":/ifly-cli -w /ifly-cli gradle:3.4 bash

#!/usr/bin/env bash

sbt clean compile coverage test coverageOff coverageReport A11y/test dependencyUpdates
#!/bin/sh

set -e

exec java "$@" -jar /mantis-sync-core/mantis-sync-core.jar

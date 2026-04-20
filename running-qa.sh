#!/bin/sh
set -eu

IMAGE="${1:-}"
BRANCH_NAME="${2:-unknown}"

if [ -z "$IMAGE" ]; then
    echo "Usage: ./running-qa.sh <image> [branch-name]" >&2
    exit 1
fi

./k8s-deploy.sh qa "$IMAGE" "$BRANCH_NAME"

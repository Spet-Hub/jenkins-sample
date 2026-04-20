#!/bin/sh
set -eu

ENVIRONMENT="${1:-}"
IMAGE="${2:-}"
BRANCH_NAME="${3:-unknown}"

if [ -z "$ENVIRONMENT" ] || [ -z "$IMAGE" ]; then
    echo "Usage: ./k8s-deploy.sh <dev|qa|prod|default> <image> [branch-name]" >&2
    exit 1
fi

case "$ENVIRONMENT" in
    dev)
        TEMPLATE="k8s-dev.yaml"
        ;;
    qa)
        TEMPLATE="k8s-qa.yaml"
        ;;
    prod)
        TEMPLATE="k8s-prod.yaml"
        ;;
    default)
        TEMPLATE="k8s.yaml"
        ;;
    *)
        echo "Unknown environment: $ENVIRONMENT" >&2
        exit 1
        ;;
esac

sed \
    -e "s|__IMAGE__|$IMAGE|g" \
    -e "s|__BRANCH__|$BRANCH_NAME|g" \
    "$TEMPLATE" | kubectl apply -f -

echo "Applied $TEMPLATE using image $IMAGE"

#!/bin/sh
set -eu

ENVIRONMENT="${1:-prod}"
DEPLOYMENT_NAME="${2:-jenkins-sample-java}"

case "$ENVIRONMENT" in
    dev)
        NAMESPACE="development"
        ;;
    qa)
        NAMESPACE="qatest"
        ;;
    prod)
        NAMESPACE="production"
        ;;
    default)
        NAMESPACE="default"
        ;;
    *)
        echo "Unknown environment: $ENVIRONMENT" >&2
        exit 1
        ;;
esac

kubectl rollout undo deployment "$DEPLOYMENT_NAME" -n "$NAMESPACE"
kubectl rollout history deployment "$DEPLOYMENT_NAME" -n "$NAMESPACE"

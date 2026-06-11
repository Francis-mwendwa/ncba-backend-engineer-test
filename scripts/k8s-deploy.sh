#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="ncba-countries"
IMAGE="your-dockerhub-username/backend-engineer-test:latest"

usage() {
  echo "Usage: $0 [-n NAMESPACE] [-i IMAGE]" >&2
  exit 1
}

while getopts ":n:i:" opt; do
  case $opt in
    n) NAMESPACE="$OPTARG" ;;
    i) IMAGE="$OPTARG" ;;
    *) usage ;;
  esac
done

echo "[deploy] Ensuring namespace: $NAMESPACE"
kubectl apply -f k8s/namespace.yaml

echo "[deploy] Applying manifests"
kubectl -n "$NAMESPACE" apply -f k8s/configmap.yaml
kubectl -n "$NAMESPACE" apply -f k8s/service.yaml
kubectl -n "$NAMESPACE" apply -f k8s/deployment.yaml
if [ -f k8s/ingress.yaml ]; then
  kubectl -n "$NAMESPACE" apply -f k8s/ingress.yaml
fi

echo "[deploy] Setting image to $IMAGE"
kubectl -n "$NAMESPACE" set image deployment/countries-deploy countries="$IMAGE" --record

echo "[deploy] Waiting for rollout..."
kubectl -n "$NAMESPACE" rollout status deployment/countries-deploy

echo "[deploy] Service details:"
kubectl -n "$NAMESPACE" get svc countries-svc -o wide

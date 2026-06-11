#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="ncba-countries"

usage() {
  echo "Usage: $0 [-n NAMESPACE]" >&2
  exit 1
}

while getopts ":n:" opt; do
  case $opt in
    n) NAMESPACE="$OPTARG" ;;
    *) usage ;;
  esac
done

echo "[delete] Deleting resources in namespace: $NAMESPACE"
if [ -f k8s/ingress.yaml ]; then
  kubectl -n "$NAMESPACE" delete -f k8s/ingress.yaml --ignore-not-found
fi
kubectl -n "$NAMESPACE" delete -f k8s/deployment.yaml --ignore-not-found
kubectl -n "$NAMESPACE" delete -f k8s/service.yaml --ignore-not-found
kubectl -n "$NAMESPACE" delete -f k8s/configmap.yaml --ignore-not-found

echo "[delete] Optionally delete the namespace: kubectl delete namespace $NAMESPACE"

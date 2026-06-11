#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="ncba-countries"
LOGS=200

usage() {
  echo "Usage: $0 [-n NAMESPACE] [-l LOG_LINES]" >&2
  exit 1
}

while getopts ":n:l:" opt; do
  case $opt in
    n) NAMESPACE="$OPTARG" ;;
    l) LOGS="$OPTARG" ;;
    *) usage ;;
  esac
done

echo "[status] Namespace: $NAMESPACE"
kubectl get ns "$NAMESPACE"

echo "\n[status] Deployments:"
kubectl -n "$NAMESPACE" get deploy -o wide

echo "\n[status] Pods:"
kubectl -n "$NAMESPACE" get pods -o wide

echo "\n[status] Services:"
kubectl -n "$NAMESPACE" get svc -o wide

echo "\n[status] Recent events:"
kubectl -n "$NAMESPACE" get events --sort-by=.lastTimestamp | tail -n 20

echo "\n[status] Recent logs from deployment/countries-deploy (last $LOGS lines):"
kubectl -n "$NAMESPACE" logs deployment/countries-deploy --tail="$LOGS" || true

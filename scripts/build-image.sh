#!/usr/bin/env bash
set -euo pipefail

IMAGE_TAG="latest"
REGISTRY="backend-engineer-test"

usage() {
  echo "Usage: $0 [-t IMAGE_TAG] [-r REGISTRY/name]" >&2
  exit 1
}

while getopts ":t:r:" opt; do
  case $opt in
    t) IMAGE_TAG="$OPTARG" ;;
    r) REGISTRY="$OPTARG" ;;
    *) usage ;;
  esac
done

IMG="${REGISTRY}:${IMAGE_TAG}"
echo "[build] Packaging with Maven..."
mvn -q -DskipTests package

echo "[build] Building Docker image: $IMG"
docker build -t "$IMG" .

echo "[build] Done. To push run: docker push $IMG"

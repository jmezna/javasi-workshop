# JavaSI: Mikrostoritve in cloud-native arhitektura z Javo

## Zagon potrebnih storitev

### Zagon podatkovnih baz

```bash
docker run -d --name postgres-customers -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=customer -p 5432:5432 postgres:latest
docker run -d --name postgres-orders -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=order -p 5433:5432 postgres:latest
```

### Zagon strežnika etcd

```bash
$ docker run -d -p 2379:2379 \
    --name etcd \
    --volume=/tmp/etcd-data:/etcd-data \
    quay.io/coreos/etcd:latest \
    /usr/local/bin/etcd \
    --name my-etcd-1 \
    --data-dir /etcd-data \
    --listen-client-urls http://0.0.0.0:2379 \
    --advertise-client-urls http://0.0.0.0:2379 \
    --listen-peer-urls http://0.0.0.0:2380 \
    --initial-advertise-peer-urls http://0.0.0.0:2380 \
    --initial-cluster my-etcd-1=http://0.0.0.0:2380 \
    --initial-cluster-token my-etcd-token \
    --initial-cluster-state new \
    --auto-compaction-retention 1 \
    -cors="*"
``` 

## Zagon druge instance mikrostoritve Orders

Z okoljskimi spremenljivkami nastavimo port in base-url druge instance:

KUMULUZEE_SERVER_BASEURL=http://localhost:8082

KUMULUZEE_SERVER_HTTP_PORT=8082

## KumuluzEE Tutorial

Podrobnejši tutorial lahko najdete na [GitHub-u](https://github.com/kumuluz/kumuluzee-samples/tree/master/tutorial-microservice-config-discovery-faulttolerance-logs-metrics-security).
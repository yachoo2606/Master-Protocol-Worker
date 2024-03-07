# BUILD

run this command in pulled repository to build and docker image

```shell
docker build -t worker .
```

# RUN

Replace <strong>N</strong> in WORKER_ID environmental and in container name to match id of worker you want to run

```shell
docker run --rm --network=masterNetwork -e PEER_1_URL=http://172.20.0.2:9001/eureka/,http://172.20.0.3:9002/eureka/,http://172.20.0.4:9003/eureka/ -e WORKER_ID=N -e LOGSTASH_DESTINATION_ONE=172.20.0.12:5000 -e LOGSTASH_DESTINATION_TWO=172.20.0.13:5000 -e LOGSTASH_DESTINATION_THREE=172.20.0.14:5000 --name=Worker-N -p 12000:11000 worker
```
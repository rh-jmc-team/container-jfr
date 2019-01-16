FROM openjdk:11-jre-slim
COPY build/libs/docker-test.jar /
EXPOSE 9090 9091
CMD java \
        -Dcom.sun.management.jmxremote.rmi.port=9091 \
        -Dcom.sun.management.jmxremote=true \
        -Dcom.sun.management.jmxremote.port=9091 \
        -Dcom.sun.management.jmxremote.ssl=false \
        -Dcom.sun.management.jmxremote.authenticate=false \
        -Dcom.sun.management.jmxremote.local.only=false \
        -Djava.rmi.server.hostname=0.0.0.0 \
        -cp docker-test.jar \
        es.andrewazor.dockertest.Listener
FROM openjdk:11
ENV APP_ROOT /app
RUN mkdir ${APP_ROOT}
WORKDIR ${APP_ROOT}

COPY target/gateway-service.jar ${APP_ROOT}/

ENTRYPOINT ["java","-jar","gateway-service.jar"]

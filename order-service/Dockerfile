FROM openjdk:11
ENV APP_ROOT /app
RUN mkdir ${APP_ROOT}
WORKDIR ${APP_ROOT}

COPY target/order-service.jar ${APP_ROOT}/

ENTRYPOINT ["java","-jar","order-service.jar"]

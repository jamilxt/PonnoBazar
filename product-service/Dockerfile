FROM openjdk:11
ENV APP_ROOT /app
RUN mkdir ${APP_ROOT}
WORKDIR ${APP_ROOT}

COPY target/product-service.jar ${APP_ROOT}/

ENTRYPOINT ["java","-jar","product-service.jar"]

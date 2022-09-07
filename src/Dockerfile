FROM openjdk:17
VOLUME /tmp
RUN cp /usr/share/zoneinfo/Asia/Tokyo /etc/localtime && echo 'Asia/Tokyo' >/etc/timezone
ADD plano-cycle-api-0.0.1.jar plano_cycle_api.jar
EXPOSE 8080
ENTRYPOINT ["java","-Dfile.encoding=utf-8","-jar","plano_cycle_api.jar"]

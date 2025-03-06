FROM openjdk:21-bullseye

RUN echo "America/Sao_Paulo" > /etc/timezone              && \
    rm /etc/localtime                                     && \
    ln -sf /usr/share/zoneinfo/Brazil/East /etc/localtime && \
    dpkg-reconfigure -f noninteractive tzdata

WORKDIR /srv

COPY web/target/signer.war .

VOLUME /srv/arquivos

EXPOSE 8080

CMD ["java", "-jar", "signer.war"]

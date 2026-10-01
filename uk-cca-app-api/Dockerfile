FROM eclipse-temurin:21-jre

RUN echo "ttf-mscorefonts-installer msttcorefonts/accepted-mscorefonts-eula select true" | debconf-set-selections \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
        fontconfig \
        ttf-mscorefonts-installer \
    && rm -rf /var/lib/apt/lists/*

COPY src/main/resources/symbol.ttf /usr/share/fonts
RUN fc-cache -f -v

COPY target/uk-cca-app-api-env_version.jar /opt/applications/uk-cca-app-api/

RUN groupadd -g 499 cca && useradd -m -r -u 499 -g cca cca
USER cca
WORKDIR /opt/applications/uk-cca-app-api/

CMD ["/bin/bash", "-c", "sleep 15 && java -XX:+PrintFlagsFinal -Xmx1024m -Xms512m -jar uk-cca-app-api-env_version.jar"]

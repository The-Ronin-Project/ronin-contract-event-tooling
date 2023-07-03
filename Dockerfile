FROM gradle:8.1.1-jdk17

RUN useradd -rm ronin
RUN apt update && apt-get install -y python3 python3-pip && apt-get clean && pip install json-schema-for-humans

ADD --chown=ronin ./contract-tools /usr/local/bin
RUN chmod 755 /usr/local/bin/contract-tools
ADD --chown=ronin ronin-contract-json-plugin/build/initializer /usr/local/initializer
ADD --chown=ronin ronin-contract-json-plugin/src/main/initializer/.gitignore /usr/local/initializer/.gitignore

USER ronin
WORKDIR /app

CMD /usr/local/bin/contract-tools

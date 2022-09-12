FROM docker-proxy.devops.projectronin.io/alpine:3.16

RUN adduser -S -D ronin
RUN apk add bash npm python3 py3-pip
RUN npm install -g ajv-cli ajv-formats
RUN pip install json-schema-for-humans

ADD --chown=ronin ./contract-tools /usr/local/bin
RUN chmod 755 /usr/local/bin/contract-tools
USER ronin
WORKDIR /app

CMD /usr/local/bin/contract-tools
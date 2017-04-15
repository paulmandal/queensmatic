FROM alpine:3.5

EXPOSE 31337

ENV APP_DIR=/app

WORKDIR $APP_DIR
RUN mkdir -p $APP_DIR

RUN apk add --no-cache python3 && ln -s /usr/bin/python3.5 /usr/bin/python

COPY requirements.txt $APP_DIR

RUN pip3.5 install -r requirements.txt

COPY . $APP_DIR

CMD ["/usr/bin/python", "led-controller-uservice.py"]


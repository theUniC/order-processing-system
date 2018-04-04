FROM hseeberger/scala-sbt
COPY . /root/
RUN sbt update \
  && sbt compile
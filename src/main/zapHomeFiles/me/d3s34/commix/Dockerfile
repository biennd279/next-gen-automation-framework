FROM python:3-slim

RUN apt update

RUN apt install git -y

RUN git clone https://github.com/commixproject/commix.git /commix

WORKDIR /commix

RUN ["/commix/commix.py", "--install"]

ENTRYPOINT ["/bin/bash"]

CMD ["commix", "-h"]

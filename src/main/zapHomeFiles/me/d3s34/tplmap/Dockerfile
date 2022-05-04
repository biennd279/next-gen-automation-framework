FROM python:2.7

RUN apt update -y

RUN apt install git -y

RUN git clone https://github.com/epinna/tplmap.git /tplmap

WORKDIR /tplmap

RUN pip install -r requirements.txt

ENTRYPOINT ["/tplmap/tplmap.py"]

CMD ["-h"]

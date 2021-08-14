FROM python:latest
WORKDIR /usr/app/control-center
COPY ./ ./
RUN pip install -r requirements.txt
CMD ["python", "control-center.py"]
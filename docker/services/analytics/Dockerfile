FROM python:latest
WORKDIR /usr/app/analytics
COPY ./ ./
RUN pip install -r requirements.txt
CMD ["python", "analytics.py"]
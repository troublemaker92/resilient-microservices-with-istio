FROM python:latest
WORKDIR /usr/app/billing
COPY ./ ./
RUN pip install -r requirements.txt
CMD ["python", "billing.py"]
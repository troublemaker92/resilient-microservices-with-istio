from flask import Flask, jsonify, request, redirect, abort, Response, json, make_response
import socket
import requests
import random
from operator import itemgetter
import threading
from urllib.parse import urlencode
import logging


app = Flask(__name__)
PORT = 8080

IP_ADDRESS = socket.gethostbyname(socket.gethostname())
REGISTER_SERVICE = 'http://distributed-calculation.default.svc.cluster.local:8080/'
#REGISTER_SERVICE = 'http://localhost:8080/'
HEADER_APP_JSON = {'Content-Type': 'application/json'}
bind_to = {'hostname': "0.0.0.0", 'port': PORT}
SERVICE_VERSION = 1.1
users = {}

def generate_url(service_data):
    ms_name = service_data["msName"]
    port = str(service_data["msPort"])
    ms_function = service_data["msFunction"]
    return "http://" + ms_name + ":" + port + "/" + ms_function


def get_prices(service_name):
    uri = REGISTER_SERVICE + "getWorkloadByType/" + service_name
    response = requests.get(url=uri)
    price_main = int(response.text) * 3 + 10 + random.uniform(0, 20.5)
    response_body = {
        'estimatedPriceInEur': price_main,
        'estimatedPriceInDollar': price_main - 1,
        'estimatedTime': random.uniform(1, price_main),
        'function': service_name,
        'responseBody': response.status_code
    }
    return response_body

@app.route('/getAnalytics', methods=['GET'])
def get_analytics():
    estimated_pirce = get_prices(request.args.get("function"))
    status_code = estimated_pirce['responseBody']
    del estimated_pirce['responseBody']
    if status_code != 200:
        response_body = {
            'error': "Something went wrong."
        }
        return create_response(response_body, status_code)
    return create_response(estimated_pirce, status_code)


@app.route('/version', methods=['GET'])
def get_version():
    response_body = {
        'version': SERVICE_VERSION,
    }
    return create_response(response_body, 200)

def create_response(body, code):
    response = make_response(
        jsonify(body),
        code,
    )
    response.headers["Content-Type"] = "application/json"
    return response


if __name__ == '__main__':
    app.run(host=bind_to['hostname'], port=int(bind_to['port']), debug=True, use_reloader=False)

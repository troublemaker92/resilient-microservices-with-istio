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
#REGISTER_SERVICE = 'http://192.168.64.3:30005/findServiceByFunction'
REGISTER_SERVICE = 'http://service-repository.default.svc.cluster.local:8080/findServiceByFunction'
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
    uri = REGISTER_SERVICE + "?" + service_name
    response = requests.get(url=uri)
    result = []
    for i in json.loads(response.text):
        temp_info = i
        price_main = i["workload"] * 3 + 10 + random.uniform(0, 20.5)
        temp_info["estimatedPriceInEur"] = price_main
        temp_info["estimatedPriceInDollar"] = price_main - 1
        temp_info["estimatedTime"] = random.uniform(1, 20)
        result.append(temp_info)
    return result

@app.route('/getAnalytics', methods=['GET'])
def get_analytics():
    queryParams = urlencode(request.args)
    estimated_pirce = get_prices(queryParams)
    return create_response(estimated_pirce, 200)


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

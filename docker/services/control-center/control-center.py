from flask import Flask, jsonify, request, redirect, abort, Response, json
import socket
import requests
from operator import itemgetter
import threading
from urllib.parse import urlencode
import logging


app = Flask(__name__)
PORT = 8080
IP_ADDRESS = socket.gethostbyname(socket.gethostname())
#REGISTER_SERVICE = 'http://service-repository:8080/findServiceByFunction'
#REGISTER_SERVICE = 'http://service-repository.default.svc.cluster.local:8080/findServiceByFunction'
CALCULATION_URL = 'http://distributed-calculation.default.svc.cluster.local:8080/'
BILLING_URL = 'http://billing.default.svc.cluster.local:8080/'
#CALCULATION_URL = 'http://localhost:8080/'
#BILLING_URL = 'http://localhost:8081/'
HEADER_APP_JSON = {'Content-Type': 'application/json'}
bind_to = {'hostname': "0.0.0.0", 'port': PORT}

users = {}


#def find_service_by_name(service_name):
#    uri = REGISTER_SERVICE + "?function=" + service_name
#    app.logger.info(uri)
#    response = requests.get(url=uri)
#    ms_name = json.loads(response.text)[0]["msName"]
#    port = str(json.loads(response.text)[0]["msPort"])
#    ms_function = str(json.loads(response.text)[0]["msFunction"])
#    url = "http://" + ms_name + ":" + port + "/" + ms_function
#    app.logger.info(url)
#    return url

def generate_url(service_data):
    ms_name = service_data["msName"]
    port = str(service_data["msPort"])
    ms_function = service_data["msFunction"]
    return "http://" + ms_name + ":" + port + "/" + ms_function


#def get_prices(service_name):
#    uri = REGISTER_SERVICE + "?function=" + service_name
#    response = requests.get(url=uri)
#    result = []
#    for i in json.loads(response.text):
#        temp_info = i
#        temp_info["price"] = i["workload"] * 3 + 10
#        result.append(temp_info)
#    return result

@app.route('/createAccount', methods=['POST'])
def add_account():
    queryParams = urlencode(request.args)
    uri = BILLING_URL + "createAccount" + "?" + queryParams
    return Response(requests.post(url=uri), mimetype='application/json')


@app.route('/depositAccount', methods=['POST'])
def deposit_account():
    queryParams = urlencode(request.args)
    uri = BILLING_URL + "depositAccount" + "?" + queryParams
    return Response(requests.post(url=uri), mimetype='application/json')


@app.route('/chargeAccount', methods=['POST'])
def charge_account():
    queryParams = urlencode(request.args)
    uri = BILLING_URL + "chargeAccount" + "?" + queryParams
    return Response(requests.post(url=uri), mimetype='application/json')

@app.route('/getAccountInfo', methods=['GET'])
def get_account_info():
    queryParams = urlencode(request.args)
    return Response(get_account_info_(queryParams), mimetype='application/json')

def get_account_info_(queryParams):
    uri = BILLING_URL + "getAccountInfo" + "?" + queryParams
    return requests.get(url=uri)

def charge_account_(queryParams):
    uri = BILLING_URL + "chargeAccount" + "?" + queryParams
    return requests.post(url=uri)

@app.route('/deleteAccount', methods=['DELETE'])
def remove_account():
    queryParams = urlencode(request.args)
    uri = BILLING_URL + "deleteAccount" + "?" + queryParams
    return Response(requests.delete(url=uri), mimetype='application/json')

@app.route('/getAnalytics', methods=['GET'])
def get_analytics():
    queryParams = urlencode(request.args)
    return Response(requests.get("http://analytics:8080/getAnalytics" + queryParams), mimetype='application/json')

@app.route('/getAnalyticsVersion', methods=['GET'])
def get_analyticsVersion():
    return Response(requests.get("http://analytics:8080/version"), mimetype='application/json')

@app.route('/calculate2', methods=['POST'])
def calculate2():
    user = request.get_json()['user']
    calculationType = request.get_json()['calculationType']
    price = request.get_json()['price']
    firstNumber = str(request.get_json()['firstNumber'])
    secondNumber = str(request.get_json()['secondNumber'])
    if float(price) < 1:
        return Response("Price cannot be < 1", status=400)
    account_info = get_account_info_('user=' + user)
    if (account_info.status_code != 200):
        return Response(account_info, 400, mimetype='application/json')
    user_balance = json.loads(account_info.text)['balance']
    #prices = get_prices(calculationType)
    #nearest_price = prices[min(range(len(prices)), key=lambda i: abs(float(prices[i]['price'])-float(price)))]
    query_params = 'user=' + user + '&amount=' + str(price)
    charge_response = charge_account_(query_params)
    if (charge_response.status_code != 200):
        return Response(charge_response, 400, mimetype='application/json')
    url = CALCULATION_URL + calculationType
    if calculationType == 'add' or calculationType == 'multiply':
        request_body = {
            'decimalOne': firstNumber,
            'decimalTwo': secondNumber
        }
        return Response(requests.post(url, data=json.dumps(request_body), headers=HEADER_APP_JSON), 200)

    if calculationType == 'prime' or calculationType == 'fibonacci':
        url = url + "?n=" + firstNumber
        return Response(requests.post(url, headers=HEADER_APP_JSON), 200)
    return abort(403)


if __name__ == '__main__':
    app.run(host=bind_to['hostname'], port=int(bind_to['port']), debug=True, use_reloader=False)

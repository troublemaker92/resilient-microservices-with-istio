from flask import Flask, jsonify, request, redirect, abort, Response, json, make_response
from apscheduler.schedulers.background import BackgroundScheduler
from datetime import datetime
import socket
import requests
import threading
import time
import atexit

app = Flask(__name__)
PORT = 8080
IP_ADDRESS = socket.gethostbyname(socket.gethostname())
REGISTER_SERVICE = 'http://service-repository:8080/register'
HEADER_APP_JSON = {'Content-Type': 'application/json'}

def current_time_seconds():
    return round(time.time() * 1000)



bind_to = {'hostname': "0.0.0.0", 'port': PORT}
users = {}
global last_time_health_called
last_time_health_called = current_time_seconds()

@app.route('/isUserExists', methods=['GET'])
def isUserExists():
    if request.method == 'GET':
        user = request.args.get('user')
        if users.__contains__(user) is True:
            return Response(status=200)
        else:
            return Response(status=404)
    return abort(403)

@app.route('/createAccount', methods=['POST'])
def add_account():
    if request.method == 'POST':
        user = request.args.get('user')
        if users.__contains__(user) is True:
            response_body = {
                'Error': 'User ' + user + ' is already registered.',
            }
            return create_response(response_body, 400)
        #balance for current customer is 0
        users[user] = 0
        return create_response(get_account_info_by_username(user), 200)
    return abort(403)

@app.route('/depositAccount', methods=['POST'])
def deposit_account():
    if request.method == 'POST':
        user = request.args.get('user')
        amount = try_to_int(request.args.get('amount'))
        if users.__contains__(user) is True:
            if amount is None or amount <= 0:
                response_body = {
                    'Error': 'Amount is not a number or negative.',
                }
                return make_response(response_body, 400)
            users[user] = users[user] + amount
            return make_response(get_account_info_by_username(user), 200)
        else:
            response_body = {
                'Error': 'User ' + user + ' does not exist.',
            }
            return make_response(response_body, 400)
    return abort(403)

@app.route('/chargeAccount', methods=['POST'])
def charge_account():
    if request.method == 'POST':
        user = request.args.get('user')
        amount = try_to_int(request.args.get('amount'))
        if users.__contains__(user) is True:
            if amount is None or amount <= 0:
                response_body = {
                    'Error': 'Amount is not a number or negative.',
                }
                return make_response(response_body, 400)
            diff = users[user] - amount
            if diff < 0:
                response_body = {
                    'Error': 'Account balance cannot be charged (Reason: Insufficient funds)',
                }
                return make_response(response_body, 400)
            users[user] = diff
            return make_response(get_account_info_by_username(user), 200)
        else:
            response_body = {
                'Error': 'User ' + user + ' does not exist.',
            }
            return make_response(response_body, 400)
    return abort(403)

@app.route('/health', methods=['GET'])
def health():
    global last_time_health_called
    last_time_health_called = current_time_seconds()
    return Response("OK", status=200)

@app.route('/getAccountInfo', methods=['GET'])
def get_account_info():
    if request.method == 'GET':
        user = request.args.get('user')
        if users.__contains__(user) is True:
            return make_response(get_account_info_by_username(user), 200)
        else:
            response_body = {
                'Error': "User " + user + " does not exist.",
            }
            return make_response(response_body, 400)
    return abort(403)

@app.route('/deleteAccount', methods=['DELETE'])
def remove_account():
    if request.method == 'DELETE':
        user = request.args.get('user')
        if users.__contains__(user) is True:
            users.__delitem__(user)
            return Response('User was successfully deleted.', status=200)
        else:
            return Response("User " + user + " does not exist.", status=404)
    return abort(403)

def send_description():
    services_descriptions = [
        {'msName': IP_ADDRESS, 'msPort': PORT, 'msDescription': 'Deletes account of a given user.', 'msFunction': 'deleteAccount', 'msType': 'account'},
        {'msName': IP_ADDRESS, 'msPort': PORT, 'msDescription': 'Gets account info for a given user.', 'msFunction': 'getAccountInfo','msType': 'account'},
        {'msName': IP_ADDRESS, 'msPort': PORT, 'msDescription': 'Charges account of a given user.', 'msFunction': 'chargeAccount','msType': 'account'},
        {'msName': IP_ADDRESS, 'msPort': PORT, 'msDescription': 'Deposits account of a given user.', 'msFunction': 'depositAccount','msType': 'account'},
        {'msName': IP_ADDRESS, 'msPort': PORT, 'msDescription': 'Creates account for a given user.', 'msFunction': 'createAccount','msType': 'account'}
    ]
    for s in services_descriptions:
        requests.post(url=REGISTER_SERVICE, json=s, headers=HEADER_APP_JSON)

def check_if_registration_works():
    if current_time_seconds() - last_time_health_called > 60000:
        exit(1)
    if current_time_seconds() - last_time_health_called > 5000:
        send_description()

def try_to_int(amount):
    try:
        return int(amount)
    except ValueError:
        return None

def create_response(body, code):
    response = make_response(
        jsonify(body),
        code,
    )
    response.headers["Content-Type"] = "application/json"
    return response

def get_account_info_by_username(user_name):
    response_body = {
        'name': user_name,
        'balance': users[user_name]
    }
    return response_body

if __name__ == '__main__':
    threading.Thread(target=send_description).start()
    scheduler = BackgroundScheduler()
    scheduler.add_job(func=check_if_registration_works, trigger="interval", seconds=5)
    scheduler.start()
    app.run(host=bind_to['hostname'], port=int(bind_to['port']), debug=True, use_reloader=False)
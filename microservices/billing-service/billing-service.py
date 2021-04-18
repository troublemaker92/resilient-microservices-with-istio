from flask import Flask, jsonify, request, redirect, abort, Response, json
import socket
import requests
import threading


app = Flask(__name__)
PORT = 5000
IP_ADDRESS = socket.gethostbyname(socket.gethostname())
REGISTER_SERVICE = 'http://localhost:7000/register'
HEADER_APP_JSON = {'Content-Type': 'application/json'}


bind_to = {'hostname': "0.0.0.0", 'port': PORT}

users = {}

@app.route('/createAccount', methods=['POST'])
def add_account():
    if request.method == 'POST':
        user = request.args.get('user')
        if users.__contains__(user) is True:
            return Response("User " + user + " is already registered.", status=404)
        users[user] = 0
        return Response(users, status=200)
    return abort(403)

@app.route('/depositAccount', methods=['POST'])
def deposit_account():
    if request.method == 'POST':
        user = request.args.get('user')
        amount = try_to_int(request.args.get('amount'))
        if users.__contains__(user) is True:
            if amount is None or amount <= 0:
                return Response('Amount is not a number or negative.')
            users[user] = users[user] + amount
            return Response('You deposit is {}'.format(users[user]), status=200)
        else:
            return Response("User " + user + " does not exist.", status=404)
    return abort(403)

@app.route('/chargeAccount', methods=['POST'])
def charge_account():
    if request.method == 'POST':
        user = request.args.get('user')
        amount = try_to_int(request.args.get('amount'))
        if users.__contains__(user) is True:
            if amount is None or amount <= 0:
                return Response('Amount is not a number or negative.')
            diff = users[user] - amount
            if diff < 0:
                return Response('Account balance cannot be charged (Reason: Insufficient funds)')
            users[user] = diff
            return Response('You deposit is {}'.format(users[user]), status=200)
        else:
            return Response("User " + user + " does not exist.", status=404)
    return abort(403)

@app.route('/health', methods=['GET'])
def health():
    return Response("OK", status=200)

@app.route('/getAccountInfo', methods=['GET'])
def get_account_info():
    if request.method == 'GET':
        user = request.args.get('user')
        if users.__contains__(user) is True:
            return Response('You deposit is {}'.format(users[user]), status=200)
        else:
            return Response("User " + user + " does not exist.", status=404)
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
        {'msName': IP_ADDRESS, 'msPort': PORT, 'msDescription': 'Deletes account of a given user.', 'msFunction': 'deleteAccount'},
        {'msName': IP_ADDRESS, 'msPort': PORT, 'msDescription': 'Gets account info for a given user.', 'msFunction': 'getAccountInfo'},
        {'msName': IP_ADDRESS, 'msPort': PORT, 'msDescription': 'Charges account of a given user.', 'msFunction': 'chargeAccount'},
        {'msName': IP_ADDRESS, 'msPort': PORT, 'msDescription': 'Deposits account of a given user.', 'msFunction': 'depositAccount'},
        {'msName': IP_ADDRESS, 'msPort': PORT, 'msDescription': 'Creates account for a given user.', 'msFunction': 'createAccount'}
    ]
    for s in services_descriptions:
        requests.post(url=REGISTER_SERVICE, json=s, headers=HEADER_APP_JSON)

def try_to_int(amount):
    try:
        return int(amount)
    except ValueError:
        return None

if __name__ == '__main__':
    threading.Thread(target=send_description).start()
    app.run(host=bind_to['hostname'], port=int(bind_to['port']), debug=True, use_reloader=False)
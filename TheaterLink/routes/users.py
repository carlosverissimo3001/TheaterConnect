from flask import Flask, render_template, request, redirect, url_for, jsonify, Blueprint
from db import crud_ops
from utils import utils
import uuid, json

def construct_blueprint(dbConn):
    user_page = Blueprint('user_page', __name__)

    @user_page.route('/users', methods=['GET'])
    def users():
        users = crud_ops.get_users(dbConn)

        return jsonify(users)

    @user_page.route('/register', methods=['POST'])
    def register_user():
        # Get data from the request
        jsonData = request.json

        data = jsonData.get('data')
        data_json = json.loads(data)

        signature = jsonData.get('signature')
        public_key = data_json['public_key']

        # Validate the signature against the public key
        verified = utils.verify_signature(signature, public_key, data)

        if (not verified):
            return jsonify({'error': 'Invalid signature'}), 403


        # extract parameters from the data
        # NIF is used for unique identification
        nif  = data_json.get('nif')
        name = data_json.get('name')
        card = data_json.get('card')

        # Add the user to the database
        row = crud_ops.add_user(dbConn, name, nif, card, public_key)

        if row is None:
            return jsonify({'error': 'Error adding user to the database'}), 500
        else:
            user_id = row[0]

        # Return the user_id with success message
        return jsonify({'user_id': user_id, 'message': 'User added successfully'}), 201

    # given a user id, return the user details
    @user_page.route('/get_user', methods=['GET'])
    def get_name():
        user_id = request.args.get('user_id')

        user = crud_ops.get_user_by_user_id(dbConn, user_id)

        if user is None:
            return jsonify({'error': 'User not found'}), 404
        else:
            user = user[0]

        return jsonify(user), 200

    @user_page.route('/get_user_pkey', methods=['GET'])
    def get_public_key():
        user_id = request.args.get('user_id')

        user = crud_ops.get_user_by_user_id(dbConn, user_id)

        if user is None:
            return jsonify({'error': 'User not found'}), 404
        else:
            user = user[0]

        return jsonify({'publickey': user["publickey"]}), 200

    return user_page
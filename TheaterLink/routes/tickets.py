import base64, psycopg2, random, json
from flask import request, jsonify, Blueprint
from db import crud_ops
from utils import utils
from routes import transactions


VOUCHER_TYPE = ["FIVE_PERCENT", "FREE_COFFEE", "FREE_POPCORN"]


def construct_blueprint(dbConn: psycopg2.extensions.connection):
    ticket_page = Blueprint('ticket_page', __name__)


    @ticket_page.route('/purchase_tickets', methods=['POST'])
    def purchase_tickets():
        # Get data from the request
        jsonData = request.json

        # Get the data and signature fields from the request
        data = jsonData.get('data')
        signature = jsonData.get('signature')

        # Load the data into a json object
        data_json = json.loads(data)

        # extract user_id from the data
        user_id = data_json["user_id"]
        # Get the public key of the user
        public_key = utils.get_public_key(dbConn, user_id)

        # Validate the signature against the public key
        verified = utils.verify_signature(signature, public_key, data)

        if (not verified):
            return jsonify({'error': 'Invalid signature'}), 403

        # Extract the rest of the parameters from the data
        show_date_id = data_json["show_date_id"]
        num_tickets = data_json["num_tickets"]
        total_cost = data_json["total_cost"]

        # need to add a row to the tickets table for each ticket
        # for each ticket purchased, a voucher is also created
        ticket_data = []
        voucher_data = []

        # Add a row to the transactions table
        # Don't distinguish between different types of transactions for now
        trans_id = transactions.put_transaction('TICKET_PURCHASE', user_id, total_cost)

        for _ in range(num_tickets):
            ## TICKET LOGIC ##
            ticket_row = crud_ops.create_ticket(dbConn, user_id, show_date_id)
            if ticket_row is None:
                return jsonify({'message': 'Error purchasing tickets!'})

            ticket = utils.get_full_ticket(dbConn, ticket_row[0])
            ticket_data.append(ticket)

            ## VOUCHER LOGIC ##
            vc_type = random.choice(VOUCHER_TYPE[1:])
            voucher_row = crud_ops.create_voucher(dbConn, user_id, vc_type, trans_id)

            if voucher_row is None:
                return jsonify({'message': 'Error purchasing tickets! Error creating voucher!'})

            voucher_data.append(voucher_row[0])


        # give a 5% discount every 200 dollars spent
        num_vouchers_added = round(total_cost // 200)

        for _ in range(num_vouchers_added):
            vc_type = VOUCHER_TYPE[0]
            voucher_row = crud_ops.create_voucher(dbConn, user_id, vc_type, trans_id)

            if voucher_row is None:
                return jsonify({'message': 'Error purchasing tickets! Error creating voucher!'})

            voucher_data.append(voucher_row[0])

        ## HANDLE TICKET TRANSACTION ##
        if transactions.handle_ticket_purchase(trans_id, ticket_data) is None:
            return jsonify({'message': 'Error purchasing tickets! Error creating ticket transaction!'})

        return jsonify({'message': 'Tickets purchased successfully!', 'tickets': ticket_data, 'vouchers': voucher_data})


    @ticket_page.route('/tickets', methods=['GET'])
    def get_user_tickets():
        user_id = request.args.get('user_id')

        tickets = crud_ops.get_user_tickets(dbConn, user_id)
        active = request.args.get('active') == 'true'

        if active:
            tickets = [t for t in tickets if not t['isUsed']]

        return {"tickets" : tickets}

    # Will be called by the validation terminal app after the ticket is scanned
    @ticket_page.route('/validate_tickets', methods=['POST'])
    def validate_tickets():
        userid = request.json.get('userid')
        ticketids = request.json.get('ticketids')

        response = []

        for tid in ticketids:
            data = {"ticketid": tid}
            ticket = utils.get_full_ticket(dbConn, tid)

            if ticket.get('isUsed'):
                data['state'] = 'Ticket already used!'
                response.append(data)
                continue

            if ticket.get('userid') != userid:
                data['state'] = 'Ticket does not belong to user!'
                response.append(data)
                continue

            data['state'] = 'Ticket validated!'
            response.append(data)
            crud_ops.mark_ticket_as_used(dbConn, tid)


        return jsonify(response)

    # Will be called by the validation terminal app after the ticket is validated
    # Might not have to be an actual endpoint, could be handled by the validate_ticket endpoint
    # TODO: Decide on the implementation
    @ticket_page.route('/set_ticket_as_used', methods=['POST'])
    def mark_ticket_as_used():
        data = request.json

        ticket_id = data.get('ticket_id')

        crud_ops.mark_ticket_as_used(dbConn, ticket_id)

        return jsonify({'message': 'Ticket marked as used!'})



    return ticket_page

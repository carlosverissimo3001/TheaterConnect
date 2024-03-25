import psycopg2
from db import crud_ops

def get_public_key(dbConn: psycopg2.extensions.connection, user_id: str, ) -> str:
    '''
    Get the public key of a user

        Parameters:
            dbConn (psycopg2.extensions.connection): connection to the database
            user_id (str): user's id

        Returns:
            str: user's public key
    '''
    user = crud_ops.get_user_by_user_id(dbConn, user_id)

    if user is None:
        return None
    else:
        user = user[0]

    return user.get('publickey')

def get_full_ticket(dbConn: psycopg2.extensions.connection, ticket_id: str) -> dict:
    '''
    Get the full ticket information

        Parameters:
            dbConn (psycopg2.extensions.connection): connection to the database
            ticket_id (str): ticket's id

        Returns:
            dict: ticket's information
    '''
    ticket = crud_ops.get_ticket_by_ticket_id(dbConn, ticket_id)

    if ticket is None:
        return None

    return ticket[0]
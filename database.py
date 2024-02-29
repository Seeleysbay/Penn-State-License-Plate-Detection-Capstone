from sqlalchemy import create_engine, text
import os

# Build the certificate path dynamically
cert_path = os.path.join('static', 'CapstoneCert', 'cert.pem')

db_ConnectionString = "place password here"

engine = create_engine(
    db_ConnectionString, pool_size=20, max_overflow=0,
    connect_args={
        "ssl": {
            "ca": cert_path
        }
    }
)


def load_all_registered_from_db():
    with engine.connect() as conn:
        result_Register = conn.execute(text("select * from Registration"))
        result_all_Register = result_Register.all()

        Registery = []

        for rows in result_all_Register:
            Registery.append(rows)
        conn.commit()

        return Registery


def load_registered_from_db(PlateNum, PlateState):
    with engine.connect() as conn:
        result_Register = conn.execute(
            text("select * from Registration where PlateNum = :val and PlateState = :val2", val=PlateNum, val2=PlateState))

        findings = result_Register.all()
        if len(findings) == 0:
            conn.commit()
            return None
        else:
            conn.commit()
            return findings



def add_register_to_db(data):
    with engine.connect() as conn:
        params = {
            'ID': data['ID#'],
            'plate': data['Plate#'],
            'state': data['State'],
            'email': data['Email'],
            'make': data['Make'],
            'model': data['Model'],
            'name': data['Name'],
            'personType': data['PersonType']
        }
        result_Register = conn.execute(
            text("select * from Registration where PlateNum = :plate and PlateState = :state"), params)
        findings = result_Register.all()
        if len(findings) == 0:
            conn.execute(
                text("insert into Registration values (:ID,:name, :plate, :state,:make,:model,:personType, :email);"),
                params)
            conn.commit()
        else:
            conn.commit()
            return "Already Registered"


def delete_register_from_db(data):
    with engine.connect() as conn:
        params = {
            'ID': data['ID#'],
            'plate': data['Plate#'],
            'state': data['State'],

        }
        result_Register = conn.execute(
            text("select * from Registration where PlateNum = :plate and PlateState = :state"), params)
        findings = result_Register.all()
        if len(findings) == 0:
            conn.commit()
            return "Person Not Registered"
        else:
            conn.execute(text("delete from Registration where IDnum = :ID"), params)
            conn.commit()

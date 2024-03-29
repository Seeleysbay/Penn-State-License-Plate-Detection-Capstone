from sqlalchemy import create_engine, text
import os

# Build the certificate path dynamically
cert_path = os.path.join('static', 'CapstoneCert', 'cert.pem')

db_ConnectionString = "mysql+pymysql://di1vqwhs70eptombtzbe:pscale_pw_642ore1mRl3rdj0emmHgvu1WqHub09lHb6wEgqCvyzV@aws.connect.psdb.cloud/plate-recognition_database?charset=utf8mb4"

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

def load_all_unregistered_from_db():
    # Placeholder for unregistered users' data
    # TODO: Implement actual database querying when the table is available
    placeholder_data = [
        # Mimic the structure of your expected data
        # Example:
        # ('IDNum', 'Name', 'PlateNum', 'PlateState', 'VehicleMake', 'VehicleModel', 'PersonType', 'Email')
    ]
    return placeholder_data



def load_vehicles_from_db():
    with engine.connect() as conn:
        result_Vehicle = conn.execute(text("select * from Vehicle"))
        result_all_Vehicle = result_Vehicle.all()

        Vehicles = []

        for rows in result_all_Vehicle:
            Vehicles.append(rows)

        return Vehicles


def load_all_unregistered_from_db():
    with engine.connect() as conn:
        result_Unregistered = conn.execute(text("select * from Unregistered"))
        result_all_Unregistered = result_Unregistered.all()

        Unregistered = []

        for rows in result_all_Unregistered:
            Unregistered.append(rows)

        return Unregistered


def load_registered_from_db(PlateNum):
    with engine.connect() as conn:
        result_Register = conn.execute(text("select * from Registration where PlateNum = :val", val=PlateNum))

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
            'email': data['Email']
         }
        conn.execute(text("insert into Registration values (:ID, :plate, :state, :email);"), params)

        conn.commit()

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

def database_search_name(name):
    with engine.connect() as conn:
        result_Register = conn.execute(text("select * from Registration where Name= :val", val=name))
        findings = result_Register.all()
        if len(findings) == 0:
            conn.commit()
            return "Person not in Register"
        else:
            conn.commit()
            return findings

def database_search_date(date):
    with engine.connect() as conn:
        result_Register = conn.execute(text("select * from Registration where StartReg= :val", val=date))
        findings = result_Register.all()
        if len(findings) == 0:
            conn.commit()
            return "Not in Register"
        else:
            conn.commit()
            return findings

def database_search_state(state):
    with engine.connect() as conn:
        result_Register = conn.execute(text("select * from Registration where PlateState= :val", val=state))
        findings = result_Register.all()
        if len(findings) == 0:
            conn.commit()
            return "Not in Register"
        else:
            conn.commit()
            return findings

def database_search_plate(plate):
    with engine.connect() as conn:
        result_Register = conn.execute(text("select * from Registration where PlateNum= :val", val=plate))
        findings = result_Register.all()
        if len(findings) == 0:
            conn.commit()
            return "Not in Register"
        else:
            conn.commit()
            return findings

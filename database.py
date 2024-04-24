from sqlalchemy import create_engine, text
import os
from datetime import timedelta, datetime

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


def load_all_unregistered_from_db():
    with engine.connect() as conn:
        search_Result = conn.execute(text("select * from RegFails where LogDate < current_date-12;"))
        findings = search_Result.all()
        if len(findings) == 0:
            result_Fails = conn.execute(text("select * from RegFails"))
            result_all_unregistered = result_Fails.all()

            RegisterFails = []

            for rows in result_all_unregistered:
                RegisterFails.append(rows)
            conn.commit()

            return RegisterFails
        else:
            conn.execute(text("delete from RegFails where LogDate < current_date-12;"))
            result_Fails = conn.execute(text("select * from RegFails"))
            result_all_unregistered = result_Fails.all()

            RegisterFails = []

            for rows in result_all_unregistered:
                RegisterFails.append(rows)
            conn.commit()

            return RegisterFails


def load_all_registered_from_db():
    with engine.connect() as conn:
        search_Result = conn.execute(text("select * from Registration where EndReg <= CURRENT_DATE;"))
        findings = search_Result.all()
        if len(findings) == 0:
            result_Register = conn.execute(text("select * from Registration"))
            result_all_Register = result_Register.all()

            Registry = []

            for rows in result_all_Register:
                Registry.append(rows)
            conn.commit()

            return Registry
        else:
            conn.execute(text("delete from Registration where EndReg <= CURRENT_DATE;"))
            result_Register = conn.execute(text("select * from Registration"))
            result_all_Register = result_Register.all()

            Registry = []

            for rows in result_all_Register:
                Registry.append(rows)
            conn.commit()

            return Registry


def load_registered_from_db(PlateNum, PlateState):
    with engine.connect() as conn:
        result_Register = conn.execute(
            text("select * from Registration where PlateNum = :val and PlateState = :val2", val=PlateNum,
                 val2=PlateState))

        findings = result_Register.all()
        if len(findings) == 0:
            two_weeks_ago = datetime.now() - timedelta(days=14)
            search_Result = conn.execute(
                text("select * from RegFails where LogDate <= :time;").bindparams(time=two_weeks_ago))
            findings = search_Result.all()
            if len(findings) == 0:
                conn.execute(text("insert into RegFails values (:val1, :val2, CURRENT_TIMESTAMP)", val1=PlateNum,
                                  val2=PlateState))
                conn.commit()
                return "Plate Not Registered, Added to Fails"
            else:
                conn.execute(text("delete from RegFails where LogDate <= ;").bindparams(time=two_weeks_ago))

                conn.execute(text("insert into RegFails values (:val1, :val2, CURRENT_TIMESTAMP)", val1=PlateNum,
                                  val2=PlateState))
                conn.commit()
                return "Plate Not Registered, Added to Fails"

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
            if params['personType'] == 'Visitor':
                conn.execute(text(
                    "insert into Registration values (:ID,:name, :plate, :state,:make,:model,:personType, :email, Current_TIMESTAMP, Current_DATE+1);"),
                    params)
                conn.commit()
            else:
                conn.execute(text(
                    "insert into Registration values (:ID,:name, :plate, :state,:make,:model,:personType, :email, Current_TIMESTAMP,DATE_ADD(CURRENT_DATE, INTERVAL 6 MONTH));"),
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
            'state': data['State']
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


def extend_register_from_db(data):
    extend_date = datetime.strptime(data['ExtendDate'], '%Y-%m-%d')
    current_date = datetime.now()
    if extend_date.year > current_date.year:
        return "Year Exceeded"
    if extend_date.month > 12 | extend_date.month < 0:
        return "Month Exceeded or is Unrealistic"
    if extend_date.day > 31 | extend_date.day < 0:
        return "Day Exceeded or is Unrealistic"
    if extend_date < current_date:
        return "Extension date cannot be in the past"
    with engine.connect() as conn:
        params = {
            'ID': data['ID#'],
            'plate': data['Plate#'],
            'state': data['State'],
            'extendDate': extend_date
        }
        result_Register = conn.execute(
            text("select * from Registration where PlateNum = :plate and PlateState = :state"), params)
        findings = result_Register.all()
        if len(findings) == 0:
            conn.commit()
            return "Person Not Registered"
        else:
            conn.execute(text("update Registration set EndReg = :extendDate where IDnum = :ID"), params)
            conn.commit()


def database_search_any(query):
    print("Search function called with query:", query)
    with engine.connect() as conn:
        query_string = f"%{query}%"
        param = {
            'val': query_string
        }
        result_Register = conn.execute(text(
            "SELECT * FROM Registration WHERE Name LIKE :val OR StartReg LIKE :val OR PlateState LIKE :val OR PlateNum LIKE :val"),
            param)
        findings = result_Register.fetchall()
        return findings


def database_search_name(name):
    with engine.connect() as conn:
        param = {
            'val': name
        }
        result_Register = conn.execute(text("select * from Registration where Name= :val"), param)
        findings = result_Register.all()
        if len(findings) == 0:
            return []
        else:

            return findings


def database_search_date(date):
    with engine.connect() as conn:
        param = {'val': date}
        result_Register = conn.execute(text("SELECT * FROM Registration WHERE StartReg = :val"), param)
        findings = result_Register.fetchall()
        print("Query results:", findings)

        if len(findings) == 0:
            return []
        else:
            return findings


def database_search_state(state):
    with engine.connect() as conn:
        param = {
            'val': state
        }
        result_Register = conn.execute(text("select * from Registration where PlateState= :val"), param)
        findings = result_Register.all()
        if len(findings) == 0:
            return []
        else:

            return findings


def database_search_plate(plate):
    with engine.connect() as conn:
        param = {
            'val': plate
        }
        result_Register = conn.execute(text("select * from Registration where PlateNum= :val"), param)
        findings = result_Register.all()
        if len(findings) == 0:
            return []
        else:

            return findings


def database_search_make(make):
    with engine.connect() as conn:
        param = {
            'val': make
        }
        result_Register = conn.execute(text("select * from Registration where Make= :val"), param)
        findings = result_Register.all()
        if len(findings) == 0:
            return []
        else:

            return findings


def database_search_model(model):
    with engine.connect() as conn:
        param = {
            'val': model
        }
        result_Register = conn.execute(text("select * from Registration where Model= :val"), param)
        findings = result_Register.all()
        if len(findings) == 0:
            return []
        else:

            return findings


def database_search_email(email):
    with engine.connect() as conn:
        param = {
            'val': email
        }
        result_Register = conn.execute(text("select * from Registration where Email= :val"), param)
        findings = result_Register.all()
        if len(findings) == 0:
            return []
        else:

            return findings


def database_search_persontype(persontype):
    with engine.connect() as conn:
        param = {
            'val': persontype
        }
        result_Register = conn.execute(text("select * from Registration where PersonType= :val"), param)
        findings = result_Register.all()
        if len(findings) == 0:
            return []
        else:

            return findings


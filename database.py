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
        result_Register = conn.execute(text("select * from Registration where PlateNum = :plate and PlateState = :state"), params)
        findings = result_Register.all()
        if len(findings) == 0:
            if params['personType'] == 'Visitor':
                conn.execute(text("insert into Registration values (:ID,:name, :plate, :state,:make,:model,:personType, :email, Current_TIMESTAMP, Current_DATE+1);"), params)
                conn.commit()
            else:
                conn.execute(text("insert into Registration values (:ID,:name, :plate, :state,:make,:model,:personType, :email, Current_TIMESTAMP,DATE_ADD(CURRENT_DATE, INTERVAL 6 MONTH);"), params)
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
    extend_date = datetime.strptime(data['ExtendDate'], '%Y-%m-%d %H:%M:%S')
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
        result_Register = conn.execute(text("select * from Registration where PlateNum = :plate and PlateState = :state"), params)
        findings = result_Register.all()
        if len(findings) == 0:
            conn.commit()
            return "Person Not Registered"
        else:
            conn.execute(text("update Registration set EndReg = :extendDate where IDnum = :ID"), params)
            conn.commit()

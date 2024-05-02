from fastapi import FastAPI, Depends, HTTPException, Header
from pydantic import BaseModel
from sqlalchemy import create_engine, text
from datetime import datetime
from fastapi.security import OAuth2PasswordBearer
import uvicorn
import re

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="key")

app = FastAPI()

class Plate(BaseModel):
    state: str
    plateNumber: str

with open('/code/app/secretKey.txt', 'r') as file:
    dataKey = file.read().rstrip()

def get_api_key(api_key: str = Header(...)):
    if api_key != dataKey:  # Replace "your_api_key" with your actual API key
        raise HTTPException(status_code=401, detail=None)
    return api_key

with open('app/secret.txt', 'r') as file:
    data = file.read().rstrip()

db_ConnectionString = data

engine = create_engine(
    db_ConnectionString, pool_size=20, max_overflow=0,
    connect_args={
        "ssl": { 
            "ca": "/code/app/cert.pem"
        }
    }
)

def load_registered_from_db(State, PlateNum):
    with engine.connect() as conn:
        result_Register = conn.execute(text("select * from Registration where PlateState = " + "\'" + State + '\'' + " AND " + "PlateNum = " + "\'" + PlateNum + "\'"))

        findings = result_Register.all()
        if len(findings) == 0:
            conn.commit()
            return None
        else:
            conn.commit()
            return findings

def insert_into_unregistered(State, PlateNum):
    with engine.connect() as conn:

        try:
            time = datetime.now()
            timestamp = time.strftime("%Y-%m-%d %H:%M:%S")
            result = conn.execute(text("insert into RegFails values (\'" + PlateNum + "\' , \'" + State + "\' , \'" + timestamp + "\')")) 
            conn.commit()
        except Exception:
            print("Error")

@app.get("/")
def home_page():
    formatText = "please use this format /lp?state=&plate=" 
    return formatText

@app.get("/lp")
async def read_plate(state: str, plate: str, api_key: str = Depends(get_api_key)):
    try:
        regexState = re.sub("[^a-zA-Z]+", '', state)
        regexPlate = re.sub("[^a-zA-Z0-9-]+", '', plate)
        regexState = regexState.upper()
        regexPlate = regexPlate.upper()

        results = load_registered_from_db(regexState, regexPlate)

        if(results == None):
            insert_into_unregistered(regexState, regexPlate)
            return None
        else:
            for row in results:
                info = row._mapping
        
            return info
    except:
        return None

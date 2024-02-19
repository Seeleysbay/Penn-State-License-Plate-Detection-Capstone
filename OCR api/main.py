from fastapi import FastAPI
import cmdp as cm
import subprocess
app = FastAPI()


@app.get("/")
def read_root():
    return {"Hello": "World"}


@app.get("/lp/{item_id}")
def read_item(item_id: str):
    results: str = cm.check(item_id)
    return {item_id: results}
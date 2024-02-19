import subprocess as sp

def check(data: str):
    results = sp.run(["java", "StandardClassification", data], shell=True, capture_output=True, text=True)

    # results = sp.run([data], shell=True, capture_output=True, text=True)
    ret: str = results.stdout
    return ret
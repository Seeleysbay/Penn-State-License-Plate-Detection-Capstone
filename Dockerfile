
FROM python:3.9-slim


WORKDIR /app


COPY . /app


COPY requirements.txt /app/
RUN pip install --no-cache-dir -r requirements.txt


EXPOSE 5000


ENV FLASK_APP=app.py
ENV FLASK_RUN_HOST=0.0.0.0

CMD ["flask", "run", "--port", "5000"]

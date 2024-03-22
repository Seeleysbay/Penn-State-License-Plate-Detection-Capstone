from flask import Flask, request, render_template, send_from_directory, redirect, url_for
from database import (load_all_registered_from_db, add_register_to_db, delete_register_from_db,
                      database_search_name, database_search_date, database_search_state, database_search_plate,
                      load_all_unregistered_from_db)
import cv2
import numpy as np
import io
import os
from datetime import datetime
from flask import session, flash, redirect, request, g, url_for
from functools import wraps
from flask import get_flashed_messages


app = Flask(__name__)

app.secret_key = 'admin123'

@app.route('/')
def home():
    return render_template('index.html')


@app.route('/update2')
def update2():
    return render_template('update2.html')


@app.route('/about')
def about():
    return render_template('about.html')  # Assuming you have an 'about.html' template


@app.route('/database')
def database():
    view_type = request.args.get('view', 'registered')
    if view_type == 'registered':
        data = load_all_registered_from_db()
    else:
        # Placeholder for unregistered view
        data = []
    is_admin = session.get('logged_in') and session.get('user_role') == 'admin'
    return render_template('database.html', data=data, is_admin=is_admin, view=view_type)



@app.route('/changelog')
def changelog():
    # Your logic to fetch changelog entries
    return render_template('changelog.html')  # Make sure 'changelog.html' exists in your templates directory


@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        # Here, you would typically check against a user database
        if username == 'admin' and password == 'password':
            session['logged_in'] = True
            session['user_role'] = 'admin'  # Setting user role in the session
            return redirect('/')  # Redirect to an admin page
        else:
            flash('Invalid credentials')
    return render_template('login.html')

def login_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'logged_in' not in session:
            return redirect(url_for('login', next=request.url))
        return f(*args, **kwargs)
    return decorated_function

@app.route('/logout')
def logout():
    session.clear()
    return redirect(url_for('login'))


@app.route('/AddRegister', methods=['POST', 'GET'])
def register():
    if request.method == 'POST':
        data = request.form
        add_register_to_db(data)
    data = load_all_registered_from_db()
    return render_template('database.html', data=data, is_admin=is_admin, get_flashed_messages=get_flashed_messages)


@app.route('/DeleteRegister', methods=['POST'])
def delete_register():
    if not session.get('logged_in'):
        return redirect(url_for('login'))

    data = request.form
    message = delete_register_from_db(data)
    if message == "Person Not Registered":
        flash("Person Not Registered")
    else:
        flash("Person Successfully Deleted")
    return redirect(url_for('database'))


@app.route('/search', methods=['GET'])
def search():
    # Retrieve search parameters from the query string
    name = request.args.get('name', '')
    date = request.args.get('date', '')
    state = request.args.get('state', '')
    plate = request.args.get('plate', '')

    # Perform search based on the provided parameters
    if name:
        results = database_search_name(name)
    elif date:
        results = database_search_date(date)
    elif state:
        results = database_search_state(state)
    elif plate:
        results = database_search_plate(plate)
    else:
        results = "Please provide a search parameter."

    # Assuming you want to use the same template to display the results
    is_admin = session.get('logged_in') and session.get('user_role') == 'admin'
    return render_template('database.html', data=results, is_admin=is_admin)

@app.route('/static/<filename>')
def static_file(filename):
    return send_from_directory('static', filename)

if __name__ == '__main__':
    app.run(debug=True)

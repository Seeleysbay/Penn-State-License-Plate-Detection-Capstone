from flask import Flask, request, render_template, send_from_directory, redirect, url_for
from database import (load_all_registered_from_db, add_register_to_db, delete_register_from_db,
                      load_all_unregistered_from_db, database_search_any, extend_register_from_db)
from flask import session, flash, redirect, request, g, url_for
from functools import wraps
from flask import get_flashed_messages

ADMIN_EMAIL = 'jmt6265@psu.edu'
ADMIN_PASSWORD = 'admin123'

app = Flask(__name__)
app.secret_key = 'your_secret_key'


@app.route('/database')
def database():
    # Determine the view type from the URL parameter or default to 'registered'
    view_type = request.args.get('view')
    search_query = request.args.get('search_query', None)

    # If there is a search query, perform the search regardless of the view type
    if search_query:
        data1 = database_search_any(search_query)
        print("Search results:", data1)
    else:
        # Load registered data
        data1 = load_all_registered_from_db()
        print("Registered data")

        data2 = load_all_unregistered_from_db()
        print("Unregistered data")

    is_admin = session.get('is_admin', False)
    return render_template('database.html', data1=data1, data2=data2, is_admin=is_admin, view=view_type)


@app.route('/', methods=['GET', 'POST'])
def login():
    error = None
    if request.method == 'POST':
        email = request.form['login']
        password = request.form['password']
        if email == ADMIN_EMAIL and password == ADMIN_PASSWORD:
            session['logged_in'] = True
            session['is_admin'] = True  # Set admin flag
            return redirect(url_for('database'))
        else:
            error = 'Invalid credentials. Please try again.'
    return render_template('login.html', error=error)


@app.route('/logout')
def logout():
    session.pop('logged_in', None)
    session.pop('is_admin', None)
    flash('You have been logged out.')
    return redirect(url_for('login'))


@app.route('/admin_dashboard')
def admin_dashboard():
    # Check if user is logged in
    if not session.get('logged_in'):
        # Not logged in, redirect to login page
        return redirect(url_for('login'))
    # Logged in, render admin dashboard
    return 'Welcome to the admin dashboard!'


@app.template_filter('formatdate')
def format_date_filter(value, format='%Y-%m-%d'):
    """Custom Jinja2 filter to format datetime objects."""
    if value is None:
        return ""
    return value.strftime(format)


@app.route('/AddRegister', methods=['POST', 'GET'])
def register():
    if not session.get('logged_in'):
        # If not logged in, redirect to the login page
        return redirect(url_for('login'))

    if request.method == 'POST':
        data = request.form
        add_register_to_db(data)
        flash("Registration added successfully.")  # Give feedback to the user that the operation was successful

    data = load_all_registered_from_db()
    is_admin = session.get('is_admin', False)  # Fetch admin status from the session
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


@app.route('/ExtentionRegister', methods=['POST', 'GET'])
def register_extension():
    if not session.get('logged_in'):
        return redirect(url_for('login'))

    if request.method == 'POST':
        data = request.form
        extend_register_from_db(data)
        flash("Registration Extension successful.")  # Give feedback to the user that the operation was successful

    data = load_all_registered_from_db()
    is_admin = session.get('is_admin', False)  # Fetch admin status from the session
    return render_template('database.html', data=data, is_admin=is_admin, get_flashed_messages=get_flashed_messages)


@app.route('/static/<filename>')
def static_file(filename):
    return send_from_directory('static', filename)


if __name__ == '__main__':
    app.run(debug=True)

from flask import Flask, render_template, request, redirect, url_for
"""
This module implements a Flask web application with several routes and a utility function to generate random CPF-like numbers.
Functions:
    generate_random_cpf(): Generates a random CPF-like number.
    index(): Renders the index.html template.
    display(): Handles GET and POST requests to display a user and view, along with a generated CPF.
    post_one(): Handles POST requests for the 'genius' route.
    post_two(): Handles GET and POST requests for the 'notforget' route.
Routes:
    /: Renders the index.html template.
    /display: Handles GET and POST requests to display a user and view, along with a generated CPF.
    /genius: Handles POST requests for the 'genius' route.
    /notforget: Handles GET and POST requests for the 'notforget' route.
"""
import random

app = Flask(__name__)

def generate_random_cpf():

    """ Generates a random CPF-like number. """
    cpf = [random.randint(0, 9) for _ in range(9)]
    for _ in range(2):
        val = sum([(len(cpf)+1-i) * v for i, v in enumerate(cpf)]) % 11
        cpf.append(11 - val if val > 1 else 0)
    return "{}.{}.{}-{}".format("".join(map(str, cpf[:3])),
                                "".join(map(str, cpf[3:6])),
                                "".join(map(str, cpf[6:9])),
                                "".join(map(str, cpf[9:])))


""" Main route of the application """
@app.route('/')
def index():
    return render_template('index.html')

""" First route of the application """
@app.route('/display', methods=['GET', 'POST'])
def display():
    if request.method == 'GET':
        user = request.args.get('user')
        view = request.args.get('view')
    else:
        user = request.form['user']
        view = request.form['view']
    
    cpf = generate_random_cpf()
    return render_template('display.html', user=user, view=view, cpf=cpf)

""" Genius route of the application """
@app.route('/genius', methods=['POST'])
def post_one():
    action = request.form['action']
    # Handle POST request for button one
    return render_template('genius.html')

""" Notforget route of the application """
@app.route('/notforget', methods=['GET', 'POST'])
def post_two():
    if request.method == 'GET':
        action = request.args.get('action')
    else:
        action = request.form['action']
    # Handle POST request for button two
    return render_template('notforget.html')

""" Enable the debug mode """
if __name__ == '__main__':
    app.run(debug=True)

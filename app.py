from flask import Flask, request
from recommender import get_output, update_weights, get_nearbyrestaurants,ratings

app = Flask(__name__)


# Requête pour donner des recommandations à l'utilisateur
@app.route('/id=<user>&latitude=<latitude>&longitude=<longitude>', methods=['GET'])
def recommend(user, latitude, longitude):
    return get_output(str(user), float(latitude), float(longitude))


# Requête pour mettre à jour les poids de l'utilisateur
@app.route('/id=<user>&restaurant_id=<restaurant_id>&latitude=<lat>&longitude=<long>', methods=['GET'])
def updateweights(user, restaurant_id, lat, long):
    update_weights(str(user), int(restaurant_id), float(lat), float(long))


# Requête pour donner les restaurants les plus proches
@app.route('/nearby/latitude=<lat>&longitude=<long>', methods=['GET'])
def getnearbyrestaurants(lat, long):
    return get_nearbyrestaurants(float(lat), float(long))

@app.route('/ratings')
def getratings():
    return ratings().to_json(orient='records')

@app.route('/')
def index():
    return ""


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, threaded=True)

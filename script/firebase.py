import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import json
import utm
import random

with open('.../Tra-aparcamientos.json') as file:
    data = json.load(file)

cred = credentials.Certificate('.../prueba.json')

firebase_admin.initialize_app(cred, {
    'databaseURL' : 'https://valenparking-66ec4.firebaseio.com'
})

root = db.reference()
root.child('parkings').delete()
for parking in data['features']:
    coordenadas = parking['geometry']['coordinates']
    parking = parking['properties']

    try:
        total = int(parking['plazastota'])
        libres = random.randrange(total)
    except:
        libres = 50

    #pip install utm
    coordenadas = utm.to_latlon(coordenadas[0], coordenadas[1], 30, 'U')
    new_parking = root.child('parkings').push({
        'name': str(parking['nombre']),
        'address': str(parking['direccion']),
        'type': str(parking['tipo']),
        'total': str(total),
        'free': str(libres),
        'coordinates': {
            'lat': coordenadas[0],
            'lon': coordenadas[1]
        },
        'comments': {}
    })
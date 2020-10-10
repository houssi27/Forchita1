#!/usr/bin/env python3
"""
Very simple HTTP server in python for logging requests
Usage::
    ./server.py [<port>]
"""
from importlib import reload
from recommender import get_output,update_weights
from http.server import BaseHTTPRequestHandler, HTTPServer
import logging


class S(BaseHTTPRequestHandler):
    def _set_response(self):
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.end_headers()

    def do_GET(self):
        logging.info("GET request,\nPath: %s\nHeaders:\n%s\n", str(self.path), str(self.headers))
        self._set_response()

        param = self.path.split("&")
        liste = []
        for i in param:
            liste.append(i.split('=')[1])
        user = str(liste[0])
        latitude = float(liste[1])
        longitude = float(liste[2])
        resultat = get_output(user, latitude, longitude)
        self.wfile.write(res.encode('utf-8'))



    def do_POST(self):
        logging.info("GET request,\nPath: %s\nHeaders:\n%s\n", str(self.path), str(self.headers))
        self._set_response()
        param=self.path.split("&")
        liste = []
        for i in param:
            liste.append(i.split('=')[1])
        user = str(liste[0])
        restaurant_id = int(liste[1])
        lat = float(liste[2])
        long = float(liste[3])
        update_weights(user, restaurant_id, lat, long)



def run(server_class=HTTPServer, handler_class=S, port=8080):
    logging.basicConfig(level=logging.INFO)
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    logging.info('Starting httpd...\n')
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    httpd.server_close()
    logging.info('Stopping httpd...\n')

from sys import argv

if len(argv) == 2:
    run(port=int(argv[1]))
else:
    run()
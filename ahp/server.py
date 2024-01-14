import json
import logging
import re
from http.server import BaseHTTPRequestHandler, HTTPServer
import insert
import ahp

class LocalData(object):
    records = {}


class HTTPRequestHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        if re.search('/item_comparison', self.path):
            length = int(self.headers.get('content-length'))
            data = self.rfile.read(length).decode('utf8')

            result = json.loads(data)
            insert.insert_alternative_ranking(result["ids"], 0, 0, result["matrix"])
            self.send_response(200)
        elif re.search('/criteria_comparison', self.path):
            length = int(self.headers.get('content-length'))
            data = self.rfile.read(length).decode('utf8')

            result = json.loads(data)
            insert.insert_criteria_ranking(result["ids"], 0, result["matrix"])
            self.send_response(200)
        elif re.search('/facilitator_config', self.path):
            length = int(self.headers.get('content-length'))
            data = self.rfile.read(length).decode('utf8')

            config = json.loads(data)

        else:
            self.send_response(403)
        self.end_headers()

    def do_GET(self):
        if re.search('/items', self.path):
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()

            items = insert.get_alternative_ids_and_names()
            criteria = insert.get_criteria_ids_and_names()

            data = json.dumps({**items, **criteria}).encode('utf-8')

            self.wfile.write(data)
        elif re.search('/ranking', self.path):
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()

            alternatives = ahp.get_alternatives()

            bottom_criteria = ahp.get_bottom_criteria()

            experts = ahp.get_experts()

            ranking = ahp.create_ranking(alternatives, bottom_criteria, experts)

            print(alternatives)
            print(ranking)

            result = list(map(lambda x: x[1][1], sorted(zip(ranking, alternatives), reverse=True)))

            print(f"result: {result}")

            data = json.dumps(result).encode('utf-8')

            self.wfile.write(data)
        else:
            self.send_response(403)
        self.end_headers()


if __name__ == '__main__':
    server = HTTPServer(('0.0.0.0', 8000), HTTPRequestHandler)
    logging.info('Starting httpd...\n')
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        pass
    server.server_close()
    logging.info('Stopping httpd...\n')
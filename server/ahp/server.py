import json
import logging
import re
import socketserver
from copy import copy, deepcopy
from http.server import BaseHTTPRequestHandler, HTTPServer
import database
import ahp_logic

input_json = {}
output_json = {}

class HTTPRequestHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        global input_json
        if re.search('/item_comparison', self.path):
            length = int(self.headers.get('content-length'))
            data = self.rfile.read(length).decode('utf8')

            result = json.loads(data)
            input_json["responses"].append(result)
            database.insert_alternative_ranking(result["ids"], result["criterionId"], result["expertId"], result["matrix"])
            self.send_response(200)
        elif re.search('/criteria_comparison', self.path):
            length = int(self.headers.get('content-length'))
            data = self.rfile.read(length).decode('utf8')

            result = json.loads(data)
            input_json["responses"].append(result)
            database.insert_criteria_ranking(result["ids"], result["expertId"], result["matrix"])
            self.send_response(200)
        elif re.search('/facilitator_config', self.path):
            length = int(self.headers.get('content-length'))
            data = self.rfile.read(length).decode('utf8')

            config = json.loads(data)
            database.create_ranking(config)

            input_json = deepcopy(config)

            for alt in input_json["alternatives"]:
                alt.pop("criteria_descriptions")

            input_json["responses"] = []

            print(input_json)

            self.send_response(200)
        else:
            self.send_response(403)
        self.end_headers()

    def do_GET(self):
        global input_json
        global output_json
        if re.search('/items', self.path):
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()

            items = database.get_alternative_ids_and_names()
            criteria = database.get_criteria_ids_and_names()

            data = json.dumps({**items, **criteria}).encode('utf-8')

            self.wfile.write(data)
        elif re.search('/ranking', self.path):
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()

            alternatives = ahp_logic.get_alternatives()
            bottom_criteria = ahp_logic.get_bottom_criteria()
            experts = ahp_logic.get_experts()
            ranking = ahp_logic.create_ranking(alternatives, bottom_criteria, experts)

            result = list(map(lambda x: x[1][1], sorted(zip(ranking, alternatives))))

            output_json = list(map(lambda x: {"Name": x[1][1], "Score": x[0]}, sorted(zip(ranking, alternatives))))

            data = json.dumps(result).encode('utf-8')

            self.wfile.write(data)
        elif re.search('/expert_id/.*', self.path):
            expert_id = database.get_expert_id(self.path.removeprefix("/expert_id/"))
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()
            self.wfile.write(str(expert_id).encode('utf-8'))
        elif re.search('/input_json', self.path):
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()
            self.wfile.write(json.dumps(input_json).encode('utf-8'))
        elif re.search('/output_json', self.path):
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()
            self.wfile.write(json.dumps(output_json).encode('utf-8'))
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

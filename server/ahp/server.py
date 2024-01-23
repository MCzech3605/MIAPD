import json
import logging
import re
from http.server import BaseHTTPRequestHandler, HTTPServer
import database
import ahp_logic


class HTTPRequestHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        if re.search('/item_comparison', self.path):
            length = int(self.headers.get('content-length'))
            data = self.rfile.read(length).decode('utf8')

            result = json.loads(data)
            database.insert_alternative_ranking(result["ids"], result["criterionId"], result["expertId"], result["matrix"])
            self.send_response(200)
        elif re.search('/criteria_comparison', self.path):
            length = int(self.headers.get('content-length'))
            data = self.rfile.read(length).decode('utf8')

            result = json.loads(data)
            database.insert_criteria_ranking(result["ids"], result["expertId"], result["matrix"])
            self.send_response(200)
        elif re.search('/facilitator_config', self.path):
            length = int(self.headers.get('content-length'))
            data = self.rfile.read(length).decode('utf8')

            config = json.loads(data)
            database.create_ranking(config)

            self.send_response(200)
        else:
            self.send_response(403)
        self.end_headers()

    def do_GET(self):
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

            data = json.dumps(result).encode('utf-8')

            self.wfile.write(data)
        elif re.search('/expert_id/.*', self.path):
            expert_id = database.get_expert_id(self.path.removeprefix("/expert_id/"))
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()
            self.wfile.write(str(expert_id).encode('utf-8'))
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

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
        if re.search('/comparison', self.path):
            length = int(self.headers.get('content-length'))
            data = self.rfile.read(length).decode('utf8')

            result = json.loads(data)
            insert.insert_alternative_ranking(result["ids"], 0, 0, result["matrix"])
            self.send_response(200)
        else:
            self.send_response(403)
        self.end_headers()

    def do_GET(self):
        if re.search('/items', self.path):
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()

            # Return json, even though it came in as POST URL params
            # data = json.dumps(LocalData.records[record_id]).encode('utf-8')
            data = json.dumps(insert.get_alternative_ids_and_names()).encode('utf-8')

            self.wfile.write(data)
        elif re.search('/ranking', self.path):
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()

            # Return json, even though it came in as POST URL params
            # data = json.dumps(LocalData.records[record_id]).encode('utf-8')
            alternatives = map(lambda x: x[1], sorted(ahp.get_alternatives()))

            data = json.dumps(alternatives).encode('utf-8')

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
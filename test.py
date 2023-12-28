from ahp import get_alternatives_with_desc
import sqlite3


conn: sqlite3.Connection = sqlite3.connect("ahp/baza.db")
cur: sqlite3.Cursor = conn.cursor()

print(get_alternatives_with_desc(cur))


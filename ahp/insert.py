import sqlite3

conn: sqlite3.Connection = sqlite3.connect("baza.db")
cur: sqlite3.Cursor = conn.cursor()


def get_criteria_ids_and_names(cur=cur):
    query = """
        select id, name, description, parent_criterion from Criteria
        where ranking=(select id from ranking order by id desc limit 1)
        order by id
        """
    cur.execute(query)

    result = cur.fetchall()
    return {
        "criteria_ids": [crit[0] for crit in result],
        "criteria_names": [crit[1] for crit in result],
        "criteria_descriptions": [crit[2] for crit in result],
        "criteria_parent_ids": [crit[3] for crit in result]
    }


def get_alternative_ids_and_names(cur=cur):
    query = """
        select id, name, description from Alternatives
        where ranking=(select id from ranking order by id desc limit 1)
        order by id
        """
    cur.execute(query)
    result = cur.fetchall()

    return {
        "item_ids": [crit[0] for crit in result],
        "item_names": [crit[1] for crit in result],
        "item_descriptions": [crit[2] for crit in result]
    }


def insert_criteria_ranking(c: list[int], expert_id: int, m: list[list[int]], cur=cur, conn=conn):
    """
    given a matrix of criteria comparisons from an expert,
    update the database with all comparisons

    m - comparison matrix for alternatives, where ids are provided inside a list
    c - list of compared criteria, sorted in ascending order
    """
    n = len(m)

    print("crit_ids:", c)
    print("matrix:", m)

    for i in range(n):
        for j in range(n):
            if i == j: continue
            
            query = """
                insert into CriteriaComparisons (first_criterion, second_criterion, expert, scale)
                values (?, ?, ?, ?)
                """

            info = (c[i], c[j], expert_id, int(m[i][j]))
            cur.execute(query, info)
            conn.commit()



def insert_alternative_ranking(a: list[int], criterion: int, expert_id: int, m: list[list[int]], cur=cur, conn=conn):
    """
    given a matrix of alternative comparisons according to specified criterion
    from an expert, update the database with all comparisons

    m - comparison matrix from alternatives, where ids are sorted in ascending order
    a - list of compared alternatives, sorted in ascending order
    """
    n = len(m)

    print("alt_ids:", a)
    print("criterion:", criterion)
    print("matrix:", m)

    for i in range(n):
        for j in range(n):
            if i == j: continue
            
            query = """
                insert into AlternativeComparisons (criterion, first_alternative, second_alternative, expert, scale)
                values (?, ?, ?, ?, ?)
                """

            info = (criterion, a[i], a[j], expert_id, m[i][j])
            cur.execute(query, info)
            conn.commit()


def create_ranking(decoded_json):
    """
    given decoded json dictionary (a python dict) with ranking info,
    insert them into the database
    """

    cur.execute("insert into ranking values (NULL)")
    conn.commit()
    ranking_id = cur.lastrowid

    crit_ids = {}
    for c in decoded_json["criteria"]:
        query = """
        insert into criteria (name, description, parent_criterion, ranking)
        values (?, ?, ?, ?)
        """

        p_str = c["parent_criterion"]
        parent = crit_ids[p_str] if p_str != "" else None

        info = (c["name"], c["description"], parent, ranking_id)
        cur.execute(query, info)
        conn.commit()

        crit_ids[c["name"]] = cur.lastrowid


    for a in decoded_json["alternatives"]:
        query = """
        insert into alternatives (name, description, ranking)
        values (?, ?, ?)
        """

        info = (a["name"], a["description"], ranking_id)
        cur.execute(query, info)
        conn.commit()

        alt_id = cur.lastrowid

        for (crit, desc) in a["criteria_descriptions"].items():
            if desc == "": continue

            crit_id = crit_ids[crit]
            query = """
            insert into AlternativeCriteriaDesc (alternative, criterion, description)
            values (?, ?, ?)
            """

            info = (alt_id, crit_id, desc)
            cur.execute(query, info)
            conn.commit()


    for e in decoded_json["experts"]:
        query = "insert into experts (name, email) values (?, ?)"

        info = (e["name"], e["email"])
        cur.execute(query, info)
        conn.commit()

def get_expert_id(name):
    query = "select id, name from experts where name == ?"

    cur.execute(query, (name,))
    result = cur.fetchall()
    return -1 if len(result) == 0 else result[0][0]

def empty_database(cur=cur):
    cur.execute("SELECT name FROM sqlite_master WHERE type='table';")
    tables = cur.fetchall()

    for table_name in tables:
        try:
            cur.execute(f"DELETE FROM {table_name[0]};")
            print(f"All records deleted from table {table_name[0]}")
        except sqlite3.OperationalError as e:
            print(f"An error occurred: {e}")

    conn.commit()
    conn.close()

if __name__ == "__main__":
    criteria = get_criteria_ids_and_names()
    alternatives = get_alternative_ids_and_names()

    cursor = cur

    cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
    tables = cursor.fetchall()

    for table_name in tables:
        try:
            cursor.execute(f"DELETE FROM {table_name[0]};")
            print(f"All records deleted from table {table_name[0]}")
        except sqlite3.OperationalError as e:
            print(f"An error occurred: {e}")

    conn.commit()
    conn.close()

    query = """
    select * from Alternatives
    """

    cur.execute(query)
    result = cur.fetchall()
    print("\n".join(map(str, result)))

    print("\n\n\n")

    query = """
        select * from AlternativeComparisons
        """

    cur.execute(query)
    result = cur.fetchall()
    print("\n".join(map(str, result)))

    print(criteria, alternatives)

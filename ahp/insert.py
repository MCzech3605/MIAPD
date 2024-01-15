import sqlite3

conn: sqlite3.Connection = sqlite3.connect("baza.db")
cur: sqlite3.Cursor = conn.cursor()


def get_criteria_ids_and_names(cur=cur):
    query = "select id, name, parent_criterion from Criteria order by id"
    cur.execute(query)

    result = cur.fetchall()
    return {
        "ids": [crit[0] for crit in result],
        "names": [crit[1] for crit in result],
        "parent_ids": [crit[2] for crit in result]
    }


def get_alternative_ids_and_names(cur=cur):
    query = "select id, name from Alternatives order by id"
    cur.execute(query)
    result = cur.fetchall()

    return {
        "ids": [crit[0] for crit in result],
        "names": [crit[1] for crit in result]
    }


def insert_criteria_ranking(c: list[int], expert_id: int, m: list[list[int]], cur=cur, conn=conn):
    """
    given a matrix of criteria comparisons from an expert,
    update the database with all comparisons

    m - comparison matrix for alternatives, where ids are provided inside a list
    c - list of compared criteria, sorted in ascending order
    """
    n = len(m)

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

    crit_ids = {}
    for c in decoded_json["criteria"]:
        query = """
        insert into criteria (name, description, parent_criterion)
        values (?, ?, ?)
        """

        p_str = c["parent_criterion"]
        parent = crit_ids[p_str] if p_str != "" else None

        info = (c["name"], c["description"], parent)
        cur.execute(query, info)
        conn.commit()

        crit_ids[c["name"]] = cur.lastrowid


    for a in decoded_json["alternatives"]:
        query = """
        insert into alternatives (name, description)
        values (?, ?)
        """

        info = (a["name"], a["description"])
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


    for s in decoded_json["scales"]:
        query = "insert into scales (value, description) values (?, ?)"

        info = (s["value"], s["description"])
        cur.execute(query, info)
        conn.commit()


    for e in decoded_json["experts"]:
        query = "insert into experts (name, email) values (?, ?)"

        info = (e["name"], e["email"])
        cur.execute(query, info)
        conn.commit()


if __name__ == "__main__":
    criteria = get_criteria_ids_and_names()
    alternatives = get_alternative_ids_and_names()
    print(criteria, alternatives)

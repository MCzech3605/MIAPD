import sqlite3
from pprint import pprint
import numpy as np

conn: sqlite3.Connection = sqlite3.connect("baza.db")
cur: sqlite3.Cursor = conn.cursor()


def eigenvalue_method(matrix):
    eigenvalues, eigenvectors = np.linalg.eig(matrix)

    # Find the index of the maximum eigenvalue
    max_eigenvalue_index = np.argmax(eigenvalues)

    # Extract the corresponding eigenvector
    priority_vector = np.real(eigenvectors[:, max_eigenvalue_index])

    # Normalize the priority vector
    priority_vector /= np.sum(priority_vector)

    return priority_vector

# wip for multiple layers of criteria
def calculate_steps(tuples):
    relationships = {t[0]: t[1] for t in tuples}

    def steps_to_ancestor(node):
        steps = 0
        while node is not None:
            steps += 1
            node = relationships.get(node)
        return steps - 1  

    result = {t[0]: steps_to_ancestor(t[0]) for t in tuples}
    return result


def get_alternatives(cur=cur): 
    query = "select * from alternatives order by id"
    cur.execute(query)

    dupa = cur.fetchall()

    return dupa


def get_alternatives_with_desc(cur=cur):
    alternatives = get_alternatives(cur)
    n_alt = len(alternatives)

    res = [[] for _ in range(n_alt)]
    alternative_index = {alternatives[i][0]: i for i in range(n_alt)}

    for alt in alternatives:
        alt_id = alt[0]
        query = f"""
                select criterion, description from AlternativeCriteriaDesc
                where alternative={alt_id}
                order by criterion
                """
        cur.execute(query)
        res[alternative_index[alt_id]].append(cur.fetchall())

    return res


def get_bottom_criteria(cur=cur):
    query = "select * from criteria where parent_criterion is null order by id"
    cur.execute(query)

    return cur.fetchall()


def get_experts(cur=cur):
    query = """
            select experts.id from experts
            inner join CriteriaComparisons
            inner join AlternativeComparisons
            group by experts.id
            """
    cur.execute(query)

    return [t[0] for t in cur.fetchall()]


def summarize_priorities(m):
    return np.prod(m, axis=0) ** (1. / len(m))


def create_ranking(alternatives, criteria, expert_ids, cur=cur):
    n_crit = len(criteria)
    n_alt = len(alternatives)
    n_exp = len(expert_ids)
    m = [[[[1 for _ in range(n_alt)] for _ in range(n_alt)] for _ in range(n_crit)] for _ in range(n_exp)]

    criteria_index = {criteria[i][0]: i for i in range(n_crit)}
    expert_index = {expert_ids[i]: i for i in range(n_exp)}
    
    for expert_id in expert_ids:
        for crit in criteria:
            crit_id = crit[0]
            query = f"""
                select first_alternative, second_alternative, value
                from AlternativeComparisons 
                join scales on scale=scales.id
                where criterion={crit_id} and expert={expert_id}
                """
            cur.execute(query)
            comparisons = cur.fetchall()
            
            for first, second, val in comparisons:
                m[expert_index[expert_id]][criteria_index[crit_id]][criteria_index[first]][criteria_index[second]] = val

    alt_wages_per_expert = [[eigenvalue_method(np.array(a)) for a in exp_m] for exp_m in m]
    alternative_wages = summarize_priorities(alt_wages_per_expert)

    m = [[[1 for _ in range(n_crit)] for _ in range(n_crit)] for _ in range(n_exp)]

    for expert_id in expert_ids:
        query = f"""
            select first_criterion, second_criterion, value
            from CriteriaComparisons 
            join scales on scale=scales.id
            where expert={expert_id}
            """
        cur.execute(query)
        comparisons = cur.fetchall()

        for first, second, val in comparisons:
            m[expert_index[expert_id]][criteria_index[first]][criteria_index[second]] = val

    crit_wages_per_expert = [eigenvalue_method(np.array(a)) for a in m]
    criterion_wages = summarize_priorities(crit_wages_per_expert)

    res = [0 for _ in range(n_alt)]
    for i in range(n_alt):
        sum = 0
        for j in range(n_crit):
            sum += criterion_wages[j] * alternative_wages[j][i]
            
        res[i] = sum

    return res


if __name__ == "__main__":

    alternatives = get_alternatives()

    bottom_criteria = get_bottom_criteria()

    experts = get_experts()

    res = create_ranking(alternatives, bottom_criteria, experts)
    pprint(res)

    pprint(get_alternatives_with_desc())
    
    cur.close()
    conn.close()

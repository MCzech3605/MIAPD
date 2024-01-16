import sqlite3
from pprint import pprint
from itertools import groupby
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


def calculate_steps(tuples):
    relationships = {t[0]: t[1] for t in tuples}

    def steps_to_ancestor(node):
        steps = 0
        while node != -1:
            steps += 1
            node = relationships.get(node)
        return steps - 1  

    result = {t[0]: steps_to_ancestor(t[0]) + 1 for t in tuples}
    result[-1] = 0
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


def get_criteria():
    query = "select id, parent_criterion from criteria order by id"
    cur.execute(query)

    return cur.fetchall()


def get_bottom_criteria(cur=cur):
    query = f"""
        select * from criteria
        where id not in (
            select parent_criterion from criteria
            where parent_criterion is not null
        )
        """
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
    m = np.array(m, dtype=object)
    return np.prod(m, axis=0) ** (1. / len(m))


def create_ranking(alternatives, bottom_criteria, expert_ids, cur=cur):
    n_crit = len(bottom_criteria)
    n_alt = len(alternatives)
    n_exp = len(expert_ids)
    m = [[[[1 for _ in range(n_alt)] for _ in range(n_alt)] for _ in range(n_crit)] for _ in range(n_exp)]

    bottom_criteria = [(c[0], c[3] if c[3] is not None else -1) for c in bottom_criteria]

    bottom_criteria_index = {bottom_criteria[i][0]: i for i in range(n_crit)}
    alternative_index = {alternatives[i][0]: i for i in range(n_alt)}
    expert_index = {expert_ids[i]: i for i in range(n_exp)}
    
    for expert_id in expert_ids:
        for crit in bottom_criteria:
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
                m[expert_index[expert_id]][bottom_criteria_index[crit_id]][alternative_index[first]][alternative_index[second]] = val

    alt_wages_per_expert = [[eigenvalue_method(np.array(a)) for a in exp_m] for exp_m in m]
    alternative_wages = summarize_priorities(alt_wages_per_expert)

    m = [[[1 for _ in range(n_crit)] for _ in range(n_crit)] for _ in range(n_exp)]

    criteria = [(c[0], c[1] if c[1] is not None else -1) for c in get_criteria()]
    criteria.sort(key=lambda x: x[1])
    steps = calculate_steps(criteria)
    parents = {c[0]: c[1] for c in criteria}
    parents[-1] = None

    children_graph = [(id, [c[0] for c in group]) for id, group in groupby(criteria, key=lambda x: x[1])]
    children_graph.sort(key=lambda x: steps[x[0]], reverse=True)
    n_g = len(children_graph)

    levels = [(id, [c[0] for c in group]) for id, group in groupby(steps.items(), key=lambda x: x[1])]
    levels.sort(key=lambda x: x[0], reverse=True)

    parent_index = {children_graph[i][0]: i for i in range(n_g)}
    child_index = [{g[1][i]: i for i in range(len(g[1]))} for g in children_graph]

    m = []

    for expert_id in expert_ids:
        ma = [[[1 for _ in g[1]] for _ in g[1]] for g in children_graph]

        query = f"""
            select first_criterion, second_criterion, parent_criterion, value
            from CriteriaComparisons 
            join scales on scale=scales.id
            join Criteria on first_criterion=Criteria.id
            where expert={expert_id}
            """
        cur.execute(query)
        comparisons = cur.fetchall()

        for first, second, p, val in comparisons:
            if p is None: p = -1
            p_i = parent_index[p]
            ma[p_i][child_index[p_i][first]][child_index[p_i][second]] = val

        m.append(ma)


    # only from pairwise comparisons
    crit_wages_per_expert = [[eigenvalue_method(np.array(a)) for a in exp_m] for exp_m in m]
    criterion_wages = summarize_priorities(crit_wages_per_expert)


    wage_prod = [1.0 for _ in range(n_crit)]
    for c in bottom_criteria:
        prod = 1.0
        crit_id = c[0]
        parent_id = parents[crit_id]

        while crit_id != -1:
            i = parent_index[parent_id]
            cmp = criterion_wages[i]
            val = cmp[child_index[i][crit_id]]
            prod *= val

            crit_id = parent_id
            parent_id = parents[crit_id]
        
        wage_prod[bottom_criteria_index[c[0]]] = prod


    res = [0 for _ in range(n_alt)]
    for i in range(n_alt):
        sum = 0
        for j in range(n_crit):
            sum += wage_prod[j] * alternative_wages[j][i]
            
        res[i] = sum


    return res


if __name__ == "__main__":

    alternatives = get_alternatives()

    bottom_criteria = get_bottom_criteria()

    experts = get_experts()

    res = create_ranking(alternatives, bottom_criteria, experts)
    pprint(res)

    # pprint(get_alternatives_with_desc())
    
    cur.close()
    conn.close()

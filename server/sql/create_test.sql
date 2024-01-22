insert into ranking values (NULL);

insert into alternatives (name, description, ranking) values
    ('alt1', 'x', 1),
    ('alt2', 'x', 1),
    ('alt3', 'x', 1);

insert into criteria (name, description, parent_criterion, ranking) values
    ('crit1', 'x', NULL, 1),
    ('crit2', 'x', NULL, 1),
    ('crit3', 'x', NULL, 1);

insert into experts (name, email) values
    ('e1', 'x@a.com'),
    ('e2', 'x@a.com');

insert into criteriacomparisons (first_criterion, second_criterion, expert, scale) values
    (1, 2, 1, 0.5),
    (1, 3, 1, 0.5),
    (2, 3, 1, 0.5),
    (2, 1, 1, 0.5),
    (3, 1, 1, 2),
    (3, 2, 1, 2),
    (1, 2, 2, 2),
    (1, 3, 2, 2),
    (2, 3, 2, 2),
    (2, 1, 2, 0.5),
    (3, 1, 2, 0.5),
    (3, 2, 2, 0.5);


insert into alternativecomparisons (criterion, first_alternative, second_alternative, expert, scale) values
    (1, 1, 2, 1, 0.5),
    (1, 1, 3, 1, 0.5),
    (1, 2, 3, 1, 0.5),
    (1, 2, 1, 1, 2),
    (1, 3, 1, 1, 2),
    (1, 3, 2, 1, 2),
    (1, 1, 2, 2, 2),
    (1, 1, 3, 2, 2),
    (1, 2, 3, 2, 2),
    (1, 2, 1, 2, 0.5),
    (1, 3, 1, 2, 0.5),
    (1, 3, 2, 2, 0.5),
    (2, 1, 2, 1, 0.5),
    (2, 1, 3, 1, 0.5),
    (2, 2, 3, 1, 0.5),
    (2, 2, 1, 1, 2),
    (2, 3, 1, 1, 2),
    (2, 3, 2, 1, 2),
    (2, 1, 2, 2, 2),
    (2, 1, 3, 2, 2),
    (2, 2, 3, 2, 2),
    (2, 2, 1, 2, 0.5),
    (2, 3, 1, 2, 0.5),
    (2, 3, 2, 2, 0.5),
    (3, 1, 2, 1, 0.5),
    (3, 1, 3, 1, 0.5),
    (3, 2, 3, 1, 0.5),
    (3, 2, 1, 1, 2),
    (3, 3, 1, 1, 2),
    (3, 3, 2, 1, 2),
    (3, 1, 2, 2, 2),
    (3, 1, 3, 2, 2),
    (3, 2, 3, 2, 2),
    (3, 2, 1, 2, 0.5),
    (3, 3, 1, 2, 0.5),
    (3, 3, 2, 2, 0.5);

insert into alternativecriteriadesc (alternative, criterion, description) values
    (0, 0, 'x'),
    (0, 1, 'x'),
    (0, 2, 'x'),
    (1, 0, 'x'),
    (1, 1, 'x'),
    (1, 2, 'x'),
    (2, 0, 'x'),
    (2, 1, 'x'),
    (2, 2, 'x');

-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2024-01-16 13:20:53.381

-- tables
-- Table: AlternativeComparisons
CREATE TABLE AlternativeComparisons (
    id integer NOT NULL CONSTRAINT AlternativeComparisons_pk PRIMARY KEY AUTOINCREMENT,
    criterion integer NOT NULL,
    first_alternative integer NOT NULL,
    second_alternative integer NOT NULL,
    expert integer NOT NULL,
    scale double NOT NULL,
    CONSTRAINT alternative_comparison_expert FOREIGN KEY (expert)
    REFERENCES Experts (id),
    CONSTRAINT AlternativeComparisons_Criteria FOREIGN KEY (criterion)
    REFERENCES Criteria (id),
    CONSTRAINT second_alternative_ref FOREIGN KEY (second_alternative)
    REFERENCES Alternatives (id),
    CONSTRAINT first_alternative_ref FOREIGN KEY (first_alternative)
    REFERENCES Alternatives (id)
);

-- Table: AlternativeCriteriaDesc
CREATE TABLE AlternativeCriteriaDesc (
    id integer NOT NULL CONSTRAINT AlternativeCriteriaDesc_pk PRIMARY KEY AUTOINCREMENT,
    alternative integer NOT NULL,
    criterion integer NOT NULL,
    description text NOT NULL,
    CONSTRAINT described_alternative FOREIGN KEY (alternative)
    REFERENCES Alternatives (id),
    CONSTRAINT considered_criterion FOREIGN KEY (criterion)
    REFERENCES Criteria (id)
);

-- Table: Alternatives
CREATE TABLE Alternatives (
    id integer NOT NULL CONSTRAINT Alternatives_pk PRIMARY KEY AUTOINCREMENT,
    name text NOT NULL,
    description text NOT NULL,
    ranking integer NOT NULL,
    CONSTRAINT Alternatives_Ranking FOREIGN KEY (ranking)
    REFERENCES Ranking (id)
);

-- Table: Criteria
CREATE TABLE Criteria (
    id integer NOT NULL CONSTRAINT Criteria_pk PRIMARY KEY AUTOINCREMENT,
    name text NOT NULL,
    description text NOT NULL,
    parent_criterion integer,
    ranking integer NOT NULL,
    CONSTRAINT subcriterion_parent FOREIGN KEY (parent_criterion)
    REFERENCES Criteria (id),
    CONSTRAINT Criteria_Ranking FOREIGN KEY (ranking)
    REFERENCES Ranking (id)
);

-- Table: CriteriaComparisons
CREATE TABLE CriteriaComparisons (
    id integer NOT NULL CONSTRAINT CriteriaComparisons_pk PRIMARY KEY AUTOINCREMENT,
    first_criterion integer NOT NULL,
    second_criterion integer NOT NULL,
    expert integer NOT NULL,
    scale double NOT NULL,
    CONSTRAINT criteria_comparison_expert FOREIGN KEY (expert)
    REFERENCES Experts (id),
    CONSTRAINT second_criterion_ref FOREIGN KEY (second_criterion)
    REFERENCES Criteria (id),
    CONSTRAINT first_criterion_ref FOREIGN KEY (first_criterion)
    REFERENCES Criteria (id)
);

-- Table: Experts
CREATE TABLE Experts (
    id integer NOT NULL CONSTRAINT Experts_pk PRIMARY KEY AUTOINCREMENT,
    name text NOT NULL,
    email text NOT NULL CHECK (email LIKE '%@%.%')
);

-- Table: Ranking
CREATE TABLE Ranking (
    id integer NOT NULL CONSTRAINT Ranking_pk PRIMARY KEY AUTOINCREMENT
);

-- End of file.


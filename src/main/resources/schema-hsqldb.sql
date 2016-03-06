-- Création de la table DUAL pour HSQLDB
CREATE TABLE dual (
dummy varchar(1) );

INSERT INTO dual (dummy) VALUES ('X');

-- Création des tables contenant les énumérations

CREATE TABLE mantis_enum_custom_field_types  (
    id int NOT NULL PRIMARY KEY,
    name varchar(32) NOT NULL
);

CREATE TABLE mantis_enum_etas  (
    id int NOT NULL PRIMARY KEY,
    name varchar(32) NOT NULL
);

CREATE TABLE mantis_enum_priorities  (
    id int NOT NULL PRIMARY KEY,
    name varchar(32) NOT NULL
);

CREATE TABLE mantis_enum_projections  (
    id int NOT NULL PRIMARY KEY,
    name varchar(32) NOT NULL
);

CREATE TABLE mantis_enum_project_status  (
    id int NOT NULL PRIMARY KEY,
    name varchar(32) NOT NULL
);

CREATE TABLE mantis_enum_project_view_states  (
    id int NOT NULL PRIMARY KEY,
    name varchar(32) NOT NULL
);

CREATE TABLE mantis_enum_reproducibilities  (
    id int NOT NULL PRIMARY KEY,
    name varchar(32) NOT NULL
);

CREATE TABLE mantis_enum_resolutions  (
    id int NOT NULL PRIMARY KEY,
    name varchar(32) NOT NULL
);

CREATE TABLE mantis_enum_severities  (
    id int NOT NULL PRIMARY KEY,
    name varchar(32) NOT NULL
);

-- Création des tables contenant les données liées à un projet

CREATE TABLE mantis_project_table  (
    id int NOT NULL PRIMARY KEY,
    name varchar(128) NOT NULL
);

CREATE TABLE mantis_project_hierarchy_table  (
    parent_id int NOT NULL,
    child_id int NOT NULL,
    
    PRIMARY KEY(parent_id, child_id),
    FOREIGN KEY (parent_id) REFERENCES mantis_project_table(id),
    FOREIGN KEY (child_id) REFERENCES mantis_project_table(id)
);

CREATE TABLE mantis_category_table  (
    id int IDENTITY NOT NULL PRIMARY KEY,
    project_id int NOT NULL,
    name varchar(128) NOT NULL,
    
    FOREIGN KEY (project_id) REFERENCES mantis_project_table(id)
);

CREATE TABLE mantis_custom_field_table  (
    id int NOT NULL PRIMARY KEY,
    name varchar(64) NOT NULL,
    type_id int NOT NULL,
    possible_values varchar(500),
    default_value varchar(255),
    valid_regexp varchar(255),
    
    FOREIGN KEY (type_id) REFERENCES mantis_enum_custom_field_types(id)
);

CREATE TABLE mantis_custom_field_project_table  (
    field_id int NOT NULL,
    project_id int NOT NULL,
    
    PRIMARY KEY(project_id, field_id),
    FOREIGN KEY (field_id) REFERENCES mantis_custom_field_table(id),
    FOREIGN KEY (project_id) REFERENCES mantis_project_table(id)
);

CREATE TABLE mantis_user_table  (
    id int NOT NULL PRIMARY KEY,
    name varchar(32) NOT NULL
);

CREATE TABLE mantis_project_user_list_table  (
    project_id int NOT NULL,
    user_id int NOT NULL,
    
    PRIMARY KEY(project_id, user_id),
    FOREIGN KEY (user_id) REFERENCES mantis_user_table(id),
    FOREIGN KEY (project_id) REFERENCES mantis_project_table(id)
);

CREATE TABLE mantis_project_version_table (
	id int NOT NULL PRIMARY KEY,
	project_id int NOT NULL,
	version varchar(64) NOT NULL,
	description varchar(500),
	released boolean,
	obsolete boolean,

    FOREIGN KEY (project_id) REFERENCES mantis_project_table(id)
);

-- Création des tables contenant les données liées à une mantis

CREATE TABLE mantis_bug_table (
	id int NOT NULL PRIMARY KEY,
	project_id int NOT NULL,
	reporter_id int,
	handler_id int,
	priority_id int,
	severity_id int,
	status_id int,
	resolution_id int,
	description varchar(500),
	steps_to_reproduce varchar(500),
	additional_information varchar(500),
	platform varchar(32),
	version varchar(64),
	fixed_in_version varchar(64),
	target_version varchar(64),
	summary varchar(128) NOT NULL,
	category varchar(128),
	date_submitted datetime,
	last_updated datetime,
	
	FOREIGN KEY (project_id) REFERENCES mantis_project_table(id),
	FOREIGN KEY (reporter_id) REFERENCES mantis_user_table(id),
	FOREIGN KEY (handler_id) REFERENCES mantis_user_table(id),
	FOREIGN KEY (priority_id) REFERENCES mantis_enum_priorities(id),
	FOREIGN KEY (severity_id) REFERENCES mantis_enum_severities(id),
	FOREIGN KEY (status_id) REFERENCES mantis_enum_project_status(id),
	FOREIGN KEY (resolution_id) REFERENCES mantis_enum_resolutions(id)
);

CREATE TABLE mantis_bugnote_table (
	id int NOT NULL PRIMARY KEY,
	bug_id int NOT NULL,
	reporter_id int NOT NULL,
	text varchar(500),
	date_submitted datetime,
	last_modified datetime,
	
	FOREIGN KEY (bug_id) REFERENCES mantis_bug_table(id),
	FOREIGN KEY (reporter_id) REFERENCES mantis_user_table(id)
);

CREATE TABLE mantis_custom_field_string_table (
	field_id int NOT NULL,
	bug_id int NOT NULL,
	field_value varchar(255),
	
	PRIMARY KEY(field_id, bug_id),
	FOREIGN KEY (field_id) REFERENCES mantis_custom_field_table(id),
	FOREIGN KEY (bug_id) REFERENCES mantis_bug_table(id)
);

CREATE TABLE mantis_bug_history_table (
	id int IDENTITY NOT NULL PRIMARY KEY,
	bug_id int NOT NULL,
	user_id int NOT NULL,
	field_name varchar(64),
	old_value varchar(255),
	new_value varchar(255),
	history_type int,
	date_modified datetime NOT NULL,
	
	FOREIGN KEY (user_id) REFERENCES mantis_user_table(id),
	FOREIGN KEY (bug_id) REFERENCES mantis_bug_table(id)
);

-- Indexes

CREATE INDEX idx_cat_proj ON mantis_category_table (project_id);
CREATE INDEX idx_field_type ON mantis_custom_field_table (type_id);
CREATE INDEX idx_version_proj ON mantis_project_version_table (project_id);

CREATE INDEX idx_mantis_proj ON mantis_bug_table (project_id);
CREATE INDEX idx_mantis_reporter_usr ON mantis_bug_table (reporter_id);
CREATE INDEX idx_mantis_handler_usr ON mantis_bug_table (handler_id);
CREATE INDEX idx_mantis_priority ON mantis_bug_table (priority_id);
CREATE INDEX idx_mantis_severity ON mantis_bug_table (severity_id);
CREATE INDEX idx_mantis_status ON mantis_bug_table (status_id);
CREATE INDEX idx_mantis_resolution ON mantis_bug_table (resolution_id);

CREATE INDEX idx_bugnote_mantis ON mantis_bugnote_table (bug_id);
CREATE INDEX idx_bugnote_reporter_usr ON mantis_bugnote_table (reporter_id);

CREATE INDEX idx_bughistory_mantis ON mantis_bug_history_table (bug_id);
CREATE INDEX idx_bughistory_reporter_usr ON mantis_bug_history_table (user_id);
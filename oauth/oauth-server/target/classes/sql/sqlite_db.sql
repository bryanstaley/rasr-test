DROP TABLE clients; 
DROP TABLE nodes;
DROP TABLE active_tokens;

--creates
CREATE TABLE clients(id integer primary key autoincrement,client_name varchar(128),client_secret varchar(1024),auth_server varchar(128),created datetime);
CREATE TABLE nodes(id integer primary key autoincrement,node_id integer,client_id integer);
CREATE TABLE active_tokens(id integer primary key autoincrement,token varchar(1024),client_id varchar(128),auth_server varchar(128),expire_time datetime);

--adds
INSERT INTO clients (client_name,client_secret,auth_server,created) VALUES ('bstaley','bstaley','test server','now');
INSERT INTO nodes (node_id,client_id)VALUES (42,(select id from clients where client_name = 'bstaley'));

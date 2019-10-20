CREATE TABLE users (
  id int NOT NULL,
  name varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- insert into "users" (id, name)
--   values (1, 'Jane'), (2, 'Fred'), (3, 'Jane'), (4, 'Fred'),
--          (5, 'Jane'), (6, 'Fred'), (7, 'Jane'), (8, 'Fred'),
--          (9, 'Jane'), (10, 'Fred')

#Database

``` markdown 
   docker pull mariadb
   docker run -p "3306:3306"  --detach --name compare-database --env MARIADB_USER=compare-user --env MARIADB_PASSWORD=userpassword123! --env MARIADB_ROOT_PASSWORD=rootpassword123!  mariadb:latest
```


```roomsql 
CREATE TABLE simple_table (
id BIGINT auto_increment NOT NULL,
name varchar(100) NOT NULL,
description varchar(1000) NULL,
createDate DATETIME DEFAULT NOW(),
createdBy varchar(100) NULL,
CONSTRAINT simple_table_PK PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_general_ci;```



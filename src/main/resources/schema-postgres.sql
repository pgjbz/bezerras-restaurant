--CREATE TABLES

CREATE TABLE TB_CATEGORY(
                            ID_CATEGORY BIGSERIAL PRIMARY KEY,
                            NM_CATEGORY VARCHAR(50) NOT NULL,
                            FL_MENU BOOLEAN DEFAULT FALSE
);

CREATE TABLE TB_PRODUCT(
                           ID_PRODUCT BIGSERIAL PRIMARY KEY ,
                           NM_PRODUCT VARCHAR(50) NOT NULL,
                           VL_PRODUCT DECIMAL(10, 2) NOT NULL,
                           ID_CATEGORY INT NOT NULL,
                           FOREIGN KEY(ID_CATEGORY) REFERENCES TB_CATEGORY(ID_CATEGORY)
);

CREATE TABLE TB_TABLE (
                          ID_TABLE SERIAL PRIMARY KEY,
                          NM_TABLE VARCHAR(50) NOT NULL
);

CREATE TABLE TB_ORDER_ADDRESS (
                                  ID_ORDER_ADDRESS BIGSERIAL primary KEY,
                                  NM_CLIENT VARCHAR(255) NOT NULL,
                                  NM_STREET VARCHAR(255) NOT NULL,
                                  NR_NUMBER VARCHAR(6),
                                  DS_COMPLEMENT VARCHAR(20),
                                  NM_DISTRICT VARCHAR(30) NOT NULL,
                                  NM_CITY VARCHAR NOT NULL,
                                  NM_STATE VARCHAR(30) NOT NULL
);

CREATE TABLE TB_ORDER_STATUS (
                                 ID_ORDER_STATUS SERIAL PRIMARY KEY,
                                 DS_ORDER_STATUS VARCHAR(20)
);

INSERT INTO
    TB_ORDER_STATUS (
    ID_ORDER_STATUS,
    DS_ORDER_STATUS)
VALUES
(3,'COMPLETE'),
(1,'DOING'),
(4,'CANCELED'),
(2,'DELIVERED');

CREATE TABLE TB_ORDER_TYPE (
                               ID_ORDER_TYPE SERIAL PRIMARY KEY,
                               DS_ORDER_TYPE VARCHAR(20)
);

INSERT INTO
    TB_ORDER_TYPE (
    ID_ORDER_TYPE,
    DS_ORDER_TYPE)
VALUES
(1,'DELIVERY'),
(2,'TABLE'),
(3,'DESK');

CREATE TABLE TB_ORDER (
                          ID_ORDER BIGSERIAL PRIMARY KEY,
                          DT_ORDER DATE NOT NULL,
                          VL_ORDER DECIMAL(8, 2) NOT NULL,
                          VL_DELIVERY DECIMAL(8, 2),
                          ID_TABLE INT,
                          ID_ORDER_STATUS INT NOT NULL,
                          ID_ORDER_TYPE INT NOT NULL,
                          ID_ORDER_ADDRESS BIGINT,
                          FOREIGN KEY(ID_TABLE) REFERENCES TB_TABLE(ID_TABLE),
                          FOREIGN KEY(ID_ORDER_STATUS) REFERENCES TB_ORDER_STATUS(ID_ORDER_STATUS),
                          FOREIGN KEY(ID_ORDER_TYPE) REFERENCES TB_ORDER_TYPE(ID_ORDER_TYPE),
                          FOREIGN KEY(ID_ORDER_ADDRESS) REFERENCES TB_ORDER_ADDRESS(ID_ORDER_ADDRESS)
);

CREATE TABLE TB_ORDER_ITEM (
                               ID_ORDER_ITEM BIGSERIAL PRIMARY KEY,
                               ID_PRODUCT INT NOT NULL,
                               ID_ORDER BIGINT NOT NULL,
                               QT_ORDER_ITEM SMALLINT NOT NULL,
                               VL_ORDER_ITEM DECIMAL(8, 2) NOT NULL,
                               FOREIGN KEY(ID_PRODUCT) REFERENCES TB_PRODUCT(ID_PRODUCT),
                               FOREIGN KEY(ID_ORDER) REFERENCES TB_ORDER(ID_ORDER)
);

CREATE TABLE TB_MENU (
                         ID_MENU SERIAL PRIMARY KEY,
                         NM_MENU VARCHAR(50),
                         DAY_OF_WEEK INT NOT NULL UNIQUE CHECK (DAY_OF_WEEK IN (1, 2, 3, 4, 5, 6, 7))
);

CREATE TABLE TB_MENU_ITEM (
                              ID_MENU BIGINT,
                              ID_PRODUCT INT,
                              FOREIGN KEY(ID_MENU) REFERENCES TB_MENU(ID_MENU),
                              FOREIGN KEY(ID_PRODUCT) REFERENCES TB_PRODUCT(ID_PRODUCT),
                              PRIMARY KEY(ID_MENU, ID_PRODUCT)
);

CREATE TABLE TB_ROLE (
                         ID_ROLE SERIAL PRIMARY KEY,
                         NM_ROLE VARCHAR(25) NOT NULL
);

INSERT INTO
    TB_ROLE(ID_ROLE, NM_ROLE)
VALUES(1, 'ROLE_USER'), (2, 'ROLE_ADMIN');

CREATE TABLE TB_USER (
                         ID_USER BIGSERIAL PRIMARY KEY,
                         NM_USER VARCHAR(75) NOT NULL,
                         DS_USERNAME VARCHAR(75) NOT NULL UNIQUE ,
                         DS_PASSWORD VARCHAR(500) NOT NULL,
                         ID_ROLE BIGINT NOT NULL,
                         FOREIGN KEY(ID_ROLE) REFERENCES TB_ROLE(ID_ROLE)
);

INSERT INTO TB_USER(ID_USER, NM_USER, DS_USERNAME, DS_PASSWORD, ID_ROLE)
VALUES(1, 'admin', 'admin', '$2a$10$K/XgssHrPZO0ED8s/zRiSO54CBIPz5bCWqrmKw1xce96ojtodP6bO', 2);




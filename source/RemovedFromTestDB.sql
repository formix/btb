
-- CREATE TABLE Contactable (
--     ContactableId  BIGINT NOT NULL,
--     Email          VARCHAR(256),
--     PhoneNumber    VARCHAR(20),
--     FaxNumber      VARCHAR(20),
--     Address1       VARCHAR(256),
--     Address2       VARCHAR(256),
--     PostalCode     VARCHAR(10),
--     ProvinceState  VARCHAR(50),
--     Country        VARCHAR(50),
--     CreatedDate    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     	PRIMARY KEY(id)
-- );


-- L'entit� Customer est contactable.
-- CREATE TABLE Customer (
--     CustomerId     BIGINT NOT NULL REFERENCES Contactable (ContactableId),
--     CustomerNumber VARCHAR(20) NOT,
--     Name           VARCHAR(50) NOT,
-- 
--     FOREIGN KEY(CustomerId) REFERENCES Contactable(ContactableId),
--     UNIQUE INDEX IX_Name (CustomerNumber),
--     PRIMARY KEY(id)
-- );
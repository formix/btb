#Bridge to Babylon

Bridge to Babylon is a lightweight object relational mapper. The
main goal of this library is simplicity. It is intended to be
used with POJOs without the need of external XML mapping
definition or any kind of annotation. This approache makes
Bridge to Babylon the best compromize between simplicity (no xml
mapping) and light library footprint: You don't use annotations
so you don't have to deploy Bridge to Babylon along your
entities' jar on remote clients.

**Moved from SourceForge**

##Installation
As a maven dependency:
```xml
<dependencies>
  <dependency>
	  <groupId>org.formix</groupId>
	  <artifactId>btb</artifactId>
	  <version>1.3.2</version>
  </dependency>
</dependencies>
```

##Create Your Database
When creating your database, you have to follow these simple naming rules for your table and column names:

1. The primary key name must be either "id" or "<table name>Id. (Ex: employeeId (for the table "Employee"))
2. Use pascal case for table names (Ex: EmployeeStore)
3. Use camel case for column names (Ex: createdDate)

Note that the second and third rules stated here are to be considered only if your DBMS is case sensitive.

Table Example (Derby syntax):
```sql
CREATE TABLE Employee (
  id           INT         NOT NULL GENERATED ALWAYS AS IDENTITY,
  firstName    VARCHAR(50) NULL,
  lastName     VARCHAR(50) NULL,
  wages        DOUBLE      NOT NULL,
    PRIMARY KEY(id)
);
```

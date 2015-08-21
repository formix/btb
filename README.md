#Bridge to Babylon

Bridge to Babylon is a lightweight object relational mapper. The
main goal of this library is simplicity. It is intended to be
used with POJOs without the need of external XML mapping
definition or any kind of annotation. This approache makes
Bridge to Babylon the best compromize between simplicity (no xml
mapping) and light library footprint: You don't use annotations
so you don't have to deploy Bridge to Babylon along your
entities' jar on remote clients.

**Moved from [SourceForge](https://sourceforge.net/projects/btb/)**

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

##Create Your Plain Old Java Object (POJO)

POJO Corresponding to the Employee table:

```java
public class Employee {

    private int     id;
    private String  firstName;
    private String  lastName;
    private double  wages;

    public Employee() {
        this.id        = -1;
        this.firstName = "";
        this.lastName  = "";
        this.wages     = 0;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setFirstName(String firstName) {
        this.name = name;
    }

    public String getFirstName() {
        return name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setWages(double wages) {
        this.wages = wages;
    }

    public double getWages() {
        return wages;
    }
}
```

So what do we have to say now? In fact, your are in right to ask yourself how
does Bridge to Babylon handles auto incremented primary keys. First of all, 
you must now that protected and private methods of your object's properties 
can be accessed by Bridge to Babylon. Secondly, BTB automatically fetch back 
from your database any read-only columns that your table may have upon insert 
and update operations.

In this particular case, you don't want the end user of the POJO to be able 
to directly change the "id" property because it's the database responsibility 
to do so. But BTB still need to access it through a method during a select or 
a fetch back operation. That's why the "setId" method have to be protected 
(or private).

##Get Connected to Your Database

Bridge to Babylon offers a class named ConnectionManager to help you handle 
any database connection you may need. By default, when you create an 
instance of this class, it looks for a file named "bridge.xml" in the 
application root folder. You can change this path by adding the command 
line argument "-Dbridge.properties.path=path to your property file" when 
launching your application.

Example of a minimal "bridge.properties" content:
```properties
database.url=jdbc:derby:data/testdb;create=true
```

As you can see, a ConnectionManager needs only one key to be functional. 
With this file in your application folder, any time you creates a Bridge 
you will be connected to a Derby database in the "data" folder, under 
your application directory. It is possible to handle more database 
connections with a ConnectionManager. For more information on how to 
handle multiple connections, please read the corresponding Javadoc.

##Use Bridge to Babylon

Now it's time to get to the real thing. Do I have to tell you to create 
your database and bridge.xml file prior executing the following snippet?

In any case, this is a simple insertion example:

```java
Employee e = new Employee();
e.setId(123);
e.setFirstName("John");
e.setLastName("Doe");
e.setWages(16.45);

Bridge<Employee> bridge = new Bridge<Employee>(Employee.class);
bridge.insert(e);
```

Now a simple query example returning all elements from a table:

```java
Bridge<Employee> bridge = new Bridge<Employee>(Employee.class);
Query<Employee> query = bridge.newQuery();
List<Employee> list = query.execute();
```

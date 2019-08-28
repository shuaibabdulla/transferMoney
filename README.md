
# TRANSFER MONEY API

This rest api consist of transfer the amount from one account to another account.The Api will handle the concurrency in the account transfer.
## Getting Started

For the dependency management purpose, MAVEN is used. <br/>
After the project has been cloned, in the project directory <br/>
"mvn test" and "mvn package" commands can be called. <br/>

### Prerequisites

java version 11.0.3 <br/>
MAVEN <br/>
This project uses port "7000" as default <br/>
please make sure that this port is not already in use.
If to change port it easy just change the port in the applicationPort value in the applicationConstant;

### Dependencies
javalin -> for rest api <br/>
jackson -> json marshall, un-marshall <br/>
testng  -> unit testing <br/>
unirest -> unit testing <br/>

### Installing

Run "mvn package"<br/>
Under the target directory "TransferMoneyAPI-<version>.jar" will be created.<br/>
This jar file contains all the necessary dependencies.<br/>
It can be directly called via "java -jar <jar_name>"<br/>
#####java -jar TransferMoneyAPI-1.0-SNAPSHOT.jar

## Running the tests

Calling "mvn test" command will eventually execute all the test cases.
There is also Postman collection is added ("TransferMoneyAPI.postman_collection.json").


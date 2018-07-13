# Access Log Parser
Access log parser is a java command line tool to parse an access log.

## Usage
The tool is executed with the following command:
```
java -cp "parser.jar" com.ef.Parser --accesslog=/path/to/file --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100
```
Make sure you have in the same directory of the jar the properties file named `parser.properties`. There you can configure the DataBase Access
## Parameters
The parameters are:
 - `accesslog` = Path to the access log file
 - `startDate` = The date we want to monitor. The format must be `yyyy-MM-dd.HH:mm:ss`
 - `duration` = We span of time we want to monitor beginning with `startDate`. It can be `hourly` or `daily`
 - `threshold` = The amount of request an IP address must exceed in the given time span to be blocked

## File format
The file is a pipe ('|') separated file. It must have the following format:
```
date_time(yyyy-MM-dd HH:MM:ss.SSS)|ip_address(String)|request(String)|status_code(Integer)|user_agent(String)
```

## Database Creation
If you need to create the database in your local environment, please, make sure you have installed MySQL 8. You will have to create a new schema.
Once you create the schema, find in the "dbScripts" folder, the "create-tables.sql" file and run it. Configure the "parser.properties" file to run with the new schema configuration.

## Database queries
You will find database queries in the "dbScripts folder"
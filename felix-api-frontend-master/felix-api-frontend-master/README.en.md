# Felix-API-Frontend

## Project introduction
The Felix-API interface opens the front-end code repository of the platform. This is a platform that provides API interface for developers to call.
Users can log in, register and activate the permission to call the API. The calls to the API will be counted and may be charged later.
Administrators can publish interfaces, offline interfaces, access interfaces, and online debugging interfaces.


## Project background

1. Front-end developers need to use back-end interfaces to obtain data.
   two。 There are many ready-made API interface calling platforms on the Internet.

Make an API interface platform by yourself:
1. Users can access the front desk, log in, register, enable and disable the permission to call the API.
   two。 The administrator can add, delete, modify and check the interface

Request:
1. Prevent attacks (security)
2. Cannot be called at will (restrict, activate)
3. Count the number of calls
4. Flow protection
5. API access

## Technology selection
### frontend
- Ant Design Pro
- React
- Ant Design Procomponents
- Umi
- Umi Request (encapsulation of Axios)

### backend
- Spring Boot
- Spring Cloud Gateway
- Dobbo
- Nacos
- Spring Boot Starter (SDK development)

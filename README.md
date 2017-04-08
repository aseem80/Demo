# Coding Assignment #

This assignment is attempted with Programming Language Java built as RestFul Micro WebService to achieve the required
functionality.

## Project Build instructions ##

This is spring boot application which runs Embedded Tomcat Container and requires minimum configuration

### PreRequisties to Build ###

1. JDK 1.8
2. Maven 3.3.9 (mvn clean install)
If run first time it will download many dependencies from Maven Central


### Running the application ###

Please make sure port 8080 is not in use where application is going to be run

The application can be run as per following:-
1. Command line (Using spring Boot Plugin):- mvn spring-boot:run
2. Command line (After jar is built with mvn clean install) :- java -jar Demo-1.0-SNAPSHOT.jar
3. Intellij/Eclipse (Run CodingAssignmentApp)

### GetAllTransactions DataSource ###

The application used a Rest Client to get All transactions and has been configured to Retry 3 times with Back-off-policy
in case of any IOException(Connect Timeout or ReadTime out)

### Rest End Points ###

1. Request HTTP GET :- http://localhost:8080/demo/v2/monthlySummary
 Sample Response :-
 {
   "2014-10": {
     "spent": "$15784400.00",
     "income": "$34297900.00"
   },
    "average": {
       "spent": "$34641270.00",
       "income": "$33181450.00"
       }
  }
  Average is calculated by adding monthly income and expense and dividing by number of periods

  2. Request HTTP GET:- http://localhost:8080/demo/v2/monthlySummary?ignore-category=donuts
  Query Param ignore-category=donuts will ignore any expense and refund from merchants described in requirements

  3. Request HTTP GET:- http://localhost:8080/demo/v2/monthlySummary?ignore-cc-payments=true
  Query Param ignore-cc-payments=true will ignore credit card payments from checking and credit card accounts based on
  criteria of amounts and 24 hrs period as per requirments

  4. Request HTTP GET:- http://localhost:8080/demo/v2/monthlySummary?ignore-cc-payments=true&ignore-category=donuts
  Both query params ignore-category=donuts and ignore-cc-payments=true are supported if provided together.

  5. Request HTTP GET:- http://localhost:8080/demo/v2/creditCardPayments
  This end point list all credit card payments detected (part of request in assignment)
  Sample Response(With one transaction couple) :-
  {
    "credit_card_payments": [
      {
        "amount": 5194500,
        "categorization": "Unknown",
        "merchant": "Credit Card Payment",
        "pending": false,
        "is-pending": false,
        "aggregation-time": 1415041080000,
        "account-id": "nonce:comfy-cc/hdhehe",
        "clear-date": 1415042820000,
        "transaction-id": "1415042820000",
        "raw-merchant": "CREDIT CARD PAYMENT",
        "transaction-time": "2014-11-03T18:58:00.000Z"
      },
      {
            "amount": -5194500,
            "categorization": "Unknown",
            "merchant": "CC Payment",
            "pending": false,
            "is-pending": false,
            "aggregation-time": 1415048280000,
            "account-id": "nonce:comfy-checking/hdhehe",
            "clear-date": 1415193660000,
            "transaction-id": "1415193660000",
            "raw-merchant": "CC PAYMENT",
            "transaction-time": "2014-11-03T20:58:00.000Z"
          }

     ]
   }

  6. Request HTTP GET:- http://localhost:8080/demo/v2/getProjectedTransactionsForMonth
  This end point was added to support projected transactions of current or any future month.
  Projected Transactions are done based on transactions in month prior to current month
  (for which data is available from API exposed). The reason of this month chosen is to be as close in time to income
  and spending habit.
  This end point supports query param as well(http://localhost:8080/demo/v2/getProjectedTransactionsForMonth?monthOfYear=2017-06).
  If query param monthOfYear is not provided it defaults to current month.Support format of value is "yyyy-dd"
  Sample response:-
  {
    "actual": [
      {
        "amount": -60100,
        "categorization": "Shopping",
        "merchant": "Dollar General 8286",
        "pending": false,
        "is-pending": false,
        "aggregation-time": 1491154520447,
        "account-id": "nonce:comfy-cc/hdhehe",
        "clear-date": 1491279300000,
        "transaction-id": "1491279300000",
        "raw-merchant": "DOLLAR GENERAL 8286",
        "transaction-time": "2017-04-02T00:00:00.000Z"
      },
      {
            "amount": -99900,
            "categorization": "Music",
            "merchant": "Spotify.com",
            "pending": false,
            "is-pending": false,
            "aggregation-time": 1491154520447,
            "account-id": "nonce:comfy-cc/hdhehe",
            "clear-date": 1491332400000,
            "transaction-id": "1491332400000",
            "raw-merchant": "Spotify.com",
            "transaction-time": "2017-04-02T00:00:00.000Z"
      }
    ],
   "expected": [
       {
         "amount": -313200,
         "categorization": "Shopping",
         "merchant": "Petsmart",
         "pending": false,
         "is-pending": false,
         "aggregation-time": 1491519600000,
         "account-id": "nonce:comfy-cc/hdhehe",
         "clear-date": null,
         "transaction-id": null,
         "raw-merchant": "PetSmart",
         "transaction-time": "2017-04-06T23:00:00.000Z"
       },
       {
         "amount": -860800,
         "categorization": "Phone",
         "merchant": "At&T Bill Payment",
         "pending": false,
         "is-pending": false,
         "aggregation-time": 1491606000000,
         "account-id": "nonce:comfy-checking/hdhehe",
         "clear-date": null,
         "transaction-id": null,
         "raw-merchant": "AT&T BILL PAYMENT",
         "transaction-time": "2017-04-07T23:00:00.000Z"
       }
      ]
   }









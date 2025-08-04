# request-corporation-tax-number-frontend

CT UTR is a microservice that interacts with Digital Mail Service / Robotics. The customer enters two peices of information, the Company Registration Number (CRN) and the company name. The CRN is used by robotics to check against the Corporation Tax HOD which will issue a hard copy CT UTR to the customer's registered company address. If robotics cannot process the files received from DMS, the case is sent to a worklist and manually worked by back office staff.

|Repositories|Link|
|------------|----|
|Backend|https://github.com/hmrc/request-corporation-tax-number|
|Stub|https://github.com/hmrc/request-corporation-tax-number-stubs|
|Acceptance tests|https://github.com/hmrc/request-corporation-tax-number-journey-tests|
|Performance tests|https://github.com/hmrc/request-corporation-tax-number-performance-test|

## Info

This service is also known as Ask for a copy of your Corporation Tax UTR

This project is a Scala web application using [code scaffolds](https://github.com/hmrc/hmrc-frontend-scaffold.g8)

Service has nginx limiter set, this is available in:
    https://github.com/hmrc/aws-ami-public-routing-proxy/tree/main/files/nginx/frontend-conf

Example for this is here: https://github.com/hmrc/aws-ami-public-routing-proxy/blob/5322a3f66bbb73d1034a61f71d93e4516ab2488b/files/nginx/frontend-conf/frontend-external-production.conf#L9

This is set via the location in: https://github.com/hmrc/mdtp-frontend-routes/blob/main/{environment}/frontend-proxy-application-rules.conf

---

## Running the service locally

You will need to have the following:

* Installed/configured [service manager 2](https://github.com/hmrc/sm2)
* Installed [MongoDB](https://www.mongodb.com/docs/manual/installation/)

The service manager profile for this service is: `REQUEST_CORPORATION_TAX_NUMBER_FRONTEND`

Use the following to run all the microservices and dependant services used for CT UTR:

`sm2 --start CTUTR_ALL`

If you want to run your local copy, then stop running the frontend service via the service manager and run your local code by using the following:

```
sm2 --start CTUTR_ALL
sm2 --stop REQUEST_CORPORATION_TAX_NUMBER_FRONTEND

cd request-corporation-tax-number-frontend
sbt run
```

### Routes

Access the service locally by going to: http://localhost:9200/ask-for-copy-of-your-corporation-tax-utr

| Url | Description |
|-------|---------------|
| /enter-company-details | main page for entering details |
| /check-your-answers | displays details users have entered |


---

## Testing the Service

This service uses [sbt-scoverage](https://github.com/scoverage/sbt-scoverage) to provide test coverage reports.

Run this script before raising a PR to ensure your code changes pass the Jenkins pipeline. This runs all the unit tests with checks for dependency updates:

```
./run_all_tests.sh
```

For the accessibility tests Node v12 or above is needed. Details can be found [here](https://github.com/hmrc/sbt-accessibility-linter). 

Run the tests with command:
```
sbt clean A11y/test
```

---

## License

This code is open source software licensed under the Apache 2.0 License.

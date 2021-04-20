# Request corporation tax number frontend

CT UTR is a microservice that interacts with Digital Mail Service / Robotics. The customer enters two peices of information, the Company Registration Number (CRN) and the company name. The CRN is used by robotics to check against the Corporation Tax HOD which will issue a hard copy CT UTR to the customer's registered company address. If robotics cannot process the files received from DMS, the case is sent to a worklist and manually worked by back office staff.

## Info

This service is also known as Ask for a copy of your Corporation Tax UTR

This project is a Scala web application using [code scaffolds](https://github.com/hmrc/hmrc-frontend-scaffold.g8)

Service has nginx limiter set, this is available in:
    https://github.com/hmrc/aws-ami-public-routing-proxy/tree/master/files/nginx/frontend-conf

Example for this is here: https://github.com/hmrc/aws-ami-public-routing-proxy/blob/5322a3f66bbb73d1034a61f71d93e4516ab2488b/files/nginx/frontend-conf/frontend-external-production.conf#L9

This is set via the location in: https://github.com/hmrc/mdtp-frontend-routes/blob/master/{environment}/frontend-proxy-application-rules.conf

## Running the service

Service Manager: CTUTR_ALL 

|Repositories|Link|
|------------|----|
|Backend|https://github.com/hmrc/request-corporation-tax-number|
|Stub|https://github.com/hmrc/request-corporation-tax-number-stubs|
|Performance tests|https://github.com/hmrc/request-corporation-tax-number-performance-test|
|Acceptance tests|https://github.com/hmrc/request-corporation-tax-number-journey-tests|

## Routes
-------
Start the service locally by going to http://localhost:9200/ask-for-copy-of-your-corporation-tax-utr

| *Url* | *Description* |
|-------|---------------|
| /enter-company-details | main page for entering details |
| /check-your-answers | displays details users have entered |

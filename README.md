# Frontend for the Ask for a copy of your Corporation Tax UTR service #
===============================================================================

Service Manager:    CTUTR_ALL
Backend repository: https://github.com/hmrc/request-corporation-tax-number
Performance tests:  https://github.com/hmrc/request-corporation-tax-number-performance-test

Service has nginx limiter set, this is available in;
    https://github.com/hmrc/aws-ami-public-routing-proxy/tree/master/files/nginx/frontend-conf

Example for this is here; https://github.com/hmrc/aws-ami-public-routing-proxy/blob/5322a3f66bbb73d1034a61f71d93e4516ab2488b/files/nginx/frontend-conf/frontend-external-production.conf#L9

This is set via the location in; https://github.com/hmrc/mdtp-frontend-routes/blob/master/{environment}/frontend-proxy-application-rules.conf

#Routes
-------
| Url | Description |
| /enter-company-details | main page for entering details |
| /check-your-answers | displays details users have entered |
# Ask for a copy of your Corporation Tax UTR service Frontend #

Service Manager:    CTUTR_ALL
Backend repository: https://github.com/hmrc/request-corporation-tax-number
Performance tests:  https://github.com/hmrc/request-corporation-tax-number-performance-test

Service has nginx limiter set, this is in Zone "ctutr" available in;
    https://github.com/hmrc/aws-ami-public-routing-proxy/tree/master/files/nginx/frontend-conf
Limited to 20 requests per minute (10 per proxy).
This is set via; location /ask-for-copy-of-your-corporation-tax-utr/check-your-answers
in; https://github.com/hmrc/mdtp-frontend-routes/blob/master/{environment}/frontend-proxy-application-rules.conf

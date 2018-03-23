#!/bin/bash

echo "Applying migration CompanyDetails"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /companyDetails                       controllers.CompanyDetailsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /companyDetails                       controllers.CompanyDetailsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeCompanyDetails                       controllers.CompanyDetailsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeCompanyDetails                       controllers.CompanyDetailsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "companyDetails.title = companyDetails" >> ../conf/messages.en
echo "companyDetails.heading = companyDetails" >> ../conf/messages.en
echo "companyDetails.field1 = Field 1" >> ../conf/messages.en
echo "companyDetails.field2 = Field 2" >> ../conf/messages.en
echo "companyDetails.checkYourAnswersLabel = companyDetails" >> ../conf/messages.en
echo "companyDetails.error.field1.required = Enter field1" >> ../conf/messages.en
echo "companyDetails.error.field2.required = Enter field2" >> ../conf/messages.en
echo "companyDetails.error.field1.length = field1 must be 40 characters or less" >> ../conf/messages.en
echo "companyDetails.error.field2.length = field2 must be 8 characters or less" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def companyDetails: Option[CompanyDetails] = cacheMap.getEntry[CompanyDetails](CompanyDetailsId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def companyDetails: Option[AnswerRow] = userAnswers.companyDetails map {";\
     print "    x => AnswerRow(\"companyDetails.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.CompanyDetailsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration CompanyDetails completed"

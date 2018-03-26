#!/bin/bash

echo "Applying migration FailedToSubmit"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /failedToSubmit                       controllers.FailedToSubmitController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "failedToSubmit.title = failedToSubmit" >> ../conf/messages.en
echo "failedToSubmit.heading = failedToSubmit" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration FailedToSubmit completed"

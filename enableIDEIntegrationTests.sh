#!/usr/bin/env bash

#This script will unzip the command and query raml files onto the
# integration-test target folder, so that the Integration Tests can be run from the IDE.

#Copy files
cp staging-command/staging-command-api/target/*raml.jar staging-integration-test/target/ ;
cp staging-query/staging-query-api/target/*raml.jar staging-integration-test/target/ ;
cp staging-event/staging-event-api/target/*raml.jar staging-integration-test/target/ ;
cp staging-event/staging-event-processor/target/*raml.jar staging-integration-test/target/ ;

#unzip files
unzip -o staging-integration-test/target/staging-command-api-*-raml.jar raml/* -d staging-integration-test/target/test-classes/;
unzip -o staging-integration-test/target/staging-query-api-*-raml.jar raml/* -d staging-integration-test/target/test-classes/;
unzip -o staging-integration-test/target/staging-event-api-*-raml.jar raml/* -d staging-integration-test/target/test-classes/;
unzip -o staging-integration-test/target/staging-event-processor-*-raml.jar raml/* -d staging-integration-test/target/test-classes/;

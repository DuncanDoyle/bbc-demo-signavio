'use strict';

angular.module('creditCardApplicationApp')
    .controller('applicationCtrl', function($scope, $http, $location, util, sharedStateService) {
        $scope.selectedCard = sharedStateService.getSelectedCard();
        //Reset the selected card.
        sharedStateService.setSelectedCard("");

        $scope.data = {};
        //Initialize the booleans in our model.
        $scope.data.application = {};
        $scope.data.application.bankruptcies = false;
        $scope.data.application.defaults = false;
        $scope.data.application.settlement = false;

        $scope.createApplication = function(application) {
            var url = util.getKieServerUrl()
                + "/kie-server/services/rest/server/containers/"
                + util.getCreditCardAppContainer()
                + "/processes/"
                + util.getCreditCardProcess()
                + "/instances";

            //JSON representation of the request. CreditCard type still needs to be bound to the model.
            var applicationVar = {
                input : {
                  fICO: application.fICO,
                  bankruptcies: application.bankruptcies,
                  medical: application.medical,
                  defaults: application.defaults,
                  rentOrMortgage: application.rentOrMortgage,
                  income: application.income,
                  settlement: application.settlement,
                  consumerDebt: application.consumerDebt
                },
                prospect : {
                  name: application.name,
                  email: application.email,
                  ssn: application.ssn
                },
                prospectComment : application.comment
            };

            //$http.defaults.headers.common.Authorization = 'Bearer ' + $scope.token;
            //Base64 encoded username and password for Basic Authentication.
            $http.defaults.headers.common.Authorization = 'Basic a2llc2VydmVyOmtpZXNlcnZlcjEh';
            $http.defaults.headers.common['Accept'] = "application/json";
            $http.defaults.headers.common['Content-type'] = "application/json";
            console.log("Sending request");
            $http.post(url, applicationVar)
                .success(function (data) {
                    $scope.data.result = data;
                })
                .error(function (error) {
                    $scope.data.error = {};
                    $scope.data.error.code = 'createCcApplication';
                    $scope.data.error.message = 'The application could not be submitted.'
                })
                .finally(function () {
                    $scope.data.ticket = {};
                });
        }

        $scope.reload = function() {
            $scope.data = {};
        }



    });
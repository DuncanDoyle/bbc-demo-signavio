/*
 * Main application controller.
 */
'use strict';

var app = angular.module("creditCardApplicationApp", ["ngRoute"]);

// Configure application routing
app.config(function($routeProvider) {
            console.log("Bootstrapping AngularJS RouteProvider")
            $routeProvider
                .when('/', {
                    templateUrl: 'views/main.html',
                    controller: 'mainCtrl',
                    controllerAs: 'main'
                })
                .when('/apply', {
                    templateUrl: 'views/apply.html',
                    controller: 'applyCtrl',
                    controllerAs: 'apply'
                })
                .when('/reviewSilverCard', {
                    templateUrl: 'views/reviewSilverCard.html',
                    // controller: 'reviewSilverCardCtrl',
                    // controllerAs: 'reviewSilverCard'
                })
                .when('/developGoldCard', {
                    templateUrl: 'views/developGoldCard.html',
                    // controller: 'developGoldCardCtrl',
                    // controllerAs: 'developGoldCard'
                })
                .when('/processInstances', {
                    templateUrl: 'views/processInstances.html',
                    controller: 'processInstancesCtrl',
                    controllerAs: 'processInstances'
                 })
                 .otherwise({
                    redirectTo: '/'
                 });
         })
        .config(function($httpProvider) {
            // Enable cross domain calls
            $httpProvider.defaults.useXDomain = true;
        })
        .service('sharedStateService', function() {
            var selectedCard;
            var currentProcessInstance;

            return {
                getSelectedCard: function() {
                    return selectedCard;
                },
                setSelectedCard: function(value) {
                    selectedCard = value;
                },
                getCurrentProcessInstance: function() {
                    return currentProcessInstance;
                },
                setCurrentProcessInstance: function(value) {
                    currentProcessInstance = value;
                }
            };
        })
        .factory("util", function() {
            return {
                getKieServerUrl: function() {
                    return "http://" + ENV.kieserver_host + ":" + ENV.kieserver_port;
                },
                getCreditCardAppContainer: function() {
                    return ENV.kieserver_containerId;
                },
                getCreditCardProcess: function() {
                    return ENV.kieserver_processId;
                }
            }
        });

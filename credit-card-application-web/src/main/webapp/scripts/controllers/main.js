'use strict';

angular.module('creditCardApplicationApp')
    .controller('mainCtrl', function($scope, sharedStateService) {
      $scope.selectedCard = "";

      $scope.selectCard = function(card) {
        console.log("Selecting card: " + card);
        $scope.selectedCard = card;
        sharedStateService.setSelectedCard(card);
      };

    });

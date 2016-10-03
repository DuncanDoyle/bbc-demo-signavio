'use strict';

angular.module('creditCardApplicationApp')
    .controller('mainCtrl', function($scope, sharedStateService) {

        var selectedCard;

        $(document).ready(function($scope) {
            // Card Single Select
            $('.card-pf-view-single-select').click(function() {
                if ($(this).hasClass('active')) {
                    $(this).removeClass('active');
                } else {
                    //Let's see if we can get the id.
                    $('.card-pf-view-single-select').removeClass('active');
                    $(this).addClass('active');
                    //We use the id of the card-node to determine the redirect.
                    selectedCard = $(this).context.id;

                }
                console.log("Selected Card:"  + selectedCard);
                sharedStateService.setSelectedCard(selectedCard);
            });

        });
    });

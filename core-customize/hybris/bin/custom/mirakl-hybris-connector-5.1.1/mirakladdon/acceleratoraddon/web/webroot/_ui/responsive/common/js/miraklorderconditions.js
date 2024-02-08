ACC.miraklorderconditions = {

    _autoload: [
        ["bindOfferQuantitySelectors", $(".js-offer-qty-selector").length > 0]
    ],

    bindOfferQuantitySelectors: function () {

        $elements = $(".js-offer-qty-selector");

        $elements.each(function () {

            var minQty = $(this).data("min");
            var maxQty = $(this).data("max");
            var step = $(this).data("step");
            var validation = $(this).data("validation") === true;
            var effectiveMaxQty = maxQty - maxQty % step;
            var effectiveMinQty = minQty % step === 0 ? minQty : minQty + step - minQty % step;

            var minQtyMessage = ACC.addons.mirakladdon["order.condition.min.qty.message"].replace("{0}", minQty);
            var maxQtyMessage = ACC.addons.mirakladdon["order.condition.max.qty.message"].replace("{0}", maxQty);
            var stepQtyMessage = ACC.addons.mirakladdon["order.condition.step.qty.message"].replace("{0}", step);
            var invalidQtyMessage = ACC.addons.mirakladdon["order.condition.invalid.qty.message"];

            var $qtyInput = $(this).find(".js-offer-qty-input");
            var $qtyMinus = $(this).find(".js-offer-qty-minus");
            var $qtyPlus = $(this).find(".js-offer-qty-plus");
            var $qtyForm = $(this).find(".add_to_cart_form");
            var $submitBtn = $qtyForm.find("button[type=submit], input[type=submit]").first();
            var $qtyHiddenField = $qtyForm.find(".js-qty-selector-input").first();
            var $infoElement = $("<div class='order-condition-info'>&nbsp;</div>");

            if (maxQty < minQty && validation) {
                $qtyInput.val(0);
                ACC.miraklorderconditions.disableButton($qtyMinus, true);
                ACC.miraklorderconditions.disableButton($qtyPlus, true);
                ACC.miraklorderconditions.disableButton($submitBtn, true);
                ACC.miraklorderconditions.disableButton($qtyInput, true);
                return;
            }

            $qtyInput.before($infoElement);
            var leftOffset = Math.round($qtyInput.outerWidth() / 2);
            $infoElement.hide();
            $qtyInput.val(effectiveMinQty);
            $qtyHiddenField.val(effectiveMinQty);
            ACC.miraklorderconditions.disableButton($qtyMinus, validation);

            $qtyInput.on("input", function (e) {

                if (!validation) {
                    $qtyHiddenField.val($qtyInput.val());
                    return;
                }

                // Validate numeric input
                if (!$qtyInput.val().match(/^[0-9]+$/)) {
                    ACC.miraklorderconditions.disableButton($qtyMinus, true);
                    ACC.miraklorderconditions.disableButton($qtyPlus, true);
                    ACC.miraklorderconditions.disableButton($submitBtn, true);
                    ACC.miraklorderconditions.displayTooltip($infoElement, leftOffset, invalidQtyMessage);
                    return;
                }

                // If the order conditions are not verified, we lock the buy button
                var qty = parseInt($qtyInput.val(), 10);
                if (qty < minQty) {
                    $submitBtn.prop("disabled", true);
                    ACC.miraklorderconditions.displayTooltip($infoElement, leftOffset, minQtyMessage);
                } else if (qty > maxQty) {
                    $submitBtn.prop("disabled", true);
                    ACC.miraklorderconditions.displayTooltip($infoElement, leftOffset, maxQtyMessage);
                } else if (qty % step !== 0) {
                    $submitBtn.prop("disabled", true);
                    ACC.miraklorderconditions.displayTooltip($infoElement, leftOffset, stepQtyMessage);
                } else {
                    $submitBtn.prop("disabled", false);
                    $infoElement.fadeOut();
                }

                // Enable/Disable the + and - buttons
                if (qty >= effectiveMaxQty) {
                    ACC.miraklorderconditions.disableButton($qtyPlus, true);
                    ACC.miraklorderconditions.disableButton($qtyMinus, false);
                } else if (qty <= effectiveMinQty) {
                    ACC.miraklorderconditions.disableButton($qtyPlus, false);
                    ACC.miraklorderconditions.disableButton($qtyMinus, true);
                } else {
                    ACC.miraklorderconditions.disableButton($qtyPlus, false);
                    ACC.miraklorderconditions.disableButton($qtyMinus, false);
                }

                $qtyHiddenField.val(qty)
            });

            $qtyMinus.on("click", function (e) {
                e.preventDefault();
                var qty = parseInt($qtyInput.val(), 10);
                if (qty > effectiveMaxQty) {
                    $qtyInput.val(effectiveMaxQty);
                } else {
                    var qtyToRemove = qty % step !== 0 ? qty % step : step;
                    $qtyInput.val(qty - qtyToRemove);
                }
                $qtyInput.trigger("input");
            });

            $qtyPlus.on("click", function (e) {
                var qty = parseInt($qtyInput.val(), 10);
                e.preventDefault();
                $qtyInput.val(qty < effectiveMinQty ? effectiveMinQty : qty + step - qty % step);
                $qtyInput.trigger("input");
            });
        });

    },

    disableButton: function ($button, disabled) {
        if ($button != null) {
            $button.prop("disabled", disabled);
        }
    },

    displayTooltip: function ($tooltip, leftOffset, message) {
        $tooltip.html("<span class='glyphicon glyphicon-exclamation-sign text-primary'></span> " + message);
        $tooltip.fadeIn();
        $tooltip.css("margin-top", "-" + ($tooltip.outerHeight() + 7) + "px");
        $tooltip.css("margin-left", "-" + (Math.round($tooltip.outerWidth() / 2) - leftOffset) + "px");
    }


};

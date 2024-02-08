ACC.miraklratingstars = {

    _autoload: [
        ["bindRatingStars", $(".js-miraklRatingCalc").length > 0],
        ["bindRatingStarsSet", $(".js-miraklRatingCalcSet").length > 0]
    ],

    bindRatingStars: function () {

        $elements = $(".js-miraklRatingCalc");

        $elements.each(function () {
            var $ratingData = $(this).data("rating");
            var $ratingIcon = $(this).find(".js-miraklRatingIcon");

            var rating = $ratingData.rating;
            var maxRating = $ratingData.total;

            var fullIconCount = Math.floor(rating);
            var emptyIconCount = maxRating - Math.ceil(rating);

            $ratingIcon.removeClass("js-miraklRatingIcon");

            //Displaying full icons
            for (var i = 0; i < fullIconCount && i < maxRating; i++) {
                $ratingIcon.clone()
                    .addClass("active")
                    .insertBefore($ratingIcon);
            }

            if (fullIconCount + emptyIconCount < maxRating) {
                //Displaying active half icon
                var iconFirstHalfValue = rating - fullIconCount;
                $ratingIcon.clone()
                    .addClass("active")
                    .css("width", iconFirstHalfValue + "em")
                    .css("margin-right", "0px")
                    .insertBefore($ratingIcon);

                //Displaying inactive half icon
                var iconSecondHalfValue = 1 - iconFirstHalfValue;
                $ratingIcon.clone()
                    .css("width", iconSecondHalfValue + "em")
                    .css("text-indent", -iconFirstHalfValue + "em")
                    .css("margin-left", "0px")
                    .insertBefore($ratingIcon);
            }

            //Displaying empty icons
            for (var i = 0; i < emptyIconCount && i < maxRating; i++) {
                $ratingIcon.clone().insertBefore($ratingIcon);
            }

            //Removing the icon sample
            $ratingIcon.remove();
        })
    },

    bindRatingStarsSet: function () {

        $e = $(".js-miraklRatingCalcSet");

        $e.on("mouseenter", ".js-miraklRatingIconSet", function (e) {
            e.preventDefault();
            $(this).parent().children().removeClass("active");
            var cIndex = $(this).index() + 1;
            var $i = $(this).parent().children(".js-miraklRatingIconSet:lt(" + cIndex + ")");
            $i.addClass("active");
        });

        $(document).on("mouseleave", ".js-miraklRatingCalcSet", function (e) {
            e.preventDefault();
            $(this).find(".js-miraklRatingIconSet").removeClass("active");
            var rating = $(this).parent().find(".js-miraklRatingSetInput").val();
            var $i = $(this).find(".js-miraklRatingIconSet:lt(" + rating+ ")");
            $i.addClass("active");
        });

        $e.on("click", ".js-miraklRatingIconSet", function (e) {
            e.preventDefault();
            var ratingData = $(this).parents().eq(1).data("rating");
            var cIndex = $(this).index() + 1;
            ratingData.rating = cIndex;
            
            $(this).parents().eq(2).find(".js-miraklRatingSetInput").val(ratingData.rating);
        });

        $e.each(function () {
            var ratingData = $(this).data("rating");
            var $ratingIcon = $(this).find(".js-miraklRatingIcon");

            for (var i = 1; i <= ratingData.total; i++) {
                var $clone = $ratingIcon.clone().removeClass("js-miraklRatingIcon");
                $clone.insertBefore($ratingIcon);
            }
            // delete the template icon
            $ratingIcon.remove();

            //Initial Display
            $(this).find(".js-miraklRatingIconSet").removeClass("active");
            var rating = $(this).parent().find(".js-miraklRatingSetInput").val();
            var $i = $(this).find(".js-miraklRatingIconSet:lt(" + rating+ ")");
            $i.addClass("active");
        })
    }

};

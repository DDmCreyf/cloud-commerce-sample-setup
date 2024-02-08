ACC.mirakltabs = {
    _autoload: [
        ["bindTabs", $(".js-tabs").length > 0]
    ],

    evaluationTab: {
        currentPage: -1,
        evaluationUrl: ""
    },

    bindTabs: function () {

        $e = $(".js-tabs");

        $e.on("click", "#mirakltabreviews", function (e) {
            e.preventDefault();
            ACC.mirakltabs.setEvaluationTabData();
            ACC.mirakltabs.loadReviews(ACC.mirakltabs.evaluationTab.currentPage);
        });

        ACC.mirakltabs.bindButtons();

    },

    bindButtons: function() {

        $e = $(".js-tabs");

        $e.on("click", ".pagination li a", function (e) {
            e.preventDefault();
        });

        $e.on("click", ".page-list:not(.active) .page-link", function (e) {
            ACC.mirakltabs.loadReviews($(e.target).html());
        });

        $e.on("click", ".pagination-next:not(.disabled)", function (e) {
            ACC.mirakltabs.loadReviews(ACC.mirakltabs.evaluationTab.currentPage + 1);
        });

        $e.on("click", ".pagination-prev:not(.disabled)", function (e) {
            $(e.target).addClass("disabled");
            ACC.mirakltabs.loadReviews(ACC.mirakltabs.evaluationTab.currentPage - 1);
        });
    },

    setEvaluationTabData: function () {
        if (ACC.mirakltabs.evaluationTab.currentPage == -1) {
            ACC.mirakltabs.evaluationTab.currentPage = 1;
            ACC.mirakltabs.evaluationTab.evaluationUrl = $("#reviewsbody").data("reviewurl");
        }
    },

    loadReviews: function (page) {
        var url = ACC.mirakltabs.evaluationTab.evaluationUrl + page;
        $.get(url, function (result) {
            if (result != "") {
                $('#reviewsbody').html(result);
                ACC.miraklratingstars.bindRatingStars();
                ACC.mirakltabs.evaluationTab.currentPage = parseInt(page);
            }
        });
    }
};

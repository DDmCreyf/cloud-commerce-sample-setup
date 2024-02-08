ACC.miraklpopups = {

    _autoload: [
        ["bindIncidentPopupLinks", $(".js-incidentPopup").length > 0],
        ["bindDocumentsPopupLinks", $(".js-documentsPopup").length > 0]
    ],

    bindIncidentPopupLinks: function () {

        $elements=$(".js-incidentPopup");

        $(document).on("click", ".js-incidentPopup", function (e) {
            e.preventDefault();
            var title = $(this).html();
            var url = $(this).data("incidenturl");
            ACC.colorbox.open(title, {
                href: url,
                width: "460px"
            });
        });

        $elements.each(function() {
            $(this).removeClass("text-muted");
        });

    },

    bindDocumentsPopupLinks: function () {

        $elements = $(".js-documentsPopup");

        $(document).on("click", ".js-documentsPopup", function (e) {
            e.preventDefault();
            var title = $(this).data("popuptitle");
            var contentId = $(this).data("documentslist");
            var content = $("#" + contentId).html();
            ACC.colorbox.open(title, {
                html: content,
                width: "600px"
            });
            ACC.miraklpopups.bindDocumentsPopupDownloadLinks();
        });

        $elements.each(function () {
            $(this).removeClass("text-muted");
        });

    },

    bindDocumentsPopupDownloadLinks: function () {

        $elements = $("#colorbox .document-line");

        $(document).on("click", "#colorbox .document-line", function (e) {
            e.preventDefault();
            window.location.href = $(this).data("href");
        });

    }

};

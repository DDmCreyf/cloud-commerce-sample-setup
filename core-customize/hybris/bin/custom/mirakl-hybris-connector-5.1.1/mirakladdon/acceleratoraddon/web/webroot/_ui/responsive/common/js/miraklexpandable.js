ACC.miraklexpandable = {

    _autoload: [
        ["bindExpandButtons", $(".js-expand-button").length > 0]
    ],

    bindExpandButtons: function () {

        $elements = $(".js-expand-button");

        $elements.each(function () {
            var contentId = this.attributes["data-expandable"].value;
            var $contentElement = $("#" + contentId);
            if ($contentElement.hasClass("collapsed")) {
                $(this).append("&nbsp;<span>&#9656;</span>");
            } else {
                $(this).append("&nbsp;<span>&#9662;</span>");
            }
        });

        $(document).on("click", ".js-expand-button", function (e) {
            e.preventDefault();
            var expandButton = e.target;
            while (!expandButton.classList.contains("js-expand-button")) {
                expandButton = expandButton.parentElement;
            }
            var contentId = expandButton.attributes["data-expandable"].value;
            var arrowElement = expandButton.lastChild;
            var $contentElement = $("#" + contentId);
            if ($contentElement.hasClass("collapsed")) {
                $contentElement.css("max-height", $contentElement.prop('scrollHeight') + 'px');
                $contentElement.removeClass("collapsed");
                arrowElement.innerHTML = "&#9662;";
            } else {
                $contentElement.css("max-height", '0px');
                $contentElement.addClass("collapsed");
                arrowElement.innerHTML = "&#9656;";
            }
        });

    }
};

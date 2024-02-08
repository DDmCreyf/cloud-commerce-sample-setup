ACC.miraklthreadinbox = {

	_autoload: [
		["bindLoadMoreButton", $("#nextPageToken").length > 0]
	],

	spinner: $("<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />"),

	bindLoadMoreButton: function () {
		$('#more-button').on('click', function (event) {
			event.preventDefault();
			var token = $('#nextPageToken').val();
			var consignmentCode = $('#consignmentCode').val();

			$.ajax({
				url: ACC.config.encodedContextPath + "/my-account/inbox?pageToken=" + encodeURIComponent(token) + (consignmentCode ? ("&consignmentCode=" + encodeURIComponent(consignmentCode)) : ''),
				type: 'GET',
				beforeSend: function () {
					$('#threadlist-content').block({ message: ACC.miraklthreadinbox.spinner });
				},
				success: function (result) {
					var data = JSON.parse(result);
					var newPage = data.threadPageContent;
					$('#thread-list').append(newPage);
					var nextPageToken = data.nextPageToken;
					if (nextPageToken && nextPageToken !== '') {
						$('#nextPageToken').val(nextPageToken);
					} else {
						$('#more-button').hide();
					}
				},
				complete: function () {
					$('#threadlist-content').unblock();
				}
			});
		});
	}


}

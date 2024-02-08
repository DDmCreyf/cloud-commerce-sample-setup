ACC.miraklthreaddetails = {

	_autoload: [
		["bindUploadFileButton", $(".thread-attachments").length > 0],
		["changeFileUploadAppearance", $(".js-attachment-upload").length != 0],
		["bindTopicValue", $("#topicCodeOther").length != 0]
	],

	spinner: $("<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />"),

	changeFileUploadAppearance: function () {
		var filesToUpload = [];

		$('.js-attachment-upload__input').on('change', function () {
			var files = (this.files);
			for (var index = 0; index < files.length; index++) {
				var file = files[index];
				var fileId = ACC.miraklthreaddetails.generateFileId();
				var fileSize = ACC.miraklthreaddetails.calculateHumanReadableFileSize(file);
				$('#attachment-files').append($('#file-attachement-template').tmpl({ fileName: file.name, fileId: fileId, fileSize: fileSize }));
				filesToUpload.push({
					id: fileId,
					file: file
				});
			}

			$('.remove-attachment').on('click', function (event) {
				event.preventDefault();
				ACC.miraklthreaddetails.clearGlobalAlerts();
				var fileId = $(this).data("fileid");
				for (var i = 0; i < filesToUpload.length; ++i) {
					if (filesToUpload[i].id === fileId) {
						filesToUpload.splice(i, 1);
						break;
					}
				}
				$(this).parent().remove();
			});
		});

		$('#topicCode').on('change', function () {
			var topicValue = ACC.miraklthreaddetails.bindTopicValue();
			if (topicValue === 'visible') {
				$('#topicValue').focus();
			}

		});

		$('#threadMessageForm').on('submit', function (event) {
			event.preventDefault();

			ACC.miraklthreaddetails.clearGlobalAlerts();
			if (!ACC.miraklthreaddetails.isSelectedFilesValid(filesToUpload)) {
				return false;
			}

			var threadMessageForm = document.getElementById('threadMessageForm');
			formData = new FormData(threadMessageForm);
			for (var i = 0, len = filesToUpload.length; i < len; i++) {
				formData.append("files", filesToUpload[i].file);
			}

			$.ajax({
				url: threadMessageForm.action,
				type: 'POST',
				data: formData,
				contentType: false,
				processData: false,
				beforeSend: function () {
					$('#threadMessageForm').block({ message: ACC.miraklthreaddetails.spinner });
				},
				success: function (res) {
					if (res.submittedSuccessfully) {
						var url = ACC.config.encodedContextPath + res.threadPageUrl;
						window.location.replace(url);
					} else if (!res.validated) {
						$.each(res.errorMessages, function (key, value) {
							var inputs = $('input[name=' + key + ']:visible');
							var textareas = $('textarea[name=' + key + ']:visible');
							var selects = $('select[name=' + key + ']:visible');

							if (inputs.length > 0) {
								inputs.addClass('thread-input-error').after('<span class="thread-error-message">' + value + '</span>');
								inputs.on('change', function (event) {
									$(this).removeClass('thread-input-error');
									$(this).next($('.thread-error-message')).remove();
								});
							} else if (textareas.length > 0) {
								textareas.addClass('thread-input-error').after('<span class="thread-error-message">' + value + '</span>');
								textareas.on('change', function (event) {
									$(this).removeClass('thread-input-error');
									$(this).next($('.thread-error-message')).remove();
								});
							} else if (selects.length > 0) {
								selects.addClass('thread-input-error').after('<span class="thread-error-message">' + value + '</span>');
								selects.on('change', function (event) {
									$(this).removeClass('thread-input-error');
									$(this).next($('.thread-error-message')).remove();
								});
							}
						});
					}
					if (res.globalErrorMessage) {
						ACC.miraklthreaddetails.displayGlobalAlert({ type: 'error', message: res.globalErrorMessage });
					}

				},
				complete: function () {
					$('#threadMessageForm').unblock();
				}
			});

		});
	},

	bindTopicValue: function () {
		var topicCodeOtherElement = $('#topicCodeOther');
		if (topicCodeOtherElement) {
			var topicCodeOther = topicCodeOtherElement.val();
			var topicCode = $('#topicCode').val();
			var errorMessageElement = $('#topicValue').parent().children('.thread-error-message');
			if (topicCode && topicCode !== '' && topicCode !== topicCodeOther) {
				$('#topicCodeDisplayValue').val($("#topicCode option:selected").text());
				$('#topicValue').hide();
				if (errorMessageElement.length > 0) {
					errorMessageElement.hide();
				}
				return 'hidden';
			} else if (topicCode === topicCodeOther) {
				$('#topicValue').show();
				if (errorMessageElement.length > 0) {
					errorMessageElement.show();
				}
				return 'visible';
			}
		}
	},

	bindUploadFileButton: function () {
		$('#chooseFileButton').on('click', function (event) {
			ACC.miraklthreaddetails.clearGlobalAlerts();
		});

	},

	isSelectedFilesValid: function (selectedFiles) {
		if (window.File && window.Blob && selectedFiles) {
			var totalSelectedSize = 0;
			for (var index = 0; index < selectedFiles.length; index++) {
				totalSelectedSize += selectedFiles[index].file.size;
			}
			var fileMaxSize = $('.js-attachment-upload__input').data('file-max-size');
			if ($.isNumeric(fileMaxSize)) {
				if (totalSelectedSize > parseFloat(fileMaxSize)) {
					ACC.miraklthreaddetails.displayGlobalAlert({ type: 'error', messageId: 'import-attachment-file-max-size-exceeded-error-message' });
					return false;
				}
			}
		}
		return true;
	},

	displayGlobalAlert: function (options) {
		ACC.miraklthreaddetails.clearGlobalAlerts();
		var alertTemplateSelector = '#global-alert-danger-template';

		if (typeof options.messageId != 'undefined') {
			$('#attach-file-alerts').append($(alertTemplateSelector).tmpl({ message: $(document).find('#' + options.messageId).text() }));
		}
		if (typeof options.message != 'undefined') {
			$('#attach-file-alerts').append($(alertTemplateSelector).tmpl({ message: options.message }));
		}

		$(".closeAccAlert").on("click", function () {
			$(this).parent('.getAccAlert').remove();
		});
	},

	clearGlobalAlerts: function () {
		$('#attach-file-alerts').empty();
		$('.thread-error-message').remove();
		$('.thread-input-error').removeClass('thread-input-error');
	},

	clearChosenFile: function () {
		document.getElementById('attachments').value = '';
		$('#attachment-files').empty();
		$('.remove-attachment').hide();
	},

	generateFileId: function () {
		return '_' + Math.random().toString(36).substr(2, 9);
	},

	calculateHumanReadableFileSize: function (file) {
		var size = file.size;
		var i = Math.floor(Math.log(size) / Math.log(1000));
		return (size / Math.pow(1000, i)).toFixed(2) * 1 + ' ' + ['B', 'kB', 'MB', 'GB', 'TB'][i];
	}

}

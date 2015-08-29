app.service('DiffService', function(LineChange) {
	this.computeDiff = function(bug, callback) {
		LineChange.query({tracker: bug.issueTracker.name, key: bug.key}, function(lineChanges) {
			var currentFile = null;
			var currentOldLineNumber = null;
			var currentChange = null;
			var result = {
				'files': []
			};

			for (var i = 0; i < lineChanges.length; i++) {
				var lineChange = lineChanges[i];

				if (currentFile === null || currentFile.fileName !== lineChange.fileName) {
					if (currentChange !== null) {
						currentFile.changes.push(currentChange);
					}

					currentOldLineNumber = null;
					currentChange = null;
					currentFile = {
						'fileName': lineChange.fileName,
						'changes': []
					};

					result.files.push(currentFile);
				}

				if (lineChange.kind === 'DELETION') {
					if (currentChange !== null) {
						currentFile.changes.push(currentChange);
					}

					currentChange = {
						'deletion': lineChange,
						'addition': null
					};
				}

				if (lineChange.kind === 'ADDITION') {
					if (currentChange !== null) {
						currentChange.addition = lineChange;
					} else {
						currentChange = {
							'deletion': null,
							'addition': lineChange
						}
					}

					currentFile.changes.push(currentChange);
					currentChange = null;
				}
			}
			
			callback(result);
		});
	};
});

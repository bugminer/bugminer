app.service('DiffService', function(LineChange) {
	this.computeDiff = function(bug, callback) {
		var result = {};

		LineChange.query({tracker: bug.issueTracker.name, key: bug.key}, function(lineChanges) {
			result.files = [];
			var currentFileName = '';
			var currentFile = null;
			var currentLine = {
				additions: [],
				deletions: []
			};
			var currentHunk = {
				lines: [currentLine],
				lastLineNumber: -1
			};


			for (var i = 0; i < lineChanges.length; i++) {
				var currentLineChange = lineChanges[i];
				var oldLineNumber = currentLineChange.oldLineNumber;

				// if file name changed then start next file
				if (currentFileName !== currentLineChange.fileName) {
					currentFileName = currentLineChange.fileName;

					currentFile = {
						name: currentFileName,
						hunks: []
					};

					result.files.push(currentFile);
				}

				// if line number of this line change is greater than the last + 1
				// this line change belongs to the next hunk
				if (oldLineNumber > currentHunk.lastLineNumber + 1) {
					var currentLine = {
						additions: [],
						deletions: []
					};
					var currentHunk = {
						lines: [currentLine],
						lastLineNumber: 0
					};

					currentFile.hunks.push(currentHunk);
				} else if (oldLineNumber > currentHunk.lastLineNumber) {
					var currentLine = {
						additions: [],
						deletions: []
					};

					currentHunk.lines.push(currentLine);
				}

				if (currentLineChange.kind === 'ADDITION') {
					currentLine.additions.push(currentLineChange);
					currentHunk.lastLineNumber = oldLineNumber;
				} else {
					currentLine.deletions.push(currentLineChange);
					currentHunk.lastLineNumber = oldLineNumber;
				}
			}
			console.log(result);
			callback(result);
		});
	};
});

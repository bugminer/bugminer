package de.unistuttgart.iste.rss.bugminer.computing.vagrant;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import de.unistuttgart.iste.rss.bugminer.model.NodeStatus;

@Component
public class VagrantStatusParser {
	private static final String STATUS_LINE_PATTERN = ".*([\\S]+)\\s+([a-zA-Z]+).*";

	private static final Map<String, NodeStatus> STATUS_STRINGS =ImmutableMap.of(
			"poweroff", NodeStatus.OFFLINE,
			"saved", NodeStatus.OFFLINE,
			"running", NodeStatus.ONLINE);

	/**
	 * Extracts the status of the output of a call to `vagrant status`. Assumes that there is only
	 * one machine
	 *
	 * @param output the output of `vagrant status`
	 * @return the status as string
	 */
	public NodeStatus parseStatusOutput(String output) {
		String[] lines = output.split("\n");
		boolean isStatus = false;
		for (String line : lines) {
			if (line.contains("Current machine states:")) {
				isStatus = true;
			} else if (isStatus) {
				Matcher matcher = Pattern.compile(STATUS_LINE_PATTERN).matcher(line);
				if (matcher.matches()) {
					return parseStatusString(matcher.group(2)); // this is the status group
				}
			}
		}
		return NodeStatus.UNKNOWN;
	}

	public NodeStatus parseStatusString(String status) {
		if (!STATUS_STRINGS.containsKey(status))
			return NodeStatus.UNKNOWN;
		return STATUS_STRINGS.get(status);
	}
}

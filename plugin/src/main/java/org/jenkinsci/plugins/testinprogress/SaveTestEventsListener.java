package org.jenkinsci.plugins.testinprogress;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.plugins.testinprogress.events.build.BuildTestEvent;
import org.jenkinsci.plugins.testinprogress.events.build.IBuildTestEventListener;
import org.jenkinsci.plugins.testinprogress.events.run.RunEndEvent;
import org.jenkinsci.plugins.testinprogress.events.run.RunStartEvent;

/**
 * Save the test events when they occur 
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 */
public class SaveTestEventsListener implements IBuildTestEventListener {
	private final static Logger LOG = Logger
			.getLogger(SaveTestEventsListener.class.getName());

	private final File directory;
	private ConcurrentMap<String, FileWriter> fileWriters = new ConcurrentHashMap<String, FileWriter>();

	public SaveTestEventsListener(File directory) {
		this.directory = directory;
	}

	private void saveEvent(BuildTestEvent runTestEvent) {
		try {
			if (runTestEvent.getRunTestEvent() instanceof RunStartEvent) {
				FileWriter fileWriter = new FileWriter(new File(directory,
						runTestEvent.getRunId() + ".events"));
				fileWriters.put(runTestEvent.getRunId(), fileWriter);
			}
			FileWriter fileWriter = fileWriters.get(runTestEvent.getRunId());
			if (fileWriter == null) {
				// should never happen
				return;
			}
			fileWriter
					.write(runTestEvent.getRunTestEvent().toString(true) + '\n');
			if (runTestEvent.getRunTestEvent() instanceof RunEndEvent) {
				fileWriter.close();
				fileWriters.remove(runTestEvent.getRunId());
			}
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Cannot save junit event", e);
		}
	}

	public void init() throws IOException {
		directory.mkdirs();
	}

	public void destroy() {
		for (FileWriter fileWriter : fileWriters.values()) {
			try {
				fileWriter.close();
			} catch (IOException e) {
				LOG.log(Level.WARNING, "Cannot close junit events file", e);
			}
		}
		fileWriters.clear();
	}

	public void event(BuildTestEvent runTestEvent) {
		saveEvent(runTestEvent);
	}

}
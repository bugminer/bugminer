package de.unistuttgart.iste.rss.bugminer.utils;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionWrapper {
	@Transactional
	public void runInTransaction(ThrowingRunnable runnable) throws Exception {
		runnable.run();
	}
}

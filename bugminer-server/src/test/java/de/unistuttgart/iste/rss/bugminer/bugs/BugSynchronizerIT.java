package de.unistuttgart.iste.rss.bugminer.bugs;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import de.unistuttgart.iste.rss.bugminer.TestConfig;
import de.unistuttgart.iste.rss.bugminer.config.EntityFactory;
import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.Classification;
import de.unistuttgart.iste.rss.bugminer.model.repositories.BugRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@SpringApplicationConfiguration(classes = TestConfig.class)
public class BugSynchronizerIT extends AbstractTransactionalJUnit4SpringContextTests {

	private static final String BUG_KEY_2 = "Key2";

	private static final String BUG_KEY = "Key1337";

	private static final String BUG_OLD_DESCR = "Old description of bug";

	private static final String BUG_NEW_DESCR = "New description of bug";

	@Autowired
	BugRepository bugRepo;

	@Autowired
	BugSynchronizer synchronizer;

	@Autowired
	EntityFactory factory;

	Collection<Bug> bugsToSynchronize;


	@Before
	public void init() {
		Bug bugToSynchronize = factory.make(Bug.class);
		bugToSynchronize.setDescription(BUG_NEW_DESCR);
		bugToSynchronize.setKey(BUG_KEY);

		Bug bugToSynchronize2 = factory.make(Bug.class);
		bugToSynchronize2.setDescription(BUG_NEW_DESCR);
		bugToSynchronize2.setKey(BUG_KEY_2);

		bugsToSynchronize = new HashSet<Bug>();
		bugsToSynchronize.add(bugToSynchronize);
		bugsToSynchronize.add(bugToSynchronize2);

		Bug existingBug = new Bug();
		existingBug.setDescription(BUG_OLD_DESCR);
		existingBug.setKey(BUG_KEY);

		Bug existingBug2 = new Bug();
		existingBug2.setDescription(BUG_OLD_DESCR);
		existingBug2.setKey(BUG_KEY_2);

		Classification classification = factory.make(Classification.class);
		classification.setBug(existingBug2);
		existingBug2.getClassifications().add(classification);

		bugRepo.save(existingBug);
		bugRepo.save(existingBug2);
	}

	@Test
	public void testSynchronize() {
		synchronizer.synchronize(bugsToSynchronize);

		assertThat(bugRepo.findByKey(BUG_KEY).get().getDescription(), is(BUG_NEW_DESCR));
		assertThat(bugRepo.findByKey(BUG_KEY_2).get().getDescription(), is(BUG_OLD_DESCR));
	}
}

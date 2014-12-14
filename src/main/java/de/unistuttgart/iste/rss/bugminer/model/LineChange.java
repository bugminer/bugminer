package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Entity
public class LineChange {

	@ManyToOne
	private Bug bug;

	@ManyToOne
	private CodeRepo codeRepo;

	private String fileName;

	private int oldLineNumber;

	@Basic(optional = true)
	private Integer newLineNumberIndex;

	private LineChangeKind kind;
}

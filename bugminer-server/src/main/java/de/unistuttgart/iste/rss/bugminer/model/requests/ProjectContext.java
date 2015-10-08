package de.unistuttgart.iste.rss.bugminer.model.requests;

public class ProjectContext {
    private String projectName;
    private String git;
    private String jira;

    public ProjectContext() {

    }

    public ProjectContext(String projectName, String git, String jira) {
        this.projectName = projectName;
        this.git = git;
        this.jira = jira;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getGit() {
        return git;
    }

    public String getJira() {
        return jira;
    }
}

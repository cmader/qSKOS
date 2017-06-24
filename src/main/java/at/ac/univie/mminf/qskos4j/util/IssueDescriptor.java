package at.ac.univie.mminf.qskos4j.util;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.language.util.LanguageCoverage;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by christian on 24.06.17.
 */
public class IssueDescriptor {
    private final static Logger logger = LoggerFactory.getLogger(IssueDescriptor.Builder.class);

    public enum IssueType {STATISTICAL, ANALYTICAL}

    private String id, name, description;
    private IssueType type;
    private URL weblink;
    private Issue dependentIssue;

    public static class Builder {
        // required
        private String id, name, description;
        private IssueType type;

        // optional
        private URL weblink;
        private Issue dependentIssue;

        public Builder(String id, String name, String description, IssueType type) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.type = type;
        }

        public Builder weblink(String weblink) {
            try {
                this.weblink = new URL(weblink);
            }
            catch (MalformedURLException e) {
                logger.warn("The provided URL for issue '" +name+ "' is not valid: '" +weblink+ "'");
            }
            return this;
        }

        public Builder dependentIssue(Issue dependentIssue) {
            this.dependentIssue = dependentIssue;
            return this;
        }

        public IssueDescriptor build() {
            return new IssueDescriptor(this);
        }
    }

    private IssueDescriptor(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.type = builder.type;
        this.weblink = builder.weblink;
        this.dependentIssue = builder.dependentIssue;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public IssueType getType() {
        return type;
    }

    public Issue getDependentIssue() {
        return dependentIssue;
    }

    public URL getWeblink() {
        return weblink;
    }
}

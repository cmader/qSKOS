package at.ac.univie.mminf.qskos4j;

public class UnknownIssueIdException extends RuntimeException {

    public UnknownIssueIdException(String issueId) {
        super("Issue ID not supported: '" +issueId+ "'");
    }

}

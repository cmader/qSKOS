package at.ac.univie.mminf.qskos4j.cmd;

class UnsupportedIssueIdException extends RuntimeException {

    private String unsupportedId;

    UnsupportedIssueIdException(String unsupportedId) {
        this.unsupportedId = unsupportedId;
    }

    String getUnsupportedId() {
        return unsupportedId;
    }

}

package at.ac.univie.mminf.qskos4j.issues.pp;

public class RepairFailedException extends Exception {

    public RepairFailedException(String message) {
        super(message);
    }

    public RepairFailedException(Throwable cause) {
        super(cause);
    }

}

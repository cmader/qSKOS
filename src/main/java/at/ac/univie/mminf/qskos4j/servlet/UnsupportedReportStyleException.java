package at.ac.univie.mminf.qskos4j.servlet;

/**
 * Created by christian
 * Date: 09.02.13
 * Time: 17:14
 */
public class UnsupportedReportStyleException extends RuntimeException {

    public UnsupportedReportStyleException(String reportStyle) {
        super("Unsupported Report Format: '" +reportStyle+ "'");
    }

}

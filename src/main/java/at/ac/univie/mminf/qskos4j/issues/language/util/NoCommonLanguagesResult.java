package at.ac.univie.mminf.qskos4j.issues.language.util;

import at.ac.univie.mminf.qskos4j.result.Result;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;

public class NoCommonLanguagesResult extends Result<Collection<String>> {

    public NoCommonLanguagesResult(Collection<String> data) {
        super(data);
    }

    @Override
    protected void generateTextReport(BufferedWriter osw, ReportStyle style) throws IOException
    {
        switch (style) {
            case SHORT:
                if (getData().isEmpty()) {
                    osw.write("Concepts are not described in a common language");
                }
                else {
                    osw.write("At least one common language for text literals of all concepts has been found");
                }
                break;

            case EXTENSIVE:
                if (!getData().isEmpty()) {
                    osw.write("Common language(s) for all concepts: " +getData().toString());
                }
                break;
        }
    }

    @Override
    public boolean isProblematic() {
        return getData().isEmpty();
    }

}

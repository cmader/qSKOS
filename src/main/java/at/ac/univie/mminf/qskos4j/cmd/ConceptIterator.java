package at.ac.univie.mminf.qskos4j.cmd;

import java.io.*;
import java.util.Iterator;

/**
 * Iterates over a file which contains (substrings of) concept URIs in each lines
 */
class ConceptIterator implements  Iterator<String> {

    private BufferedReader br;
    private String currentLine;

    ConceptIterator(File conceptsFile) throws IOException
    {
        FileInputStream fstream = new FileInputStream(conceptsFile);
        DataInputStream in = new DataInputStream(fstream);

        br = new BufferedReader(new InputStreamReader(in));
    }

    @Override
    public boolean hasNext() {
        try {
            currentLine = br.readLine();
        }
        catch (IOException e) {
            return false;
        }

        return currentLine != null;
    }

    @Override
    public String next() {
        return currentLine;
    }

    @Override
    public void remove() {
        // not implemented intentionally
    }

}

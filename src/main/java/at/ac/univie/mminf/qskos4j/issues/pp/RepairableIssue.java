package at.ac.univie.mminf.qskos4j.issues.pp;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public abstract class RepairableIssue<T extends Report<?>> extends Issue<T> {

    protected RepairableIssue(
        VocabRepository vocabRepository,
        String id,
        String name,
        String description,
        IssueType type)
    {
        super(vocabRepository, id, name, description, type);
    }

    public void repair() throws RepairFailedException
    {
        invokeRepair();
        reset();
    }

    protected abstract void invokeRepair() throws RepairFailedException;

}
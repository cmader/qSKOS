package at.ac.univie.mminf.qskos4j.report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;

public class ExtrapolatedCollectionReport<T> extends CollectionReport<T> {

	private Float subsetSize_percent;
	
	public ExtrapolatedCollectionReport(Collection<T> data, Float subsetSize_percent)
	{
		super(data);
		this.subsetSize_percent = subsetSize_percent;
	}

    @Override
    protected void generateTextReport(BufferedWriter osw, ReportStyle style) throws IOException {
        switch (style) {
            case SHORT:
                osw.write(getShortReport());
                break;

            case EXTENSIVE:
                super.generateTextReport(osw, style);
                break;
        }
    }

	private String getShortReport() {
		long elementCount = getData().size();
		
		StringBuilder report = new StringBuilder("count: "+elementCount);
		
		if (subsetSize_percent != null) {
			elementCount *= 100 / subsetSize_percent;
			report.append(", extrapolated: ").append(elementCount);
		}
		
		return report.toString();
	}

}

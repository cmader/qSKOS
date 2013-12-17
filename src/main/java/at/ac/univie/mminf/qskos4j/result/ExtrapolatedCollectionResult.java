package at.ac.univie.mminf.qskos4j.result;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;

public class ExtrapolatedCollectionResult<T> extends CollectionResult<T> {

	private Float subsetSize_percent;
    private Collection<T> data;
	
	public ExtrapolatedCollectionResult(Collection<T> data, Float subsetSize_percent)
	{
		super(data);
        this.data = data;
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
		long elementCount = data.size();
		
		StringBuilder report = new StringBuilder("count: "+elementCount);
		
		if (subsetSize_percent != null) {
			elementCount *= 100 / subsetSize_percent;
			report.append(", extrapolated: ").append(elementCount);
		}
		
		return report.toString();
	}

}

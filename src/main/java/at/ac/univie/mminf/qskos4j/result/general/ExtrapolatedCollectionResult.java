package at.ac.univie.mminf.qskos4j.result.general;

import java.util.Collection;

public class ExtrapolatedCollectionResult<T> extends CollectionResult<T> {

	private Float subsetSize_percent;
	
	public ExtrapolatedCollectionResult(Collection<T> data, Float subsetSize_percent)
	{
		super(data);
		this.subsetSize_percent = subsetSize_percent;
	}
	
	@Override
	public String getShortReport() {
		long elementCount = getData().size();
		
		String report = "count: "+elementCount;
		
		if (subsetSize_percent != null) {
			elementCount *= 100 / subsetSize_percent;
			report += ", extrapolated: " +elementCount;
		}
		
		return report;
	}

}

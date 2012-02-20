package at.ac.univie.mminf.qskos4j.util;

public class Pair<T> {
		
	private T first, second;
	
	public Pair(T first, T second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			Pair<?> other = (Pair<?>) obj;
			
			return (first.equals(other.first) && second.equals(other.second)) ||
				   (first.equals(other.second) && second.equals(other.first));
		}
		return false;
	}
	
	public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;

        return hashFirst + hashSecond;
    }

	@Override
	public String toString() {
		return "("+first.toString() +", "+ second.toString() +")";
	}
	
	public T getFirst() {
		return first;
	}
	
	public T getSecond() {
		return second;
	}
	
}
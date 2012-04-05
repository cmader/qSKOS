package at.ac.univie.mminf.qskos4j.result;

import org.jgrapht.DirectedGraph;
import org.openrdf.model.Resource;

import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;

public abstract class Result<T> {

	private T data;
	private DirectedGraph<Resource, NamedEdge> graph;
	
	public Result(T data) {
		this.data = data;
	}
	
	public Result(T data, DirectedGraph<Resource, NamedEdge> graph) {
		this.data = data;
		this.graph = graph;
	}

	public T getData() {
		return data;
	}
	
	public DirectedGraph<Resource, NamedEdge> getGraph() {
		return graph;
	}
	
	public abstract String getShortReport();
	
	public abstract String getExtensiveReport();
	
	@Override
	public String toString() {
		return getShortReport();
	}
	
}

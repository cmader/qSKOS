package at.ac.univie.mminf.qskos4j.util.graph;

import org.jgrapht.graph.DefaultEdge;

@SuppressWarnings("serial")
public class NamedEdge extends DefaultEdge {

	private String name = "";
	
	public NamedEdge() {
		this("");
	}
	
	public NamedEdge(String name) {
		super();
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}

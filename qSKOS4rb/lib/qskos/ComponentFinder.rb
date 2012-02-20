require 'igraph'

require_relative 'LoggingRdfReader'
require_relative 'GraphBuilder'

=begin rdoc
Finds weakly connected components in a SKOS RDF graph, i.e. components that aren't connected to the rest of the graph using SKOS properties
=end

class ComponentFinder

	def initialize(loggingRdfReader, log, allConcepts, writeGraphToDisk=false)
		log.info("identifying weakly connected components")
		
		@log = log
		@graph = GraphBuilder.new(loggingRdfReader, log, allConcepts) do
			IGraph.new([], true)
		end.graphs[0]

		if (writeGraphToDisk)
			outputToFile
		end
	end

	def getComponents
		@log.info("decomposing")
		@graph.decompose(IGraph::WEAK)
	end

	private 

	def outputToFile
		@graph.write_graph_graphml(File.open("out.graphml", 'w'))

		indexFile = File.new('vertices.txt', 'w')
		@graph.vertices.each_index do |index|
			indexFile.puts("#{index}, '#{@graph.vertices[index].to_s}'")
		end
		indexFile.close
	end

end

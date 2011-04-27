require 'igraph'

require_relative 'LoggingRdfReader'
require_relative 'GraphBuilder'

class ComponentFinder

	def initialize(loggingRdfReader, log, allConcepts, writeGraphToDisk=false)
		log.info("identifying weakly connected components")
		
		@log = log
		@graph =  GraphBuilder.new(loggingRdfReader, log, allConcepts) do
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
		@graph.write_graph_gml(File.open("out.gml", 'w'))

		indexFile = File.new('vertices.txt', 'w')
		@graph.vertices.each_index do |index|
			indexFile.puts("#{index}, '#{@graph.vertices[index].to_s}'")
		end
		indexFile.close
	end

end

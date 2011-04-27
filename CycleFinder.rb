require 'rgl/adjacency'
include RGL

class CycleFinder

	def initialize(loggingRdfReader, log, allConcepts)
		log.info("identifying minimal cycles")
		@log = log
		@reader = loggingRdfReader
		@allConcepts = allConcepts

		@graphsPredicateConstrains = [
			[SKOS.broader] 
#			[SKOS.narrower, SKOS.narrowerTransitive]
		]

		buildGraphs
		identifyCycles
	end

	private

	def buildGraphs
		@log.info("building #{@graphsPredicateConstrains.size} graph(s)")
		@graphs = GraphBuilder.new(@reader, @log, @allConcepts, @graphsPredicateConstrains) do
			DirectedAdjacencyGraph.new
		end.graphs
	end

	def identifyCycles
		@graphs.each do |graph|
			puts graph.cycles.size
		end
	end

end

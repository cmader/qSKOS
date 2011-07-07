require 'rgl/adjacency'
require_relative 'GraphBuilder'

include RGL

class CycleFinder

	def initialize(loggingRdfReader, log, allConcepts)
		log.info("identifying minimum cycles")
		@log = log
		@reader = loggingRdfReader
		@allConcepts = allConcepts

		@graphsPredicateConstrains = [
			[SKOS.broader, SKOS.broaderTransitive],
			[SKOS.broadMatch]
		]

		buildGraphs
	end

	def getCycles
		@log.info("processing graph(s)")

		allCycles = []
		@graphs.each do |graph|
			graph.cycles.each do |cycle|
				allCycles << cycle
			end
		end
		allCycles
	end

	private

	def buildGraphs
		@log.info("building #{@graphsPredicateConstrains.size} graph(s)")
		@graphs = GraphBuilder.new(@reader, @log, @allConcepts, @graphsPredicateConstrains, true) do
			DirectedAdjacencyGraph.new
		end.graphs
	end

end

class CycleFinder


	def initialize(loggingRdfReader, log, allConcepts)
		@log = log

		@graphsPredicateConstrains = [
			[SKOS.broader, SKOS.broaderTransitive], 
			[SKOS.narrower, SKOS.narrowerTransitive]
		]
		@graphs = GraphBuilder.new(loggingRdfReader, log, allConcepts, @graphsPredicateConstrains).graphs
		identifyCycles
	end

	private

	def identifyCycles
		@graphs.each do |graph|
			
		end
	end

end

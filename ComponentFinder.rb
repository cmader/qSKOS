require 'igraph'
require_relative 'LoggingRdfReader'

class ComponentFinder

	def initialize(loggingRdfReader, log, allConcepts)
		log.info("identifying weakly connected components")

		@reader = loggingRdfReader
		@log = log
		@iGraph = IGraph.new([], false)
		@allConcepts = allConcepts

		populateGraph
	end

	def getComponents
		@log.info("decomposing")
		@iGraph.decompose(0)
	end

	private 

	def populateGraph
		addAllConceptsAsVertices
		constructEdges
	end

	def addAllConceptsAsVertices
		@log.info("initializing graph")
		@allConcepts.each do |concept|
			@iGraph.add_vertex(concept.to_s)
		end
	end

	def constructEdges
		@log.info("constructing edges")
		@reader.loopStatements do |statement|
			if (isSkosPredicate(statement.predicate))
				addToGraph(statement)
			end
		end
	end

	def isSkosPredicate(predicate)
		return predicate.to_s.include?("skos")
	end

	def addToGraph(statement)
		allVertices = @iGraph.vertices
		if (!allVertices.include?(statement.subject.to_s))
			@iGraph.add_vertex(statement.subject)
		end

		if (statement.object.resource?)
			if (!allVertices.include?(statement.object.to_s))
				@iGraph.add_vertex(statement.object)
			end
			@iGraph.add_edge(statement.subject.to_s, statement.object.to_s)
		end
	end

end

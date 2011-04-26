require 'igraph'
require_relative 'LoggingRdfReader'

class GraphBuilder

	attr_reader :graph

	def initialize(loggingRdfReader, log, allConcepts, constrainToPredicates = [])
		@reader = loggingRdfReader
		@log = log
		@allConcepts = allConcepts
		@constrainToPredicates = constrainToPredicates

		@graph = IGraph.new([], false)
		populateGraph
	end

	private

	def populateGraph
		addAllConceptsAsVertices
		constructEdges
	end

	def addAllConceptsAsVertices
		@log.info("initializing graph")
		@allConcepts.each do |concept|
			@graph.add_vertex(concept.to_s)
		end
	end

	def constructEdges
		@log.info("constructing edges")
		@reader.loopStatements do |statement|
			if (inSkosNamespace?(statement.predicate) && !inSkosNamespace?(statement.subject) && isValidPredicate(statement.predicate))
				addToGraph(statement)
			end
		end
	end

	def inSkosNamespace?(predicate)
		return predicate.to_uri.start_with?(SKOS.to_uri)
	end

	def isValidPredicate(predicate)
		if (@constrainToPredicates.empty?)
			return true
		else
			return @constrainToPredicates.include?(predicate)
		end
	end

	def addToGraph(statement)
		begin
			addNodeToGraph(statement)
			@graph.add_edge(statement.subject.to_s, statement.object.to_s)
		rescue Exception
			#@log.info("skipping triple #{statement}")
		end
	end

	# at least subject or object must already be contained in the graph (i.e. is a concept 
	# or a resource connected to a concept)
	def addNodeToGraph(statement)
		subjectInGraph = @graph.vertices.include?(statement.subject.to_s)
		objectInGraph = @graph.vertices.include?(statement.object.to_s)

		if (!subjectInGraph && !objectInGraph)
			raise InvalidStatementException.new
		elsif (!subjectInGraph)
			@graph.add_vertex(statement.subject)
		elsif (!objectInGraph)
			if (statement.object.resource?)
				@graph.add_vertex(statement.object)
			else
				raise IgnoredStatementException
			end
		end
	end

	class InvalidStatementException < Exception
	end

	class IgnoredStatementException < Exception
	end

end

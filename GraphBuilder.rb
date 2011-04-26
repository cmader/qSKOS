require 'igraph'
require_relative 'LoggingRdfReader'

class GraphBuilder

	attr_reader :graphs

	def initialize(loggingRdfReader, log, allConcepts, constrainToPredicates = [[]])
		@reader = loggingRdfReader
		@log = log
		@allConcepts = allConcepts
		@constrainToPredicates = constrainToPredicates

		@graphs = Array.new(constrainToPredicates.size, IGraph.new([], false))
		populateGraphs
	end

	private

	def populateGraphs
		addAllConceptsAsVertices
		constructEdges
	end

	def addAllConceptsAsVertices
		@log.info("initializing #{@constrainToPredicates.size} graph(s)")

		@graphs.each do |graph|
			@allConcepts.each do |concept|
				graph.add_vertex(concept.to_s)
			end
		end
	end

	def constructEdges
		@log.info("constructing edges")
		@reader.loopStatements do |statement|
			if (inSkosNamespace?(statement.predicate) && !inSkosNamespace?(statement.subject))
				identifyGraphsToReceivePredicate(statement.predicate).each do |graph|
					addToGraph(graph, statement)
				end
			end
		end
	end

	def inSkosNamespace?(predicate)
		return predicate.to_uri.start_with?(SKOS.to_uri)
	end

	def identifyGraphsToReceivePredicate(predicate)
		graphsReceivingPredicate = []
		@constrainToPredicates.each_index do |predicateListIndex|
			predicateList = @constrainToPredicates[predicateListIndex]
			if (predicateList.empty? || predicateList.include?(predicate))
				graphsReceivingPredicate << @graphs[predicateListIndex]
			end			
		end
		return graphsReceivingPredicate
	end

	def addToGraph(graph, statement)
		begin
			addNodeToGraph(graph, statement)
			graph.add_edge(statement.subject.to_s, statement.object.to_s)
		rescue Exception
			#@log.info("skipping triple #{statement}")
		end
	end

	# at least subject or object must already be contained in the graph (i.e. is a concept 
	# or a resource connected to a concept)
	def addNodeToGraph(graph, statement)
		subjectInGraph = graph.vertices.include?(statement.subject.to_s)
		objectInGraph = graph.vertices.include?(statement.object.to_s)

		if (!subjectInGraph && !objectInGraph)
			raise InvalidStatementException.new
		elsif (!subjectInGraph)
			graph.add_vertex(statement.subject)
		elsif (!objectInGraph)
			if (statement.object.resource?)
				graph.add_vertex(statement.object)
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

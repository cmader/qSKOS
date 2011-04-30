require_relative 'LoggingRdfReader'
require_relative 'SKOSUtils'

class GraphBuilder

	attr_reader :graphs

	def initialize(loggingRdfReader, log, allConcepts, 
		constrainToPredicates = [[]], includeInverseProperties = false)
		@reader = loggingRdfReader
		@log = log
		@allConcepts = allConcepts
		@constrainToPredicates = constrainToPredicates
		@includeInverseProperties = includeInverseProperties

		@graphs = Array.new(constrainToPredicates.size) do |index|
			yield
		end
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
			if isValidStatement(statement)
				routeStatementToGraph(statement) do |graph, statement|
					addToGraph(graph, statement)
				end
			end
		end
	end

	def isValidStatement(statement)
		SKOSUtils.instance.inSkosNamespace?(statement.predicate) && !inSkosNamespace?(statement.subject)
	end

	def routeStatementToGraph(statement)
		@constrainToPredicates.each_index do |predicateListIndex|
			predicateList = @constrainToPredicates[predicateListIndex]

			if allowedByList?(predicateList, statement.predicate)
				yield(@graphs[predicateListIndex], statement)
			elsif includeInvertedStatement?(predicateList, statement.predicate)
				yield(@graphs[predicateListIndex], invertStatement(statement))
			end	
		end
	end

	def allowedByList?(predicateList, predicate)
		predicateList.empty? || predicateList.include?(predicate)
	end

	def includeInvertedStatement?(predicateList, predicate)
		if @includeInverseProperties
			begin			
				inverseProperty = SKOSUtils.instance.getInverseProperty(predicate)
				return predicateList.include?(inverseProperty)
			rescue Exception
				return false
			end
		end
		false
	end

	def invertStatement(statement)
		newSubject = statement.object
		newObject = statement.subject
		Statement.new(newSubject, SKOSUtils.instance.getInverseProperty(statement.predicate), newObject)
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
			graph.add_vertex(statement.subject.to_s)
		elsif (!objectInGraph)
			if (statement.object.resource?)
				graph.add_vertex(statement.object.to_s)
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

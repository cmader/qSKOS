require 'igraph'
require_relative 'LoggingRdfReader'

class ComponentFinder

	def initialize(loggingRdfReader, log, allConcepts, writeGraphToDisk=false)
		log.info("identifying weakly connected components")

		@reader = loggingRdfReader
		@log = log
		@iGraph = IGraph.new([], false)
		@allConcepts = allConcepts

		populateGraph
		if (writeGraphToDisk)
			outputToFile
		end
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

	def outputToFile
		@iGraph.write_graph_gml(File.open("out.gml", 'w'))

		indexFile = File.new('vertices.txt', 'w')
		@iGraph.vertices.each_index do |index|
			indexFile.puts("#{index}, '#{@iGraph.vertices[index].to_s}'")
		end
		indexFile.close
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
			if (inSkosNamespace?(statement.predicate) && !inSkosNamespace?(statement.subject))
				addToGraph(statement)
			end
		end
	end

	def inSkosNamespace?(predicate)
		return predicate.to_uri.start_with?(SKOS.to_uri)
	end

	def addToGraph(statement)
		begin
			addNodeToGraph(statement)
			@iGraph.add_edge(statement.subject.to_s, statement.object.to_s)
		rescue Exception
			#@log.info("skipping triple #{statement}")
		end
	end

	# at least subject or object must already be contained in the graph (i.e. is a concept 
	# or a resource connected to a concept)
	def addNodeToGraph(statement)
		subjectInGraph = @iGraph.vertices.include?(statement.subject.to_s)
		objectInGraph = @iGraph.vertices.include?(statement.object.to_s)

		if (!subjectInGraph && !objectInGraph)
			raise InvalidStatementException.new
		elsif (!subjectInGraph)
			@iGraph.add_vertex(statement.subject)
		elsif (!objectInGraph)
			if (statement.object.resource?)
				@iGraph.add_vertex(statement.object)
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

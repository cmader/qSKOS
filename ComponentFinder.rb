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
		@iGraph.add_vertex(statement.subject)

		if (statement.object.resource?)
			@iGraph.add_vertex(statement.object)
			@iGraph.add_edge(statement.subject.to_s, statement.object.to_s)
		end
	end

end

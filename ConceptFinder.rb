require 'rdf'

class ConceptFinder

	SKOS_CONCEPT_URI = RDF::URI.new("http://www.w3.org/2004/02/skos/core#Concept")

	def initialize(rdfGraph, log)
		log.info("identifying concepts")

		@rdfGraph = rdfGraph
		findSubclassedConcepts
		findTypedConcepts

		findImplicitConcepts.each do |solution|
			puts solution.inspect
		end
	end

	private

	def findSubclassedConcepts
		query = RDF::Query.new({
			:conceptSubClass => {
				RDFS.subClassOf => SKOS_CONCEPT_URI
			},
			:concept => {
				RDF.type => :conceptSubClass
			}
		})
		return query.execute(@rdfGraph)
	end

	def findTypedConcepts
		query = RDF::Query.new({
			:concept => {
				RDF.type => SKOS_CONCEPT_URI
			}
		})
		return query.execute(@rdfGraph)
	end

	def findImplicitConcepts

	end

end

require 'rdf'

class ConceptFinder

	SKOS_CONCEPT_URI = RDF::URI.new("http://www.w3.org/2004/02/skos/core#Concept")

	def initialize(rdfGraph, log)
		log.info("identifying concepts")

		@identifiedConcepts = []
		@rdfGraph = rdfGraph

		findSubclassedConcepts
		findTypedConcepts
		findImplicitConcepts

		@identifiedConcepts.each do |concept|
			puts concept
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
		
		query.execute(@rdfGraph).each do |solution|
			@identifiedConcepts << solution[:concept]
		end
	end

	def findTypedConcepts
		query = RDF::Query.new({
			:concept => {
				RDF.type => SKOS_CONCEPT_URI
			}
		})

		query.execute(@rdfGraph).each do |solution|
			@identifiedConcepts << solution[:concept]
		end
	end

	def findImplicitConcepts
		queries = createConceptQueries

		queries.each do |query|
			query.execute(@rdfGraph).each do |solution|
				@identifiedConcepts << solution[:concept]
				@identifiedConcepts << solution[:otherConcept]
			end
		end
	end

	def createConceptQueries
		queries = []
		queries << RDF::Query.new({:concept => {SKOS.hasTopConcept => :someTopConcept}})
		queries << RDF::Query.new({:someTopConcept => {SKOS.topConceptOf => :concept}})
		queries << RDF::Query.new({:concept => {SKOS.broader => :otherConcept}})
		return queries
	end

end

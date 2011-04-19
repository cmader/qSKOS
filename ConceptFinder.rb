require 'rdf'

class ConceptFinder

	SKOS_CONCEPT_URI = RDF::URI.new("http://www.w3.org/2004/02/skos/core#Concept")

	def initialize(rdfGraph, log)
		log.info("identifying concepts")

		@allSolutions = []
		@rdfGraph = rdfGraph

		findSubclassedConcepts
		findTypedConcepts
		findImplicitConcepts

		getConceptUris
	end

	def getConceptUris
		conceptUris = []
		@allSolutions.each do |solutions|
			solutions.each do |solution|
				conceptUris << solution[:concept]

				if (solution[:otherConcept] != nil)
					conceptUris << solution[:otherConcept]
				end
			end
		end

		conceptUris.uniq
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
		
		@allSolutions << query.execute(@rdfGraph)
	end

	def findTypedConcepts
		query = RDF::Query.new({
			:concept => {
				RDF.type => SKOS_CONCEPT_URI
			}
		})

		@allSolutions << query.execute(@rdfGraph)
	end

	def findImplicitConcepts
		queries = createConceptQueries

		queries.each do |query|
			@allSolutions << query.execute(@rdfGraph)
		end
	end

	def createConceptQueries
		queries = []
		queries << RDF::Query.new({:concept => {SKOS.hasTopConcept => :someTopConcept}})
		queries << RDF::Query.new({:someTopConcept => {SKOS.topConceptOf => :concept}})
		queries << RDF::Query.new({:concept => {SKOS.related => :otherConcept}})
		queries << RDF::Query.new({:concept => {SKOS.broader => :otherConcept}})
		queries << RDF::Query.new({:concept => {SKOS.narrower => :otherConcept}})
		queries << RDF::Query.new({:concept => {SKOS.broaderTransitive => :otherConcept}})
		queries << RDF::Query.new({:concept => {SKOS.narrowerTransitive => :otherConcept}})
		queries << RDF::Query.new({:concept => {SKOS.mappingRelation => :otherConcept}})
		queries << RDF::Query.new({:concept => {SKOS.broadMatch => :otherConcept}})
		queries << RDF::Query.new({:concept => {SKOS.narrowMatch => :otherConcept}})
		queries << RDF::Query.new({:concept => {SKOS.closeMatch => :otherConcept}})
		queries << RDF::Query.new({:concept => {SKOS.exactMatch => :otherConcept}})
		return queries
	end

end

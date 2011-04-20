class ConceptFinder

	SKOS_CONCEPT_URI = RDF::URI.new("http://www.w3.org/2004/02/skos/core#Concept")

	def initialize(loggingRdfReader, log)
		log.info("identifying concepts")

		@conceptRelations = [SKOS.related, SKOS.broader, SKOS.narrower, SKOS.broaderTransitive, SKOS.narrowerTransitive,
			SKOS.mappingRelation, SKOS.broadMatch, SKOS.narrowMatch, SKOS.closeMatch, SKOS.exactMatch]
		@conceptSubClasses = []
		@concepts = []
		@reader = loggingRdfReader
		@log = log

		findConceptSubClasses
		log.info("1st pass finished, one pass left")

		log.info("2nd pass")
		findConcepts

		log.info("concept identification finished")
	end

	def getAllConcepts
		@concepts.uniq
	end

	private

	def findConceptSubClasses
		@reader.loopStatements do |statement|
			if (definesSubClassOfConcept(statement))
				@conceptSubClasses << statement.subject	
			end
		end
	end

	def definesSubClassOfConcept(statement)
		if (statement.predicate == RDFS.subClassOf &&
				(statement.object == SKOS_CONCEPT_URI || 
				@conceptSubClasses.include?(statement.object)))
			return true
		else
			return false
		end
	end

	def findConcepts
		@reader.loopStatements do |statement|
			examineStatement(statement)
		end
	end

	def examineStatement(statement)
		if (statement.predicate == RDF.type)
			processTypeStatement(statement)
		elsif (statement.predicate == SKOS.hasTopConcept)
			@concepts << statement.object
		elsif (statement.predicate == SKOS.topConceptOf)
			@concepts << statement.subject
		elsif (@conceptRelations.include?(statement.predicate))
			@concepts << statement.subject
			@concepts << statement.object
		end
	end

	def processTypeStatement(statement)
		if (statement.object == SKOS_CONCEPT_URI || @conceptSubClasses.include?(statement.object))
			@concepts << statement.subject
		end
	end

end

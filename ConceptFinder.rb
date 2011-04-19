require 'rdf'

class ConceptFinder

	SKOS_CONCEPT_URI = RDF::URI.new("http://www.w3.org/2004/02/skos/core#Concept")

	def initialize(rdfReader, log)
		log.info("identifying concepts")

		@conceptRelations = [SKOS.related, SKOS.broader, SKOS.narrower, SKOS.broaderTransitive, SKOS.narrowerTransitive,
			SKOS.mappingRelation, SKOS.broadMatch, SKOS.narrowMatch, SKOS.closeMatch, SKOS.exactMatch]
		@conceptSubClasses = []
		@concepts = []
		@rdfReader = rdfReader
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
		loopStatements do |statement|
			if (definesSubClassOfConcept(statement))
				@conceptSubClasses << statement.subject	
			end
		end
	end

	def loopStatements
		i = 1;
		@rdfReader.each_statement do |statement|
			if (@totalStatements == nil)
				outputProcessedTriples(i)
			else
				outputPercentage(i)
			end
			i += 1

			yield(statement)
		end

		@totalStatements = i
		@prevPercentage = 0
	end

	def outputProcessedTriples(count)
		if (count % 5000 == 0) 
			@log.info("processed >#{count} triples")
		end
	end

	def outputPercentage(count)
		percentage = Integer((count.fdiv(@totalStatements)) * 100)

		if (percentage % 10 == 0 && percentage > @prevPercentage)
			@log.info("#{percentage}% finished")
		
			@prevPercentage = percentage
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
		loopStatements do |statement|
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

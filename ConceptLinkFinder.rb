require_relative 'SKOSUtils'

class ConceptLinkFinder

	def initialize(loggingRdfReader, log, allConcepts)
		log.info("identifying concept links")

		@reader = loggingRdfReader
		@allConcepts = allConcepts

		@conceptResources = {}
		findLinks
	end

	private

	def findLinks
		@reader.loopStatements do |statement|
			if @allConcepts.include?(statement.subject.to_s) && 
				!SKOSUtils.instance.inSkosNamespace?(statement.object) &&
				statement.object.resource?

				addResourceToHash(statement.subject.to_s, statement.object)
			end
		end
	end

	def addResourceToHash(conceptUri, resource)
		if @conceptResources[conceptUri] == nil
			@conceptResources[conceptUri] = []
		end
		@conceptResources[conceptUri] << resource
	end

end

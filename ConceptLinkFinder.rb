require_relative 'SKOSUtils'

class ConceptLinkFinder

	def initialize(loggingRdfReader, log, allConcepts)
		log.info("identifying concept links")

		@reader = loggingRdfReader
		@allConcepts = allConcepts

		# holds the concept -> resource mappings
		@conceptResources = {}

		# holds linked resources on a different hosts
		@hostMismatches = {}

		findLinks
		identifyExternalLinks

		#dumpConceptResources
	end

	# this is not exactly true; however, we suppose either subject or object of
	# a triple involving a concept to NOT redefine or enhance an existing concept
	def getExternalLinks
		@hostMismatches.values
	end

	private

	def findLinks
		@reader.loopStatements do |statement|
			if @allConcepts.include?(statement.subject.to_s) && 
				statement.object.resource? &&
				!SKOSUtils.instance.inSkosNamespace?(statement.object)

				addResourceToConcept(statement.subject, statement.object)
			end
		end
	end

	def addResourceToConcept(conceptUri, resource)
		if @conceptResources[conceptUri] == nil
			@conceptResources[conceptUri] = []
		end
		@conceptResources[conceptUri] << resource
	end

	def identifyExternalLinks
		@conceptResources.keys.each do |key|
			@conceptResources[key].each do |value|
				if key.host != value.host
					@hostMismatches[key] = [] if !@hostMismatches.key?(key)
					@hostMismatches[key] << value
				end
			end
		end
	end

	def dumpConceptResources
		@conceptResources.keys.each do |key|
			puts "concept: #{key}"
			@conceptResources[key].each do |value|
				puts "resource: #{value}"
				puts value.host
			end
			puts "==="
		end
	end

end

require_relative 'SKOSUtils'

class ConceptLinkFinder

	def initialize(loggingRdfReader, log, allConcepts, publishingHost = nil)

		log.info("collecting concept links")

		@log = log
		@reader = loggingRdfReader
		@allConcepts = allConcepts
		
		@publishingHost = publishingHost
		if (publishingHost == nil) 
			@publishingHost = guessPublishingHost
		end

		@conceptResources = {}
		@hostMismatches = []

		findLinks
	end

	def getExternalLinks
		@log.info("identifying external links")
		identifyExternalLinks

		@hostMismatches
	end

	private

	def guessPublishingHost
		hostnames = collectConceptHostnames
		publishingHost = mostOftenMentionedHostName(hostnames)

		@log.info("guessed publishing host: '#{publishingHost}'")
		publishingHost
	end

	def collectConceptHostnames
		hostNames = Hash.new(0)

		@allConcepts.each do |concept|
			if !concept.host.empty?
				hostNames[concept.host] += 1
			end
		end

		hostNames
	end

	def mostOftenMentionedHostName(hostnames)
		sorted = hostnames.sort_by do |host, count|
			count
		end

		return nil if sorted.empty?
		sorted.last.first.to_s
	end

	def findLinks
		@reader.loopStatements do |statement|
			if @allConcepts.include?(statement.subject.to_s) && 
				statement.object.resource? &&
				!statement.object.node? &&
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
		@conceptResources.each do |key, resources|
			resources.each do |uri|
				if !uri.host.eql?(@publishingHost.to_s)
					@hostMismatches << uri
				end
			end
		end
	end

end

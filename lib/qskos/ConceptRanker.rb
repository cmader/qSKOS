require 'sparql/client'

include SPARQL

class ConceptRanker

	def initialize(log, sparqlEndpoints)
		@clients = []
		@rankedConcepts = Hash.new({})

		sparqlEndpoints.each do |endpoint|
			@clients << Client.new(endpoint)
		end

	end

	def rankConcepts(concepts)
		@clients.each do |client|
			concepts.each do |concept|
				rankConcept(concept, client)
			end
		end

		@rankedConcepts
	end

	private

	def rankConcept(concept, client)
		inLinkResult = client.query("SELECT distinct ?s WHERE { ?s ?p <#{concept.to_s}> . FILTER(regex(str(?s), \"http://.*\")) }")

		@rankedConcepts[concept] = {
			:conceptIsObjectTripleCount => inLinkResult.size,
			:hosts => getDistinctHosts(inLinkResult, concept.host)}

		@rankedConcepts
	end

	def getDistinctHosts(inLinkResult, conceptHost)
		hosts = []
		inLinkResult.each do |solution|
			resultResourceHost = solution[:s].host

			hosts << resultResourceHost unless resultResourceHost == conceptHost
		end

		hosts.uniq
	end

end

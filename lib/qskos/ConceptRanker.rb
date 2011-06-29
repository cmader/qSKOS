require 'sparql/client'

include SPARQL

class ConceptRanker

	def initialize(log, sparqlEndpoints)
		@clients = []

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
	end

	private

	def rankConcept(concept, client)
		query = client.select.distinct.where([:s, :p, concept])

puts query.inspect
puts query.result.size

		query.each_solution do |solution|
			puts solution
		end
	end

end

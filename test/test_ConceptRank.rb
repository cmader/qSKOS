require 'helper'
require 'logger'
require 'qSKOS'

class TestConceptRank < Test::Unit::TestCase

  def test_conceptRank_1
		QSKOS.init("test/testdata/rankConcepts.rdf", @log)

		foundConcepts = QSKOS.findAllConcepts
		rankedConcepts = QSKOS.rankConcepts(foundConcepts, @sindiceSparqlEndpoint)

		rankedConcepts.each_key do |key|
			case key.to_s
				when "http://dbpedia.org/resource/Michael_Jackson"
					assert_equal(rankedConcepts[key][:hosts].size, 10)
				when "http://zbw.eu/stw/descriptor/13845-2"
					assert_equal(rankedConcepts[key][:conceptIsObjectTripleCount], 1)
			end
		end
	end

	def setup
		@log = Logger.new(STDOUT)
		@dbpediaSparqlEndpoint = "http://dbpedia.org/sparql"
		@sindiceSparqlEndpoint = "http://sparql.sindice.com/sparql"
	end

end

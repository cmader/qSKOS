require 'helper'
require 'logger'
require 'qSKOS'

class TestConceptRank < Test::Unit::TestCase

  def test_conceptRank_1
		QSKOS.init("test/testdata/rankConcepts.rdf", @log)

		foundConcepts = QSKOS.findAllConcepts
		rankedConcepts = QSKOS.rankConcepts(foundConcepts, @sindiceSparqlEndpoint)

		#TODO: add assertion
	end

	def setup
		@log = Logger.new(STDOUT)
		@dbpediaSparqlEndpoint = "http://dbpedia.org/sparql"
		@sindiceSparqlEndpoint = "http://sparql.sindice.com/sparql"
	end

end

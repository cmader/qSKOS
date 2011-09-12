require 'helper'
require 'logger'
require 'qSKOS'

class TestLanguageCollector < Test::Unit::TestCase

  def test_languageCollector
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
	
		allConcepts = QSKOS.findAllConcepts
		langCov = QSKOS.getLanguageCoverage(allConcepts)

		languageDistribution = langCov[:langDistribution]
		
		assert_equal(langCov[:coverageRatioPerConcept].keys.size, 14)
		assert_equal(langCov[:fullCoverageConcepts].size, 1)
		assert_equal(languageDistribution[1].size, 11)
		assert_equal(languageDistribution[2].size, 2)
		assert_equal(languageDistribution[3].size, 1)
  end

end

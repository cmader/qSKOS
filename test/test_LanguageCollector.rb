require 'helper'
require 'logger'
require 'qSKOS'

class TestLanguageCollector < Test::Unit::TestCase

  def test_languageCollector
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
	
		allConcepts = QSKOS.findAllConcepts
		langCov = QSKOS.getLanguageCoverage(allConcepts)
		
		assert_equal(langCov[:coverageRatioPerConcept].keys.size, 14)
		assert_operator(langCov[:avgRatio], :>, 0.5)
		assert_equal(langCov[:fullCoverageConcepts].size, 1)
  end

end

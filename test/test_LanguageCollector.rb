require 'helper'
require 'logger'
require 'qSKOS'

class TestLanguageCollector < Test::Unit::TestCase

  def test_languageCollector
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
	
		allConcepts = QSKOS.findAllConcepts
		langCov = QSKOS.getLanguageCoverage(allConcepts)
		
		assert_equal(langCov[0].keys.size, 14)
		assert_operator(langCov[1], :>, 0.5)
		assert_equal(langCov[2].size, 1)
  end

end

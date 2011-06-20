require 'helper'
require 'logger'
require 'qSKOS'

class TestFindExternalLinks < Test::Unit::TestCase

  def test_countExternalLinks_1
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
		allConcepts = QSKOS.findAllConcepts
		extLinks = QSKOS.getExternalLinks(allConcepts)
		assert_equal(extLinks.size, 0)
  end

  def test_countExternalLinks_2
		QSKOS.init("test/testdata/concepts.rdf", Logger.new(STDOUT))
		allConcepts = QSKOS.findAllConcepts
		extLinks = QSKOS.getExternalLinks(allConcepts)
		assert_equal(extLinks.size, 0)
  end

end

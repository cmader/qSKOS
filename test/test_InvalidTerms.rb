require 'helper'
require 'logger'
require 'qSKOS'

class TestInvalidTerms < Test::Unit::TestCase

  def test_invalidTerms_1
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))

		invalidTerms = QSKOS.getInvalidTerms
		unknownTerms = invalidTerms.first
		deprecatedTerms = invalidTerms.last
		
		assert_equal(unknownTerms.size, 0)
		assert_equal(deprecatedTerms.size, 0)
  end

  def test_invalidTerms_2
		QSKOS.init("test/testdata/concepts.rdf", Logger.new(STDOUT))

		invalidTerms = QSKOS.getInvalidTerms
		unknownTerms = invalidTerms.first
		deprecatedTerms = invalidTerms.last

		assert_equal(unknownTerms.size, 2)
		assert_equal(deprecatedTerms.size, 1)
  end

end

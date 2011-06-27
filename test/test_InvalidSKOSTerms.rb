require 'helper'
require 'logger'
require 'qSKOS'

class TestInvalidSKOSTerms < Test::Unit::TestCase

  def test_invalidSKOSTerms_1
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
		invalidTerms = QSKOS.getInvalidSKOSTerms
		
		assert_equal(invalidTerms.size, 0)
  end

  def test_invalidSKOSTerms_2
		QSKOS.init("test/testdata/concepts.rdf", Logger.new(STDOUT))
		invalidTerms = QSKOS.getInvalidSKOSTerms

		assert_equal(invalidTerms.size, 1)
  end

end

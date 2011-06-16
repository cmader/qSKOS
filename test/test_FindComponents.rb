require 'helper'
require 'logger'
require 'qSKOS'

class TestFindComponents < Test::Unit::TestCase

  def test_countComponents
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
		allConcepts = QSKOS.findAllConcepts
		foundComponents = QSKOS.findComponents(allConcepts)
		assert_equal(foundComponents.size, 6)
  end

end

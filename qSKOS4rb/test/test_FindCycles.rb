require 'helper'
require 'logger'
require 'qSKOS'

class TestFindCycles < Test::Unit::TestCase

  def test_countCycles
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
		allConcepts = QSKOS.findAllConcepts
		foundCycles = QSKOS.findCycles(allConcepts)
		assert_equal(foundCycles.size, 5)
  end

end

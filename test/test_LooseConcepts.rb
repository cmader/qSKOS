require 'helper'
require 'logger'
require 'qSKOS'

class TestFindConcepts < Test::Unit::TestCase

  def test_countLooseConcepts
		QSKOS.init("test/testdata/concepts.rdf", Logger.new(STDOUT))
		allConcepts = QSKOS.findAllConcepts
		looseConcepts = QSKOS.findLooseConcepts(allConcepts)
		assert_equal(looseConcepts.size, 6)

		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
		allConcepts = QSKOS.findAllConcepts
		looseConcepts = QSKOS.findLooseConcepts(allConcepts)
		assert_equal(looseConcepts.size, 1)
  end

end

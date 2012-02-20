require 'helper'
require 'logger'
require 'qSKOS'

class TestAmbiguousLabels < Test::Unit::TestCase

  def test_ambiguousLabels
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
	
		allConcepts = QSKOS.findAllConcepts
		ambiguouslyLabeledConcepts = QSKOS.getAmbiguouslyLabeledConcepts(allConcepts)
		
		assert_equal(ambiguouslyLabeledConcepts.keys.size, 3)
  end

end

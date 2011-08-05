require 'helper'
require 'logger'
require 'qSKOS'

class TestUnconnectedRelatedConcepts < Test::Unit::TestCase

  def test_unconnectedRelatedConcepts
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
	
		allConcepts = QSKOS.findAllConcepts
		QSKOS.getUnconnectedRelatedConcepts(allConcepts)
		
		#assert_equal(ambiguouslyLabeledConcepts.keys.size, 3)
  end

end

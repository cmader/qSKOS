require 'helper'
require 'logger'
require 'qSKOS'

class TestUnconnectedRelatedConcepts < Test::Unit::TestCase

  def test_unconnectedRelatedConcepts
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
	
		allConcepts = QSKOS.findAllConcepts
		relatedConcepts = QSKOS.getUnconnectedRelatedConcepts(allConcepts)

		unconnectedRelatedConcepts = relatedConcepts[:unconnectedRelatedConcepts]
		identicallyLabeledConcepts = relatedConcepts[:relatedConcepts]
		
		assert_equal(unconnectedRelatedConcepts.size, 2)
		assert_equal(identicallyLabeledConcepts.size, 3)
  end

end

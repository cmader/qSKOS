require 'helper'
require 'logger'
require 'qSKOS'

class TestPropertyPartitions < Test::Unit::TestCase

  def test_propertyPartitions_1
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
	
		allConcepts = QSKOS.findAllConcepts
		propertyPartitions = QSKOS.getPropertyPartitions(allConcepts)	

		docPropertyStatements = propertyPartitions[:docStatements]
		humanReadableLabels = propertyPartitions[:humanReadableLabels]
		
		assert_equal(docPropertyStatements.size, 2)
		assert_equal(humanReadableLabels.empty?, false)
  end

  def test_propertyPartitions_2
		QSKOS.init("test/testdata/concepts.rdf", Logger.new(STDOUT))

		allConcepts = QSKOS.findAllConcepts
		propertyPartitions = QSKOS.getPropertyPartitions(allConcepts)	

		docPropertyStatements = propertyPartitions[:docStatements]
		humanReadableLabels = propertyPartitions[:humanReadableLabels]

		assert_equal(docPropertyStatements.size, 1)
		assert_equal(humanReadableLabels.empty?, false)
  end

end

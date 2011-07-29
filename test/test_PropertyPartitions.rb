require 'helper'
require 'logger'
require 'qSKOS'

class TestPropertyPartitions < Test::Unit::TestCase

  def test_propertyPartitions_1
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
	
		allConcepts = QSKOS.findAllConcepts
		docPropertyStatements = QSKOS.getPropertyPartitions(allConcepts)[:docStatements]
		
		assert_equal(docPropertyStatements.size, 2)
  end

  def test_propertyPartitions_2
		QSKOS.init("test/testdata/concepts.rdf", Logger.new(STDOUT))

		allConcepts = QSKOS.findAllConcepts
		docPropertyStatements = QSKOS.getPropertyPartitions(allConcepts)[:docStatements]

		assert_equal(docPropertyStatements.size, 1)
  end

end

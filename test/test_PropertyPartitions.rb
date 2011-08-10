require 'helper'
require 'logger'
require 'qSKOS'

class ConceptPropertiesCollector < Test::Unit::TestCase

  def test_documentation_statements_1
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
	
		allConcepts = QSKOS.findAllConcepts
		propertyPartitions = QSKOS.getPropertyPartitions(allConcepts)

		docPropertyStatements = propertyPartitions[:docStatements]
		
		assert_equal(docPropertyStatements.size, 2)
  end

  def test_documentation_statements_2
		QSKOS.init("test/testdata/concepts.rdf", Logger.new(STDOUT))

		allConcepts = QSKOS.findAllConcepts
		propertyPartitions = QSKOS.getPropertyPartitions(allConcepts)

		docPropertyStatements = propertyPartitions[:docStatements]

		assert_equal(docPropertyStatements.size, 1)
  end

	def test_language_tags
		QSKOS.init("test/testdata/components.rdf", Logger.new(STDOUT))
	
		allConcepts = QSKOS.findAllConcepts
		untaggedLiterals = QSKOS.getLanguageTagSupport

		assert_equal(untaggedLiterals[:naturalLanguageLiteralsWithoutLangTag].size, 3)
	end

end

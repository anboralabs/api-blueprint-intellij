package co.anbora.labs.apiblueprint.highlighting

import org.junit.Test
import org.junit.Assert.*

/**
 * Test that different patterns don't conflict and cause overlapping highlighting.
 */
class ApiBlueprintPatternConflictTest {

    @Test
    fun testAttributesSectionDoesNotMatchMsonPattern() {
        val attributesLines = listOf(
            "+ Attributes (Coupon)",
            "    + Attributes (User)",
            "\t+ Attributes (object)", 
            "+ Attributes (array[Product])"
        )
        
        attributesLines.forEach { line ->
            // Should match the Attributes section pattern
            val attrMatch = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
            assertNotNull("Should match Attributes pattern: '$line'", attrMatch)
            
            // Should NOT match the MSON attribute pattern (due to negative lookahead)
            val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
            assertNull("Should NOT match MSON attribute pattern: '$line'", msonMatch)
        }
    }

    @Test
    fun testOtherSectionHeadersDoNotMatchMsonPattern() {
        val sectionHeaders = listOf(
            "+ Parameters",
            "+ Headers", 
            "+ Body",
            "+ Schema",
            "+ Request (application/json)",
            "+ Response 200 (application/json)",
            "+ Default: `10`",
            "+ Relation: self"
        )
        
        sectionHeaders.forEach { line ->
            val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
            assertNull("Section header should NOT match MSON pattern: '$line'", msonMatch)
        }
    }

    @Test
    fun testRealMsonAttributesStillMatch() {
        val realMsonAttributes = listOf(
            "    + id: 250FF (string, required)",
            "    + created: 1415203908 (number) - Time stamp",
            "    + percent_off: 25 (number)",
            "    + user_name: \"john_doe\" (string, optional) - User identifier",
            "    + is_active: true (boolean) - Status flag",
            "    + tags (array[string], optional) - Available tags",
            "    + metadata (object, nullable) - Additional data"
        )
        
        realMsonAttributes.forEach { line ->
            val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
            assertNotNull("Real MSON attribute should match: '$line'", msonMatch)
            
            val attributeName = msonMatch?.groups?.get(1)?.value
            assertNotNull("Should extract attribute name from: '$line'", attributeName)
            assertTrue("Attribute name should not be empty", attributeName!!.isNotEmpty())
            
            // Verify it doesn't match reserved section names
            val reservedNames = listOf("Attributes", "Parameters", "Headers", "Body", "Schema", 
                                     "Request", "Response", "Default", "Relation")
            assertFalse("Attribute name '$attributeName' should not be reserved section name", 
                       reservedNames.any { it.equals(attributeName, ignoreCase = true) })
        }
    }

    @Test
    fun testParametersVersusAttributes() {
        val parameterLine = "    + limit (number, optional)"
        val attributeLine = "    + id: 250FF (string, required)"
        val attributesSection = "+ Attributes (Coupon)"
        
        // Parameter should match parameter pattern but not MSON attribute pattern
        val paramMatch = ApiBlueprintRegexUtils.PARAMETER_PATTERN.find(parameterLine)
        assertNotNull("Parameter should match parameter pattern", paramMatch)
        
        val paramMsonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(parameterLine)
        assertNotNull("Parameter should also match MSON pattern (they're similar)", paramMsonMatch)
        
        // MSON attribute should match MSON pattern
        val attrMsonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(attributeLine)
        assertNotNull("MSON attribute should match MSON pattern", attrMsonMatch)
        
        // Attributes section should NOT match MSON pattern
        val sectionMsonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(attributesSection)
        assertNull("Attributes section should NOT match MSON pattern", sectionMsonMatch)
    }

    @Test
    fun testNegativeLookaheadWorksWithCaseInsensitive() {
        val testCases = listOf(
            "+ attributes (User)" to false,    // lowercase - should not match MSON
            "+ ATTRIBUTES (User)" to false,   // uppercase - should not match MSON  
            "+ Attributes (User)" to false,   // normal case - should not match MSON
            "+ parameters" to false,          // lowercase - should not match MSON
            "+ PARAMETERS" to false,          // uppercase - should not match MSON
            "+ user_id: 123 (number)" to true // normal attribute - should match MSON
        )
        
        testCases.forEach { (line, shouldMatch) ->
            val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
            if (shouldMatch) {
                assertNotNull("Should match MSON pattern: '$line'", msonMatch)
            } else {
                assertNull("Should NOT match MSON pattern: '$line'", msonMatch)
            }
        }
    }

    @Test
    fun testComplexAttributesPatternMatching() {
        // Test edge cases to ensure the negative lookahead doesn't break normal functionality
        val edgeCases = listOf(
            // These should NOT match MSON pattern (they're section headers)
            "+ Attributes" to false,
            "+ Attributes (User)" to false, 
            "+ Parameters" to false,
            "+ Headers" to false,
            
            // These should match MSON pattern (they're actual attributes)
            "+ attribute_name (string)" to true,
            "+ AttributeValue: test (string)" to true,
            "+ param_value: 123 (number)" to true,
            "+ header_data (object)" to true
        )
        
        edgeCases.forEach { (line, shouldMatchMson) ->
            val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
            
            if (shouldMatchMson) {
                assertNotNull("Should match MSON pattern: '$line'", msonMatch)
                println("✓ MSON match: '$line' -> '${msonMatch?.groups?.get(1)?.value}'")
            } else {
                assertNull("Should NOT match MSON pattern: '$line'", msonMatch)
                println("✓ No MSON match: '$line'")
            }
        }
    }
}
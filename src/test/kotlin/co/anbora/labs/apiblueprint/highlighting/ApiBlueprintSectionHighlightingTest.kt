package co.anbora.labs.apiblueprint.highlighting

import co.anbora.labs.apiblueprint.ide.highlighting.ApiBlueprintColorSettingsPage
import co.anbora.labs.apiblueprint.ide.highlighting.ApiBlueprintHighlighterKeys
import co.anbora.labs.apiblueprint.ide.highlighting.ApiBlueprintRegexUtils
import org.junit.Test
import org.junit.Assert.*

/**
 * Test specifically for section keyword highlighting to ensure all section keywords are properly highlighted.
 */
class ApiBlueprintSectionHighlightingTest {

    @Test
    fun testAllSectionKeywordsHighlighted() {
        val sectionExamples = listOf(
            // Basic sections
            "+ Parameters" to "Parameters",
            "+ Headers" to "Headers", 
            "+ Body" to "Body",
            "+ Schema" to "Schema",
            
            // Attributes sections (using specific patterns)
            "+ Attributes" to "Attributes",
            "+ Attributes (object)" to "Attributes", 
            "+ Attributes (Coupon)" to "Attributes",
            "+ Attributes (array[User])" to "Attributes",
            
            // Request/Response sections
            "+ Request" to "Request",
            "+ Request (application/json)" to "Request",
            "+ Response 200" to "Response",
            "+ Response 200 (application/json)" to "Response",
            "+ Response 404 (application/json)" to "Response"
        )
        
        println("Testing section keyword highlighting:")
        println("====================================")
        
        sectionExamples.forEach { (line, expectedKeyword) ->
            println("Line: '$line'")
            
            var keywordHighlighted = false
            var highlightedKeyword: String? = null
            
            // Check basic section pattern
            val sectionMatch = ApiBlueprintRegexUtils.SECTION_PATTERN.find(line)
            if (sectionMatch != null) {
                highlightedKeyword = sectionMatch.groups[1]?.value
                keywordHighlighted = true
                println("  ✓ SECTION pattern highlights: '$highlightedKeyword'")
            }
            
            // Check Attributes section pattern  
            val attrMatch = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
            if (attrMatch != null) {
                highlightedKeyword = attrMatch.groups[1]?.value
                keywordHighlighted = true
                println("  ✓ ATTRIBUTES pattern highlights: '$highlightedKeyword'")
                
                val typeRef = attrMatch.groups[2]?.value
                if (typeRef != null) {
                    println("    ✓ Type reference also highlighted: '$typeRef'")
                }
            }
            
            // Check simple Attributes pattern
            val simpleAttrMatch = ApiBlueprintRegexUtils.SIMPLE_ATTRIBUTES_PATTERN.find(line)
            if (simpleAttrMatch != null && attrMatch == null) {
                highlightedKeyword = simpleAttrMatch.groups[1]?.value
                keywordHighlighted = true
                println("  ✓ SIMPLE_ATTRIBUTES pattern highlights: '$highlightedKeyword'")
            }
            
            // Check Response pattern
            val responseMatch = ApiBlueprintRegexUtils.RESPONSE_PATTERN.find(line)
            if (responseMatch != null) {
                highlightedKeyword = responseMatch.groups[1]?.value
                keywordHighlighted = true
                println("  ✓ RESPONSE pattern highlights: '$highlightedKeyword'")
                
                val statusCode = responseMatch.groups[2]?.value
                if (statusCode != null) {
                    println("    ✓ Status code also highlighted: '$statusCode'")
                }
            }
            
            // Check Request pattern
            val requestMatch = ApiBlueprintRegexUtils.REQUEST_PATTERN.find(line)
            if (requestMatch != null) {
                highlightedKeyword = requestMatch.groups[1]?.value
                keywordHighlighted = true
                println("  ✓ REQUEST pattern highlights: '$highlightedKeyword'")
            }
            
            // Validate the result
            assertTrue("Section keyword should be highlighted in: '$line'", keywordHighlighted)
            assertEquals("Highlighted keyword should match expected", expectedKeyword, highlightedKeyword)
            
            println()
        }
    }

    @Test
    fun testUserExampleSectionHighlighting() {
        val userExampleLines = listOf(
            "+ Parameters",
            "+ Attributes (object)",
            "+ Response 200 (application/json)",
            "+ Attributes (Coupon)",
            "+ Attributes (array[Coupon])",
            "+ Parameters",
            "+ Response 200 (application/json)",
            "+ Attributes (Coupons)",
            "+ Attributes (object)",
            "+ Request (application/json)",
            "+ Response 200 (application/json)",
            "+ Attributes (Coupon)"
        )
        
        println("Testing user's specific example lines:")
        println("====================================")
        
        var allHighlighted = true
        var failedLines = mutableListOf<String>()
        
        userExampleLines.forEach { line ->
            var highlighted = false
            
            // Check if any section pattern matches
            if (ApiBlueprintRegexUtils.SECTION_PATTERN.find(line) != null ||
                ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line) != null ||
                ApiBlueprintRegexUtils.SIMPLE_ATTRIBUTES_PATTERN.find(line) != null ||
                ApiBlueprintRegexUtils.RESPONSE_PATTERN.find(line) != null ||
                ApiBlueprintRegexUtils.REQUEST_PATTERN.find(line) != null) {
                highlighted = true
                println("✓ '$line' - Highlighted")
            } else {
                allHighlighted = false
                failedLines.add(line)
                println("❌ '$line' - NOT highlighted")
            }
        }
        
        if (!allHighlighted) {
            fail("Some section lines were not highlighted: $failedLines")
        } else {
            println("\n✅ All section keywords should be properly highlighted!")
        }
    }

    @Test
    fun testColorSettingsRegistration() {
        // Test that all highlighting keys are registered in the color settings
        val colorSettings = ApiBlueprintColorSettingsPage()
        val descriptors = colorSettings.attributeDescriptors
        
        val expectedKeys = listOf(
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_META,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_API_NAME,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_GROUP,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_RESOURCE,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_ACTION_NAME,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_METHOD,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_URI_VAR,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_SECTION,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_MIME,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_STATUS,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_RELATION,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_TYPE_ANNOTATION,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_NUMBER,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_MSON_TYPE,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_PARAMETER_NAME,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_PROPERTY,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_FLAG,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_STRING_VALUE,
            ApiBlueprintHighlighterKeys.APIBLUEPRINT_DATA_STRUCTURES
        )
        
        val registeredKeys = descriptors.map { it.key }.toSet()
        
        expectedKeys.forEach { key ->
            assertTrue("Highlighting key should be registered in color settings: $key", 
                      registeredKeys.contains(key))
        }
        
        println("✅ All ${expectedKeys.size} highlighting keys are properly registered in color settings")
    }
}
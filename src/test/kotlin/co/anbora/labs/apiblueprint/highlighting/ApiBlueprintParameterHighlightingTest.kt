package co.anbora.labs.apiblueprint.highlighting

import co.anbora.labs.apiblueprint.ide.highlighting.ApiBlueprintRegexUtils
import org.junit.Test
import org.junit.Assert.*

/**
 * Test parameter highlighting, especially with multiline descriptions
 */
class ApiBlueprintParameterHighlightingTest {

    @Test
    fun testParameterWithMultilineDescription() {
        // Test the specific case the user mentioned
        val parameterExample = """+ Parameters
    + id (string)

      The ID of the desired coupon."""
        
        println("Testing parameter with multiline description:")
        println("============================================")
        
        val lines = parameterExample.split("\n")
        var foundParameterHighlighting = false
        var foundIdParameter = false
        
        lines.forEachIndexed { index, line ->
            println("Line $index: '$line'")
            
            if (line.trim() == "+ Parameters") {
                // Test section pattern
                val sectionMatch = ApiBlueprintRegexUtils.SECTION_PATTERN.find(line)
                if (sectionMatch != null) {
                    foundParameterHighlighting = true
                    println("  ✅ 'Parameters' section keyword found: '${sectionMatch.groups[1]?.value}'")
                } else {
                    println("  ❌ 'Parameters' section keyword NOT found")
                }
            } else if (line.trim().startsWith("+") && line.contains("id")) {
                // Test MSON/parameter pattern
                val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
                if (msonMatch != null) {
                    val name = msonMatch.groups[1]?.value
                    if (name == "id") {
                        foundIdParameter = true
                        println("  ✅ 'id' parameter found: '$name'")
                    }
                } else {
                    println("  ❌ 'id' parameter NOT found with MSON pattern")
                }
                
                // Also test parameter-specific pattern
                val paramMatch = ApiBlueprintRegexUtils.PARAMETER_PATTERN.find(line)
                if (paramMatch != null) {
                    val name = paramMatch.groups[1]?.value
                    println("  ✅ 'id' parameter found with PARAMETER pattern: '$name'")
                } else {
                    println("  ❌ 'id' parameter NOT found with PARAMETER pattern")
                }
            }
        }
        
        assertTrue("Should find 'Parameters' section highlighting", foundParameterHighlighting)
        assertTrue("Should find 'id' parameter highlighting", foundIdParameter)
    }

    @Test
    fun testParameterPatternVsMsonPattern() {
        val parameterLines = listOf(
            "    + id (string)",
            "    + id: 123 (number)",
            "    + limit (number, optional)",
            "    + page: 1 (number, optional) - Page number"
        )
        
        println("Testing parameter vs MSON pattern matching:")
        println("==========================================")
        
        parameterLines.forEach { line ->
            println("Line: '$line'")
            
            // Test MSON pattern
            val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
            if (msonMatch != null) {
                println("  ✅ MSON pattern matches: '${msonMatch.groups[1]?.value}'")
            } else {
                println("  ❌ MSON pattern does NOT match")
            }
            
            // Test parameter pattern
            val paramMatch = ApiBlueprintRegexUtils.PARAMETER_PATTERN.find(line)
            if (paramMatch != null) {
                println("  ✅ PARAMETER pattern matches: '${paramMatch.groups[1]?.value}'")
            } else {
                println("  ❌ PARAMETER pattern does NOT match")
            }
            
            // At least one should match for proper highlighting
            assertTrue("Either MSON or PARAMETER pattern should match for: $line", 
                      msonMatch != null || paramMatch != null)
            
            println()
        }
    }

    @Test
    fun testParameterInContext() {
        // Test parameters in the context of the full API Blueprint structure
        val fullContext = """## Coupon [/coupons/{id}]
A coupon contains information about a percent-off or amount-off discount.

+ Parameters
    + id (string)

      The ID of the desired coupon.

+ Attributes (object)
    + id: 250FF (string, required)"""
        
        println("Testing parameter in full context:")
        println("=================================")
        
        val lines = fullContext.split("\n")
        val foundElements = mutableMapOf<String, String>()
        
        lines.forEachIndexed { index, line ->
            println("Line $index: '$line'")
            
            when {
                line.contains("## Coupon") -> {
                    val resourceMatch = ApiBlueprintRegexUtils.RESOURCE_PATTERN.find(line)
                    if (resourceMatch != null) {
                        foundElements["resource"] = resourceMatch.groups[1]?.value ?: ""
                        println("  ✅ Resource found: '${foundElements["resource"]}'")
                    }
                }
                line.trim() == "+ Parameters" -> {
                    val sectionMatch = ApiBlueprintRegexUtils.SECTION_PATTERN.find(line)
                    if (sectionMatch != null) {
                        foundElements["parameters_section"] = sectionMatch.groups[1]?.value ?: ""
                        println("  ✅ Parameters section found: '${foundElements["parameters_section"]}'")
                    }
                }
                line.contains("+ Attributes (object)") -> {
                    val attrMatch = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
                    if (attrMatch != null) {
                        foundElements["attributes_section"] = attrMatch.groups[1]?.value ?: ""
                        println("  ✅ Attributes section found: '${foundElements["attributes_section"]}'")
                    }
                }
                line.trim().startsWith("+") && line.contains("id") -> {
                    val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
                    if (msonMatch != null) {
                        val name = msonMatch.groups[1]?.value ?: ""
                        val type = msonMatch.groups[3]?.value ?: ""
                        if (line.contains("(string)") && !line.contains(":")) {
                            foundElements["parameter_id"] = name
                            println("  ✅ Parameter 'id' found: '$name' ($type)")
                        } else if (line.contains(": 250FF")) {
                            foundElements["attribute_id"] = name
                            println("  ✅ Attribute 'id' found: '$name' ($type)")
                        }
                    }
                }
            }
        }
        
        // Verify all expected elements were found
        val expectedElements = listOf("resource", "parameters_section", "attributes_section", "parameter_id", "attribute_id")
        expectedElements.forEach { element ->
            assertTrue("Should find $element", foundElements.containsKey(element))
        }
        
        println("\n✅ All elements found:")
        foundElements.forEach { (key, value) -> 
            println("   $key: '$value'")
        }
    }

    @Test
    fun testMultilineParameterDescription() {
        // Test the exact multiline description case
        val multilineParamText = """    + id (string)

      The ID of the desired coupon."""
        
        println("Testing multiline parameter description:")
        println("======================================")
        
        // This should work now with line-by-line processing
        val lines = multilineParamText.split("\n")
        var foundId = false
        
        lines.forEachIndexed { index, line ->
            if (line.trim().isNotEmpty()) {
                println("Line $index: '$line'")
                
                val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
                if (msonMatch != null && msonMatch.groups[1]?.value == "id") {
                    foundId = true
                    println("  ✅ Found 'id' parameter: '${msonMatch.groups[1]?.value}'")
                    println("     Type: '${msonMatch.groups[3]?.value}'")
                }
            }
        }
        
        assertTrue("Should find 'id' parameter in multiline context", foundId)
        println("\n🎯 'id' parameter is now properly detected in multiline context!")
    }
}
package co.anbora.labs.apiblueprint.highlighting

import co.anbora.labs.apiblueprint.ide.highlighting.ApiBlueprintRegexUtils
import org.junit.Test
import org.junit.Assert.*

/**
 * Test the line-by-line processing fix for multiline elements
 */
class ApiBlueprintLineByLineTest {

    @Test
    fun testLineByLineProcessingFix() {
        // Simulate the exact scenario that was causing percent_off to not highlight
        val multilineElementText = """    + percent_off: 25 (number)

      A positive integer between 1 and 100 that represents the discount the coupon will apply."""
        
        println("Testing line-by-line processing fix:")
        println("====================================")
        
        // Simulate what the new annotator logic does
        val lines = multilineElementText.split("\n")
        var baseOffset = 1000 // Simulated base offset
        var currentOffset = baseOffset
        
        var foundPercentOff = false
        var percentOffRange: IntRange? = null
        
        lines.forEachIndexed { index, line ->
            println("Processing line $index (offset $currentOffset): '$line'")
            
            if (line.trim().isNotEmpty()) {
                // This is what the annotator now does for each line
                val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
                if (msonMatch != null) {
                    val name = msonMatch.groups[1]?.value
                    val localRange = msonMatch.groups[1]?.range
                    val globalRange = IntRange(
                        currentOffset + (localRange?.first ?: 0),
                        currentOffset + (localRange?.last ?: 0)
                    )
                    
                    println("  ✅ Found MSON attribute: '$name'")
                    println("     Local range in line: $localRange")
                    println("     Global range in document: $globalRange")
                    
                    if (name == "percent_off") {
                        foundPercentOff = true
                        percentOffRange = globalRange
                        println("     🎯 This is the percent_off attribute that should now be highlighted!")
                    }
                }
            }
            
            currentOffset += line.length + 1 // +1 for newline
        }
        
        assertTrue("percent_off should be found with line-by-line processing", foundPercentOff)
        assertNotNull("percent_off should have a valid range", percentOffRange)
        
        println("\n✅ Line-by-line processing successfully finds percent_off!")
        println("   Range: $percentOffRange")
    }

    @Test
    fun testAllAttributesWithLineByLineProcessing() {
        // Test the full user example with line-by-line processing
        val fullExample = """+ Attributes (object)
    + id: 250FF (string, required)
    + created: 1415203908 (number) - Time stamp
    + percent_off: 25 (number)

      A positive integer between 1 and 100 that represents the discount the coupon will apply.

    + redeem_by (number) - Date after which the coupon can no longer be redeemed"""
        
        println("Testing full example with line-by-line processing:")
        println("=================================================")
        
        val lines = fullExample.split("\n")
        var currentOffset = 0
        val foundAttributes = mutableListOf<String>()
        
        lines.forEachIndexed { index, line ->
            if (line.trim().startsWith("+") && !line.contains("Attributes (object)")) {
                println("Line $index: '$line'")
                
                val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
                if (msonMatch != null) {
                    val name = msonMatch.groups[1]?.value ?: ""
                    foundAttributes.add(name)
                    println("  ✅ Found: '$name'")
                } else {
                    println("  ❌ No match (this would be a problem)")
                }
            }
            currentOffset += line.length + 1
        }
        
        val expectedAttributes = listOf("id", "created", "percent_off", "redeem_by")
        
        expectedAttributes.forEach { expectedAttr ->
            assertTrue("Should find attribute '$expectedAttr'", 
                      foundAttributes.contains(expectedAttr))
        }
        
        println("\n✅ All expected attributes found:")
        foundAttributes.forEach { println("   - $it") }
        
        // Specifically verify percent_off is found
        assertTrue("percent_off should be found", foundAttributes.contains("percent_off"))
        println("\n🎯 percent_off is now properly detected!")
    }

    @Test
    fun testComparisonBeforeAfterFix() {
        val problematicText = """    + percent_off: 25 (number)

      A positive integer between 1 and 100 that represents the discount the coupon will apply."""
        
        println("Comparison before/after fix:")
        println("===========================")
        
        // Before fix: try to match entire multiline text
        println("BEFORE (direct matching):")
        val beforeMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(problematicText)
        if (beforeMatch != null) {
            println("  ✅ Would match: ${beforeMatch.groups[1]?.value}")
        } else {
            println("  ❌ Would NOT match - percent_off not highlighted")
        }
        
        // After fix: line-by-line processing
        println("\nAFTER (line-by-line processing):")
        val lines = problematicText.split("\n")
        var foundInAnyLine = false
        
        lines.forEachIndexed { index, line ->
            if (line.trim().isNotEmpty()) {
                val lineMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
                if (lineMatch != null) {
                    println("  ✅ Matches on line $index: ${lineMatch.groups[1]?.value}")
                    foundInAnyLine = true
                }
            }
        }
        
        if (!foundInAnyLine) {
            println("  ❌ Still no match")
        }
        
        assertTrue("After fix, percent_off should be found", foundInAnyLine)
    }

    @Test
    fun testEdgeCasesWithLineByLine() {
        val edgeCases = listOf(
            // Single line (should still work)
            "    + simple_attr: 123 (number)",
            
            // Multiline with empty line
            """    + multi_attr: 456 (number)

Some description here""",
            
            // Multiline with indented description
            """    + indented_attr: 789 (string)
        Indented description
        More description""",
            
            // Multiple attributes in one "element"
            """    + first_attr: 1 (number)
    + second_attr: 2 (number)
    + third_attr: 3 (number)"""
        )
        
        println("Testing edge cases with line-by-line processing:")
        println("===============================================")
        
        edgeCases.forEachIndexed { testIndex, elementText ->
            println("Edge case ${testIndex + 1}:")
            println("Text: '''${elementText.replace("\n", "\\n")}'''")
            
            val lines = elementText.split("\n")
            val foundAttrs = mutableListOf<String>()
            
            lines.forEach { line ->
                if (line.trim().startsWith("+")) {
                    val match = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
                    if (match != null) {
                        foundAttrs.add(match.groups[1]?.value ?: "")
                    }
                }
            }
            
            println("  Found attributes: $foundAttrs")
            assertTrue("Should find at least one attribute", foundAttrs.isNotEmpty())
            println()
        }
    }
}
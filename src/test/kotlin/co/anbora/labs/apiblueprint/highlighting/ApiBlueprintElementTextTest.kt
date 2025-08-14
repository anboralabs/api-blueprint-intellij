package co.anbora.labs.apiblueprint.highlighting

import co.anbora.labs.apiblueprint.ide.highlighting.ApiBlueprintRegexUtils
import org.junit.Test

/**
 * Test how PSI element text might contain multiline content
 */
class ApiBlueprintElementTextTest {

    @Test
    fun testMultilineElementText() {
        // Simulate what the PSI element text might contain for the percent_off case
        val possibleElementTexts = listOf(
            // Single line element
            "    + percent_off: 25 (number)",
            
            // Multiline element with description
            """    + percent_off: 25 (number)

      A positive integer between 1 and 100 that represents the discount the coupon will apply.""",
            
            // Multiline element with different spacing
            """    + percent_off: 25 (number)
      A positive integer between 1 and 100 that represents the discount the coupon will apply.""",
            
            // Element with trailing newlines
            "    + percent_off: 25 (number)\n\n",
            
            // Element with mixed content
            """    + percent_off: 25 (number)

      A positive integer between 1 and 100 that represents the discount.

    + redeem_by (number) - Date after which the coupon can no longer be redeemed"""
        )
        
        println("Testing multiline PSI element text scenarios:")
        println("============================================")
        
        possibleElementTexts.forEachIndexed { index, elementText ->
            println("Scenario ${index + 1}:")
            println("Element text: '''${elementText.replace("\n", "\\n")}'''")
            
            // Test direct MSON pattern matching (current approach)
            val directMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(elementText)
            if (directMatch != null) {
                println("  ✓ Direct pattern matches: '${directMatch.groups[1]?.value}'")
            } else {
                println("  ❌ Direct pattern does NOT match")
            }
            
            // Test line-by-line processing (better approach)
            val lines = elementText.split("\n")
            var foundInLine = false
            lines.forEachIndexed { lineIndex, line ->
                if (line.trim().startsWith("+") && line.contains("percent_off")) {
                    val lineMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
                    if (lineMatch != null) {
                        println("  ✓ Line-by-line matches on line $lineIndex: '${lineMatch.groups[1]?.value}'")
                        foundInLine = true
                    } else {
                        println("  ❌ Line-by-line does NOT match on line $lineIndex")
                    }
                }
            }
            
            if (!foundInLine && elementText.contains("percent_off")) {
                println("  ⚠️  This scenario would cause highlighting failure!")
            }
            
            println()
        }
    }

    @Test
    fun testRegexMultilineFlags() {
        // Test if our current patterns work with multiline content
        val multilineText = """    + percent_off: 25 (number)

      A positive integer between 1 and 100 that represents the discount the coupon will apply."""
        
        println("Testing regex multiline behavior:")
        println("=================================")
        
        // Current pattern (single line mode)
        val currentMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(multilineText)
        if (currentMatch != null) {
            println("✓ Current pattern matches multiline text")
            println("  Matched: '${currentMatch.value}'")
        } else {
            println("❌ Current pattern does NOT match multiline text")
        }
        
        // Test with multiline flag
        val multilinePattern = Regex(
            ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.pattern,
            setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE)
        )
        val multilineMatch = multilinePattern.find(multilineText)
        if (multilineMatch != null) {
            println("✓ Multiline pattern matches")
            println("  Matched: '${multilineMatch.value}'")
        } else {
            println("❌ Multiline pattern does NOT match")
        }
        
        // Test first line only
        val firstLine = multilineText.split("\n")[0]
        val firstLineMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(firstLine)
        if (firstLineMatch != null) {
            println("✅ First line only matches: '${firstLineMatch.groups[1]?.value}'")
        } else {
            println("❌ First line does NOT match")
        }
    }

    @Test
    fun testLineBasedProcessing() {
        // Test the solution: process element text line by line
        val problematicElementText = """    + percent_off: 25 (number)

      A positive integer between 1 and 100 that represents the discount the coupon will apply."""
        
        println("Testing line-based processing solution:")
        println("======================================")
        
        val lines = problematicElementText.split("\n")
        var cumulativeOffset = 0
        
        lines.forEachIndexed { index, line ->
            println("Line $index (offset $cumulativeOffset): '$line'")
            
            if (line.trim().isNotEmpty()) {
                val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
                if (msonMatch != null) {
                    val name = msonMatch.groups[1]?.value
                    val nameRange = msonMatch.groups[1]?.range
                    println("  ✅ Found MSON attribute: '$name'")
                    println("     Local range: $nameRange")
                    println("     Global range would be: ${nameRange?.first?.plus(cumulativeOffset)}..${nameRange?.last?.plus(cumulativeOffset)}")
                } else if (line.trim().startsWith("+")) {
                    println("  ❌ Line starts with '+' but doesn't match MSON pattern")
                }
            }
            
            cumulativeOffset += line.length + 1 // +1 for newline character
        }
    }
}
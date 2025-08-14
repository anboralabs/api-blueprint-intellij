package co.anbora.labs.apiblueprint.highlighting

import co.anbora.labs.apiblueprint.ide.highlighting.ApiBlueprintRegexUtils
import org.junit.Test

/**
 * Test the exact format and spacing from the user's example
 */
class ApiBlueprintExactFormatTest {

    @Test
    fun testExactUserFormat() {
        // Copy the exact text from the user's message 
        val userText = """+ Attributes (object)
    + id: 250FF (string, required)
    + created: 1415203908 (number) - Time stamp
    + percent_off: 25 (number)

      A positive integer between 1 and 100 that represents the discount the coupon will apply.

    + redeem_by (number) - Date after which the coupon can no longer be redeemed"""
        
        val lines = userText.split("\n")
        
        println("Testing exact user format:")
        println("=========================")
        
        lines.forEachIndexed { index, line ->
            println("Line $index: '$line'")
            println("  Hex bytes: ${line.toByteArray().joinToString(" ") { "%02x".format(it) }}")
            println("  Char codes: ${line.toCharArray().joinToString(" ") { "${it.code}:$it" }}")
            
            if (line.trim().startsWith("+") && line.contains(":")) {
                // Test MSON pattern
                val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
                if (msonMatch != null) {
                    println("  ✓ MSON matches - Name: '${msonMatch.groups[1]?.value}'")
                } else {
                    println("  ❌ MSON does NOT match")
                }
            }
            println()
        }
    }

    @Test
    fun testWhitespaceVariations() {
        val variations = listOf(
            "    + percent_off: 25 (number)",           // 4 spaces
            "\t+ percent_off: 25 (number)",             // 1 tab
            "        + percent_off: 25 (number)",       // 8 spaces
            "  + percent_off: 25 (number)",             // 2 spaces
            " + percent_off: 25 (number)"               // 1 space
        )
        
        println("Testing whitespace variations for percent_off:")
        println("==============================================")
        
        variations.forEach { line ->
            println("Line: '$line'")
            
            val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
            if (msonMatch != null) {
                println("  ✓ Matches - Name: '${msonMatch.groups[1]?.value}' at ${msonMatch.groups[1]?.range}")
            } else {
                println("  ❌ Does NOT match")
                
                // Debug the regex step by step
                val steps = listOf(
                    "^\\s*" to "Leading whitespace",
                    "^\\s*\\+" to "Plus symbol",
                    "^\\s*\\+\\s+" to "Plus and spaces",
                    "^\\s*\\+\\s+percent_off" to "Plus, spaces, and percent_off",
                    "^\\s*\\+\\s+percent_off:" to "Plus, spaces, percent_off, and colon"
                )
                
                steps.forEach { (pattern, desc) ->
                    if (Regex(pattern).find(line) != null) {
                        println("    ✓ $desc matches")
                    } else {
                        println("    ❌ $desc does NOT match")
                    }
                }
            }
            println()
        }
    }

    @Test
    fun testPercentOffSpecifically() {
        val percentOffLine = "    + percent_off: 25 (number)"
        
        println("Detailed analysis of percent_off line:")
        println("=====================================")
        println("Line: '$percentOffLine'")
        
        // Test current MSON pattern
        val currentPattern = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN
        println("Current MSON pattern: ${currentPattern.pattern}")
        
        val match = currentPattern.find(percentOffLine)
        if (match != null) {
            println("✓ Current pattern MATCHES")
            match.groups.forEachIndexed { index, group ->
                if (group != null) {
                    println("  Group $index: '${group.value}' at range ${group.range}")
                }
            }
        } else {
            println("❌ Current pattern does NOT match")
        }
        
        // Test if it could be matching a section pattern instead
        val sectionMatch = ApiBlueprintRegexUtils.SECTION_PATTERN.find(percentOffLine)
        if (sectionMatch != null) {
            println("❌ ERROR: Line is matching SECTION pattern instead! Value: '${sectionMatch.groups[1]?.value}'")
        }
        
        val attributesMatch = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(percentOffLine)
        if (attributesMatch != null) {
            println("❌ ERROR: Line is matching ATTRIBUTES section pattern instead!")
        }
        
        // Compare with a working line
        val workingLine = "    + id: 250FF (string, required)"
        val workingMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(workingLine)
        if (workingMatch != null) {
            println("✓ Comparison: 'id' line DOES match MSON pattern")
            workingMatch.groups.forEachIndexed { index, group ->
                if (group != null && index <= 3) {
                    println("  Group $index: '${group.value}'")
                }
            }
        }
    }

    @Test
    fun testRegexEdgeCases() {
        val edgeCaseLines = listOf(
            "    + percent_off: 25 (number)" to true,
            "    + percent-off: 25 (number)" to false,    // hyphen instead of underscore
            "    + percentOff: 25 (number)" to true,      // camelCase
            "    + PERCENT_OFF: 25 (number)" to true,     // uppercase
            "    + percent_: 25 (number)" to true,        // trailing underscore
            "    + _percent_off: 25 (number)" to true,    // leading underscore
            "    + 25_percent_off: 25 (number)" to false, // starts with number
        )
        
        println("Testing regex edge cases:")
        println("========================")
        
        edgeCaseLines.forEach { (line, shouldMatch) ->
            val match = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
            val actuallyMatches = match != null
            
            println("Line: '$line'")
            if (actuallyMatches == shouldMatch) {
                if (actuallyMatches) {
                    println("  ✓ Correctly matches - Name: '${match?.groups?.get(1)?.value}'")
                } else {
                    println("  ✓ Correctly does NOT match")
                }
            } else {
                if (actuallyMatches) {
                    println("  ❌ Unexpectedly matches - Name: '${match?.groups?.get(1)?.value}'")
                } else {
                    println("  ❌ Should match but does NOT match")
                }
            }
        }
    }
}
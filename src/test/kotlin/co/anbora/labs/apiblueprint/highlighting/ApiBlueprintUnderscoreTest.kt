package co.anbora.labs.apiblueprint.highlighting

import org.junit.Test
import org.junit.Assert.*

/**
 * Test specifically for the percent_off highlighting issue.
 */
class ApiBlueprintUnderscoreTest {

    @Test
    fun testSpecificUserExample() {
        val userExample = """
+ Attributes (object)
    + id: 250FF (string, required)
    + created: 1415203908 (number) - Time stamp
    + percent_off: 25 (number)

      A positive integer between 1 and 100 that represents the discount the coupon will apply.

    + redeem_by (number) - Date after which the coupon can no longer be redeemed
        """.trimIndent()
        
        val lines = userExample.split("\n")
        
        println("Testing user's specific underscore example:")
        println("========================================")
        
        lines.forEachIndexed { index, line ->
            if (line.trim().startsWith("+") && !line.contains("Attributes")) {
                println("Line $index: '$line'")
                
                // Test MSON attribute pattern
                val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
                if (msonMatch != null) {
                    val name = msonMatch.groups[1]?.value
                    val value = msonMatch.groups[2]?.value
                    val type = msonMatch.groups[3]?.value
                    val description = msonMatch.groups[4]?.value
                    
                    println("  ✓ MSON pattern matches:")
                    println("    Name: '$name' (range: ${msonMatch.groups[1]?.range})")
                    println("    Value: '$value' (range: ${msonMatch.groups[2]?.range})")
                    println("    Type: '$type' (range: ${msonMatch.groups[3]?.range})")
                    if (description != null) {
                        println("    Description: '$description' (range: ${msonMatch.groups[4]?.range})")
                    }
                } else {
                    println("  ❌ MSON pattern does NOT match - This is the problem!")
                    
                    // Let's debug why it doesn't match
                    println("    Debugging pattern mismatch:")
                    println("    Line contains: '${line.replace(" ", "·").replace("\t", "→")}'")
                    
                    // Test parts of the pattern
                    val basicPattern = Regex("^\\s*\\+\\s+([A-Za-z_][A-Za-z0-9_]*)")
                    val basicMatch = basicPattern.find(line)
                    if (basicMatch != null) {
                        println("    ✓ Basic '+ name' part matches: '${basicMatch.groups[1]?.value}'")
                    } else {
                        println("    ❌ Basic '+ name' part doesn't match")
                    }
                    
                    val withColon = Regex("^\\s*\\+\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*:")
                    val colonMatch = withColon.find(line)
                    if (colonMatch != null) {
                        println("    ✓ '+ name:' part matches: '${colonMatch.groups[1]?.value}'")
                    } else {
                        println("    ❌ '+ name:' part doesn't match")
                    }
                }
                println()
            }
        }
    }

    @Test
    fun testUnderscoreAttributeNames() {
        val underscoreAttributes = listOf(
            "    + id: 250FF (string, required)",
            "    + created: 1415203908 (number) - Time stamp",
            "    + percent_off: 25 (number)",
            "    + redeem_by (number) - Date after which the coupon can no longer be redeemed",
            "    + user_name: \"john_doe\" (string)",
            "    + is_active: true (boolean)",
            "    + first_name: \"John\" (string)",
            "    + last_name: \"Doe\" (string)",
            "    + created_at: \"2023-01-01\" (string)",
            "    + updated_at (string, optional)"
        )
        
        println("Testing various underscore attribute patterns:")
        println("===========================================")
        
        var allMatched = true
        val failedLines = mutableListOf<String>()
        
        underscoreAttributes.forEach { line ->
            println("Testing: '$line'")
            
            val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
            if (msonMatch != null) {
                val name = msonMatch.groups[1]?.value
                println("  ✓ Matches - Name: '$name'")
            } else {
                println("  ❌ Does NOT match")
                allMatched = false
                failedLines.add(line)
            }
        }
        
        if (!allMatched) {
            println("\nFailed lines:")
            failedLines.forEach { println("  - $it") }
            fail("Some underscore attributes didn't match: $failedLines")
        }
    }

    @Test
    fun testMsonPatternComponents() {
        val problematicLine = "    + percent_off: 25 (number)"
        
        println("Debugging MSON pattern components for: '$problematicLine'")
        println("======================================================")
        
        // Current pattern
        val currentPattern = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN
        println("Current pattern: ${currentPattern.pattern}")
        
        val match = currentPattern.find(problematicLine)
        if (match != null) {
            println("✓ Current pattern matches")
        } else {
            println("❌ Current pattern does NOT match")
            
            // Let's break down the pattern piece by piece
            val patterns = listOf(
                "^\\s*\\+\\s+" to "Leading whitespace and plus",
                "^\\s*\\+\\s+([A-Za-z_][A-Za-z0-9_]*)" to "Plus and attribute name",
                "^\\s*\\+\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*:" to "Plus, name, and colon",
                "^\\s*\\+\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*:\\s*([^(\\-]+?)" to "Plus, name, colon, and value",
                "^\\s*\\+\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*:\\s*([^(\\-]+?)\\s*\\(([^)]+)\\)" to "Plus, name, colon, value, and type"
            )
            
            patterns.forEach { (pattern, description) ->
                val testPattern = Regex(pattern)
                val testMatch = testPattern.find(problematicLine)
                if (testMatch != null) {
                    println("  ✓ $description matches")
                    testMatch.groups.forEachIndexed { index, group ->
                        if (index > 0 && group != null) {
                            println("    Group $index: '${group.value}' (${group.range})")
                        }
                    }
                } else {
                    println("  ❌ $description does NOT match")
                }
            }
        }
    }

    @Test  
    fun testRegexNegativeLookahead() {
        val line = "    + percent_off: 25 (number)"
        
        println("Testing negative lookahead in MSON pattern:")
        println("==========================================")
        
        // Test without negative lookahead
        val withoutLookahead = Regex("^\\s*\\+\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*(?::\\s*([^(\\-]+?)\\s*)?(?:\\(([^)]+)\\))?\\s*(?:-\\s*(.*))?$")
        val withoutMatch = withoutLookahead.find(line)
        
        if (withoutMatch != null) {
            println("✓ Pattern WITHOUT negative lookahead matches")
            println("  Name: '${withoutMatch.groups[1]?.value}'")
        } else {
            println("❌ Pattern WITHOUT negative lookahead does NOT match")
        }
        
        // Test with negative lookahead (current pattern)
        val withLookahead = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN
        val withMatch = withLookahead.find(line)
        
        if (withMatch != null) {
            println("✓ Pattern WITH negative lookahead matches")
            println("  Name: '${withMatch.groups[1]?.value}'")
        } else {
            println("❌ Pattern WITH negative lookahead does NOT match")
            
            // Check if percent_off is being caught by the exclusion
            val exclusionPattern = "(?!(?:Attributes|Parameters|Headers|Body|Schema|Request|Response|Default|Relation)\\b)"
            println("Negative lookahead excludes: Attributes|Parameters|Headers|Body|Schema|Request|Response|Default|Relation")
            println("'percent_off' should NOT be excluded by this pattern")
            
            // Test the exclusion specifically
            if ("percent_off".matches(Regex("(?:Attributes|Parameters|Headers|Body|Schema|Request|Response|Default|Relation)\\b", RegexOption.IGNORE_CASE))) {
                println("❌ 'percent_off' is being excluded by negative lookahead!")
            } else {
                println("✓ 'percent_off' should not be excluded by negative lookahead")
            }
        }
    }
}
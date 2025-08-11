package co.anbora.labs.apiblueprint.highlighting

import org.junit.Test
import org.junit.Assert.*

/**
 * Test if multi-line descriptions after attributes affect highlighting
 */
class ApiBlueprintMultilineTest {

    @Test
    fun testMultilineDescriptionEffect() {
        // Test percent_off with and without the multiline description
        val withoutDescription = "    + percent_off: 25 (number)"
        val withDescription = """    + percent_off: 25 (number)

      A positive integer between 1 and 100 that represents the discount the coupon will apply."""
        
        println("Testing multiline description effect:")
        println("====================================")
        
        // Test the line by itself
        println("1. Testing without description:")
        println("Line: '$withoutDescription'")
        val match1 = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(withoutDescription)
        if (match1 != null) {
            println("  ✓ Matches - Name: '${match1.groups[1]?.value}'")
        } else {
            println("  ❌ Does NOT match")
        }
        
        // Test just the first line when followed by description
        val firstLineOnly = withDescription.split("\n")[0]
        println("\n2. Testing first line of multiline block:")
        println("Line: '$firstLineOnly'")
        val match2 = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(firstLineOnly)
        if (match2 != null) {
            println("  ✓ Matches - Name: '${match2.groups[1]?.value}'")
        } else {
            println("  ❌ Does NOT match")
        }
        
        // Test if the full multiline text affects pattern matching
        println("\n3. Testing full multiline block:")
        val lines = withDescription.split("\n")
        lines.forEachIndexed { index, line ->
            if (line.trim().isNotEmpty()) {
                println("  Line $index: '$line'")
                val match = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
                if (match != null) {
                    println("    ✓ Matches MSON - Name: '${match.groups[1]?.value}'")
                } else {
                    println("    - No MSON match")
                }
            }
        }
    }

    @Test
    fun testAllAttributesInContext() {
        // Test all attributes in the exact context from the user's example
        val fullContext = """+ Attributes (object)
    + id: 250FF (string, required)
    + created: 1415203908 (number) - Time stamp
    + percent_off: 25 (number)

      A positive integer between 1 and 100 that represents the discount the coupon will apply.

    + redeem_by (number) - Date after which the coupon can no longer be redeemed"""
        
        println("Testing all attributes in full context:")
        println("======================================")
        
        val lines = fullContext.split("\n")
        lines.forEachIndexed { index, line ->
            if (line.trim().startsWith("+") && !line.contains("Attributes (object)")) {
                println("Line $index: '$line'")
                
                // Test MSON pattern
                val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
                if (msonMatch != null) {
                    val name = msonMatch.groups[1]?.value
                    println("  ✓ MSON matches - Name: '$name' (range: ${msonMatch.groups[1]?.range})")
                    
                    // Check if the name should be highlighted
                    if (name == "percent_off") {
                        println("    🔍 This is the percent_off line - should be highlighted!")
                    }
                } else {
                    println("  ❌ MSON does NOT match")
                    if (line.contains("percent_off")) {
                        println("    ⚠️  This is the problematic percent_off line!")
                    }
                }
            } else if (line.trim().isNotEmpty() && !line.trim().startsWith("+")) {
                println("Line $index (description): '$line'")
            }
        }
    }

    @Test
    fun testAnnotationProcessingOrder() {
        // Simulate how the annotator processes the percent_off line
        val line = "    + percent_off: 25 (number)"
        
        println("Simulating annotation processing order:")
        println("======================================")
        println("Line: '$line'")
        
        // Check in the order the annotator processes patterns
        
        // 1. Check metadata patterns (should not match)
        if (ApiBlueprintRegexUtils.FORMAT_PATTERN.matches(line) || 
            ApiBlueprintRegexUtils.HOST_PATTERN.matches(line)) {
            println("❌ ERROR: Matches metadata pattern (should not)")
        } else {
            println("✓ Does not match metadata patterns (correct)")
        }
        
        // 2. Check group patterns (should not match)
        if (ApiBlueprintRegexUtils.GROUP_PATTERN.find(line) != null) {
            println("❌ ERROR: Matches group pattern (should not)")
        } else {
            println("✓ Does not match group patterns (correct)")
        }
        
        // 3. Check resource patterns (should not match)
        if (ApiBlueprintRegexUtils.RESOURCE_PATTERN.find(line) != null) {
            println("❌ ERROR: Matches resource pattern (should not)")
        } else {
            println("✓ Does not match resource patterns (correct)")
        }
        
        // 4. Check section patterns (should not match due to negative lookahead)
        val sectionMatch = ApiBlueprintRegexUtils.SECTION_PATTERN.find(line)
        if (sectionMatch != null) {
            println("❌ ERROR: Matches section pattern: '${sectionMatch.groups[1]?.value}' (should not)")
        } else {
            println("✓ Does not match section patterns (correct)")
        }
        
        // 5. Check attributes section patterns (should not match)
        if (ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line) != null) {
            println("❌ ERROR: Matches attributes section pattern (should not)")
        } else {
            println("✓ Does not match attributes section patterns (correct)")
        }
        
        // 6. Check request/response patterns (should not match)
        if (ApiBlueprintRegexUtils.REQUEST_PATTERN.find(line) != null ||
            ApiBlueprintRegexUtils.RESPONSE_PATTERN.find(line) != null) {
            println("❌ ERROR: Matches request/response patterns (should not)")
        } else {
            println("✓ Does not match request/response patterns (correct)")
        }
        
        // 7. Finally check MSON pattern (should match)
        val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
        if (msonMatch != null) {
            println("✅ MATCHES MSON pattern correctly!")
            println("   Name: '${msonMatch.groups[1]?.value}' at range ${msonMatch.groups[1]?.range}")
            println("   This should result in highlighting at character positions 6-16")
        } else {
            println("❌ ERROR: Does NOT match MSON pattern (this would be the problem)")
        }
    }
}
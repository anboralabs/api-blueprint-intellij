package co.anbora.labs.apiblueprint.highlighting

import org.junit.Test
import org.junit.Assert.*

/**
 * Test offset calculations with indented and whitespace-containing text.
 */
class ApiBlueprintOffsetTest {

    @Test
    fun testAttributesWithLeadingWhitespace() {
        // Test various indentation levels
        val testCases = listOf(
            "    + Attributes (Coupon)" to 4,  // 4 spaces
            "\t+ Attributes (User)" to 1,      // 1 tab
            "  + Attributes (object)" to 2,    // 2 spaces
            "+ Attributes (Product)" to 0      // no leading spaces
        )
        
        testCases.forEach { (line, leadingSpaces) ->
            val match = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
            assertNotNull("Should match indented line: '$line'", match)
            
            val attributesGroup = match?.groups?.get(1)
            assertNotNull("Should find Attributes group in: '$line'", attributesGroup)
            
            val typeGroup = match?.groups?.get(2)
            assertNotNull("Should find type group in: '$line'", typeGroup)
            
            // Verify the match positions account for leading whitespace
            val attributesStart = attributesGroup?.range?.first ?: -1
            assertTrue("Attributes should start after whitespace (pos >= $leadingSpaces): $line", 
                attributesStart >= leadingSpaces)
            
            val typeStart = typeGroup?.range?.first ?: -1
            val attributesEnd = attributesGroup?.range?.last ?: -1
            assertTrue("Type should start after Attributes: $line", typeStart > attributesEnd)
            
            println("Line: '$line'")
            println("  Attributes at: $attributesStart-${attributesGroup?.range?.last}")
            println("  Type at: $typeStart-${typeGroup?.range?.last}")
            println("  Expected leading whitespace: $leadingSpaces")
        }
    }

    @Test
    fun testMsonAttributesWithIndentation() {
        val indentedAttributes = listOf(
            "    + id: 250FF (string, required)",
            "        + created: 1415203908 (number) - Time stamp",
            "\t\t+ percent_off: 25 (number)",
            "  + redeem_by (number) - Date after"
        )
        
        indentedAttributes.forEach { line ->
            val match = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
            assertNotNull("Should match indented MSON attribute: '$line'", match)
            
            val nameGroup = match?.groups?.get(1)
            assertNotNull("Should extract attribute name from: '$line'", nameGroup)
            
            // The attribute name should start after the leading whitespace and "+ "
            val nameStart = nameGroup?.range?.first ?: -1
            val plusPos = line.indexOf('+')
            assertTrue("Attribute name should start after '+ ': $line", nameStart > plusPos + 1)
            
            println("MSON line: '$line'")
            println("  '+' at position: $plusPos")
            println("  Attribute name at: $nameStart-${nameGroup?.range?.last}")
        }
    }

    @Test
    fun testParametersWithIndentation() {
        val indentedParams = listOf(
            "    + limit (number, optional)",
            "\t+ id (string, required)",
            "        + page: 1 (number) - Page number"
        )
        
        indentedParams.forEach { line ->
            val match = ApiBlueprintRegexUtils.PARAMETER_PATTERN.find(line)
            assertNotNull("Should match indented parameter: '$line'", match)
            
            val nameGroup = match?.groups?.get(1)
            assertNotNull("Should extract parameter name from: '$line'", nameGroup)
            
            println("Parameter line: '$line'")
            println("  Parameter name: '${nameGroup?.value}' at ${nameGroup?.range}")
        }
    }

    @Test
    fun testOffsetCalculationWithRealContent() {
        // Simulate how the annotator would process a multi-line text block
        val content = """
        + Attributes (Coupon)
            + id: 250FF (string, required)
            + created: 1415203908 (number) - Time stamp
        """.trimIndent()
        
        val lines = content.split("\n")
        var currentOffset = 0
        
        lines.forEach { line ->
            println("Processing line: '$line' (offset: $currentOffset)")
            
            // Test Attributes pattern
            val attrMatch = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
            if (attrMatch != null) {
                val attributesGroup = attrMatch.groups[1]
                val typeGroup = attrMatch.groups[2]
                
                println("  Attributes found at: ${attributesGroup?.range}")
                println("  Type found at: ${typeGroup?.range}")
                
                // Verify ranges are within the line length
                assertTrue("Attributes range should be within line length", 
                    (attributesGroup?.range?.last ?: -1) < line.length)
                assertTrue("Type range should be within line length", 
                    (typeGroup?.range?.last ?: -1) < line.length)
            }
            
            // Test MSON attribute pattern
            val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
            if (msonMatch != null) {
                val nameGroup = msonMatch.groups[1]
                val valueGroup = msonMatch.groups[2]
                val typeGroup = msonMatch.groups[3]
                
                println("  MSON attribute found:")
                println("    Name: '${nameGroup?.value}' at ${nameGroup?.range}")
                println("    Value: '${valueGroup?.value?.trim()}' at ${valueGroup?.range}")
                println("    Type: '${typeGroup?.value}' at ${typeGroup?.range}")
                
                // Verify all ranges are within bounds
                nameGroup?.range?.let { range ->
                    assertTrue("Name range should be within line", range.last < line.length)
                }
                valueGroup?.range?.let { range ->
                    assertTrue("Value range should be within line", range.last < line.length)
                }
                typeGroup?.range?.let { range ->
                    assertTrue("Type range should be within line", range.last < line.length)
                }
            }
            
            currentOffset += line.length + 1 // +1 for newline
        }
    }

    @Test
    fun testWhitespaceOnlyLinesIgnored() {
        val whitespaceLines = listOf(
            "    ",      // spaces only
            "\t\t",     // tabs only  
            "",         // empty
            "   \t   "  // mixed whitespace
        )
        
        whitespaceLines.forEach { line ->
            // These should not match any patterns
            val attrMatch = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
            val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
            val sectionMatch = ApiBlueprintRegexUtils.SECTION_PATTERN.find(line)
            
            assertNull("Whitespace line should not match Attributes: '$line'", attrMatch)
            assertNull("Whitespace line should not match MSON: '$line'", msonMatch)
            assertNull("Whitespace line should not match Section: '$line'", sectionMatch)
        }
    }
}
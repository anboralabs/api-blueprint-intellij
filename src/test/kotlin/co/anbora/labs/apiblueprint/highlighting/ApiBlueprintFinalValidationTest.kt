package co.anbora.labs.apiblueprint.highlighting

import org.junit.Test
import org.junit.Assert.*

/**
 * Final validation test for the reported issue: + Attributes (Coupon)
 * Should highlight "Attributes" as section and "Coupon" as type, not partial matches.
 */
class ApiBlueprintFinalValidationTest {

    @Test
    fun testAttributesCouponHighlightingExactIssue() {
        val line = "    + Attributes (Coupon)"
        
        // Verify correct pattern matching
        val match = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
        assertNotNull("Line should match Attributes section pattern", match)
        
        // Verify complete word captures, not partial
        val attributesGroup = match?.groups?.get(1)
        val typeGroup = match?.groups?.get(2)
        
        assertEquals("Should capture complete 'Attributes' word", "Attributes", attributesGroup?.value)
        assertEquals("Should capture complete 'Coupon' word", "Coupon", typeGroup?.value)
        
        // Verify exact character positions for highlighting
        val attributesStart = attributesGroup?.range?.first ?: -1
        val attributesEnd = attributesGroup?.range?.last ?: -1
        val typeStart = typeGroup?.range?.first ?: -1
        val typeEnd = typeGroup?.range?.last ?: -1
        
        println("Line: '$line'")
        println("Line positions: ${line.toCharArray().mapIndexed { i, c -> "$i:$c" }.joinToString(" ")}")
        println("'Attributes' found at positions $attributesStart..$attributesEnd")
        println("'Coupon' found at positions $typeStart..$typeEnd")
        
        // Verify the extracted substrings are correct
        val attributesSubstring = line.substring(attributesStart, attributesEnd + 1)
        val typeSubstring = line.substring(typeStart, typeEnd + 1)
        
        assertEquals("Attributes substring should be complete", "Attributes", attributesSubstring)
        assertEquals("Type substring should be complete", "Coupon", typeSubstring)
        
        // Verify this does NOT match MSON attribute pattern (the conflicting pattern)
        val conflictingMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
        assertNull("Should NOT match MSON attribute pattern", conflictingMatch)
    }

    @Test
    fun testWhitespaceHandlingInOffsets() {
        val testCases = listOf(
            "+ Attributes (Coupon)" to Pair(2, 14),           // no leading space
            "    + Attributes (Coupon)" to Pair(6, 18),       // 4 leading spaces
            "\t+ Attributes (Coupon)" to Pair(3, 15),         // 1 leading tab
            "  + Attributes (Product)" to Pair(4, 16)         // 2 leading spaces
        )
        
        testCases.forEach { (line, expectedPositions) ->
            val match = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
            assertNotNull("Should match: '$line'", match)
            
            val attributesStart = match?.groups?.get(1)?.range?.first ?: -1
            val typeStart = match?.groups?.get(2)?.range?.first ?: -1
            
            assertEquals("Attributes start position for '$line'", expectedPositions.first, attributesStart)
            assertEquals("Type start position for '$line'", expectedPositions.second, typeStart)
            
            // Verify the actual text extraction is correct
            val attributesText = match?.groups?.get(1)?.value
            val typeText = match?.groups?.get(2)?.value
            
            assertEquals("Should extract 'Attributes'", "Attributes", attributesText)
            assertTrue("Should extract type name", typeText?.isNotEmpty() == true)
        }
    }

    @Test
    fun testMultipleComplexTypeReferences() {
        val complexTypes = listOf(
            "+ Attributes (User)" to "User",
            "+ Attributes (array[Product])" to "array[Product]",
            "+ Attributes (UserProfile)" to "UserProfile", 
            "+ Attributes (object)" to "object",
            "+ Attributes (CustomType)" to "CustomType"
        )
        
        complexTypes.forEach { (line, expectedType) ->
            val match = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
            assertNotNull("Should match: '$line'", match)
            
            assertEquals("Should extract 'Attributes'", "Attributes", match?.groups?.get(1)?.value)
            assertEquals("Should extract type: '$expectedType'", expectedType, match?.groups?.get(2)?.value)
            
            // Verify no MSON conflict
            val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
            assertNull("Should not have MSON conflict for: '$line'", msonMatch)
        }
    }

    @Test
    fun testOriginalReportedBehavior() {
        val problematicLine = "+ Attributes (Coupon)"
        
        println("Testing original reported issue:")
        println("Line: '$problematicLine'")
        
        // This should now work correctly:
        // - "Attributes" highlighted as APIBLUEPRINT_SECTION 
        // - "Coupon" highlighted as APIBLUEPRINT_MSON_TYPE
        // - No partial matches like "Attribut" or "Coup"
        
        val match = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(problematicLine)
        assertNotNull("Should find Attributes pattern", match)
        
        val attributesGroup = match!!.groups[1]!!
        val typeGroup = match.groups[2]!!
        
        println("✓ 'Attributes' found at positions ${attributesGroup.range}")
        println("✓ 'Coupon' found at positions ${typeGroup.range}")
        
        // Verify complete words, not fragments
        assertEquals("Should be complete 'Attributes' word", "Attributes", attributesGroup.value)
        assertEquals("Should be complete 'Coupon' word", "Coupon", typeGroup.value)
        
        // Verify ranges are correct for highlighting
        assertTrue("Attributes range should have correct length", 
                   attributesGroup.range.last - attributesGroup.range.first + 1 == "Attributes".length)
        assertTrue("Coupon range should have correct length",
                   typeGroup.range.last - typeGroup.range.first + 1 == "Coupon".length)
        
        println("✓ Original issue is now fixed!")
    }
}
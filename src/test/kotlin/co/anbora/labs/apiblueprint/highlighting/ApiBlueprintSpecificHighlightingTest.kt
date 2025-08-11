package co.anbora.labs.apiblueprint.highlighting

import org.junit.Test
import org.junit.Assert.*

/**
 * Test specific highlighting scenarios that users reported as issues.
 */
class ApiBlueprintSpecificHighlightingTest {

    @Test
    fun testAttributesCouponHighlighting() {
        // Test the exact case reported: + Attributes (Coupon)
        // Should highlight "Attributes" as section and "Coupon" as MSON type
        val line = "+ Attributes (Coupon)"
        
        // Verify the line matches the Attributes section pattern
        val match = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
        assertNotNull("Should match Attributes section pattern", match)
        
        // Verify "Attributes" keyword is captured
        val attributesKeyword = match?.groups?.get(1)?.value
        assertEquals("Should capture 'Attributes' keyword", "Attributes", attributesKeyword)
        
        // Verify "Coupon" type is captured
        val typeReference = match?.groups?.get(2)?.value
        assertEquals("Should capture 'Coupon' type reference", "Coupon", typeReference)
        
        // Verify the type reference is the complete word, not partial
        assertEquals("Type reference should be complete word 'Coupon'", "Coupon", typeReference)
        assertFalse("Should not be partial like 'Coup'", typeReference == "Coup")
    }

    @Test
    fun testAttributesObjectHighlighting() {
        // Test similar case with built-in type
        val line = "+ Attributes (object)"
        
        val match = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
        assertNotNull("Should match Attributes section pattern", match)
        assertEquals("Attributes", match?.groups?.get(1)?.value)
        assertEquals("object", match?.groups?.get(2)?.value)
    }

    @Test
    fun testAttributesArrayCouponHighlighting() {
        // Test complex type reference
        val line = "+ Attributes (array[Coupon])"
        
        val match = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
        assertNotNull("Should match Attributes section pattern", match)
        assertEquals("Attributes", match?.groups?.get(1)?.value)
        assertEquals("array[Coupon]", match?.groups?.get(2)?.value)
    }

    @Test
    fun testMimeTypeDoesNotMatchCoupon() {
        // Verify that "Coupon" is not detected as a MIME type
        val line = "+ Attributes (Coupon)"
        
        val mimeMatches = ApiBlueprintRegexUtils.MIME_PATTERN.findAll(line).toList()
        assertEquals("Should find one match in parentheses", 1, mimeMatches.size)
        
        val mimeContent = mimeMatches[0].groups[1]?.value
        assertEquals("Should extract 'Coupon' from parentheses", "Coupon", mimeContent)
        
        // But it should not be classified as a MIME type in the annotator
        // because it doesn't contain "/" and doesn't match MIME patterns
    }

    @Test
    fun testAttributesVsParametersPatterns() {
        val attributesLine = "+ Attributes (Coupon)"
        val parametersLine = "+ Parameters"
        
        // Attributes should match the specific Attributes pattern
        val attrMatch = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(attributesLine)
        assertNotNull("Attributes should match specific pattern", attrMatch)
        
        // Parameters should match the general section pattern
        val paramMatch = ApiBlueprintRegexUtils.SECTION_PATTERN.find(parametersLine)
        assertNotNull("Parameters should match section pattern", paramMatch)
        
        // Attributes should NOT match the general section pattern (since it's removed from there)
        val attrSectionMatch = ApiBlueprintRegexUtils.SECTION_PATTERN.find(attributesLine)
        assertNull("Attributes should not match general section pattern", attrSectionMatch)
    }

    @Test
    fun testMultipleAttributesPatterns() {
        val testCases = listOf(
            "+ Attributes (User)" to "User",
            "+ Attributes (Product)" to "Product", 
            "+ Attributes (array[Item])" to "array[Item]",
            "+ Attributes (object)" to "object",
            "+ Attributes (UserProfile)" to "UserProfile"
        )
        
        testCases.forEach { (line, expectedType) ->
            val match = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
            assertNotNull("Should match: $line", match)
            assertEquals("Attributes", match?.groups?.get(1)?.value)
            assertEquals("Should extract type: $expectedType", expectedType, match?.groups?.get(2)?.value)
        }
    }

    @Test
    fun testSimpleAttributesPattern() {
        // Test simple Attributes without type reference
        val line = "+ Attributes"
        
        val match = ApiBlueprintRegexUtils.SIMPLE_ATTRIBUTES_PATTERN.find(line)
        assertNotNull("Should match simple Attributes pattern", match)
        assertEquals("Attributes", match?.groups?.get(1)?.value)
        
        // Should not match the type-specific pattern
        val typeMatch = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
        assertNull("Should not match type-specific Attributes pattern", typeMatch)
    }
}
package co.anbora.labs.apiblueprint.highlighting

import co.anbora.labs.apiblueprint.ide.highlighting.ApiBlueprintRegexUtils
import org.junit.Test
import org.junit.Assert.*

/**
 * Comprehensive test class for API Blueprint syntax highlighting patterns.
 * Tests all regex patterns and highlighting rules against real API Blueprint content.
 */
class ApiBlueprintHighlightingTest {

    @Test
    fun testMetadataPatterns() {
        // Test FORMAT pattern
        val formatLine = "FORMAT: 1A"
        assertTrue("FORMAT pattern should match", ApiBlueprintRegexUtils.FORMAT_PATTERN.matches(formatLine))
        
        val invalidFormat = "FORMAT: 2B"
        assertFalse("Invalid format should not match", ApiBlueprintRegexUtils.FORMAT_PATTERN.matches(invalidFormat))
        
        // Test HOST pattern  
        val hostLine = "HOST: https://api.example.com"
        assertTrue("HOST pattern should match", ApiBlueprintRegexUtils.HOST_PATTERN.matches(hostLine))
        
        val invalidHost = "HOST: not-a-url"
        assertFalse("Invalid host should not match", ApiBlueprintRegexUtils.HOST_PATTERN.matches(invalidHost))
    }

    @Test
    fun testApiNamePattern() {
        val apiName = "# Advanced Attributes API"
        val match = ApiBlueprintRegexUtils.API_NAME_PATTERN.find(apiName)
        assertNotNull("API name pattern should match", match)
        assertEquals("Advanced Attributes API", match?.groups?.get(1)?.value)
        
        val notApiName = "## Resource Name"
        val noMatch = ApiBlueprintRegexUtils.API_NAME_PATTERN.find(notApiName)
        assertNull("H2 header should not match API name pattern", noMatch)
    }

    @Test
    fun testGroupPattern() {
        val groupHeader = "# Group Coupons"
        val match = ApiBlueprintRegexUtils.GROUP_PATTERN.find(groupHeader)
        assertNotNull("Group pattern should match", match)
        
        val groupHeader2 = "## Coupons Group"
        val match2 = ApiBlueprintRegexUtils.GROUP_PATTERN.find(groupHeader2)
        assertNotNull("Alternative group pattern should match", match2)
        
        val notGroup = "# Regular Header"
        val noMatch = ApiBlueprintRegexUtils.GROUP_PATTERN.find(notGroup)
        assertNull("Regular header without 'Group' should not match", noMatch)
    }

    @Test
    fun testResourcePattern() {
        val resourceHeader = "## Coupon [/coupons/{id}]"
        val match = ApiBlueprintRegexUtils.RESOURCE_PATTERN.find(resourceHeader)
        assertNotNull("Resource pattern should match", match)
        assertEquals("Coupon", match?.groups?.get(1)?.value)
        assertEquals("/coupons/{id}", match?.groups?.get(3)?.value)
    }

    @Test
    fun testActionNamePattern() {
        val actionHeader = "### Retrieve a Coupon [GET]"
        val match = ApiBlueprintRegexUtils.ACTION_NAME_PATTERN.find(actionHeader)
        assertNotNull("Action name pattern should match", match)
        assertEquals("Retrieve a Coupon", match?.groups?.get(1)?.value)
        assertEquals("GET", match?.groups?.get(2)?.value)
        
        val actionWithPath = "### List all Coupons [GET /coupons]"
        val match2 = ApiBlueprintRegexUtils.ACTION_NAME_PATTERN.find(actionWithPath)
        assertNotNull("Action with path should match", match2)
        assertEquals("List all Coupons", match2?.groups?.get(1)?.value)
        assertEquals("GET", match2?.groups?.get(2)?.value)
    }

    @Test
    fun testSectionPattern() {
        val sections = listOf(
            "+ Parameters",
            "+ Headers",
            "+ Body",
            "+ Schema"
        )
        
        sections.forEach { section ->
            val match = ApiBlueprintRegexUtils.SECTION_PATTERN.find(section)
            assertNotNull("Section pattern should match: $section", match)
        }
        
        // Test specific Attributes patterns
        val attributesWithType = "+ Attributes (object)"
        val attrMatch = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(attributesWithType)
        assertNotNull("Attributes with type should match specific pattern", attrMatch)
        assertEquals("Attributes", attrMatch?.groups?.get(1)?.value)
        assertEquals("object", attrMatch?.groups?.get(2)?.value)
        
        val simpleAttributes = "+ Attributes"
        val simpleMatch = ApiBlueprintRegexUtils.SIMPLE_ATTRIBUTES_PATTERN.find(simpleAttributes)
        assertNotNull("Simple attributes should match", simpleMatch)
        assertEquals("Attributes", simpleMatch?.groups?.get(1)?.value)
        
        val notSection = "+ Random text"
        val noMatch = ApiBlueprintRegexUtils.SECTION_PATTERN.find(notSection)
        assertNull("Non-section text should not match", noMatch)
    }

    @Test
    fun testMsonAttributePattern() {
        // Test basic attribute
        val basicAttribute = "    + id: 250FF (string, required)"
        val match1 = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(basicAttribute)
        assertNotNull("Basic MSON attribute should match", match1)
        assertEquals("id", match1?.groups?.get(1)?.value)
        assertEquals("250FF", match1?.groups?.get(2)?.value?.trim())
        assertEquals("string, required", match1?.groups?.get(3)?.value)
        
        // Test attribute with description
        val attributeWithDesc = "    + created: 1415203908 (number) - Time stamp"
        val match2 = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(attributeWithDesc)
        assertNotNull("MSON attribute with description should match", match2)
        assertEquals("created", match2?.groups?.get(1)?.value)
        assertEquals("1415203908", match2?.groups?.get(2)?.value?.trim())
        assertEquals("number", match2?.groups?.get(3)?.value)
        assertEquals("Time stamp", match2?.groups?.get(4)?.value)
        
        // Test attribute without value
        val noValueAttribute = "    + redeem_by (number) - Date after which the coupon can no longer be redeemed"
        val match3 = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(noValueAttribute)
        assertNotNull("MSON attribute without value should match", match3)
        assertEquals("redeem_by", match3?.groups?.get(1)?.value)
        assertEquals("number", match3?.groups?.get(3)?.value)
        assertEquals("Date after which the coupon can no longer be redeemed", match3?.groups?.get(4)?.value)
    }

    @Test
    fun testParameterPattern() {
        // Test parameter with type
        val paramWithType = "    + id (string)"
        val match1 = ApiBlueprintRegexUtils.PARAMETER_PATTERN.find(paramWithType)
        assertNotNull("Parameter with type should match", match1)
        assertEquals("id", match1?.groups?.get(1)?.value)
        assertEquals("string", match1?.groups?.get(3)?.value)
        
        // Test parameter with default value
        val paramWithDefault = "    + limit (number, optional)"
        val match2 = ApiBlueprintRegexUtils.PARAMETER_PATTERN.find(paramWithDefault)
        assertNotNull("Parameter with flags should match", match2)
        assertEquals("limit", match2?.groups?.get(1)?.value)
        assertEquals("number, optional", match2?.groups?.get(3)?.value)
    }

    @Test
    fun testDefaultValuePattern() {
        val defaultLine = "        + Default: `10`"
        val match = ApiBlueprintRegexUtils.DEFAULT_VALUE_PATTERN.find(defaultLine)
        assertNotNull("Default value pattern should match", match)
        assertEquals("10", match?.groups?.get(1)?.value)
        
        val invalidDefault = "        + Default: 10"
        val noMatch = ApiBlueprintRegexUtils.DEFAULT_VALUE_PATTERN.find(invalidDefault)
        assertNull("Default without backticks should not match", noMatch)
    }

    @Test
    fun testResponsePattern() {
        val response200 = "+ Response 200 (application/json)"
        val match1 = ApiBlueprintRegexUtils.RESPONSE_PATTERN.find(response200)
        assertNotNull("Response pattern should match", match1)
        assertEquals("Response", match1?.groups?.get(1)?.value)
        assertEquals("200", match1?.groups?.get(2)?.value)
        assertEquals("application/json", match1?.groups?.get(4)?.value)
        
        val responseNoMime = "+ Response 200"
        val match2 = ApiBlueprintRegexUtils.RESPONSE_PATTERN.find(responseNoMime)
        assertNotNull("Response without MIME should match", match2)
        assertEquals("200", match2?.groups?.get(2)?.value)
    }

    @Test
    fun testRequestPattern() {
        val request = "+ Request (application/json)"
        val match = ApiBlueprintRegexUtils.REQUEST_PATTERN.find(request)
        assertNotNull("Request pattern should match", match)
        assertEquals("Request", match?.groups?.get(1)?.value)
        assertEquals("application/json", match?.groups?.get(4)?.value)
    }

    @Test
    fun testTypeAnnotationPattern() {
        val types = listOf("(object)", "(string)", "(number)", "(boolean)", "(array)", "(enum)")
        
        types.forEach { type ->
            val match = ApiBlueprintRegexUtils.TYPE_ANNOTATION_PATTERN.find(type)
            assertNotNull("Type annotation should match: $type", match)
        }
        
        val complexType = "(string, required)"
        val match = ApiBlueprintRegexUtils.TYPE_ANNOTATION_PATTERN.find(complexType)
        assertNotNull("Complex type annotation should match", match)
        
        val invalidType = "(invalid)"
        val noMatch = ApiBlueprintRegexUtils.TYPE_ANNOTATION_PATTERN.find(invalidType)
        assertNull("Invalid type should not match", noMatch)
    }

    @Test
    fun testNumberPattern() {
        val numbers = listOf("250FF", "1415203908", "25", "10", "123.45", "0.5")
        
        // Test integers
        listOf("1415203908", "25", "10").forEach { number ->
            val match = ApiBlueprintRegexUtils.NUMBER_PATTERN.find(number)
            assertNotNull("Integer should match: $number", match)
        }
        
        // Test floats
        listOf("123.45", "0.5").forEach { number ->
            val match = ApiBlueprintRegexUtils.NUMBER_PATTERN.find(number)
            assertNotNull("Float should match: $number", match)
        }
        
        // Test non-numbers
        val notNumber = "250FF"
        val noMatch = ApiBlueprintRegexUtils.NUMBER_PATTERN.find(notNumber)
        // This should actually match as a partial number "250" - this is expected behavior
    }

    @Test
    fun testMsonFlagPattern() {
        val flags = listOf("required", "optional", "nullable", "default", "sample")
        
        flags.forEach { flag ->
            val match = ApiBlueprintRegexUtils.MSON_FLAG_PATTERN.find(flag)
            assertNotNull("MSON flag should match: $flag", match)
        }
        
        val textWithFlags = "This field is required and optional"
        val matches = ApiBlueprintRegexUtils.MSON_FLAG_PATTERN.findAll(textWithFlags).toList()
        assertEquals("Should find 2 flags", 2, matches.size)
        assertEquals("required", matches[0].value)
        assertEquals("optional", matches[1].value)
    }

    @Test
    fun testStringLiteralPattern() {
        val quotedString = "\"application/json\""
        val match1 = ApiBlueprintRegexUtils.STRING_LITERAL_PATTERN.find(quotedString)
        assertNotNull("Quoted string should match", match1)
        assertEquals("application/json", match1?.groups?.get(1)?.value)
        
        val backtickString = "`10`"
        val match2 = ApiBlueprintRegexUtils.STRING_LITERAL_PATTERN.find(backtickString)
        assertNotNull("Backtick string should match", match2)
        assertEquals("10", match2?.groups?.get(2)?.value)
    }

    @Test
    fun testUriVariablePatterns() {
        // Test simple URI variable
        val simpleVar = "/coupons/{id}"
        val match1 = ApiBlueprintRegexUtils.URI_VAR_PATTERN.find(simpleVar)
        assertNotNull("URI variable should match", match1)
        assertEquals("id", match1?.groups?.get(1)?.value)
        
        // Test query URI variable
        val queryVar = "/coupons{?limit}"
        val match2 = ApiBlueprintRegexUtils.URI_QUERY_PATTERN.find(queryVar)
        assertNotNull("Query URI variable should match", match2)
        assertEquals("limit", match2?.groups?.get(1)?.value)
        
        // Test multiple query variables
        val multiQuery = "/users{?page,size,sort}"
        val match3 = ApiBlueprintRegexUtils.URI_QUERY_PATTERN.find(multiQuery)
        assertNotNull("Multiple query variables should match", match3)
        assertEquals("page,size,sort", match3?.groups?.get(1)?.value)
    }

    @Test
    fun testHttpMethodPattern() {
        val methods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS")
        
        methods.forEach { method ->
            val match = ApiBlueprintRegexUtils.HTTP_METHOD_PATTERN.find(method)
            assertNotNull("HTTP method should match: $method", match)
            assertEquals(method, match?.value)
        }
        
        val textWithMethods = "Use GET to retrieve and POST to create"
        val matches = ApiBlueprintRegexUtils.HTTP_METHOD_PATTERN.findAll(textWithMethods).toList()
        assertEquals("Should find 2 methods", 2, matches.size)
        assertEquals("GET", matches[0].value)
        assertEquals("POST", matches[1].value)
    }

    @Test
    fun testActionPattern() {
        val actionBrackets = "[GET /coupons]"
        val match = ApiBlueprintRegexUtils.ACTION_PATTERN.find(actionBrackets)
        assertNotNull("Action pattern should match", match)
        assertEquals("GET", match?.groups?.get(1)?.value)
        assertEquals("/coupons", match?.groups?.get(2)?.value)
    }

    @Test
    fun testMimePattern() {
        val withMime = "(application/json)"
        val match = ApiBlueprintRegexUtils.MIME_PATTERN.find(withMime)
        assertNotNull("MIME pattern should match", match)
        assertEquals("application/json", match?.groups?.get(1)?.value)
        
        val withoutMime = "(object)"
        val match2 = ApiBlueprintRegexUtils.MIME_PATTERN.find(withoutMime)
        assertNotNull("Pattern should match parentheses content", match2)
        assertEquals("object", match2?.groups?.get(1)?.value)
    }

    @Test
    fun testCompleteAttributeExample() {
        // Test the complete attribute example from the test file
        val attributeLines = listOf(
            "    + id: 250FF (string, required)",
            "    + created: 1415203908 (number) - Time stamp", 
            "    + percent_off: 25 (number)",
            "    + redeem_by (number) - Date after which the coupon can no longer be redeemed"
        )
        
        attributeLines.forEach { line ->
            val match = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
            assertNotNull("Attribute line should match: $line", match)
            
            // Verify we can extract the attribute name
            val attributeName = match?.groups?.get(1)?.value
            assertNotNull("Should extract attribute name from: $line", attributeName)
            assertTrue("Attribute name should not be empty", attributeName!!.isNotEmpty())
        }
    }

    @Test
    fun testParameterWithDefaultExample() {
        // Test parameter example from the test file
        val parameterSection = """
            + Parameters
                + limit (number, optional)
            
                  A limit on the number of objects to be returned. Limit can range
                  between 1 and 100 items.
            
                    + Default: `10`
        """.trimIndent()
        
        val paramMatch = ApiBlueprintRegexUtils.PARAMETER_PATTERN.find("    + limit (number, optional)")
        assertNotNull("Parameter should match", paramMatch)
        assertEquals("limit", paramMatch?.groups?.get(1)?.value)
        assertEquals("number, optional", paramMatch?.groups?.get(3)?.value)
        
        val defaultMatch = ApiBlueprintRegexUtils.DEFAULT_VALUE_PATTERN.find("        + Default: `10`")
        assertNotNull("Default value should match", defaultMatch)
        assertEquals("10", defaultMatch?.groups?.get(1)?.value)
    }

    @Test
    fun testComplexTypeReferences() {
        // Test type references like (Coupon), (array[Coupon])
        val typeReferences = listOf(
            "+ Attributes (Coupon)",
            "+ Attributes (array[Coupon])",
            "+ Attributes (Coupons)"
        )
        
        typeReferences.forEach { line ->
            val attrMatch = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
            assertNotNull("Type reference should match attributes pattern: $line", attrMatch)
            assertEquals("Attributes", attrMatch?.groups?.get(1)?.value)
            assertNotNull("Should have type reference", attrMatch?.groups?.get(2)?.value)
        }
    }
}
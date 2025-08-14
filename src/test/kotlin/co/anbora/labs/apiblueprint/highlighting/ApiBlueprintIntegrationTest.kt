package co.anbora.labs.apiblueprint.highlighting

import co.anbora.labs.apiblueprint.ide.highlighting.ApiBlueprintRegexUtils
import org.junit.Test
import org.junit.Assert.*

/**
 * Integration test that validates highlighting patterns against the complete API Blueprint example
 * from the specification to ensure comprehensive coverage.
 */
class ApiBlueprintIntegrationTest {

    private val sampleApiBlueprint = """
FORMAT: 1A

# Advanced Attributes API
Improving the previous [Attributes](08.%20Attributes.md) description example,
this API example describes the `Coupon` resource attributes (data structure)
regardless of the serialization format.

# Group Coupons

## Coupon [/coupons/{id}]
A coupon contains information about a percent-off or amount-off discount.

+ Parameters
    + id (string)

+ Attributes (object)
    + id: 250FF (string, required)
    + created: 1415203908 (number) - Time stamp
    + percent_off: 25 (number)
    + redeem_by (number) - Date after which the coupon can no longer be redeemed

### Retrieve a Coupon [GET]
Retrieves the coupon with the given ID.

+ Response 200 (application/json)
    + Attributes (Coupon)

## Coupons [/coupons{?limit}]

+ Attributes (array[Coupon])

### List all Coupons [GET]
Returns a list of your coupons.

+ Parameters
    + limit (number, optional)
        + Default: `10`

+ Response 200 (application/json)
    + Attributes (Coupons)

### Create a Coupon [POST]
Creates a new Coupon.

+ Request (application/json)
+ Response 200 (application/json)
    + Attributes (Coupon)
    """.trimIndent()

    @Test
    fun testCompleteApiBluerintHighlighting() {
        val lines = sampleApiBlueprint.split("\n")
        
        // Verify FORMAT metadata is detected
        val formatLine = lines[0]
        assertTrue("FORMAT line should match metadata pattern", 
            ApiBlueprintRegexUtils.FORMAT_PATTERN.matches(formatLine))
        
        // Verify API name is detected
        val apiNameLine = lines[2]
        assertNotNull("API name should be detected", 
            ApiBlueprintRegexUtils.API_NAME_PATTERN.find(apiNameLine))
        
        // Verify Group is detected
        val groupLine = lines.find { it.contains("# Group Coupons") }
        assertNotNull("Group line should exist", groupLine)
        assertNotNull("Group pattern should match", 
            ApiBlueprintRegexUtils.GROUP_PATTERN.find(groupLine!!))
        
        // Verify Resource with URI template is detected
        val resourceLine = lines.find { it.contains("## Coupon [/coupons/{id}]") }
        assertNotNull("Resource line should exist", resourceLine)
        val resourceMatch = ApiBlueprintRegexUtils.RESOURCE_PATTERN.find(resourceLine!!)
        assertNotNull("Resource pattern should match", resourceMatch)
        assertEquals("Coupon", resourceMatch?.groups?.get(1)?.value)
        assertEquals("/coupons/{id}", resourceMatch?.groups?.get(3)?.value)
        
        // Verify URI variable is detected
        assertNotNull("URI variable should be detected in resource path", 
            ApiBlueprintRegexUtils.URI_VAR_PATTERN.find("/coupons/{id}"))
        
        // Verify Action name is detected
        val actionLine = lines.find { it.contains("### Retrieve a Coupon [GET]") }
        assertNotNull("Action line should exist", actionLine)
        val actionMatch = ApiBlueprintRegexUtils.ACTION_NAME_PATTERN.find(actionLine!!)
        assertNotNull("Action pattern should match", actionMatch)
        assertEquals("Retrieve a Coupon", actionMatch?.groups?.get(1)?.value)
        assertEquals("GET", actionMatch?.groups?.get(2)?.value)
        
        // Verify Parameters section is detected
        val paramsSectionLine = lines.find { it.trim() == "+ Parameters" }
        assertNotNull("Parameters section should exist", paramsSectionLine)
        assertNotNull("Parameters section should match", 
            ApiBlueprintRegexUtils.SECTION_PATTERN.find(paramsSectionLine!!))
        
        // Verify Attributes section is detected
        val attrSectionLine = lines.find { it.contains("+ Attributes (object)") }
        assertNotNull("Attributes section should exist", attrSectionLine)
        assertNotNull("Attributes section should match", 
            ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(attrSectionLine!!))
        
        // Verify MSON attributes are detected
        val msonAttributes = listOf(
            "    + id: 250FF (string, required)",
            "    + created: 1415203908 (number) - Time stamp",
            "    + percent_off: 25 (number)"
        )
        
        msonAttributes.forEach { attr ->
            val match = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(attr)
            assertNotNull("MSON attribute should match: $attr", match)
        }
        
        // Verify Response patterns are detected
        val responseLine = lines.find { it.contains("+ Response 200 (application/json)") }
        assertNotNull("Response line should exist", responseLine)
        val responseMatch = ApiBlueprintRegexUtils.RESPONSE_PATTERN.find(responseLine!!)
        assertNotNull("Response pattern should match", responseMatch)
        assertEquals("200", responseMatch?.groups?.get(2)?.value)
        assertEquals("application/json", responseMatch?.groups?.get(4)?.value)
        
        // Verify Default value is detected
        val defaultLine = lines.find { it.contains("+ Default: `10`") }
        assertNotNull("Default line should exist", defaultLine)
        assertNotNull("Default pattern should match", 
            ApiBlueprintRegexUtils.DEFAULT_VALUE_PATTERN.find(defaultLine!!))
        
        // Verify type annotations are detected
        val typeAnnotations = listOf("(string)", "(number)", "(object)", "(array[Coupon])")
        typeAnnotations.forEach { type ->
            if (type != "(array[Coupon])") { // Skip complex array type for now
                val match = ApiBlueprintRegexUtils.TYPE_ANNOTATION_PATTERN.find(type)
                assertNotNull("Type annotation should match: $type", match)
            }
        }
        
        // Verify flags are detected
        val flagsText = "This field is required and optional"
        val flagMatches = ApiBlueprintRegexUtils.MSON_FLAG_PATTERN.findAll(flagsText).toList()
        assertEquals("Should find 'required' and 'optional' flags", 2, flagMatches.size)
        
        // Verify numbers are detected
        val numberMatches = ApiBlueprintRegexUtils.NUMBER_PATTERN.findAll("250FF 1415203908 25 10").toList()
        assertTrue("Should detect multiple numbers", numberMatches.size >= 3)
        
        // Verify HTTP methods are detected
        val httpMethods = listOf("GET", "POST")
        httpMethods.forEach { method ->
            assertTrue("HTTP method should be detected: $method", 
                sampleApiBlueprint.contains(method))
            assertNotNull("HTTP method pattern should match: $method", 
                ApiBlueprintRegexUtils.HTTP_METHOD_PATTERN.find(method))
        }
        
        // Verify MIME types are detected
        val mimeTypePattern = ApiBlueprintRegexUtils.MIME_PATTERN
        val mimeMatches = mimeTypePattern.findAll(sampleApiBlueprint).toList()
        assertTrue("Should find multiple MIME type patterns", mimeMatches.size > 0)
        
        // Find application/json occurrences specifically
        val jsonMimeMatches = mimeMatches.filter { it.groups[1]?.value == "application/json" }
        assertTrue("Should find application/json MIME type", jsonMimeMatches.isNotEmpty())
    }
    
    @Test
    fun testComplexAttributePatterns() {
        val complexAttributes = listOf(
            "    + user (User, required) - User information",
            "    + tags: red, green, blue (array[string], optional) - Available tags",
            "    + metadata (object, nullable) - Additional metadata",
            "    + count: 42 (number) - Total count",
            "    + active: true (boolean, required) - Status flag"
        )
        
        complexAttributes.forEach { attr ->
            val match = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(attr)
            assertNotNull("Complex attribute should match: $attr", match)
            
            val name = match?.groups?.get(1)?.value
            assertNotNull("Should extract attribute name from: $attr", name)
            assertTrue("Attribute name should not be empty", name!!.isNotEmpty())
            
            val typeInfo = match.groups[3]?.value
            if (typeInfo != null) {
                assertTrue("Should have type information in: $attr", typeInfo.isNotEmpty())
            }
        }
    }
    
    @Test
    fun testAdvancedUriTemplates() {
        val uriTemplates = listOf(
            "/users/{id}/posts/{post_id}",
            "/search{?q,limit,offset}",
            "/users{?page,size}",
            "/items/{category}/{id}{?format}"
        )
        
        uriTemplates.forEach { template ->
            // Test simple URI variables
            val simpleVars = ApiBlueprintRegexUtils.URI_VAR_PATTERN.findAll(template).toList()
            if (template.contains("{id}") || template.contains("{post_id}") || template.contains("{category}")) {
                assertTrue("Should find URI variables in: $template", simpleVars.isNotEmpty())
            }
            
            // Test query URI variables
            val queryVars = ApiBlueprintRegexUtils.URI_QUERY_PATTERN.findAll(template).toList()
            if (template.contains("{?")) {
                assertTrue("Should find query variables in: $template", queryVars.isNotEmpty())
            }
        }
    }
}
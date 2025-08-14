package co.anbora.labs.apiblueprint.highlighting

import co.anbora.labs.apiblueprint.ide.highlighting.ApiBlueprintRegexUtils
import org.junit.Test

/**
 * Test with the real example provided by the user to debug why section keywords aren't highlighted.
 */
class ApiBlueprintRealExampleTest {

    @Test
    fun testUserProvidedExample() {
        val userExample = """
+ Parameters
    + id (string)

      The ID of the desired coupon.

+ Attributes (object)
    + id: 250FF (string, required)
    + created: 1415203908 (number) - Time stamp
    + percent_off: 25 (number)

      A positive integer between 1 and 100 that represents the discount the coupon will apply.

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

      A limit on the number of objects to be returned. Limit can range
      between 1 and 100 items.

        + Default: `10`

+ Response 200 (application/json)
    + Attributes (Coupons)

### Create a Coupon [POST]
Creates a new Coupon.

+ Attributes (object)
    + percent_off: 25 (number)
    + redeem_by (number)

+ Request (application/json)

+ Response 200 (application/json)
    + Attributes (Coupon)
        """.trimIndent()
        
        val lines = userExample.split("\n")
        
        println("Testing user's example line by line:")
        println("=====================================")
        
        lines.forEachIndexed { index, line ->
            println("Line $index: '$line'")
            
            // Test Parameters pattern
            val paramMatch = ApiBlueprintRegexUtils.SECTION_PATTERN.find(line)
            if (paramMatch != null) {
                println("  ✓ SECTION pattern matches: '${paramMatch.groups[1]?.value}'")
            }
            
            // Test Attributes patterns
            val attrMatch = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
            if (attrMatch != null) {
                println("  ✓ ATTRIBUTES pattern matches: keyword='${attrMatch.groups[1]?.value}', type='${attrMatch.groups[2]?.value}'")
            }
            
            val simpleAttrMatch = ApiBlueprintRegexUtils.SIMPLE_ATTRIBUTES_PATTERN.find(line)
            if (simpleAttrMatch != null) {
                println("  ✓ SIMPLE_ATTRIBUTES pattern matches: '${simpleAttrMatch.groups[1]?.value}'")
            }
            
            // Test Response pattern
            val responseMatch = ApiBlueprintRegexUtils.RESPONSE_PATTERN.find(line)
            if (responseMatch != null) {
                println("  ✓ RESPONSE pattern matches: keyword='${responseMatch.groups[1]?.value}', status='${responseMatch.groups[2]?.value}', mime='${responseMatch.groups[4]?.value}'")
            }
            
            // Test Request pattern
            val requestMatch = ApiBlueprintRegexUtils.REQUEST_PATTERN.find(line)
            if (requestMatch != null) {
                println("  ✓ REQUEST pattern matches: keyword='${requestMatch.groups[1]?.value}', mime='${requestMatch.groups[4]?.value}'")
            }
            
            // Check if any pattern matched
            if (paramMatch == null && attrMatch == null && simpleAttrMatch == null && 
                responseMatch == null && requestMatch == null) {
                // Check for other patterns
                val actionMatch = ApiBlueprintRegexUtils.ACTION_NAME_PATTERN.find(line)
                val resourceMatch = ApiBlueprintRegexUtils.RESOURCE_PATTERN.find(line)
                val msonMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(line)
                
                if (actionMatch != null) {
                    println("  ✓ ACTION pattern matches: '${actionMatch.groups[1]?.value}' [${actionMatch.groups[2]?.value}]")
                } else if (resourceMatch != null) {
                    println("  ✓ RESOURCE pattern matches: '${resourceMatch.groups[1]?.value}' path='${resourceMatch.groups[3]?.value}'")
                } else if (msonMatch != null) {
                    println("  ✓ MSON pattern matches: name='${msonMatch.groups[1]?.value}', value='${msonMatch.groups[2]?.value}', type='${msonMatch.groups[3]?.value}'")
                } else {
                    println("  - No pattern matches")
                }
            }
            
            println()
        }
    }
    
    @Test
    fun testSpecificSectionLines() {
        val sectionLines = listOf(
            "+ Parameters",
            "+ Attributes (object)", 
            "+ Response 200 (application/json)",
            "+ Request (application/json)",
            "+ Attributes (Coupon)",
            "+ Attributes (array[Coupon])",
            "+ Attributes (Coupons)"
        )
        
        println("Testing specific section lines:")
        println("==============================")
        
        sectionLines.forEach { line ->
            println("Line: '$line'")
            
            var matched = false
            
            // Check Parameters pattern
            val paramMatch = ApiBlueprintRegexUtils.SECTION_PATTERN.find(line)
            if (paramMatch != null) {
                println("  ✓ Matches SECTION pattern: '${paramMatch.groups[1]?.value}'")
                matched = true
            }
            
            // Check Attributes patterns  
            val attrMatch = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(line)
            if (attrMatch != null) {
                println("  ✓ Matches ATTRIBUTES pattern: keyword='${attrMatch.groups[1]?.value}', type='${attrMatch.groups[2]?.value}'")
                matched = true
            }
            
            // Check Response pattern
            val responseMatch = ApiBlueprintRegexUtils.RESPONSE_PATTERN.find(line)
            if (responseMatch != null) {
                println("  ✓ Matches RESPONSE pattern: keyword='${responseMatch.groups[1]?.value}', status='${responseMatch.groups[2]?.value}'")
                matched = true
            }
            
            // Check Request pattern
            val requestMatch = ApiBlueprintRegexUtils.REQUEST_PATTERN.find(line)
            if (requestMatch != null) {
                println("  ✓ Matches REQUEST pattern: keyword='${requestMatch.groups[1]?.value}'")
                matched = true
            }
            
            if (!matched) {
                println("  ❌ NO PATTERN MATCHES - This is a problem!")
            }
            
            println()
        }
    }
}
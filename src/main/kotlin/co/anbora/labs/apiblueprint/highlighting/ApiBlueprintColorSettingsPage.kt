package co.anbora.labs.apiblueprint.highlighting

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

/**
 * Color settings page for API Blueprint syntax highlighting configuration.
 * Allows users to customize colors for different API Blueprint elements.
 */
class ApiBlueprintColorSettingsPage : ColorSettingsPage {
    
    override fun getIcon(): Icon? = null
    
    override fun getHighlighter(): SyntaxHighlighter {
        return object : SyntaxHighlighter {
            override fun getHighlightingLexer(): com.intellij.lexer.Lexer {
                return com.intellij.lexer.EmptyLexer()
            }
            override fun getTokenHighlights(tokenType: com.intellij.psi.tree.IElementType?): Array<TextAttributesKey> {
                return emptyArray()
            }
        }
    }
    
    override fun getDemoText(): String = """
<meta>FORMAT: 1A</meta>
<meta>HOST: https://api.example.com</meta>

# <api_name>Advanced API Blueprint Demo</api_name>
This demonstrates comprehensive syntax highlighting features.

# <group>Group Users</group>
User management endpoints for CRUD operations.

## Users Collection <resource>[/users<uri_var>{?page,size}</uri_var>]</resource>

### <action_name>List All Users</action_name> [<method>GET</method>]
Retrieve a paginated list of all users in the system.

+ <relation>Relation: self</relation>

+ <section>Parameters</section>
    + <param_name>page</param_name>: <number>1</number> (number, <flag>optional</flag>) - Page number for pagination
        + Default: <string_value>`1`</string_value>
    + <param_name>size</param_name>: <number>25</number> (number, <flag>optional</flag>) - Number of items per page
        + Default: <string_value>`20`</string_value>

+ <section>Attributes</section> <type_annotation>(array[User])</type_annotation>

+ <section>Response</section> <status>200</status> <mime>(application/json)</mime>
    + <section>Attributes</section> <mson_type>(UserListResponse)</mson_type>

### <action_name>Create User</action_name> [<method>POST</method>]
Create a new user in the system.

+ <section>Request</section> <mime>(application/json)</mime>
    + <section>Attributes</section> <mson_type>(CreateUserRequest)</mson_type>

+ <section>Response</section> <status>201</status> <mime>(application/json)</mime>
    + <section>Attributes</section> <mson_type>(User)</mson_type>

+ <section>Response</section> <status>422</status> <mime>(application/json)</mime>
    + <section>Attributes</section> <mson_type>(ValidationError)</mson_type>

## User <resource>[/users/<uri_var>{id}</uri_var>]</resource>

+ <section>Parameters</section>
    + <param_name>id</param_name>: <number>123</number> (number, <flag>required</flag>) - Unique user identifier

### <action_name>Retrieve User</action_name> [<method>GET</method>]
Get details of a specific user.

+ <section>Response</section> <status>200</status> <mime>(application/json)</mime>
    + <section>Attributes</section> <mson_type>(User)</mson_type>

+ <section>Response</section> <status>404</status> <mime>(application/json)</mime>
    + <section>Attributes</section> <mson_type>(ErrorResponse)</mson_type>

# <data_structures>Data Structures</data_structures>

## <mson_type>User</mson_type> <type_annotation>(object)</type_annotation>
Represents a user account in the system.

+ <param_name>id</param_name>: <number>123</number> (number, <flag>required</flag>) - Unique user identifier
+ <param_name>username</param_name>: <string_value>"john_doe"</string_value> (string, <flag>required</flag>) - Unique username
+ <param_name>email</param_name>: <string_value>"john@example.com"</string_value> <type_annotation>(string, required)</type_annotation> - User email address
+ <param_name>first_name</param_name>: <string_value>"John"</string_value> <type_annotation>(string, required)</type_annotation> - User first name
+ <param_name>last_name</param_name>: <string_value>"Doe"</string_value> <type_annotation>(string, required)</type_annotation> - User last name  
+ <param_name>age</param_name>: <number>30</number> <type_annotation>(number, optional)</type_annotation> - User age in years
+ <param_name>is_active</param_name>: true <type_annotation>(boolean, required)</type_annotation> - Account status
+ <param_name>created_at</param_name>: <string_value>"2023-10-01T10:30:00Z"</string_value> <type_annotation>(string, required)</type_annotation> - Account creation timestamp
+ <param_name>profile</param_name> <mson_type>(UserProfile, optional)</mson_type> - Extended profile information
+ <param_name>roles</param_name>: admin, user <type_annotation>(array[string], required)</type_annotation> - User roles

## <mson_type>UserProfile</mson_type> <type_annotation>(object)</type_annotation>
Extended profile information for a user.

+ <param_name>avatar_url</param_name>: <string_value>"https://api.example.com/avatars/123.jpg"</string_value> <type_annotation>(string, optional)</type_annotation> - Profile picture URL
+ <param_name>bio</param_name>: <string_value>"Software engineer passionate about APIs"</string_value> <type_annotation>(string, optional)</type_annotation> - User biography
+ <param_name>location</param_name>: <string_value>"San Francisco, CA"</string_value> <type_annotation>(string, optional)</type_annotation> - User location
+ <param_name>verified</param_name>: true <type_annotation>(boolean, optional)</type_annotation> - Verification status

## <mson_type>CreateUserRequest</mson_type> <type_annotation>(object)</type_annotation>
Request payload for creating a new user.

+ <param_name>username</param_name>: <string_value>"new_user"</string_value> <type_annotation>(string, required)</type_annotation> - Desired username
+ <param_name>email</param_name>: <string_value>"newuser@example.com"</string_value> <type_annotation>(string, required)</type_annotation> - User email address
+ <param_name>password</param_name>: <string_value>"securePassword123"</string_value> <type_annotation>(string, required)</type_annotation> - Account password

## <mson_type>ErrorResponse</mson_type> <type_annotation>(object)</type_annotation>
Standard error response format.

+ <param_name>error</param_name>: <string_value>"User not found"</string_value> <type_annotation>(string, required)</type_annotation> - Error message
+ <param_name>code</param_name>: <string_value>"USER_NOT_FOUND"</string_value> <type_annotation>(string, required)</type_annotation> - Error code
+ <param_name>details</param_name> <type_annotation>(array[string], optional)</type_annotation> - Additional error information
    """.trimIndent()
    
    override fun getColorDescriptors(): Array<ColorDescriptor> = emptyArray()
    
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = arrayOf(
        // Core API Blueprint elements
        AttributesDescriptor("API Blueprint//Metadata (FORMAT, HOST)", ApiBlueprintHighlighterKeys.APIBLUEPRINT_META),
        AttributesDescriptor("API Blueprint//API Name", ApiBlueprintHighlighterKeys.APIBLUEPRINT_API_NAME),
        AttributesDescriptor("API Blueprint//Group Header", ApiBlueprintHighlighterKeys.APIBLUEPRINT_GROUP),
        AttributesDescriptor("API Blueprint//Resource Path", ApiBlueprintHighlighterKeys.APIBLUEPRINT_RESOURCE),
        AttributesDescriptor("API Blueprint//Action Name", ApiBlueprintHighlighterKeys.APIBLUEPRINT_ACTION_NAME),
        AttributesDescriptor("API Blueprint//HTTP Method", ApiBlueprintHighlighterKeys.APIBLUEPRINT_METHOD),
        AttributesDescriptor("API Blueprint//URI Variable", ApiBlueprintHighlighterKeys.APIBLUEPRINT_URI_VAR),
        
        // Section elements
        AttributesDescriptor("API Blueprint//Section Name", ApiBlueprintHighlighterKeys.APIBLUEPRINT_SECTION),
        AttributesDescriptor("API Blueprint//MIME Type", ApiBlueprintHighlighterKeys.APIBLUEPRINT_MIME),
        AttributesDescriptor("API Blueprint//Status Code", ApiBlueprintHighlighterKeys.APIBLUEPRINT_STATUS),
        AttributesDescriptor("API Blueprint//Relation", ApiBlueprintHighlighterKeys.APIBLUEPRINT_RELATION),
        
        // MSON elements
        AttributesDescriptor("API Blueprint//Type Annotation", ApiBlueprintHighlighterKeys.APIBLUEPRINT_TYPE_ANNOTATION),
        AttributesDescriptor("API Blueprint//Number Value", ApiBlueprintHighlighterKeys.APIBLUEPRINT_NUMBER),
        AttributesDescriptor("API Blueprint//MSON Type Reference", ApiBlueprintHighlighterKeys.APIBLUEPRINT_MSON_TYPE),
        AttributesDescriptor("API Blueprint//Parameter/Attribute Name", ApiBlueprintHighlighterKeys.APIBLUEPRINT_PARAMETER_NAME),
        AttributesDescriptor("API Blueprint//MSON Property", ApiBlueprintHighlighterKeys.APIBLUEPRINT_PROPERTY),
        AttributesDescriptor("API Blueprint//MSON Flag (required, optional)", ApiBlueprintHighlighterKeys.APIBLUEPRINT_FLAG),
        AttributesDescriptor("API Blueprint//String Value", ApiBlueprintHighlighterKeys.APIBLUEPRINT_STRING_VALUE),
        
        // Advanced elements
        AttributesDescriptor("API Blueprint//Data Structures Section", ApiBlueprintHighlighterKeys.APIBLUEPRINT_DATA_STRUCTURES)
    )
    
    override fun getDisplayName(): String = "API Blueprint"
    
    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> = mapOf(
        // Core API Blueprint elements
        "meta" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_META,
        "api_name" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_API_NAME,
        "group" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_GROUP,
        "resource" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_RESOURCE,
        "action_name" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_ACTION_NAME,
        "method" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_METHOD,
        "uri_var" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_URI_VAR,
        
        // Section elements
        "section" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_SECTION,
        "mime" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_MIME,
        "status" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_STATUS,
        "relation" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_RELATION,
        
        // MSON elements
        "type_annotation" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_TYPE_ANNOTATION,
        "number" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_NUMBER,
        "mson_type" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_MSON_TYPE,
        "param_name" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_PARAMETER_NAME,
        "property" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_PROPERTY,
        "flag" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_FLAG,
        "string_value" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_STRING_VALUE,
        
        // Advanced elements
        "data_structures" to ApiBlueprintHighlighterKeys.APIBLUEPRINT_DATA_STRUCTURES
    )
}
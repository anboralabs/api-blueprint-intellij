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
FORMAT: 1A
HOST: https://api.example.com

# Advanced API Blueprint Demo
This demonstrates comprehensive syntax highlighting features.

# Group Users
User management endpoints for CRUD operations.

## Users Collection [/users{?page,size}]

### List All Users [GET]
Retrieve a paginated list of all users in the system.

+ Relation: self

+ Parameters
    + page: 1 (number, optional) - Page number for pagination
        + Default: `1`
    + size: 25 (number, optional) - Number of items per page
        + Default: `20`

+ Attributes (array[User])

+ Response 200 (application/json)
    + Attributes (UserListResponse)

### Create User [POST]
Create a new user in the system.

+ Request (application/json)
    + Attributes (CreateUserRequest)

+ Response 201 (application/json)
    + Attributes (User)

+ Response 422 (application/json)
    + Attributes (ValidationError)

## User [/users/{id}]

+ Parameters
    + id: 123 (number, required) - Unique user identifier

### Retrieve User [GET]
Get details of a specific user.

+ Response 200 (application/json)
    + Attributes (User)

+ Response 404 (application/json)
    + Attributes (ErrorResponse)

# Data Structures

## User (object)
Represents a user account in the system.

+ id: 123 (number, required) - Unique user identifier
+ username: "john_doe" (string, required) - Unique username
+ email: "john@example.com" (string, required) - User email address
+ first_name: "John" (string, required) - User first name
+ last_name: "Doe" (string, required) - User last name  
+ age: 30 (number, optional) - User age in years
+ is_active: true (boolean, required) - Account status
+ created_at: "2023-10-01T10:30:00Z" (string, required) - Account creation timestamp
+ profile (UserProfile, optional) - Extended profile information
+ roles: admin, user (array[string], required) - User roles

## UserProfile (object)
Extended profile information for a user.

+ avatar_url: "https://api.example.com/avatars/123.jpg" (string, optional) - Profile picture URL
+ bio: "Software engineer passionate about APIs" (string, optional) - User biography
+ location: "San Francisco, CA" (string, optional) - User location
+ verified: true (boolean, optional) - Verification status

## CreateUserRequest (object)
Request payload for creating a new user.

+ username: "new_user" (string, required) - Desired username
+ email: "newuser@example.com" (string, required) - User email address
+ password: "securePassword123" (string, required) - Account password

## ErrorResponse (object)
Standard error response format.

+ error: "User not found" (string, required) - Error message
+ code: "USER_NOT_FOUND" (string, required) - Error code
+ details (array[string], optional) - Additional error information
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
    
    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null
}
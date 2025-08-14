package co.anbora.labs.apiblueprint.ide.highlighting

/**
 * Centralized regex patterns for API Blueprint syntax detection.
 * Precompiled patterns for better performance.
 */
object ApiBlueprintRegexUtils {
    
    // Metadata patterns
    val FORMAT_PATTERN = Regex("^FORMAT:\\s*1A\\s*$", RegexOption.IGNORE_CASE)
    val HOST_PATTERN = Regex("^HOST:\\s*(https?://\\S+)\\s*$", RegexOption.IGNORE_CASE)
    
    // Group patterns
    val GROUP_PATTERN = Regex("^#+\\s*(?:(Group)\\s+(.+)|(.*?)\\s+(Group))\\s*$", RegexOption.IGNORE_CASE)
    
    // Resource patterns (## Resource Name [/path])
    val RESOURCE_PATTERN = Regex("^\\s*##\\s+(.+?)\\s+(\\[(/[^\\]]+)\\])\\s*$")
    
    // Action patterns with HTTP method
    val ACTION_PATTERN = Regex("\\[(GET|POST|PUT|PATCH|DELETE|HEAD|OPTIONS|TRACE|CONNECT)\\s+([^\\]]+)\\]", RegexOption.IGNORE_CASE)
    
    // URI template variables
    val URI_VAR_PATTERN = Regex("\\{([A-Za-z_][A-Za-z0-9_]*)\\}")
    val URI_QUERY_PATTERN = Regex("\\{\\?([A-Za-z0-9_,]+)\\}")
    
    // Section patterns (+ Parameters, + Response, etc.)
    val SECTION_PATTERN = Regex("^\\s*\\+\\s+(Parameters|Headers|Body|Schema)\\b.*$", RegexOption.IGNORE_CASE)
    
    // Attributes section with type reference: + Attributes (TypeName)
    val ATTRIBUTES_SECTION_PATTERN = Regex("^\\s*\\+\\s+(Attributes)\\s*\\(([^)]+)\\)\\s*$", RegexOption.IGNORE_CASE)
    
    // Simple Attributes section: + Attributes
    val SIMPLE_ATTRIBUTES_PATTERN = Regex("^\\s*\\+\\s+(Attributes)\\s*$", RegexOption.IGNORE_CASE)
    
    // Request/Response patterns
    val REQUEST_PATTERN = Regex("^\\s*\\+\\s+(Request)\\s*(.*?)\\s*(\\(([^)]+)\\))?\\s*$", RegexOption.IGNORE_CASE)
    val RESPONSE_PATTERN = Regex("^\\s*\\+\\s+(Response)\\s+(\\d{3})\\s*(\\(([^)]+)\\))?\\s*$", RegexOption.IGNORE_CASE)
    
    // MIME type pattern
    val MIME_PATTERN = Regex("\\(([^)]+)\\)")
    
    // Status code pattern
    val STATUS_CODE_PATTERN = Regex("\\b(\\d{3})\\b")
    
    // HTTP method pattern (standalone)
    val HTTP_METHOD_PATTERN = Regex("\\b(GET|POST|PUT|PATCH|DELETE|HEAD|OPTIONS|TRACE|CONNECT)\\b", RegexOption.IGNORE_CASE)
    
    // V2 MSON patterns (TODO: Implement in V2)
    // TODO: V2 - Data Structures section
    val DATA_STRUCTURES_PATTERN = Regex("^#+\\s*Data\\s+Structures\\s*$", RegexOption.IGNORE_CASE)
    
    // TODO: V2 - MSON type definitions (## User (object))
    val MSON_TYPE_PATTERN = Regex("^\\s*##\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\(([^)]+)\\)\\s*$")
    
    // TODO: V2 - MSON properties with types and flags
    val MSON_PROPERTY_PATTERN = Regex("^\\s*\\+\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*:\\s*(.+?)\\s*\\(([^)]+)\\)\\s*-?\\s*(.*)$")
    
    // Type annotations in parentheses ((number), (object), (string), etc.)
    val TYPE_ANNOTATION_PATTERN = Regex("\\((number|object|string|boolean|array|enum)\\b[^)]*\\)", RegexOption.IGNORE_CASE)
    
    // Number values (integers and floats)
    val NUMBER_PATTERN = Regex("\\b\\d+(?:\\.\\d+)?\\b")
    
    // MSON flags (required, optional, nullable, etc.)
    val MSON_FLAG_PATTERN = Regex("\\b(required|optional|nullable|default|sample)\\b", RegexOption.IGNORE_CASE)
    
    // API Name (first H1 after metadata)
    val API_NAME_PATTERN = Regex("^#\\s+(.+)$")
    
    // Data Structures section header
    val DATA_STRUCTURES_HEADER_PATTERN = Regex("^#+\\s*Data\\s+Structures\\s*$", RegexOption.IGNORE_CASE)
    
    // MSON attribute definition: + name: value (type, flags) - description
    // Excludes section headers like + Attributes, + Parameters, etc.
    val MSON_ATTRIBUTE_PATTERN = Regex("^\\s*\\+\\s+(?!(?:Attributes|Parameters|Headers|Body|Schema|Request|Response|Default|Relation)\\b)([A-Za-z_][A-Za-z0-9_]*)\\s*(?::\\s*([^(\\-]+?)\\s*)?(?:\\(([^)]+)\\))?\\s*(?:-\\s*(.*))?$", RegexOption.IGNORE_CASE)
    
    // Parameter definition: + name: value (type, flags) - description  
    val PARAMETER_PATTERN = Regex("^\\s*\\+\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*(?::\\s*([^(\\-]+?)\\s*)?(?:\\(([^)]+)\\))?\\s*(?:-\\s*(.*))?$")
    
    // Default value syntax: + Default: `value`
    val DEFAULT_VALUE_PATTERN = Regex("^\\s*\\+\\s*Default\\s*:\\s*`([^`]+)`\\s*$", RegexOption.IGNORE_CASE)
    
    // Action name pattern: ### Action Name [HTTP Method]
    val ACTION_NAME_PATTERN = Regex("^\\s*###\\s+(.+?)\\s*\\[([A-Z]+)\\s*([^\\]]+)?\\]\\s*$")
    
    // Resource with action pattern: ## Resource Name [/path] or ## Action Name [HTTP METHOD /path]
    val RESOURCE_ACTION_PATTERN = Regex("^\\s*##\\s+(.+?)\\s*\\[([A-Z]*\\s*[^\\]]+)\\]\\s*$")
    
    // Relation pattern: + Relation: type
    val RELATION_PATTERN = Regex("^\\s*\\+\\s*Relation\\s*:\\s*(.+)\\s*$", RegexOption.IGNORE_CASE)
    
    // String literal in quotes or backticks
    val STRING_LITERAL_PATTERN = Regex("\"([^\"]*)\"|`([^`]*)`")
    
    // Array type pattern: array[Type] or array
    val ARRAY_TYPE_PATTERN = Regex("\\barray(?:\\[([^\\]]+)\\])?\\b", RegexOption.IGNORE_CASE)
    
    // Complex type pattern: Type (base) or Type - for referencing data structures
    val COMPLEX_TYPE_PATTERN = Regex("\\b([A-Z][A-Za-z0-9_]*)(?:\\s*\\(([^)]+)\\))?\\b")
}
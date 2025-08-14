package co.anbora.labs.apiblueprint.ide.highlighting

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Font

/**
 * Defines text attribute keys for API Blueprint semantic highlighting.
 * These keys are used by the annotator to apply consistent styling across the plugin.
 */
object ApiBlueprintHighlighterKeys {
    
    // Metadata (FORMAT: 1A, HOST: ...)
    val APIBLUEPRINT_META = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_META",
        DefaultLanguageHighlighterColors.METADATA
    )
    
    // Group headers (# Group Users)
    val APIBLUEPRINT_GROUP = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_GROUP",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(0x9876AA), Color(0xCC7832))
            fontType = Font.BOLD
        }
    )
    
    // Resource paths ([/users/{id}])
    val APIBLUEPRINT_RESOURCE = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_RESOURCE",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(0x0000FF), Color(0x6897BB))
            fontType = Font.BOLD
        }
    )
    
    // HTTP methods (GET, POST, PUT, etc.)
    val APIBLUEPRINT_METHOD = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_METHOD",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(0x008000), Color(0x6A8759))
            fontType = Font.BOLD
        }
    )
    
    // URI template variables ({id}, {?page,size})
    val APIBLUEPRINT_URI_VAR = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_URI_VAR",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(0x800080), Color(0x9876AA))
            fontType = Font.ITALIC
        }
    )
    
    // Section names (+ Parameters, + Response, etc.)
    val APIBLUEPRINT_SECTION = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_SECTION",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(0x660E7A), Color(0xCC7832))
            fontType = Font.BOLD
        }
    )
    
    // MIME types ((application/json))
    val APIBLUEPRINT_MIME = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_MIME",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(0x008080), Color(0x629755))
        }
    )
    
    // Status codes (200, 404, etc.)
    val APIBLUEPRINT_STATUS = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_STATUS",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(0x0000FF), Color(0x6897BB))
            fontType = Font.BOLD
        }
    )
    
    // V2 Extensions for MSON (Data Structures)
    // TODO: V2 - MSON type definitions (User (object))
    val APIBLUEPRINT_MSON_TYPE = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_MSON_TYPE",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(0x8B4513), Color(0xD0D0FF))
            fontType = Font.BOLD
        }
    )
    
    // TODO: V2 - MSON property names (+ id: 42)
    val APIBLUEPRINT_PROPERTY = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_PROPERTY",
        DefaultLanguageHighlighterColors.INSTANCE_FIELD
    )
    
    // MSON type annotations ((number), (object), (string))
    val APIBLUEPRINT_TYPE_ANNOTATION = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_TYPE_ANNOTATION",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(0x0000FF), Color(0x6897BB))
        }
    )
    
    // Number values (25, 1415203908)
    val APIBLUEPRINT_NUMBER = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_NUMBER",
        DefaultLanguageHighlighterColors.NUMBER
    )
    
    // MSON flags/keywords (required, optional, nullable)
    val APIBLUEPRINT_FLAG = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_FLAG",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(0x000080), Color(0xCC7832))
            fontType = Font.BOLD
        }
    )
    
    // API Name (first H1 header after metadata)
    val APIBLUEPRINT_API_NAME = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_API_NAME",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(0x0000FF), Color(0x6897BB))
            fontType = Font.BOLD
        }
    )
    
    // Action names in resource headers
    val APIBLUEPRINT_ACTION_NAME = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_ACTION_NAME",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(0x008000), Color(0x6A8759))
            fontType = Font.BOLD
        }
    )
    
    // Data Structures section
    val APIBLUEPRINT_DATA_STRUCTURES = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_DATA_STRUCTURES",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(0x800080), Color(0x9876AA))
            fontType = Font.BOLD
        }
    )
    
    // Parameter names and attribute names
    val APIBLUEPRINT_PARAMETER_NAME = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_PARAMETER_NAME",
        DefaultLanguageHighlighterColors.INSTANCE_FIELD
    )
    
    // String values and samples
    val APIBLUEPRINT_STRING_VALUE = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_STRING_VALUE",
        DefaultLanguageHighlighterColors.STRING
    )
    
    // Relation attributes
    val APIBLUEPRINT_RELATION = TextAttributesKey.createTextAttributesKey(
        "APIBLUEPRINT_RELATION",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(0x8B4513), Color(0xD0D0FF))
        }
    )
}
package co.anbora.labs.apiblueprint.ide.highlighting

import co.anbora.labs.apiblueprint.extensions.isBluePrintFile
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

/**
 * Annotator for API Blueprint semantic highlighting on Markdown files.
 * Detects API Blueprint patterns and applies appropriate text attributes.
 */
class ApiBlueprintAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        // Only process elements in API Blueprint files
        val virtualFile = element.containingFile?.originalFile?.virtualFile ?: return
        if (!virtualFile.isBluePrintFile()) return
        
        // Process all text elements for API Blueprint patterns
        val text = element.text ?: return
        if (text.trim().isEmpty()) return
        
        val baseOffset = element.textRange.startOffset
        
        // Process line by line to handle multiline elements correctly
        val lines = text.split("\n")
        var currentOffset = baseOffset
        
        lines.forEach { line ->
            if (line.trim().isNotEmpty()) {
                // Check for different API Blueprint patterns on this line
                annotateApiBlueprint(element, line, currentOffset, holder)
            }
            currentOffset += line.length + 1 // +1 for newline character
        }
    }

    private fun annotateApiBlueprint(element: PsiElement, text: String, baseOffset: Int, holder: AnnotationHolder) {
        // Check for metadata (FORMAT, HOST)
        if (ApiBlueprintRegexUtils.FORMAT_PATTERN.matches(text) || 
            ApiBlueprintRegexUtils.HOST_PATTERN.matches(text)) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(element.textRange)
                .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_META)
                .create()
            return
        }
        
        // Check for group headers
        val groupMatch = ApiBlueprintRegexUtils.GROUP_PATTERN.find(text)
        if (groupMatch != null) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(element.textRange)
                .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_GROUP)
                .create()
            return
        }
        
        // Check for resource headers with path
        val resourceMatch = ApiBlueprintRegexUtils.RESOURCE_PATTERN.find(text)
        if (resourceMatch != null) {
            val pathGroup = resourceMatch.groups[2]
            if (pathGroup != null) {
                val pathRange = TextRange(
                    baseOffset + pathGroup.range.first,
                    baseOffset + pathGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(pathRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_RESOURCE)
                    .create()
            }
        }
        
        // Check for Attributes section with type reference: + Attributes (TypeName)
        val attributesSectionMatch = ApiBlueprintRegexUtils.ATTRIBUTES_SECTION_PATTERN.find(text)
        if (attributesSectionMatch != null) {
            // Highlight "Attributes" keyword
            val attributesGroup = attributesSectionMatch.groups[1]
            if (attributesGroup != null) {
                val attributesRange = TextRange(
                    baseOffset + attributesGroup.range.first,
                    baseOffset + attributesGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(attributesRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_SECTION)
                    .create()
            }
            
            // Highlight type reference (e.g., "Coupon")
            val typeGroup = attributesSectionMatch.groups[2]
            if (typeGroup != null) {
                val typeRange = TextRange(
                    baseOffset + typeGroup.range.first,
                    baseOffset + typeGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(typeRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_MSON_TYPE)
                    .create()
            }
            return  // Don't process other patterns for this line
        } 
        // Check for simple Attributes section: + Attributes
        else {
            val simpleAttributesMatch = ApiBlueprintRegexUtils.SIMPLE_ATTRIBUTES_PATTERN.find(text)
            if (simpleAttributesMatch != null) {
                val attributesGroup = simpleAttributesMatch.groups[1]
                if (attributesGroup != null) {
                    val attributesRange = TextRange(
                        baseOffset + attributesGroup.range.first,
                        baseOffset + attributesGroup.range.last + 1
                    )
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(attributesRange)
                        .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_SECTION)
                        .create()
                }
            }
            // Check for other section headers (+ Parameters, + Response, etc.)
            else {
                val sectionMatch = ApiBlueprintRegexUtils.SECTION_PATTERN.find(text)
                if (sectionMatch != null) {
                    val sectionGroup = sectionMatch.groups[1]
                    if (sectionGroup != null) {
                        val sectionRange = TextRange(
                            baseOffset + sectionGroup.range.first,
                            baseOffset + sectionGroup.range.last + 1
                        )
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .range(sectionRange)
                            .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_SECTION)
                            .create()
                    }
                }
            }
        }
        
        // Check for Request patterns
        val requestMatch = ApiBlueprintRegexUtils.REQUEST_PATTERN.find(text)
        if (requestMatch != null) {
            // Highlight the "Request" keyword
            val requestGroup = requestMatch.groups[1]
            if (requestGroup != null) {
                val requestRange = TextRange(
                    baseOffset + requestGroup.range.first,
                    baseOffset + requestGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(requestRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_SECTION)
                    .create()
            }
            
            // Highlight MIME type if present
            val mimeGroup = requestMatch.groups[4]
            if (mimeGroup != null) {
                val mimeRange = TextRange(
                    baseOffset + mimeGroup.range.first,
                    baseOffset + mimeGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(mimeRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_MIME)
                    .create()
            }
        }
        
        // Check for Response patterns
        val responseMatch = ApiBlueprintRegexUtils.RESPONSE_PATTERN.find(text)
        if (responseMatch != null) {
            // Highlight the "Response" keyword
            val responseGroup = responseMatch.groups[1]
            if (responseGroup != null) {
                val responseRange = TextRange(
                    baseOffset + responseGroup.range.first,
                    baseOffset + responseGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(responseRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_SECTION)
                    .create()
            }
            
            // Highlight status code
            val statusGroup = responseMatch.groups[2]
            if (statusGroup != null) {
                val statusRange = TextRange(
                    baseOffset + statusGroup.range.first,
                    baseOffset + statusGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(statusRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_STATUS)
                    .create()
            }
            
            // Highlight MIME type if present
            val mimeGroup = responseMatch.groups[4]
            if (mimeGroup != null) {
                val mimeRange = TextRange(
                    baseOffset + mimeGroup.range.first,
                    baseOffset + mimeGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(mimeRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_MIME)
                    .create()
            }
        }
        
        // Check for HTTP methods and URI variables
        annotateHttpMethodsAndUriVars(text, baseOffset, holder)
        
        // Check for MSON type annotations, numbers, and flags
        annotateMsonElements(text, baseOffset, holder)
        
        // Check for advanced API Blueprint elements
        annotateAdvancedElements(text, baseOffset, holder, element)
        
    }

    private fun annotateHttpMethodsAndUriVars(text: String, baseOffset: Int, holder: AnnotationHolder) {
        // Annotate HTTP methods
        ApiBlueprintRegexUtils.HTTP_METHOD_PATTERN.findAll(text).forEach { match ->
            val methodRange = TextRange(
                baseOffset + match.range.first,
                baseOffset + match.range.last + 1
            )
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(methodRange)
                .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_METHOD)
                .create()
        }
        
        // Annotate action patterns [GET /path]
        ApiBlueprintRegexUtils.ACTION_PATTERN.findAll(text).forEach { match ->
            // Highlight the entire action block
            val actionRange = TextRange(
                baseOffset + match.range.first,
                baseOffset + match.range.last + 1
            )
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(actionRange)
                .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_RESOURCE)
                .create()
            
            // Specifically highlight the HTTP method within the action
            val methodGroup = match.groups[1]
            if (methodGroup != null) {
                val methodRange = TextRange(
                    baseOffset + methodGroup.range.first,
                    baseOffset + methodGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(methodRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_METHOD)
                    .create()
            }
        }
        
        // Annotate URI variables
        annotateUriVariables(text, baseOffset, holder)
    }

    private fun annotateUriVariables(text: String, baseOffset: Int, holder: AnnotationHolder) {
        // Annotate simple URI variables {id}
        ApiBlueprintRegexUtils.URI_VAR_PATTERN.findAll(text).forEach { match ->
            val varRange = TextRange(
                baseOffset + match.range.first,
                baseOffset + match.range.last + 1
            )
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(varRange)
                .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_URI_VAR)
                .create()
        }
        
        // Annotate query URI variables {?page,size}
        ApiBlueprintRegexUtils.URI_QUERY_PATTERN.findAll(text).forEach { match ->
            val queryRange = TextRange(
                baseOffset + match.range.first,
                baseOffset + match.range.last + 1
            )
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(queryRange)
                .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_URI_VAR)
                .create()
        }
        
        // Annotate MIME types in parentheses
        ApiBlueprintRegexUtils.MIME_PATTERN.findAll(text).forEach { match ->
            val mimeGroup = match.groups[1]
            if (mimeGroup != null && isMimeType(mimeGroup.value)) {
                val mimeRange = TextRange(
                    baseOffset + mimeGroup.range.first,
                    baseOffset + mimeGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(mimeRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_MIME)
                    .create()
            }
        }
    }
    
    private fun isMimeType(text: String): Boolean {
        return text.contains("/") && (
            text.startsWith("application/") ||
            text.startsWith("text/") ||
            text.startsWith("image/") ||
            text.startsWith("audio/") ||
            text.startsWith("video/") ||
            text.startsWith("multipart/") ||
            text.startsWith("message/")
        )
    }
    
    private fun annotateMsonElements(text: String, baseOffset: Int, holder: AnnotationHolder) {
        // Annotate type annotations like (number), (object), (string)
        ApiBlueprintRegexUtils.TYPE_ANNOTATION_PATTERN.findAll(text).forEach { match ->
            val typeRange = TextRange(
                baseOffset + match.range.first,
                baseOffset + match.range.last + 1
            )
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(typeRange)
                .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_TYPE_ANNOTATION)
                .create()
        }
        
        // Annotate number values
        ApiBlueprintRegexUtils.NUMBER_PATTERN.findAll(text).forEach { match ->
            // Skip numbers that are inside type annotations or MIME types
            val matchStart = match.range.first
            val matchEnd = match.range.last + 1
            
            // Check if this number is inside parentheses (type annotation or MIME type)
            var insideParens = false
            var parenStart = -1
            
            for (i in 0 until matchStart) {
                if (text[i] == '(') parenStart = i
                else if (text[i] == ')') parenStart = -1
            }
            
            if (parenStart != -1) {
                val closeParen = text.indexOf(')', matchEnd)
                if (closeParen != -1) {
                    val parenContent = text.substring(parenStart + 1, closeParen)
                    // Skip if it looks like a type annotation or MIME type
                    if (parenContent.contains("/") || 
                        parenContent.matches(Regex(".*\\b(number|object|string|boolean|array|enum)\\b.*", RegexOption.IGNORE_CASE))) {
                        insideParens = true
                    }
                }
            }
            
            if (!insideParens) {
                val numberRange = TextRange(
                    baseOffset + match.range.first,
                    baseOffset + match.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(numberRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_NUMBER)
                    .create()
            }
        }
        
        // Annotate MSON flags (required, optional, etc.)
        ApiBlueprintRegexUtils.MSON_FLAG_PATTERN.findAll(text).forEach { match ->
            val flagRange = TextRange(
                baseOffset + match.range.first,
                baseOffset + match.range.last + 1
            )
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(flagRange)
                .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_FLAG)
                .create()
        }
    }
    
    private fun annotateAdvancedElements(text: String, baseOffset: Int, holder: AnnotationHolder, element: PsiElement) {
        // Check for API Name (first H1 header after metadata)
        val apiNameMatch = ApiBlueprintRegexUtils.API_NAME_PATTERN.find(text)
        if (apiNameMatch != null && !text.contains("Group") && !text.contains("Data Structures")) {
            val nameGroup = apiNameMatch.groups[1]
            if (nameGroup != null) {
                val nameRange = TextRange(
                    baseOffset + nameGroup.range.first,
                    baseOffset + nameGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(nameRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_API_NAME)
                    .create()
            }
        }
        
        // Check for Data Structures section
        if (ApiBlueprintRegexUtils.DATA_STRUCTURES_HEADER_PATTERN.matches(text)) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(element.textRange)
                .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_DATA_STRUCTURES)
                .create()
        }
        
        // Check for MSON attributes: + name: value (type, flags) - description
        val attributeMatch = ApiBlueprintRegexUtils.MSON_ATTRIBUTE_PATTERN.find(text)
        if (attributeMatch != null) {
            annotateAttributeComponents(attributeMatch, baseOffset, holder)
        }
        
        // Check for Action names: ### Action Name [HTTP Method]
        val actionNameMatch = ApiBlueprintRegexUtils.ACTION_NAME_PATTERN.find(text)
        if (actionNameMatch != null) {
            // Highlight action name
            val nameGroup = actionNameMatch.groups[1]
            if (nameGroup != null) {
                val nameRange = TextRange(
                    baseOffset + nameGroup.range.first,
                    baseOffset + nameGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(nameRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_ACTION_NAME)
                    .create()
            }
            
            // Highlight HTTP method
            val methodGroup = actionNameMatch.groups[2]
            if (methodGroup != null) {
                val methodRange = TextRange(
                    baseOffset + methodGroup.range.first,
                    baseOffset + methodGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(methodRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_METHOD)
                    .create()
            }
        }
        
        // Check for Relation attributes
        val relationMatch = ApiBlueprintRegexUtils.RELATION_PATTERN.find(text)
        if (relationMatch != null) {
            val relationGroup = relationMatch.groups[1]
            if (relationGroup != null) {
                val relationRange = TextRange(
                    baseOffset + relationGroup.range.first,
                    baseOffset + relationGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(relationRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_RELATION)
                    .create()
            }
        }
        
        // Check for Default values: + Default: `value`
        val defaultMatch = ApiBlueprintRegexUtils.DEFAULT_VALUE_PATTERN.find(text)
        if (defaultMatch != null) {
            val valueGroup = defaultMatch.groups[1]
            if (valueGroup != null) {
                val valueRange = TextRange(
                    baseOffset + valueGroup.range.first,
                    baseOffset + valueGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(valueRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_STRING_VALUE)
                    .create()
            }
        }
        
        // Annotate string literals in quotes and backticks
        ApiBlueprintRegexUtils.STRING_LITERAL_PATTERN.findAll(text).forEach { match ->
            val valueGroup = match.groups[1] ?: match.groups[2]
            if (valueGroup != null) {
                val stringRange = TextRange(
                    baseOffset + valueGroup.range.first,
                    baseOffset + valueGroup.range.last + 1
                )
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(stringRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_STRING_VALUE)
                    .create()
            }
        }
    }
    
    private fun annotateAttributeComponents(match: MatchResult, baseOffset: Int, holder: AnnotationHolder) {
        // Highlight parameter/attribute name
        val nameGroup = match.groups[1]
        if (nameGroup != null) {
            val nameRange = TextRange(
                baseOffset + nameGroup.range.first,
                baseOffset + nameGroup.range.last + 1
            )
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(nameRange)
                .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_PARAMETER_NAME)
                .create()
        }
        
        // Highlight sample value
        val valueGroup = match.groups[2]
        if (valueGroup != null && valueGroup.value.trim().isNotEmpty()) {
            val valueRange = TextRange(
                baseOffset + valueGroup.range.first,
                baseOffset + valueGroup.range.last + 1
            )
            // Check if it's a number or string
            if (valueGroup.value.trim().matches(Regex("\\d+(\\.\\d+)?"))) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(valueRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_NUMBER)
                    .create()
            } else {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(valueRange)
                    .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_STRING_VALUE)
                    .create()
            }
        }
        
        // Highlight type and flags in parentheses
        val typeGroup = match.groups[3]
        if (typeGroup != null) {
            val typeContent = typeGroup.value
            val typeRange = TextRange(
                baseOffset + typeGroup.range.first,
                baseOffset + typeGroup.range.last + 1
            )
            
            // Parse individual components within the type section
            annotateTypeComponents(typeContent, baseOffset + typeGroup.range.first, holder)
        }
    }
    
    private fun annotateTypeComponents(typeContent: String, baseOffset: Int, holder: AnnotationHolder) {
        // Split by commas to handle multiple components like "string, required"
        val components = typeContent.split(",").map { it.trim() }
        var currentOffset = 0
        
        components.forEach { component ->
            val componentStart = typeContent.indexOf(component, currentOffset)
            if (componentStart >= 0) {
                val componentRange = TextRange(
                    baseOffset + componentStart,
                    baseOffset + componentStart + component.length
                )
                
                // Determine what type of component this is
                when {
                    component.matches(Regex("\\b(number|string|boolean|object|array|enum)\\b", RegexOption.IGNORE_CASE)) -> {
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .range(componentRange)
                            .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_TYPE_ANNOTATION)
                            .create()
                    }
                    component.matches(Regex("\\b(required|optional|nullable|default|sample)\\b", RegexOption.IGNORE_CASE)) -> {
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .range(componentRange)
                            .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_FLAG)
                            .create()
                    }
                    component.matches(Regex("\\b[A-Z][A-Za-z0-9_]*\\b")) -> {
                        // Custom type reference
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .range(componentRange)
                            .textAttributes(ApiBlueprintHighlighterKeys.APIBLUEPRINT_MSON_TYPE)
                            .create()
                    }
                }
                currentOffset = componentStart + component.length
            }
        }
    }
}
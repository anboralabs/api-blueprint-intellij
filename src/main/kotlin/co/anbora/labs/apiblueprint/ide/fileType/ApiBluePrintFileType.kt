package co.anbora.labs.apiblueprint.ide.fileType

import co.anbora.labs.apiblueprint.icons.ApiBluePrintIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import org.intellij.plugins.markdown.lang.MarkdownLanguage

class ApiBluePrintFileType: LanguageFileType(MarkdownLanguage.INSTANCE) {
    override fun getName() = "API Blueprint"

    override fun getDescription() = "API Blueprint file"

    override fun getDefaultExtension() = "apib"

    override fun getIcon() = ApiBluePrintIcons.FILE
}
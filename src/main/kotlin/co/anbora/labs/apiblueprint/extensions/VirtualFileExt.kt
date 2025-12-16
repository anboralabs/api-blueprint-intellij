package co.anbora.labs.apiblueprint.extensions

import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.vfs.VirtualFile
import org.intellij.plugins.markdown.lang.MarkdownFileType

fun VirtualFile.isBluePrintFile(): Boolean {
    return BLUEPRINT == this.extension &&
            FileTypeRegistry.getInstance().isFileOfType(this, MarkdownFileType.INSTANCE)
}
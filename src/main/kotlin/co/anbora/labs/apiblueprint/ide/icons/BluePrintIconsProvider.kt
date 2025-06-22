package co.anbora.labs.apiblueprint.ide.icons

import co.anbora.labs.apiblueprint.extensions.isBluePrintFile
import co.anbora.labs.apiblueprint.icons.APIBluePrintIcons
import com.intellij.ide.FileIconProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.ElementBase
import com.intellij.ui.IconManager
import javax.swing.Icon

class BluePrintIconsProvider: FileIconProvider {
    override fun getIcon(file: VirtualFile, flags: Int, project: Project?): Icon? {
        if (project == null || !file.isBluePrintFile()) {
            return null
        }

        val psiFile = PsiManager.getInstance(project).findFile(file) ?: return null

        val iconManager = IconManager.getInstance()
        val iconFlags = ElementBase.transformFlags(psiFile, flags)
        return iconManager.createLayeredIcon(psiFile, APIBluePrintIcons.FILE, iconFlags)
    }
}
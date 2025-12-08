package co.anbora.labs.apiblueprint.icons

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object ApiBluePrintIcons {
    val FILE = getIcon("blueprint.svg")
    val FILE_40 = getIcon("blueprint_40.svg")


    private fun getIcon(path: String): Icon {
        return IconLoader.findIcon("/icons/$path", ApiBluePrintIcons::class.java.classLoader) as Icon
    }
}
package co.anbora.labs.apiblueprint.icons

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object APIBluePrintIcons {
    val FILE = getIcon("blueprint.svg")


    private fun getIcon(path: String): Icon {
        return IconLoader.findIcon("/icons/$path", APIBluePrintIcons::class.java.classLoader) as Icon
    }
}
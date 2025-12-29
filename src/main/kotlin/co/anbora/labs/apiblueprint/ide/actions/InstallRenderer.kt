package co.anbora.labs.apiblueprint.ide.actions

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction

class InstallRenderer: DumbAwareAction("Install") {
    override fun actionPerformed(e: AnActionEvent) {
        BrowserUtil.browse("https://plugins.jetbrains.com/plugin/29479-api-blueprint-viewer-lifetime-")
    }
}

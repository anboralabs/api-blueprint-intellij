package co.anbora.labs.apiblueprint.ide.actions

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction

class BuyLicense: DumbAwareAction("Buy") {
    override fun actionPerformed(e: AnActionEvent) {
        BrowserUtil.browse("https://plugins.jetbrains.com/plugin/28202-api-blueprint-lifetime-")
    }
}

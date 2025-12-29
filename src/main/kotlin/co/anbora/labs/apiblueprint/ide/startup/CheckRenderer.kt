package co.anbora.labs.apiblueprint.ide.startup

import co.anbora.labs.apiblueprint.ide.actions.InstallRenderer
import co.anbora.labs.apiblueprint.ide.notifications.ApiBlueprintNotifications
import com.intellij.ide.plugins.PluginManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class CheckRenderer: ProjectActivity {
    override suspend fun execute(project: Project) {
        val plugin = PluginManager.getInstance().findEnabledPlugin(
            PluginId.getId("co.anbora.labs.apiBlueprint.viewer")
        )

        if (plugin == null) {
            val notification = ApiBlueprintNotifications.createNotification(
                "API Blueprint",
                "Install API Blueprint Renderer to preview .apib files",
                NotificationType.INFORMATION,
                InstallRenderer()
            )

            ApiBlueprintNotifications.showNotification(notification, project)
        }
    }
}
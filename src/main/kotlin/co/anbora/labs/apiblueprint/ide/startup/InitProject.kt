package co.anbora.labs.apiblueprint.ide.startup

import co.anbora.labs.apiblueprint.ide.notifications.APIBlueprintNotifications
import co.anbora.labs.apiblueprint.license.CheckLicense
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class InitProject: ProjectActivity {
    override suspend fun execute(project: Project) {
        val licensed = CheckLicense.isLicensed() ?: false

        if (!licensed) {
            CheckLicense.requestLicense("Buy a license")
            APIBlueprintNotifications.supportNotification(project)
        }
    }
}
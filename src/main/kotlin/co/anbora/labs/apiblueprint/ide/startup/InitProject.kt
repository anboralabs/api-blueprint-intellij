package co.anbora.labs.apiblueprint.ide.startup

import co.anbora.labs.apiblueprint.ide.notifications.ApiBlueprintNotifications
import co.anbora.labs.apiblueprint.license.CheckLicense
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.util.concurrency.AppExecutorUtil
import java.util.concurrent.TimeUnit

class InitProject: ProjectActivity {
    override suspend fun execute(project: Project) {
        AppExecutorUtil.getAppScheduledExecutorService().schedule({
            val licensed = CheckLicense.isLicensed() ?: false

            if (!licensed && !project.isDisposed) {
                CheckLicense.requestLicense("Buy a license")
                ApiBlueprintNotifications.supportNotification(project)
            }
        }, 5, TimeUnit.MINUTES)
    }
}
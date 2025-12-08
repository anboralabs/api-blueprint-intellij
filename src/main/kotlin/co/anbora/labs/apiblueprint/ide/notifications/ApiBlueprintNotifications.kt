package co.anbora.labs.apiblueprint.ide.notifications

import co.anbora.labs.apiblueprint.icons.ApiBluePrintIcons
import co.anbora.labs.apiblueprint.ide.actions.BuyLicense
import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.Project
import javax.swing.Icon

object ApiBlueprintNotifications {

    @JvmStatic
    fun createNotification(
        title: String,
        content: String,
        type: NotificationType,
        notificationGroup: String,
        icon: Icon,
        vararg actions: AnAction
    ): Notification {
        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup(notificationGroup)
            .createNotification(content, type)
            .setTitle(title)
            .setIcon(icon)

        for (action in actions) {
            notification.addAction(action)
        }

        return notification
    }

    @JvmStatic
    fun showNotification(notification: Notification, project: Project?) {
        try {
            notification.notify(project)
        } catch (e: Exception) {
            notification.notify(project)
        }
    }

    @JvmStatic
    fun createNotification(
        title: String,
        content: String,
        type: NotificationType,
        vararg actions: AnAction
    ): Notification {
        return createNotification(
            title,
            content,
            type,
            "a6b98725-d171-4a3a-b712-ad3ac40cadc5_APIBlueprint_Notification",
            ApiBluePrintIcons.FILE_40,
            *actions
        )
    }

    @JvmStatic
    fun supportNotification(project: Project?) {
        val notification = createNotification(
            "Support APIBlueprint Plugin",
            "Buy the license; 15 USD lifetime",
            NotificationType.WARNING,
            BuyLicense()
        )

        showNotification(notification, project)
    }
}
package sdk.chat.ui.activities.thread.details

import sdk.chat.core.dao.Thread
import sdk.chat.core.dao.User
import sdk.chat.core.interfaces.ThreadType
import sdk.chat.core.session.ChatSDK

open class ThreadUser(val thread: Thread, val user: User) {
    fun isActive(): Boolean {
        return when {
            thread.typeIs(ThreadType.Group) -> ChatSDK.thread()?.isActive(thread, user) == true
            else -> user.isOnline
        }
    }
}
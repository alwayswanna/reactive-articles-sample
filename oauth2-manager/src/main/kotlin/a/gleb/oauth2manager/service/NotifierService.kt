package a.gleb.oauth2manager.service

import a.gleb.articlecommon.models.mq.AuthorChangeInfoEvent
import a.gleb.articlecommon.models.mq.EventType
import a.gleb.oauth2manager.db.entity.Account
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Service
import java.time.LocalDateTime

const val OUTPUT_MESSAGE_CHANNEL = "notify-out-0"

@Service
class NotifierService(
    private val streamBridge: StreamBridge
) {

    /**
     * Method sends notification to article-app service, for update all articles which
     * relate with user, if user changed username.
     */
    fun notify(account: Account, eventType: EventType) {
        var accountChangeEvent = AuthorChangeInfoEvent.builder()
            .timestamp(LocalDateTime.now())
            .accountId(account.id)
            .eventType(eventType)
            .newUsername(account.username)
            .build()
        streamBridge.send(OUTPUT_MESSAGE_CHANNEL, accountChangeEvent)
    }
}
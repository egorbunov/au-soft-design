package ru.mit.spbau.sd.chat.client

import org.slf4j.LoggerFactory
import ru.mit.spbau.sd.chat.client.model.ChatModel
import ru.mit.spbau.sd.chat.client.model.ChatEventsListener
import ru.mit.spbau.sd.chat.client.msg.ClientLifecycleListener
import ru.mit.spbau.sd.chat.client.msg.UsersNetEventHandler
import ru.mit.spbau.sd.chat.client.net.ChatNetworkShield
import ru.spbau.mit.sd.commons.proto.ChatMessage
import ru.spbau.mit.sd.commons.proto.ChatUserInfo
import ru.spbau.mit.sd.commons.proto.ChatUserIpAddr
import java.util.*

/**
 * Component, which have control over chat model; Every model change should
 * be made through this controller; Others can listen to this model changes
 * through `ChatEventsListener`.
 *
 * @param networkShield - chat network interface
 * @param chatModel - chat data
 */
class ChatController internal constructor(
        private val networkShield: ChatNetworkShield,
        private val chatModel: ChatModel<ChatUserIpAddr>
):
        UsersNetEventHandler<ChatUserIpAddr>,
        ClientLifecycleListener {

    companion object {
        val logger = LoggerFactory.getLogger(javaClass.name)
    }

    private val modelListeners = ArrayList<ChatEventsListener<ChatUserIpAddr>>()

    fun addChatModelChangeListener(listener: ChatEventsListener<ChatUserIpAddr>) {
        modelListeners.add(listener)
    }

    fun removeChatModelChangeListener(listener: ChatEventsListener<ChatUserIpAddr>) {
        modelListeners.remove(listener)
    }

    /**
     * This call is propagated to `networkShield`
     */
    fun startClient() {
        networkShield.startClient(chatModel.clientInfo)
    }

    /**
     * This call is propagated to `networkShield` too.
     */
    fun stopClient() {
        networkShield.stopClient()
    }

    /**
     * Send chat message to other chat client
     */
    fun sendTextMessage(userId: ChatUserIpAddr, message: ChatMessage) {
        networkShield.sendChatMessage(userId, message)
        chatModel.addMessageSentByThisUser(userId, message)
        modelListeners.forEach { it.messageSent(userId, message) }
    }

    /**
     * Change current client info
     */
    fun changeClientInfo(newInfo: ChatUserInfo) {
        networkShield.changeClientInfo(newInfo)
        chatModel.changeClientInfo(newInfo)
        modelListeners.forEach { it.currentClientInfoChanged(newInfo) }
    }


    // ============ listener methods ============

    override fun clientStarted(usersList: List<Pair<ChatUserIpAddr, ChatUserInfo>>) {
        for ((id, info) in usersList) {
            chatModel.addUser(id, info)
            modelListeners.forEach { it.userBecomeOnline(id, info) }
        }
    }

    override fun clientStopped() {
        for ((id) in chatModel.getUsers()) {
            chatModel.removeUser(id)
            modelListeners.forEach { it.userGoneOffline(id) }
        }
    }

    override fun userBecomeOnline(userId: ChatUserIpAddr, userInfo: ChatUserInfo) {
        chatModel.addUser(userId, userInfo)
        modelListeners.forEach { it.userBecomeOnline(userId, userInfo) }
    }


    override fun userGoneOffline(userId: ChatUserIpAddr) {
        chatModel.removeUser(userId)
        modelListeners.forEach { it.userGoneOffline(userId) }
    }

    override fun userChangeInfo(userId: ChatUserIpAddr, newInfo: ChatUserInfo) {
        chatModel.editUser(userId, newInfo)
        modelListeners.forEach { it.userChanged(userId, newInfo) }
    }

    override fun userSentMessage(userId: ChatUserIpAddr, message: ChatMessage) {
        chatModel.addMessageSentByOtherUser(userId, message)
        modelListeners.forEach { it.messageReceived(userId, message) }
    }
}

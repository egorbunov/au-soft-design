package ru.mit.spbau.sd.chat.client.net

import ru.spbau.mit.sd.commons.proto.ChatMessage
import ru.spbau.mit.sd.commons.proto.ChatUserInfo
import ru.spbau.mit.sd.commons.proto.ChatUserIpAddr

/**
 * Interface for interacting with network part of the chat.
 */
interface ChatNetworkInterface {
    /**
     * Start client: tell chat network, that client is available for interaction
     */
    fun startClient(clientInfo: ChatUserInfo)

    /**
     * Stop client: tell chat network, that client gone offline
     */
    fun stopClient()

    /**
     * Send text chat message through chat network to other chat user
     */
    fun sendChatMessage(userId: ChatUserIpAddr, msg: ChatMessage)

    /**
     * Notify chat network, that current chat client info has changed
     */
    fun changeClientInfo(newInfo: ChatUserInfo)
}
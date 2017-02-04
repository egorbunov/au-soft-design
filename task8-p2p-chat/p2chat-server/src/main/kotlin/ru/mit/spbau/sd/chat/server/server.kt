package ru.mit.spbau.sd.chat.server

import ru.mit.spbau.sd.chat.server.net.ChatModelPeerMsgProcessor
import ru.mit.spbau.sd.chat.server.net.ChatServer

fun main(args: Array<String>) {
    val chatModel = ConcurrentChatModel()
    val peerEventProcessor = ChatModelPeerMsgProcessor(chatModel)
    val chatServer = ChatServer(peerEventProcessor)

    chatServer.setup()

    readLine()

    chatServer.destroy()
}

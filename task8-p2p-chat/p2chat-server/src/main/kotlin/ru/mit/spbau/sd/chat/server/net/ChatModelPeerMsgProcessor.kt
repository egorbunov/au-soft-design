package ru.mit.spbau.sd.chat.server.net

import ru.mit.spbau.sd.chat.server.ChatModel
import ru.spbau.mit.sd.commons.proto.ChatUserInfo
import ru.spbau.mit.sd.commons.proto.UsersList
import java.net.InetSocketAddress

/**
 * Simple peer message processor, which delegates all events to
 * chat model
 */
class ChatModelPeerMsgProcessor(private val chatModel: ChatModel<InetSocketAddress>)
    : PeerMsgProcessor<InetSocketAddress> {
    override fun peerBecomeOnline(peer: InetSocketAddress, userInfo: ChatUserInfo) {
        chatModel.addUser(peer, userInfo)
    }

    override fun peerGoneOffline(peer: InetSocketAddress) {
        chatModel.removeUser(peer)
    }

    override fun peerChangedInfo(peer: InetSocketAddress, newInfo: ChatUserInfo) {
        chatModel.editUser(peer, newInfo)
    }

    override fun usersRequested(): UsersList {
        return chatModel.getUsers()
    }

    override fun peerDisconnected(peer: InetSocketAddress) {
    }
}

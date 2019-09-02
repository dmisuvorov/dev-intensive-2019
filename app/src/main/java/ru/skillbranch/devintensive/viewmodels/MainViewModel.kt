package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType

class MainViewModel : BaseChatViewModel() {

    override val chats: LiveData<List<ChatItem>> = Transformations.map(chatRepository.loadChats()) { chats ->
        val listOfChats = chats
            .filter { !it.isArchived }
            .map { it.toChatItem() }
            .sortedBy { it.id.toInt() }
        val listOfArchivedChats = chats.filter { it.isArchived }

        if (listOfArchivedChats.isEmpty()) return@map listOfChats else {
            val allChatsPlusArchiveItem = mutableListOf<ChatItem>()
            with(allChatsPlusArchiveItem) {
                addAll(listOfChats)
                add(0, makeArchiveItem(listOfArchivedChats))
            }
            return@map allChatsPlusArchiveItem
        }
    }


    private fun makeArchiveItem(listOfArchivedChats: List<Chat>): ChatItem {
        val count = listOfArchivedChats.fold(0) { initial, chat -> initial + chat.unreadableMessageCount() }
        val lastChat =
            if (listOfArchivedChats.none { it.unreadableMessageCount() != 0 }) listOfArchivedChats.last() else
                listOfArchivedChats.filter { it.unreadableMessageCount() != 0 }.maxBy { it.lastMessageDate()!! }!!

        return ChatItem(
            "0",
            null,
            "",
            "Архив чатов",
            lastChat.lastMessageShort().first,
            count,
            lastChat.lastMessageDate()?.shortFormat(),
            false,
            ChatType.ARCHIVE,
            lastChat.lastMessageShort().second
        )
    }
}
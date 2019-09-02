package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import ru.skillbranch.devintensive.models.data.ChatItem


class ArchiveViewModel : BaseChatViewModel() {
    override val chats: LiveData<List<ChatItem>> = Transformations.map(chatRepository.loadChats()) { chats ->
        return@map chats
            .filter { it.isArchived }
            .map { it.toChatItem() }
            .sortedBy { it.id.toInt() }
    }

}
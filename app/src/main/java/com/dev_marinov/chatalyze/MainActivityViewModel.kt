package com.dev_marinov.chatalyze

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.data.chatMessage.ChatSocketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val chatSocketRepository: ChatSocketRepository
) : ViewModel() {

//    fun connectToChat(senderPhone: String?, recipientPhone: String?) {
//        // сюда передать объект message
//
//        getAllMessages()
//        //   getAllMessages()
//        Log.d("4444", " connectToChat execute")
//        // savedStateHandle.get<String>("username")?.let { username ->
//        viewModelScope.launch {
//            val result = chatSocketRepository.initSession(sender = _sender)
//            Log.d("4444", " connectToChat result.message=" + result.message)
//            when (result) {
//                is Resource.Success -> {
//                    Log.d("4444", " connectToChat Resource.Success")
//                    chatSocketRepository.observeMessages()
//                        .onEach { message ->
//                            Log.d("4444", " connectToChat Resource.Success message=" + message)
//                            val newList = state.value.messages.toMutableList().apply {
//                                add(_state.value.messages.size, message)
//                            }
//                            _state.value = state.value.copy(
//                                messages = newList
//                            )
//
//                            // тут вызвать как-то getChats
//
//                        }.launchIn(viewModelScope)
//
//                    /////////////////////////////
//                    // test ping pong
//
////                    chatSocketService.observePingPong()
////                        .onEach {
////                            Log.d("4444", " connectToChat Resource.Success ping pong=" + it)
////                        }
////
////                    val res = chatSocketService.method("ping")
////                    Log.d("4444", " connectToChat ping res=" + res)
//
//
//                }
//                is Resource.Error -> {
//                    Log.d("4444", " connectToChat Resource.Error")
//                    _toastEvent.emit(result.message ?: "Unknown error")
//                }
//            }
//        }
//
//        viewModelScope.launch(Dispatchers.IO) {
//            // chatSocketService.getStateUsersConnection()
//
//            //chatSocketService.observePing().collect {
//            //     Log.d("4444", " connectToChat Resource.Success ping pong=" + it)
//            //   }
//        }
//
//
//        // }
//    }

}

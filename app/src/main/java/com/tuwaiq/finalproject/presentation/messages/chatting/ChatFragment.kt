package com.tuwaiq.finalproject.presentation.messages.chatting

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.marginEnd
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuwaiq.finalproject.R
import com.tuwaiq.finalproject.databinding.ChatFragmentBinding
import com.tuwaiq.finalproject.databinding.MessageItemBinding
import com.tuwaiq.finalproject.domain.model.Chat
import com.tuwaiq.finalproject.util.Constant.uid
import com.tuwaiq.finalproject.util.FirebaseCallback
import dagger.hilt.android.AndroidEntryPoint
import java.text.Format
import java.util.*
import kotlin.math.log

private const val TAG = "ChatFragment"
@AndroidEntryPoint
class ChatFragment : Fragment() {



    private val viewModel by  viewModels<ChatViewModel>()

    private val args by navArgs<ChatFragmentArgs>()

    lateinit var binding: ChatFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatFragmentBinding.inflate(layoutInflater)
        binding.chatRv.apply {
            layoutManager = LinearLayoutManager(context)

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUser(args.receiverId).observe(
            viewLifecycleOwner,{
                binding.usernameTv.text = it.name
                Glide.with(requireContext()).load(it.photoUrl).placeholder(R.drawable.ic_person).into(binding.userIv)
            }
        )

        
       viewModel.getMessage(object : FirebaseCallback{
           override fun onCallback(callback: List<Chat>) {
               val messageList = mutableListOf<Chat>()
               callback.forEach { 
                   if (it.senderId == uid && it.receiverID == args.receiverId
                       || it.senderId == args.receiverId && it.receiverID == uid){
                   messageList += it
               }
               }
               Log.e(TAG, "onCallback: $messageList", )
               binding.chatRv.adapter = ChatAdapter(messageList)
           }

       })

    }

    override fun onStart() {
        super.onStart()

        binding.sendMessageBtn.setOnClickListener {
            val messageText = binding.messageEt.text.toString()
            val senderId = uid
            val receiverId = args.receiverId
            val chat = Chat(messageText,senderId,receiverId)

            viewModel.sendMessage(chat)
        }

    }

    val format = "hh:mm aa"
    private inner class ChatHolder(val binding: MessageItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {

            binding.messageBubble.apply {
                if (chat.senderId == uid) {
                    message(R.drawable.message_item,Gravity.END,chat)
                }else{
                    message(R.drawable.message_item_receiver,Gravity.START,chat)
                }
            }

        }

        fun message(drawable:Int, gravity:Int, chat: Chat) {
            binding.messageBubble.apply {
                setBackgroundResource(drawable)
                val params = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, gravity
                )
                this.layoutParams = params
                binding.chatMessageTv.text = chat.message
                binding.chatTimeTv.text = DateFormat.format(format, chat.date)
            }
        }
    }

    private inner class ChatAdapter(val chat:List<Chat>):RecyclerView.Adapter<ChatHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
            val binding = MessageItemBinding.inflate(
                layoutInflater,
                parent,
                false
            )
            return ChatHolder(binding)
        }

        override fun onBindViewHolder(holder: ChatHolder, position: Int) {
            val chats = chat[position]
            holder.bind(chats)

        }

        override fun getItemCount(): Int = chat.size
    }


}
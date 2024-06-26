package mk.ukim.finki.expressu.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import mk.ukim.finki.expressu.adapter.RecentChatRecyclerAdapter
import mk.ukim.finki.expressu.databinding.FragmentChatBinding
import mk.ukim.finki.expressu.model.Chatroom
import mk.ukim.finki.expressu.utils.FirebaseUtil

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RecentChatRecyclerAdapter
    private lateinit var chatsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatsRecyclerView = binding.chatsRecyclerView
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val query: Query = FirebaseUtil.getAllChatRooms()
            .whereArrayContains("usersIds", FirebaseUtil.currentUserId()!!)
            .orderBy("lastMessageTimeStamp", Query.Direction.DESCENDING)

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val chatRooms = task.result.toObjects(Chatroom::class.java)
                if (chatRooms.isNotEmpty()) {
                    binding.noChatsFound.visibility = View.GONE
                    Log.d("ChatFragment", "Chats found: ${chatRooms.size}")
                    val options = FirestoreRecyclerOptions.Builder<Chatroom>()
                        .setQuery(query, Chatroom::class.java)
                        .build()

                    adapter = RecentChatRecyclerAdapter(options, requireContext())
                    chatsRecyclerView.adapter = adapter
                    chatsRecyclerView.layoutManager =
                        WrapContentLinearLayoutManager(requireContext())
                    adapter.startListening()
                } else {
                    Log.d("ChatFragment", "No chats found")
                    binding.noChatsFound.visibility = View.VISIBLE
                }
            } else {
                Log.e("ChatFragment", "Error getting chats", task.exception)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (::adapter.isInitialized) adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (::adapter.isInitialized) adapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class WrapContentLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("TAG", "meet a IOOBE in RecyclerView")
        }
    }
}

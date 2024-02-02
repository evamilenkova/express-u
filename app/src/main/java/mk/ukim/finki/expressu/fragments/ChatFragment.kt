package mk.ukim.finki.expressu.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import mk.ukim.finki.expressu.R
import mk.ukim.finki.expressu.adapter.RecentChatRecyclerAdapter
import mk.ukim.finki.expressu.databinding.ActivitySearchUserBinding
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
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val view = binding.root
        chatsRecyclerView = binding.chatsRecyclerView
        setUpRecyclerView();
        return view
    }

    fun setUpRecyclerView() {

        val query: Query = FirebaseUtil.getAllChatRooms()
            .whereArrayContains("usersIds", FirebaseUtil.currentUserId()!!)
            .orderBy("lastMessageTimeStamp", Query.Direction.DESCENDING)
        val options: FirestoreRecyclerOptions<Chatroom> =
            FirestoreRecyclerOptions.Builder<Chatroom>()
                .setQuery(query, Chatroom::class.java).build()

        adapter = RecentChatRecyclerAdapter(options, requireContext())
        chatsRecyclerView.adapter = adapter
        chatsRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter.startListening()
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
        if (::adapter.isInitialized) adapter.notifyDataSetChanged()

    }
}
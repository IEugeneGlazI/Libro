package com.example.libro.ui.library

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libro.AddShelfActivity
import com.example.libro.CabinetBooksActivity
import com.example.libro.EditShelfActivity
import com.example.libro.data.JsonHelper
import com.example.libro.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private var shelfList = mutableListOf<Shelf>()
    private lateinit var shelfAdapter: ShelfAdapter

    private val shelfActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadShelves()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadShelves()

        binding.fabAddShelf.setOnClickListener {
            val intent = Intent(requireContext(), AddShelfActivity::class.java)
            shelfActivityLauncher.launch(intent)
        }
    }

    private fun loadShelves() {
        shelfList = JsonHelper.loadShelves(requireContext())
        shelfAdapter.updateShelves(shelfList)
    }

    private fun setupRecyclerView() {
        shelfAdapter = ShelfAdapter(shelfList, 
            onShelfClick = { shelf ->
                val intent = Intent(requireContext(), CabinetBooksActivity::class.java).apply {
                    putExtra("SHELF_NAME", shelf.name)
                }
                startActivity(intent)
            },
            onEditClick = { shelf, position ->
                val intent = Intent(requireContext(), EditShelfActivity::class.java).apply {
                    putExtra("SHELF_EXTRA", shelf)
                    putExtra("SHELF_INDEX", position)
                }
                shelfActivityLauncher.launch(intent)
            },
            onDeleteClick = { shelf, position ->
                showDeleteConfirmationDialog(shelf, position)
            }
        )
        binding.recyclerViewShelves.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewShelves.adapter = shelfAdapter
    }

    private fun showDeleteConfirmationDialog(shelf: Shelf, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить полку")
            .setMessage("Вы уверены, что хотите удалить полку \"${shelf.name}\"? Все книги на этой полке также будут удалены.")
            .setPositiveButton("Удалить") { _, _ ->
                deleteShelf(position)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun deleteShelf(position: Int) {
        shelfList.removeAt(position)
        JsonHelper.saveShelves(requireContext(), shelfList)
        shelfAdapter.notifyItemRemoved(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

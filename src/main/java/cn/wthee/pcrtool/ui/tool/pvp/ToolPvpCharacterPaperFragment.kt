package cn.wthee.pcrtool.ui.tool.pvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.adapters.PvpCharacterAdapter
import cn.wthee.pcrtool.databinding.FragmentToolPvpCharacterBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ToolPvpCharacterIconFragment(
    private val position: Int,
    private val isFloatWindow: Boolean
) : Fragment() {

    private lateinit var binding: FragmentToolPvpCharacterBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolPvpCharacterBinding.inflate(inflater, container, false)
        MainScope().launch {
            val adapter = PvpCharacterAdapter(isFloatWindow, requireActivity())
            binding.icons.adapter = adapter
            adapter.submitList(
                when (position) {
                    1 -> ToolPvpFragment.character1
                    2 -> ToolPvpFragment.character2
                    3 -> ToolPvpFragment.character3
                    else -> null
                }
            ) {
                try {
                    ToolPvpFragment.progressBar.visibility = View.GONE
                } catch (e: Exception) {
                }
                try {
                    ToolPvpService.progressBar.visibility = View.GONE
                } catch (e: Exception) {
                }
            }
        }
        return binding.root
    }
}
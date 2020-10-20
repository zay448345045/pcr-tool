package cn.wthee.pcrtool.ui.tool.pvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.wthee.pcrtool.adapters.PvpCharacterAdapter
import cn.wthee.pcrtool.databinding.FragmentToolPvpCharacterBinding
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import cn.wthee.pcrtool.utils.InjectorUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ToolPvpCharacterIconFragment : Fragment() {

    private var position = 0
    private var isFloatWindow = false
    private lateinit var binding: FragmentToolPvpCharacterBinding

    private val sharedViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
            isFloatWindow = it.getBoolean("isFloatWindow", false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolPvpCharacterBinding.inflate(inflater, container, false)
        MainScope().launch {
            val data = sharedViewModel.getCharacterByPosition(position)
            val adapter = PvpCharacterAdapter(isFloatWindow)
            binding.icons.adapter = adapter
            adapter.submitList(data) {
                ToolPvpFragment.progressBar.visibility = View.GONE
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int, isFloatWindow: Boolean) =
            ToolPvpCharacterIconFragment().apply {
                arguments = Bundle().apply {
                    putInt("position", position)
                    putBoolean("isFloatWindow", isFloatWindow)
                }
            }
    }
}
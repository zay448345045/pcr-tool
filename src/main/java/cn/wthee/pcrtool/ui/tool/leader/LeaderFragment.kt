package cn.wthee.pcrtool.ui.tool.leader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.CharacterLeaderAdapter
import cn.wthee.pcrtool.data.MyAPIRepository
import cn.wthee.pcrtool.databinding.FragmentToolLeaderBinding
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class LeaderFragment : Fragment() {

    private lateinit var binding: FragmentToolLeaderBinding
    private lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab()
        binding = FragmentToolLeaderBinding.inflate(inflater, container, false)
        job = MainScope().launch {
            val list = MyAPIRepository.getLeader()
            if (list.status == 0) {
                val adapter = CharacterLeaderAdapter()
                binding.listLevel.adapter = adapter
                adapter.submitList(list.data) {
                    binding.loading.root.visibility = View.GONE
                }
            } else if (list.status == -1) {
                ToastUtil.short(list.message)
            }
        }
        //设置头部
        ToolbarUtil(binding.toolLeader).setToolHead(
            R.drawable.ic_leader,
            getString(R.string.tool_leader)
        )
        //来源
        binding.source.setOnClickListener {
            BrowserUtil.open(requireContext(), getString(R.string.leader_source_url))
        }
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        if (!job.isCancelled) {
            job.cancel()
        }
    }
}
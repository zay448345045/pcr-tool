package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.view.EquipmentMaterial
import cn.wthee.pcrtool.databinding.FragmentEquipmentDetailsBinding
import cn.wthee.pcrtool.databinding.ItemCommonBinding
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsFragment
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsViewModel
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.dp
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class EquipmentMaterialAdapter(
    private val partentBinding: FragmentEquipmentDetailsBinding,
    private val behavior: BottomSheetBehavior<View>,
    private val viewModel: EquipmentDetailsViewModel
) :
    ListAdapter<EquipmentMaterial, EquipmentMaterialAdapter.ViewHolder>(MaterialDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCommonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            bind(getItem(position))
            itemView.findViewById<MaterialTextView>(R.id.name)
                .setTextColor(
                    ResourcesUtil.getColor(
                        if (position == EquipmentDetailsFragment.materialClickPosition)
                            R.color.red
                        else
                            R.color.text
                    )
                )
        }
    }

    inner class ViewHolder(private val binding: ItemCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(info: EquipmentMaterial) {
            binding.apply {
                //修改宽度 TODO 图片大小不一致
                val params = root.layoutParams as RecyclerView.LayoutParams
                params.width = 52.dp
                root.layoutParams = params
                //加载数据
                name.text = "${info.count}"
                pic.load(Constants.EQUIPMENT_URL + info.id + Constants.WEBP) {
                    error(R.drawable.unknow_gray)
                    placeholder(R.drawable.load_mini)
                }
                //点击查看掉落地区
                root.setOnClickListener {
                    EquipmentDetailsFragment.materialClickPosition = absoluteAdapterPosition
                    notifyDataSetChanged()
                    partentBinding.progressBar.visibility = View.VISIBLE
                    //掉落地区
                    MainScope().launch {
                        //显示当前查看掉落的装备名称
                        partentBinding.materialName.visibility = View.VISIBLE
                        partentBinding.materialTip.visibility = View.VISIBLE
                        partentBinding.materialName.text = "${info.name}"
                        //掉落列表
                        val data = viewModel.getDropInfos(info.id)
                        val adapter = EquipmentDropAdapter()
                        partentBinding.drops.adapter = adapter
                        //动态限制只有一个列表可滚动
                        partentBinding.drops.isNestedScrollingEnabled = true
                        partentBinding.material.isNestedScrollingEnabled = false
                        adapter.submitList(data) {
                            behavior.state = BottomSheetBehavior.STATE_EXPANDED
                            partentBinding.progressBar.visibility = View.INVISIBLE
                        }
                        partentBinding.drops.setItemViewCacheSize(50)
                    }
                }
            }
        }
    }

}

private class MaterialDiffCallback : DiffUtil.ItemCallback<EquipmentMaterial>() {

    override fun areItemsTheSame(
        oldItem: EquipmentMaterial,
        newItem: EquipmentMaterial
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: EquipmentMaterial,
        newItem: EquipmentMaterial
    ): Boolean {
        return oldItem == newItem
    }
}
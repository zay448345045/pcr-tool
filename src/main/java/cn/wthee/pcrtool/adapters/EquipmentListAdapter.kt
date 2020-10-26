package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.edit
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.database.view.EquipmentMaxData
import cn.wthee.pcrtool.databinding.ItemEquipmentBinding
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsFragment
import cn.wthee.pcrtool.ui.main.MainPagerFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.EQUIPMENT_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import coil.load
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class EquipmentAdapter(
    private val isList: Boolean,
    private val fragmentManager: FragmentManager
) :
    ListAdapter<EquipmentMaxData, EquipmentAdapter.ViewHolder>(EquipDiffCallback()), Filterable {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEquipmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    @Suppress("UNCHECKED_CAST")
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val param: FilterEquipment = Gson().fromJson(
                    constraint.toString(),
                    object : TypeToken<FilterEquipment>() {}.type
                )
                val filterDatas = if (constraint == null) {
                    //没有过滤的内容，则使用源数据
                    currentList
                } else {
                    val filteredList = currentList.toMutableList()
                    filteredList.toHashSet().forEachIndexed { _, data ->
                        if (!param.all) {
                            //过滤非收藏角色
                            if (!MainActivity.sp.getBoolean(data.equipmentId.toString(), false)) {
                                filteredList.remove(data)
                            }
                        }
                        //种类筛选
                        if (param.type != "全部") {
                            if (param.type != data.type) {
                                filteredList.remove(data)
                            }
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filterDatas
                filterResults.count = filterDatas.size
                return filterResults
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                submitList(results?.values as List<EquipmentMaxData>)
                MainActivity.sp.edit {
                    putInt(Constants.SP_COUNT_EQUIP, results.count)
                }
                MainPagerFragment.tabLayout.getTabAt(1)?.text = results.count.toString()
            }
        }
    }

    inner class ViewHolder(private val binding: ItemEquipmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(equip: EquipmentMaxData) {
            //设置数据
            binding.apply {
                val ctx = MyApplication.context
                content.visibility = if (isList) View.VISIBLE else View.GONE
                //加载动画
                content.animation =
                    AnimationUtils.loadAnimation(ctx, R.anim.anim_scale_alpha)
                itemPic.animation =
                    AnimationUtils.loadAnimation(
                        ctx,
                        if (isList) R.anim.anim_translate else R.anim.anim_scale
                    )
                //装备名称
                name.text = equip.equipmentName
                desc.text = equip.getDesc()
                //加载装备图片
                val picUrl = EQUIPMENT_URL + equip.equipmentId + WEBP
                itemPic.load(picUrl) {
                    error(R.drawable.unknow_gray)
                    placeholder(R.drawable.load_mini)
                }
                //设置共享元素
                itemPic.transitionName = "pic_${equip.equipmentId}"
                name.transitionName = "ename_${equip.equipmentId}"
                //设置点击跳转
                itemPic.setOnClickListener {
                    click(equip)
                }
                content.setOnClickListener {
                    click(equip)
                }
            }
        }

        private fun click(equip: EquipmentMaxData) {
            MainActivity.currentEquipPosition = adapterPosition
            EquipmentDetailsFragment.getInstance(equip).show(fragmentManager, "details")
        }
    }
}

class EquipDiffCallback : DiffUtil.ItemCallback<EquipmentMaxData>() {

    override fun areItemsTheSame(
        oldItem: EquipmentMaxData,
        newItem: EquipmentMaxData
    ): Boolean {
        return oldItem.equipmentId == newItem.equipmentId
    }

    override fun areContentsTheSame(
        oldItem: EquipmentMaxData,
        newItem: EquipmentMaxData
    ): Boolean {
        return oldItem.equipmentId == newItem.equipmentId
    }
}
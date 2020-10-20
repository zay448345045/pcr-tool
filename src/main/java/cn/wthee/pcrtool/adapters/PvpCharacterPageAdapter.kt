package cn.wthee.pcrtool.adapters

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.wthee.pcrtool.ui.tool.pvp.ToolPvpCharacterIconFragment

class PvpCharacterPageAdapter(
    activity: FragmentActivity,
    isFloatWindow: Boolean) : FragmentStateAdapter(activity) {

    private val mFragments: SparseArray<Fragment> = SparseArray()

    init {
        mFragments.put(0, ToolPvpCharacterIconFragment.newInstance(1, isFloatWindow))
        mFragments.put(1, ToolPvpCharacterIconFragment.newInstance(2, isFloatWindow))
        mFragments.put(2, ToolPvpCharacterIconFragment.newInstance(3, isFloatWindow))
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getItemCount(): Int {
        return mFragments.size()
    }

}
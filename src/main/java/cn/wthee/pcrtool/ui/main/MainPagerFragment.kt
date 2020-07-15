package cn.wthee.pcrtool.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.SharedElementCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.sortAsc
import cn.wthee.pcrtool.MainActivity.Companion.sortType
import cn.wthee.pcrtool.MainActivity.Companion.sp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.MainPagerAdapter
import cn.wthee.pcrtool.databinding.*
import cn.wthee.pcrtool.ui.detail.character.CharacterBasicInfoFragment
import cn.wthee.pcrtool.ui.main.CharacterListFragment.Companion.filterParams
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.LOG_TAG
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.collections.set


class MainPagerFragment : Fragment() {

    companion object {
        var cListClick = false
        lateinit var tabLayout: TabLayout
        lateinit var progress: ProgressBar

        //弹出fab
        lateinit var popView: LayoutFunctionListBinding
        lateinit var fabSetting: ExtendedFloatingActionButton
        lateinit var fabLove: ExtendedFloatingActionButton
        lateinit var fabSearch: ExtendedFloatingActionButton
        lateinit var fabFilter: ExtendedFloatingActionButton
        lateinit var fabSort: ExtendedFloatingActionButton
    }

    private lateinit var binding: FragmentMainPagerBinding
    private lateinit var viewPager2: ViewPager2
    private val sharedCharacterViewModel by activityViewModels<CharacterViewModel> {
        InjectorUtil.provideCharacterViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainPagerBinding.inflate(inflater, container, false)
        init()
        setListener()
        prepareTransitions()
        //设置toolbar
        setHasOptionsMenu(true)
        val toolbar = ToolbarUtil(binding.toolbar)
        toolbar.setLeftIcon(R.drawable.ic_logo)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            //刷新收藏
            val vh = CharacterListFragment.characterList.findViewHolderForAdapterPosition(
                MainActivity.currentCharaPosition
            )?.itemView?.findViewById<AppCompatImageView>(R.id.love)
            vh?.visibility =
                if (CharacterBasicInfoFragment.isLoved) View.VISIBLE else View.INVISIBLE
            CharacterListFragment.characterList.scrollToPosition(MainActivity.currentCharaPosition)
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        binding.progress.visibility = View.GONE
        MainActivity.fab.visibility = View.VISIBLE
    }

    private fun init() {
        //禁止连续点击
        cListClick = false
        progress = binding.progress
        //viewpager2 配置
        viewPager2 = binding.viewPager
        viewPager2.offscreenPageLimit = 2
        viewPager2.adapter = MainPagerAdapter(requireActivity())
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                MainActivity.currentMainPage = position
            }
        })
        //tab 初始化
        tabLayout = binding.layoutTab
        TabLayoutMediator(
            tabLayout,
            viewPager2,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    //角色
                    0 -> {
                        tab.icon =
                            ResourcesCompat.getDrawable(resources, R.drawable.ic_character, null)
                        tab.text = sp.getInt(Constants.SP_COUNT_CHARACTER, 0).toString()
                    }
                    //装备
                    1 -> {
                        tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_equip, null)
                        tab.text = sp.getInt(Constants.SP_COUNT_EQUIP, 0).toString()
                    }
                    //怪物
                    2 -> {
                        tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_enemy, null)
                        tab.text = sp.getInt(Constants.SP_COUNT_ENEMY, 0).toString()
                    }
                }
            }).attach()

        //popupWindow fabs
        popView = LayoutFunctionListBinding.inflate(layoutInflater)
        fabSetting = popView.setting
        fabLove = popView.love
        fabSearch = popView.search
        fabFilter = popView.filter
        fabSort = popView.sort
        if (MainActivity.spSetting.getBoolean("fab_status", false)) {
            fabSetting.extend()
            fabLove.extend()
            fabSearch.extend()
            fabFilter.extend()
            fabSort.extend()
        } else {
            fabSetting.shrink()
            fabLove.shrink()
            fabSearch.shrink()
            fabFilter.shrink()
            fabSort.shrink()
        }
    }

    private fun setListener() {
        //功能模块
        //长按回到顶部
        MainActivity.fab.setOnLongClickListener {
            when (MainActivity.currentMainPage) {
                0 -> CharacterListFragment.characterList.smoothScrollToPosition(0)
            }
            return@setOnLongClickListener true
        }
        MainActivity.fab.setOnClickListener {
            //弹出功能列表
            val popupWindow = PopupWindow(
                popView.root,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            popupWindow.isOutsideTouchable = true
            popupWindow.isFocusable = true
            popupWindow.animationStyle = R.style.PopUpAnimation
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            popupWindow.showAtLocation(it, Gravity.END or Gravity.BOTTOM, 0, 0)
            //半透明遮罩
            ScreenUtil.setAlpha(0.5f)
            popupWindow.setOnDismissListener {
                ScreenUtil.setAlpha(1f)
            }
            //点击关闭
            popView.root.setOnClickListener {
                popupWindow.dismiss()
            }

            //设置
            fabSetting.setOnClickListener {
                popupWindow.dismiss()
                findNavController().navigate(R.id.action_containerFragment_to_settingsFragment)
            }
            //收藏
            fabLove.setOnClickListener {
                popupWindow.dismiss()
                filterParams.all = if (filterParams.all) {
                    ToastUtil.short("仅显示收藏")
                    false
                } else {
                    ToastUtil.short("显示全部")
                    true
                }
                when (MainActivity.currentMainPage) {
                    0 -> {
                        sharedCharacterViewModel.getCharacters(
                            sortType,
                            sortAsc, "", mapOf()
                        )
                        CharacterListFragment.listAdapter.notifyDataSetChanged()
                    }
                    1 -> {
                        CharacterListFragment.listAdapter.notifyDataSetChanged()
                    }
                    2 -> {
                        CharacterListFragment.listAdapter.notifyDataSetChanged()
                    }
                }
            }
            //搜索
            fabSearch.setOnClickListener {
                popupWindow.dismiss()
                //显示搜索布局
                val layout = LayoutSearchBinding.inflate(layoutInflater)
                val dialog = DialogUtil.create(requireContext(), layout.root)
                dialog.show()
                //搜索框
                val searchView = layout.searchInput
                searchView.onActionViewExpanded()
                searchView.isSubmitButtonEnabled = true
                when (MainActivity.currentMainPage) {
                    0 -> searchView.queryHint = "角色名"
                    1 -> searchView.queryHint = "装备名"
                    2 -> searchView.queryHint = "怪物名"
                }
                searchView.setOnQueryTextListener(object :
                    SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        when (MainActivity.currentMainPage) {
                            0 -> {
                                //角色名字
                                sharedCharacterViewModel.getCharacters(
                                    sortType,
                                    sortAsc, query ?: "", mapOf()
                                )
                            }
                            1 -> EquipmentListFragment.listAdapter.filter.filter(
                                query
                            )
                            2 -> EnemyListFragment.listAdapter.filter.filter(
                                query
                            )
                        }

                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        //重置
                        if (newText == "") {
                            when (MainActivity.currentMainPage) {
                                0 -> sharedCharacterViewModel.getCharacters(
                                    sortType,
                                    sortAsc, "", mapOf()
                                )
                                1 -> EquipmentListFragment.viewModel.getEquips()
                                2 -> EnemyListFragment.viewModel.getAllEnemy()
                            }
                        }

                        return false
                    }
                })
            }
            //筛选
            fabFilter.setOnClickListener {
                popupWindow.dismiss()
                //筛选
                val layout = LayoutFilterBinding.inflate(layoutInflater)
                val dialog = DialogUtil.create(requireContext(), layout.root)
                dialog.show()
                //位置筛选
                val checkChara1 = layout.checkboxChara1
                val checkChara2 = layout.checkboxChara2
                val checkChara3 = layout.checkboxChara3
                //传入筛选条件
                checkChara1.isChecked = filterParams.positon1
                checkChara2.isChecked = filterParams.positon2
                checkChara3.isChecked = filterParams.positon3
                layout.next.setOnClickListener {
                    dialog.dismiss()
                    //筛选选项
                    filterParams.positon1 = checkChara1.isChecked
                    filterParams.positon2 = checkChara2.isChecked
                    filterParams.positon3 = checkChara3.isChecked
                    sharedCharacterViewModel.getCharacters(
                        sortType,
                        sortAsc, "", mapOf()
                    )
                }
            }
            //排序
            fabSort.setOnClickListener {
                popupWindow.dismiss()
                //显示排序布局
                val layout = LayoutSortBinding.inflate(layoutInflater)
                val dialog = DialogUtil.create(requireContext(), layout.root)
                dialog.show()
                layout.next.setOnClickListener {
                    dialog.dismiss()
                    //筛选选项
                    sortType = layout.spinnerSort.selectedItemPosition
                    sortAsc = layout.radioSort.checkedRadioButtonId == R.id.sort_asc
                    sharedCharacterViewModel.getCharacters(
                        sortType,
                        sortAsc, "", mapOf()
                    )
                }
            }
        }
    }

    //配置共享元素动画
    private fun prepareTransitions() {

        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                //返回时隐藏toolbar
                binding.layoutToolbar.setExpanded(false)
                try {
                    if (names!!.isNotEmpty()) {
                        sharedElements ?: return
                        //角色列表
                        if (names.size > 0 && names[0].contains("img")) {
                            val vh =
                                CharacterListFragment.characterList.findViewHolderForAdapterPosition(
                                    MainActivity.currentCharaPosition
                                ) ?: return
                            val v0 =
                                vh.itemView.findViewById<AppCompatImageView>(R.id.character_pic)
                            val v1 =
                                vh.itemView.findViewById<ConstraintLayout>(R.id.content)
                            sharedElements[names[0]] = v0
                            sharedElements[names[1]] = v1
                        } else {
                            //装备列表
                            val euqipView =
                                EquipmentListFragment.list.findViewHolderForAdapterPosition(
                                    MainActivity.currentEquipPosition
                                ) ?: return
                            val ev0 =
                                euqipView.itemView.findViewById<AppCompatImageView>(R.id.item_pic)
                            sharedElements[names[0]] = ev0
                        }
                    }
                } catch (e: Exception) {
                    Log.e(LOG_TAG, e.message ?: "")
                }
            }

        })
    }
}

package cn.wthee.pcrtool.ui.detail.character.basic

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.CharacterPicAdapter
import cn.wthee.pcrtool.databinding.FragmentCharacterPicListBinding
import cn.wthee.pcrtool.ui.common.CommonBasicDialogFragment
import cn.wthee.pcrtool.utils.*
import coil.imageLoader
import coil.request.ImageRequest
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.transition.MaterialContainerTransform
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.launch

/**
 * 角色图片展示页面
 */
class CharacterPicListFragment : CommonBasicDialogFragment(true) {

    companion object {
        var hasLoaded = arrayListOf(false, false, false, false)
        var hasSelected = arrayListOf(false, false, false, false)
        lateinit var downLoadFab: ExtendedFloatingActionButton

        fun getInstance(uid: Int, r6Id: Int) = CharacterPicListFragment().apply {
            arguments = Bundle().apply {
                putInt(Constants.UID, uid)
                putInt(Constants.R6ID, r6Id)
            }
        }
    }

    private lateinit var binding: FragmentCharacterPicListBinding
    private lateinit var urls: ArrayList<String>
    private lateinit var adapter: CharacterPicAdapter
    private var uid = -1
    private var r6Id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //过渡
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            duration = resources.getInteger(R.integer.fragment_anim).toLong()
            setAllContainerColors(Color.TRANSPARENT)
        }
        requireArguments().apply {
            uid = getInt(Constants.UID)
            r6Id = getInt(Constants.R6ID)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterPicListBinding.inflate(inflater, container, false)
        binding.apply {
            downLoadFab = fabDownload
            ToolbarUtil(titleViewPic).setCenterTitle(getString(R.string.view_pic))
            //初始化列表
            adapter = CharacterPicAdapter(this@CharacterPicListFragment)
            pics.adapter = adapter
            urls = CharacterIdUtil.getAllPicUrl(uid, r6Id)
            adapter.submitList(urls)
            setListener()
        }

        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        hasLoaded = arrayListOf(false, false, false, false)
        hasSelected = arrayListOf(false, false, false, false)
    }

    private fun setListener() {
        binding.apply {
            fabTop.setImageResource(R.drawable.ic_left)
            fabTop.setOnClickListener {
                dialog?.dismiss()
            }
            pics.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (pics.canScrollVertically(-1)) {
                        fabTop.setImageResource(R.drawable.ic_top)
                        fabTop.setOnClickListener {
                            pics.smoothScrollToPosition(0)
                        }
                    } else {
                        fabTop.setImageResource(R.drawable.ic_left)
                        fabTop.setOnClickListener {
                            dialog?.dismiss()
                        }
                    }
                }
            })
        }

        //下载
        downLoadFab.setOnClickListener {
            if (hasSelected.contains(true)) {
                PermissionX.init(activity).permissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ).request { allGranted, _, _ ->
                    val selectedUrls = arrayListOf<String>()
                    if (allGranted) {
                        var startDownload = true
                        hasSelected.forEachIndexed { index, b ->
                            if (b && !hasLoaded[index]) {
                                startDownload = false
                            }
                            if (b && index < urls.size) {
                                selectedUrls.add(urls[index])
                            }
                        }
                        if (startDownload) {
                            ToastUtil.short("正在保存，请稍后~")
                            selectedUrls.forEachIndexed { index, url ->
                                try {
                                    val pre =
                                        url.substring(
                                            url.indexOf("card/") + 5,
                                            url.lastIndexOf('/')
                                        )
                                    val num =
                                        url.substring(
                                            url.lastIndexOf('/') + 1,
                                            url.lastIndexOf('.')
                                        )
                                    val name = "${pre}_${num}.png"
                                    //保存图片
                                    lifecycleScope.launch {
                                        val request = ImageRequest.Builder(requireContext())
                                            .data(url)
                                            .build()
                                        val bitmap =
                                            (requireContext().imageLoader.execute(request).drawable as BitmapDrawable).bitmap
                                        ImageDownloadUtil(requireActivity()).save(bitmap, name)
                                    }
                                } catch (e: Exception) {
                                    ToastUtil.short("第${index + 1}张图片未保存成功，请重试~")
                                }
                            }

                        } else {
                            ToastUtil.short("请等待图片加载完成~")
                        }
                    } else {
                        ToastUtil.short("无法保存~请允许相关权限")
                    }
                }
            }
        }
    }

}
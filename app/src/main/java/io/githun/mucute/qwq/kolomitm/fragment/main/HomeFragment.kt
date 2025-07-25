package io.githun.mucute.qwq.kolomitm.fragment.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.githun.mucute.qwq.kolomitm.R
import io.githun.mucute.qwq.kolomitm.databinding.FragmentHomeBinding
import io.githun.mucute.qwq.kolomitm.service.KoloMITMService
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var viewBinding: FragmentHomeBinding

    private val postNotificationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        toggleKoloMITM()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val floatingActionButton = viewBinding.floatingActionButton
        floatingActionButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                postNotificationLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            } else {
                toggleKoloMITM()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                KoloMITMService.activeFlow.collect { isActive ->
                    if (isActive) {
                        floatingActionButton.setImageResource(R.drawable.pause_24px)
                    } else {
                        floatingActionButton.setImageResource(R.drawable.play_arrow_24px)
                    }
                }
            }
        }
    }

    private fun toggleKoloMITM() {
        val context = requireContext()
        if (KoloMITMService.activeFlow.value) {
            context.startForegroundService(Intent(KoloMITMService.ACTION_STOP).apply {
                `package` = context.packageName
            })
        } else {
            context.startForegroundService(Intent(KoloMITMService.ACTION_START).apply {
                `package` = context.packageName
            })
        }
    }

}
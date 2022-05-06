package me.hanhngo.qrcode.view.studio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import me.hanhngo.qrcode.databinding.FragmentStudioBinding
import me.hanhngo.qrcode.util.extension.hideAppbar
import me.hanhngo.qrcode.util.extension.showAppbar

@AndroidEntryPoint
class StudioFragment : Fragment() {
    private var _binding: FragmentStudioBinding? = null
    private val binding: FragmentStudioBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStudioBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
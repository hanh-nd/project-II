package me.hanhngo.qrcode.view.studio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import me.hanhngo.qrcode.databinding.FragmentStudioBinding

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
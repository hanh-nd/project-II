package me.hanhngo.qrcode.view.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import me.hanhngo.qrcode.R
import me.hanhngo.qrcode.databinding.FragmentUrlBinding
import me.hanhngo.qrcode.util.extension.save
import me.hanhngo.qrcode.util.parseContent

class UrlFragment : Fragment() {
    private var _binding: FragmentUrlBinding? = null
    private val binding: FragmentUrlBinding get() = _binding!!

    private val args: UrlFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUrlBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val url = args.url
            val format = args.barcodeFormat
            val bitmap = parseContent(url.toBarcodeText(), format)
            binding.urlQrCodeIv.setImageBitmap(bitmap)
            binding.urlDataTv.text = url.url
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.saveBtn.setOnClickListener {
            binding.urlQrCodeIv.drawable.toBitmap().save(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
package me.hanhngo.qrcode.view.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
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
        bindUI()
        setupListener()
    }

    private fun bindUI() {
        try {
            val url = args.url
            val format = args.barcodeFormat
            val bitmap = parseContent(url.toBarcodeText(), format)
            binding.urlQrCodeIv.setImageBitmap(bitmap)
            binding.urlDataTv.text = url.url
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupListener() {
        val url = args.url

        binding.saveBtn.setOnClickListener {
            val saveResult = binding.urlQrCodeIv.drawable.toBitmap().save(requireContext())
            Toast.makeText(
                requireContext(),
                if (saveResult) "Save to gallery successfully." else "Save to gallery failed. Try again.",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.openUrlBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url.url)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
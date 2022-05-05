package me.hanhngo.qrcode.view.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.zxing.BarcodeFormat
import me.hanhngo.qrcode.databinding.FragmentOtherBinding
import me.hanhngo.qrcode.util.extension.save
import me.hanhngo.qrcode.util.parseContent


class OtherFragment : Fragment() {
    private var _binding: FragmentOtherBinding? = null
    private val binding: FragmentOtherBinding get() = _binding!!

    private val args: OtherFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOtherBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val text = args.text
            val format = args.barcodeFormat
            val bitmap = parseContent(text.toBarcodeText(), format)
            binding.textQrCodeIv.setImageBitmap(bitmap)
            binding.textDataTv.text = text.text
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.saveBtn.setOnClickListener {
            binding.textQrCodeIv.drawable.toBitmap().save(requireContext())
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
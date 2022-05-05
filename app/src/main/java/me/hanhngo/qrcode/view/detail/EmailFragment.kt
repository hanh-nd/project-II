package me.hanhngo.qrcode.view.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import me.hanhngo.qrcode.databinding.FragmentEmailBinding
import me.hanhngo.qrcode.util.extension.save
import me.hanhngo.qrcode.util.parseContent


class EmailFragment : Fragment() {
    private var _binding: FragmentEmailBinding? = null
    private val binding: FragmentEmailBinding get() = _binding!!

    private val args: EmailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEmailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val email = args.email
            val format = args.barcodeFormat
            val bitmap = parseContent(email.toBarcodeText(), format)

            binding.emailQrCodeIv.setImageBitmap(bitmap)
            binding.emailDataTv.text = email.toFormattedText()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.saveBtn.setOnClickListener {
            val saveResult = binding.emailQrCodeIv.drawable.toBitmap().save(requireContext())
            Toast.makeText(
                requireContext(),
                if (saveResult) "Save to gallery successfully." else "Save to gallery failed. Try again.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
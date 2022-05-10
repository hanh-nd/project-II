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
import me.hanhngo.qrcode.databinding.FragmentSmsBinding
import me.hanhngo.qrcode.util.extension.save
import me.hanhngo.qrcode.util.parseContent


class SmsFragment : Fragment() {
    private var _binding: FragmentSmsBinding? = null
    private val binding: FragmentSmsBinding get() = _binding!!

    private val args: SmsFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSmsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
        setupListener()
    }

    private fun setupListener() {
        val sms = args.sms

        binding.saveBtn.setOnClickListener {
            val saveResult = binding.smsQrCodeIv.drawable.toBitmap().save(requireContext())
            Toast.makeText(
                requireContext(),
                if (saveResult) "Save to gallery successfully." else "Save to gallery failed. Try again.",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.sendSmsBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("smsto:${sms.phone}")
            intent.putExtra("sms_body", sms.message)
            startActivity(Intent.createChooser(intent, "Choose an app to complete."))
        }
    }

    private fun bindUI() {
        try {
            val sms = args.sms
            val format = args.barcodeFormat
            val bitmap = parseContent(sms.toBarcodeText(), format)

            binding.smsQrCodeIv.setImageBitmap(bitmap)
            binding.smsNumberTv.text = sms.phone
            binding.smsMessageTv.text = sms.message
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
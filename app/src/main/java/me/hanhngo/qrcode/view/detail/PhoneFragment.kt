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
import me.hanhngo.qrcode.databinding.FragmentPhoneBinding
import me.hanhngo.qrcode.util.extension.save
import me.hanhngo.qrcode.util.parseContent


class PhoneFragment : Fragment() {
    private var _binding: FragmentPhoneBinding? = null
    private val binding: FragmentPhoneBinding get() = _binding!!

    private val args: PhoneFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPhoneBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
        setupListener()
    }

    private fun setupListener() {
        val phone = args.phone
        binding.saveBtn.setOnClickListener {
            val saveResult = binding.phoneQrCodeIv.drawable.toBitmap().save(requireContext())
            Toast.makeText(
                requireContext(),
                if (saveResult) "Save to gallery successfully." else "Save to gallery failed. Try again.",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.dialBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${phone.phone}")
            startActivity(Intent.createChooser(intent, "Choose an app to complete."))
        }
    }

    private fun bindUI() {
        try {
            val phone = args.phone
            val format = args.barcodeFormat
            val bitmap = parseContent(phone.toBarcodeText(), format)

            binding.phoneQrCodeIv.setImageBitmap(bitmap)
            binding.phoneDataTv.text = phone.phone
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
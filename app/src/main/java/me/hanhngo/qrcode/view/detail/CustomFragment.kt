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
import me.hanhngo.qrcode.databinding.FragmentCustomBinding
import me.hanhngo.qrcode.databinding.FragmentSmsBinding
import me.hanhngo.qrcode.util.extension.save
import me.hanhngo.qrcode.util.parseContent


class CustomFragment : Fragment() {
    private var _binding: FragmentCustomBinding? = null
    private val binding: FragmentCustomBinding get() = _binding!!

    private val args: CustomFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCustomBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
        setupListener()
    }

    private fun setupListener() {
        val custom = args.custom

        binding.saveBtn.setOnClickListener {
            val saveResult = binding.customQrCodeIv.drawable.toBitmap().save(requireContext())
            Toast.makeText(
                requireContext(),
                if (saveResult) "Save to gallery successfully." else "Save to gallery failed. Try again.",
                Toast.LENGTH_SHORT
            ).show()
        }

//        binding.sendSmsBtn.setOnClickListener {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = Uri.parse("hust:${custom.studentId}")
//            intent.putExtra("sms_body", custom.name)
//            startActivity(Intent.createChooser(intent, "Choose an app to complete."))
//        }
    }

    private fun bindUI() {
        try {
            val custom = args.custom
            val format = args.barcodeFormat
            val bitmap = parseContent(custom.toBarcodeText(), format)

            binding.customQrCodeIv.setImageBitmap(bitmap)
            binding.studentIdTv.text = custom.studentId
            binding.studentNameTv.text = custom.name
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
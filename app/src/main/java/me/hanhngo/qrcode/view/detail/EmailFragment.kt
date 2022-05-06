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
        bindUI()
        setupListener()
    }


    private fun bindUI() {
        try {
            val email = args.email
            val format = args.barcodeFormat
            val bitmap = parseContent(email.toBarcodeText(), format)

            binding.emailQrCodeIv.setImageBitmap(bitmap)
            binding.emailDataTv.text = email.email
            binding.subjectDataTv.text = email.subject
            binding.bodyDataTv.text = email.body
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupListener() {
        val email = args.email

        binding.saveBtn.setOnClickListener {
            val saveResult = binding.emailQrCodeIv.drawable.toBitmap().save(requireContext())
            Toast.makeText(
                requireContext(),
                if (saveResult) "Save to gallery successfully." else "Save to gallery failed. Try again.",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.sendEmailBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email.email))
            intent.putExtra(Intent.EXTRA_SUBJECT, email.subject)
            intent.putExtra(Intent.EXTRA_TEXT, email.body)
            startActivity(Intent.createChooser(intent, "Choose an app to complete."))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
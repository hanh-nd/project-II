package me.hanhngo.qrcode.view.studio

import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.zxing.BarcodeFormat
import dagger.hilt.android.AndroidEntryPoint
import me.hanhngo.qrcode.data.db.BarcodeEntity
import me.hanhngo.qrcode.databinding.FragmentStudioBinding
import me.hanhngo.qrcode.domain.schema.BarcodeSchema
import me.hanhngo.qrcode.domain.schema.Other


@AndroidEntryPoint
class StudioFragment : Fragment() {
    private var _binding: FragmentStudioBinding? = null
    private val binding: FragmentStudioBinding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()

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
        setupListener()
    }

    private fun setupListener() {
        binding.emailTv.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(
                StudioFragmentDirections.actionStudioFragmentToEmailStudioFragment()
            )
        }

        binding.urlTv.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(
                StudioFragmentDirections.actionStudioFragmentToUrlStudioFragment()
            )
        }

        binding.phoneTv.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(
                StudioFragmentDirections.actionStudioFragmentToPhoneStudioFragment()
            )
        }

        binding.smsTv.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(
                StudioFragmentDirections.actionStudioFragmentToSmsStudioFragment()
            )
        }

        binding.wifiTv.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(
                StudioFragmentDirections.actionStudioFragmentToWifiStudioFragment()
            )
        }

        binding.textTv.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(
                StudioFragmentDirections.actionStudioFragmentToTextStudioFragment()
            )
        }

        binding.customCodeTv.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(
                StudioFragmentDirections.actionStudioFragmentToCustomStudioFragment()
            )
        }

        binding.clipboardTv.setOnClickListener {
            val clipBoardManager =
                requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (!clipBoardManager.hasPrimaryClip() || !clipBoardManager.primaryClipDescription?.hasMimeType(
                    MIMETYPE_TEXT_PLAIN
                )!!
            ) {
                Toast.makeText(requireContext(), "No text found in clipboard!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val text = clipBoardManager.primaryClip?.getItemAt(0)?.text?.toString()
            if (text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Invalid text! Try again!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val textSchema = Other(text)

            val barcodeEntity = BarcodeEntity(
                text = textSchema.toBarcodeText(),
                schema = BarcodeSchema.OTHER,
                date = System.currentTimeMillis(),
                isGenerated = true,
                format = BarcodeFormat.QR_CODE
            )

            viewModel.insert(barcodeEntity)

            NavHostFragment.findNavController(this).navigate(
                StudioFragmentDirections.actionStudioFragmentToOtherFragment(
                    textSchema,
                    BarcodeFormat.QR_CODE
                )
            )
        }

        binding.other1dTv.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(
                StudioFragmentDirections.actionStudioFragmentToOtherOneDStudioFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
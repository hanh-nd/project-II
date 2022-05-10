package me.hanhngo.qrcode.view.studio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.zxing.BarcodeFormat
import me.hanhngo.qrcode.data.db.BarcodeEntity
import me.hanhngo.qrcode.databinding.FragmentTextStudioBinding
import me.hanhngo.qrcode.domain.schema.BarcodeSchema
import me.hanhngo.qrcode.domain.schema.Other


class TextStudioFragment : Fragment() {
    private var _binding: FragmentTextStudioBinding? = null
    private val binding: FragmentTextStudioBinding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTextStudioBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            createBtn.setOnClickListener {
                val text = textStudioEt.text.toString()
                if (text.isEmpty()) {
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

                NavHostFragment.findNavController(this@TextStudioFragment).navigate(
                    TextStudioFragmentDirections.actionTextStudioFragmentToOtherFragment(
                        textSchema,
                        BarcodeFormat.QR_CODE
                    )
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
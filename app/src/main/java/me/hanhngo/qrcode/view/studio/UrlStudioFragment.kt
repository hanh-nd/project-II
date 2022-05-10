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
import me.hanhngo.qrcode.databinding.FragmentUrlStudioBinding
import me.hanhngo.qrcode.domain.schema.BarcodeSchema
import me.hanhngo.qrcode.domain.schema.Url
import me.hanhngo.qrcode.util.extension.isUrl

class UrlStudioFragment : Fragment() {
    private var _binding: FragmentUrlStudioBinding? = null
    private val binding: FragmentUrlStudioBinding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUrlStudioBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            createBtn.setOnClickListener {
                val url = urlStudioEt.text.toString()
                if (url.isUrl().not()) {
                    Toast.makeText(requireContext(), "Invalid Url! Try again!", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                val urlSchema = Url(url)
                val barcodeEntity = BarcodeEntity(
                    text = urlSchema.toBarcodeText(),
                    schema = BarcodeSchema.URL,
                    date = System.currentTimeMillis(),
                    isGenerated = true,
                    format = BarcodeFormat.QR_CODE
                )

                viewModel.insert(barcodeEntity)

                NavHostFragment.findNavController(this@UrlStudioFragment).navigate(
                    UrlStudioFragmentDirections.actionUrlStudioFragmentToUrlFragment(
                        urlSchema,
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
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
import me.hanhngo.qrcode.databinding.FragmentPhoneStudioBinding
import me.hanhngo.qrcode.domain.schema.BarcodeSchema
import me.hanhngo.qrcode.domain.schema.Phone
import me.hanhngo.qrcode.util.extension.isPhone


class PhoneStudioFragment : Fragment() {
    private var _binding: FragmentPhoneStudioBinding? = null
    private val binding: FragmentPhoneStudioBinding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPhoneStudioBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            createBtn.setOnClickListener {
                val phoneNumber = phoneStudioEt.text.toString()
                if (phoneNumber.isPhone().not()) {
                    Toast.makeText(
                        requireContext(),
                        "Invalid phone number! Try again!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@setOnClickListener
                }
                val phoneSchema = Phone(phoneNumber)

                val barcodeEntity = BarcodeEntity(
                    text = phoneSchema.toBarcodeText(),
                    schema = BarcodeSchema.PHONE,
                    date = System.currentTimeMillis(),
                    isGenerated = true,
                    format = BarcodeFormat.QR_CODE
                )

                viewModel.insert(barcodeEntity)

                NavHostFragment.findNavController(this@PhoneStudioFragment).navigate(
                    PhoneStudioFragmentDirections.actionPhoneStudioFragmentToPhoneFragment(
                        phoneSchema,
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
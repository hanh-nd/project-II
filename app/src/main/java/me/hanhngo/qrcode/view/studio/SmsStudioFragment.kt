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
import me.hanhngo.qrcode.databinding.FragmentSmsStudioBinding
import me.hanhngo.qrcode.domain.schema.BarcodeSchema
import me.hanhngo.qrcode.domain.schema.Sms
import me.hanhngo.qrcode.util.extension.isEmail
import me.hanhngo.qrcode.util.extension.isPhone


class SmsStudioFragment : Fragment() {
    private var _binding: FragmentSmsStudioBinding? = null
    private val binding: FragmentSmsStudioBinding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSmsStudioBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            createBtn.setOnClickListener {
                val phoneNumber = smsStudioNumberEt.text.toString()
                val message = smsStudioMessageEt.text.toString()
                if (phoneNumber.isPhone().not() && message.isEmpty()) {
                    Toast.makeText(requireContext(), "Invalid phone number or message! Try again!", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                val smsSchema = Sms(phoneNumber, message)

                val barcodeEntity = BarcodeEntity(
                    text = smsSchema.toBarcodeText(),
                    schema = BarcodeSchema.SMS,
                    date = System.currentTimeMillis(),
                    isGenerated = true,
                    format = BarcodeFormat.QR_CODE
                )

                viewModel.insert(barcodeEntity)

                NavHostFragment.findNavController(this@SmsStudioFragment).navigate(
                    SmsStudioFragmentDirections.actionSmsStudioFragmentToSmsFragment(
                        smsSchema,
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
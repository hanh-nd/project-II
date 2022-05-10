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
import me.hanhngo.qrcode.databinding.FragmentEmailStudioBinding
import me.hanhngo.qrcode.domain.schema.BarcodeSchema
import me.hanhngo.qrcode.domain.schema.Email
import me.hanhngo.qrcode.util.extension.isEmail


class EmailStudioFragment : Fragment() {
    private var _binding: FragmentEmailStudioBinding? = null
    private val binding: FragmentEmailStudioBinding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEmailStudioBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            createBtn.setOnClickListener {
                val emailAddress = emailStudioAddressEt.text.toString()
                if (emailAddress.isEmail().not()) {
                    Toast.makeText(requireContext(), "Invalid Email! Try again!", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                val emailSubject = emailStudioSubjectEt.text.toString()
                val emailBody = emailStudioBodyEt.text.toString()
                val emailSchema = Email(emailAddress, emailSubject, emailBody)

                val barcodeEntity = BarcodeEntity(
                    text = emailSchema.toBarcodeText(),
                    schema = BarcodeSchema.EMAIL,
                    date = System.currentTimeMillis(),
                    isGenerated = true,
                    format = BarcodeFormat.QR_CODE
                )

                viewModel.insert(barcodeEntity)

                NavHostFragment.findNavController(this@EmailStudioFragment).navigate(
                    EmailStudioFragmentDirections.actionEmailStudioFragmentToEmailFragment(
                        emailSchema,
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
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
import me.hanhngo.qrcode.databinding.FragmentCustomStudioBinding
import me.hanhngo.qrcode.domain.schema.BarcodeSchema
import me.hanhngo.qrcode.domain.schema.Custom
import me.hanhngo.qrcode.util.encode


class CustomStudioFragment : Fragment() {
    private var _binding: FragmentCustomStudioBinding? = null
    private val binding: FragmentCustomStudioBinding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCustomStudioBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            createBtn.setOnClickListener {
                val studentId = studentIdStudioNumberEt.text.toString()
                val studentName = nameStudioMessageEt.text.toString()

                if (studentId.isEmpty() || studentName.isEmpty()) {
                    Toast.makeText(requireContext(), "Student ID or name cannot be empty! Try again!", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                val customSchema = Custom(studentId, studentName)

                val barcodeEntity = BarcodeEntity(
                    text = encode(customSchema.toBarcodeText()),
                    schema = BarcodeSchema.CUSTOM,
                    date = System.currentTimeMillis(),
                    isGenerated = true,
                    format = BarcodeFormat.QR_CODE
                )
                viewModel.insert(barcodeEntity)

                NavHostFragment.findNavController(this@CustomStudioFragment).navigate(
                    CustomStudioFragmentDirections.actionCustomStudioFragmentToCustomFragment(
                        customSchema,
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
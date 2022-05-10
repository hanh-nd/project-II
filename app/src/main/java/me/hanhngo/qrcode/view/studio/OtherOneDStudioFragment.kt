package me.hanhngo.qrcode.view.studio

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.BarcodeFormat
import me.hanhngo.qrcode.R
import me.hanhngo.qrcode.data.db.BarcodeEntity
import me.hanhngo.qrcode.databinding.FragmentOtherOneDStudioBinding
import me.hanhngo.qrcode.domain.schema.BarcodeSchema
import me.hanhngo.qrcode.domain.schema.Other
import me.hanhngo.qrcode.util.extension.addPrefixString
import me.hanhngo.qrcode.util.parseListFormat


class OtherOneDStudioFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private var _binding: FragmentOtherOneDStudioBinding? = null
    private val binding: FragmentOtherOneDStudioBinding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()

    private var format: BarcodeFormat = BarcodeFormat.CODE_128
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOtherOneDStudioBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.barcodeTypeSn.onItemSelectedListener = this

        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            parseListFormat(
                listOf(
                    Barcode.FORMAT_AZTEC,
                    Barcode.FORMAT_CODABAR,
                    Barcode.FORMAT_CODE_39,
                    Barcode.FORMAT_CODE_93,
                    Barcode.FORMAT_CODE_128,
                    Barcode.FORMAT_DATA_MATRIX,
                    Barcode.FORMAT_EAN_8,
                    Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_ITF,
                    Barcode.FORMAT_PDF417,
                    Barcode.FORMAT_QR_CODE,
                    Barcode.FORMAT_UPC_A,
                    Barcode.FORMAT_UPC_E,
                )
            )

        )

        arrayAdapter.setDropDownViewResource(
            R.layout.spinner_item_dropdown
        )

        binding.barcodeTypeSn.adapter = arrayAdapter

        binding.apply {
            createBtn.setOnClickListener {
                val text = otherOneDStudioEt.text.toString()
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
                    format = format
                )

                viewModel.insert(barcodeEntity)

                NavHostFragment.findNavController(this@OtherOneDStudioFragment).navigate(
                    OtherOneDStudioFragmentDirections.actionOtherOneDStudioFragmentToOtherFragment(
                        textSchema,
                        format
                    )
                )
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val adapter = parent?.adapter
        format = adapter?.getItem(position) as BarcodeFormat

        when (format) {
            BarcodeFormat.AZTEC -> with(binding) {
                informationTv.text = "Văn bản không có ký tự đặc biệt".addPrefixString("Ghi chú: ")

            }
            BarcodeFormat.CODABAR -> with(binding) {
                informationTv.text = "Chữ số".addPrefixString("Ghi chú: ")
            }
            BarcodeFormat.CODE_39 -> with(binding) {
                informationTv.text =
                    "Văn bản biết hoa không có ký tự đặc biệt".addPrefixString("Ghi chú: ")
            }
            BarcodeFormat.CODE_93 -> with(binding) {
                informationTv.text =
                    "Văn bản biết hoa không có ký tự đặc biệt".addPrefixString("Ghi chú: ")
            }
            BarcodeFormat.CODE_128 -> with(binding) {
                informationTv.text = "Văn bản không có ký tự đặc biệt".addPrefixString("Ghi chú: ")
            }
            BarcodeFormat.DATA_MATRIX -> with(binding) {
                informationTv.text = "Văn bản không có ký tự đặc biệt".addPrefixString("Ghi chú: ")
            }
            BarcodeFormat.EAN_8 -> with(binding) {
                informationTv.text = "8 chữ số".addPrefixString("Ghi chú: ")
            }
            BarcodeFormat.EAN_13 -> with(binding) {
                informationTv.text = "12 chữ số + 1 chữ số tổng".addPrefixString("Ghi chú: ")
            }
            BarcodeFormat.ITF -> with(binding) {
                informationTv.text = "Thậm chí số lượng chữ số".addPrefixString("Ghi chú: ")
            }
            BarcodeFormat.PDF_417 -> with(binding) {
                informationTv.text = "Văn bản".addPrefixString("Ghi chú: ")
            }
            BarcodeFormat.QR_CODE -> with(binding) {
                informationTv.text = "Văn bản".addPrefixString("Ghi chú: ")
            }
            BarcodeFormat.UPC_A -> with(binding) {
                informationTv.text = "11 chữ số + 1 chữ số tổng".addPrefixString("Ghi chú: ")
            }
            BarcodeFormat.UPC_E -> with(binding) {
                informationTv.text = "7 chữ số + 1 chữ số tổng".addPrefixString("Ghi chú: ")
            }

            else -> with(binding) {
                informationTv.text = "Không".addPrefixString("Ghi chú: ")
            }

        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

}
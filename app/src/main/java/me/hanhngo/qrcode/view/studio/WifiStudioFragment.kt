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
import me.hanhngo.qrcode.databinding.FragmentWifiStudioBinding
import me.hanhngo.qrcode.domain.schema.BarcodeSchema
import me.hanhngo.qrcode.domain.schema.Wifi


class WifiStudioFragment : Fragment() {
    private var _binding: FragmentWifiStudioBinding? = null
    private val binding: FragmentWifiStudioBinding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentWifiStudioBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            createBtn.setOnClickListener {
                val wifiName = wifiStudioNameEt.text.toString()
                val wifiPassword = wifiStudioPasswordEt.text.toString()
                if (wifiName.isEmpty() && wifiPassword.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Invalid wifi name or password! Try again!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@setOnClickListener
                }
                val isHidden = wifiStudioHiddenCb.isChecked
                val encryption = when {
                    wifiStudioEncryptionNoneRb.isChecked -> "None"
                    wifiStudioEncryptionWpaRb.isChecked -> "WPA"
                    else -> "WEP"
                }
                val wifiSchema = Wifi(encryption, wifiName, wifiPassword, isHidden)

                val barcodeEntity = BarcodeEntity(
                    text = wifiSchema.toBarcodeText(),
                    schema = BarcodeSchema.WIFI,
                    date = System.currentTimeMillis(),
                    isGenerated = true,
                    format = BarcodeFormat.QR_CODE
                )

                viewModel.insert(barcodeEntity)

                NavHostFragment.findNavController(this@WifiStudioFragment).navigate(
                    WifiStudioFragmentDirections.actionWifiStudioFragmentToWifiFragment(
                        wifiSchema,
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
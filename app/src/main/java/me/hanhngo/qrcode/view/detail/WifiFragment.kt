@file:Suppress("DEPRECATION")

package me.hanhngo.qrcode.view.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import me.hanhngo.qrcode.databinding.FragmentWifiBinding
import me.hanhngo.qrcode.util.extension.save
import me.hanhngo.qrcode.util.parseContent


class WifiFragment : Fragment() {
    private var _binding: FragmentWifiBinding? = null
    private val binding: FragmentWifiBinding get() = _binding!!

    private val args: WifiFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentWifiBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
        setupListener()
    }

    private fun setupListener() {
        val wifi = args.wifi

        binding.saveBtn.setOnClickListener {
            val saveResult = binding.wifiQrCodeIv.drawable.toBitmap().save(requireContext())
            Toast.makeText(
                requireContext(),
                if (saveResult) "Save to gallery successfully." else "Save to gallery failed. Try again.",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.copyWifiNameBtn.setOnClickListener {
            getSystemService(requireContext(), ClipboardManager::class.java)?.apply {
                setPrimaryClip(ClipData.newPlainText("Scanned Wifi name", wifi.name))
            }

            Toast.makeText(requireContext(), "Saved to clipboard.", Toast.LENGTH_SHORT).show()
        }

        binding.copyWifiPasswordBtn.setOnClickListener {
            getSystemService(requireContext(), ClipboardManager::class.java)?.apply {
                setPrimaryClip(ClipData.newPlainText("Scanned Wifi password", wifi.password))
            }

            Toast.makeText(requireContext(), "Saved to clipboard.", Toast.LENGTH_SHORT).show()
        }

        binding.connectBtn.setOnClickListener {
            val wifiConfig = WifiConfiguration()

            wifiConfig.SSID = String.format("\"%s\"", wifi.name)
            wifiConfig.preSharedKey = String.format("\"%s\"", wifi.password)

            val wifiManager = requireActivity().getSystemService(WIFI_SERVICE) as WifiManager?
            val netId = wifiManager!!.addNetwork(wifiConfig)
            wifiManager.disconnect()
            wifiManager.enableNetwork(netId, true)
            wifiManager.reconnect()
        }

    }

    private fun bindUI() {
        try {
            val wifi = args.wifi
            val format = args.barcodeFormat
            val bitmap = parseContent(wifi.toBarcodeText(), format)

            binding.wifiQrCodeIv.setImageBitmap(bitmap)
            binding.wifiNameTv.text = wifi.name
            binding.wifiPasswordTv.text = wifi.password
            binding.wifiEncryptionTv.text = wifi.encryption
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
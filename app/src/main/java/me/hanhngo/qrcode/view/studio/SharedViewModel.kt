package me.hanhngo.qrcode.view.studio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.hanhngo.qrcode.data.db.BarcodeEntity
import me.hanhngo.qrcode.repository.BarcodeRepository
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: BarcodeRepository
) : ViewModel() {

    fun insert(barcodeEntity: BarcodeEntity) {
        viewModelScope.launch {
            repository.insert(barcodeEntity)
        }
    }
}
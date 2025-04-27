package com.jihan.app.domain.utils

import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.themes.themes.ThemeDefault
import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.core.enumPref
import com.michaelflisar.kotpreferences.encryption.aes.StorageEncryptionAES
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create

object EncryptionDefinitions {
    private const val ALGORITHM = StorageEncryptionAES.DEFAULT_ALGORITHM
    private const val KEY_ALGORITHM = StorageEncryptionAES.DEFAULT_KEY_ALGORITHM
    private val KEY = StorageEncryptionAES.getKeyFromPassword(KEY_ALGORITHM, "demo", "salt")
    private val BYTE_ARRAY = listOf(
        0x16,
        0x09,
        0xc0,
        0x4d,
        0x4a,
        0x09,
        0xd2,
        0x46,
        0x71,
        0xcc,
        0x32,
        0xb7,
        0xd2,
        0x91,
        0x8a,
        0x9c
    ).map { it.toByte() }.toByteArray()
    private val IV = StorageEncryptionAES.getIv(BYTE_ARRAY) // byte array must be 16 bytes!
    val ENCRYPTION = StorageEncryptionAES(ALGORITHM, KEY, IV)
}

object Datastore : SettingsModel(
    storage = DataStoreStorage.create(
        name = "settings", encryption = EncryptionDefinitions.ENCRYPTION
    )
) {

    val themeKey by stringPref(ThemeDefault.KEY)
    val dynamic by boolPref(false)
    val showThemeLabel by boolPref(false)
    val baseTheme by enumPref(ComposeTheme.BaseTheme.System)


}
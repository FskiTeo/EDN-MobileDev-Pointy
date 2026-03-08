package com.jht.pointy.data.session

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AesGcmKeyManager
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.flow.first

private val Context.secureSessionDataStore by preferencesDataStore(name = "secure_session")

class SecureSessionStorage(private val context: Context) {

    private val aead: Aead by lazy {
        AeadConfig.register()
        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, KEYSET_PREF_FILE)
            .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle

        keysetHandle.getPrimitive(Aead::class.java)
    }

    suspend fun saveToken(token: String) {
        val encrypted = aead.encrypt(token.toByteArray(Charsets.UTF_8), ASSOCIATED_DATA)
        val payload = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        context.secureSessionDataStore.edit { prefs ->
            prefs[TOKEN_KEY] = payload
        }
    }

    suspend fun readToken(): String? {
        val encrypted = context.secureSessionDataStore.data.first()[TOKEN_KEY] ?: return null
        val decoded = Base64.decode(encrypted, Base64.NO_WRAP)
        return try {
            val plain = aead.decrypt(decoded, ASSOCIATED_DATA)
            String(plain, Charsets.UTF_8)
        } catch (_: Exception) {
            clearToken()
            null
        }
    }

    suspend fun clearToken() {
        context.secureSessionDataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }

    companion object {
        private const val MASTER_KEY_URI = "android-keystore://pointy_master_key"
        private const val KEYSET_NAME = "pointy_tink_keyset"
        private const val KEYSET_PREF_FILE = "pointy_tink_prefs"
        private val TOKEN_KEY = stringPreferencesKey("secure_auth_token")
        private val ASSOCIATED_DATA = "pointy-session-v1".toByteArray(Charsets.UTF_8)
    }
}

package com.dddlock.service

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.ContactsContract
import android.telecom.Call
import android.telecom.CallScreeningService
import androidx.core.content.ContextCompat
import com.dddlock.DDDLockApplication
import kotlinx.coroutines.runBlocking

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 *
 * Serviço de bloqueio de chamadas baseado em DDDs.
 * Utiliza exclusivamente a API oficial CallScreeningService.
 * Números na lista de contatos NÃO são bloqueados.
 */
class DDDCallScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = extractPhoneNumber(callDetails)

        // Se o número está nos contatos, não bloqueia
        if (isContact(phoneNumber)) {
            respondToCall(callDetails, createResponse(false))
            return
        }

        val ddd = extractDDDFromNumber(phoneNumber)

        if (ddd == null) {
            // Número privado, oculto ou sem DDD identificável — não bloqueia
            respondToCall(callDetails, createResponse(false))
            return
        }

        val blockedDDDs = getBlockedDDDs()
        val shouldBlock = ddd in blockedDDDs

        respondToCall(callDetails, createResponse(shouldBlock))
    }

    private fun extractPhoneNumber(callDetails: Call.Details): String? {
        val handle = callDetails.handle ?: return null
        return if (handle.schemeSpecificPart.isNullOrBlank()) null
        else handle.schemeSpecificPart
    }

    /**
     * Verifica se o número está na lista de contatos do dispositivo.
     */
    private fun isContact(phoneNumber: String?): Boolean {
        if (phoneNumber.isNullOrBlank()) return false
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        val digits = phoneNumber.filter { it.isDigit() }
        // Normalizar: remover código do país para comparação
        val normalizedNumber = when {
            digits.startsWith("55") && digits.length >= 12 -> digits.substring(2)
            digits.startsWith("0") && digits.length > 2 -> digits.substring(1)
            else -> digits
        }

        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                null,
                null,
                null
            )

            cursor?.let {
                while (it.moveToNext()) {
                    val contactNumber = it.getString(0) ?: continue
                    val contactDigits = contactNumber.filter { c -> c.isDigit() }
                    // Normalizar número do contato
                    val normalizedContact = when {
                        contactDigits.startsWith("55") && contactDigits.length >= 12 -> contactDigits.substring(2)
                        contactDigits.startsWith("0") && contactDigits.length > 2 -> contactDigits.substring(1)
                        else -> contactDigits
                    }
                    // Comparar últimos 8 dígitos (ignorando DDD para maior compatibilidade)
                    if (normalizedNumber.length >= 8 && normalizedContact.length >= 8) {
                        if (normalizedNumber.takeLast(8) == normalizedContact.takeLast(8)) {
                            return true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Em caso de erro, não bloqueia
            return false
        } finally {
            cursor?.close()
        }

        return false
    }

    /**
     * Extrai o DDD (2 dígitos) de um número de telefone brasileiro.
     *
     * Formatos suportados:
     * - +5588999999999  → 88
     * - 5588999999999   → 88
     * - 88999999999     → 88
     * - 08899999999     → 88 (com prefixo 0 de discagem)
     * - (88) 99999-9999 → 88
     * - 0800...         → null (toll-free)
     * - 0300...         → null (toll-free)
     */
    internal fun extractDDDFromNumber(number: String?): String? {
        if (number.isNullOrBlank()) return null

        var digits = number.filter { it.isDigit() }

        // Toll-free numbers (0800, 0300, etc.) — verificar ANTES de remover o 0
        if (digits.startsWith("0800") || digits.startsWith("0300")) return null

        // Remover prefixo 0 de discagem nacional (0 + DDD)
        if (digits.startsWith("0") && digits.length > 2) {
            digits = digits.substring(1)
        }

        return when {
            digits.length == 2 -> digits  // apenas o DDD
            digits.length == 10 || digits.length == 11 -> digits.substring(0, 2)
            digits.length == 12 && digits.startsWith("55") -> digits.substring(2, 4)
            digits.length == 13 && digits.startsWith("55") -> digits.substring(2, 4)
            else -> null
        }
    }

    private fun getBlockedDDDs(): Set<String> {
        val app = application as? DDDLockApplication ?: return emptySet()
        return runBlocking {
            app.container.dataStore.getBlockedDDDsOnce()
        }
    }

    private fun createResponse(block: Boolean): CallScreeningService.CallResponse {
        return if (block) {
            CallScreeningService.CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipNotification(false)
                .build()
        } else {
            CallScreeningService.CallResponse.Builder()
                .setDisallowCall(false)
                .build()
        }
    }
}

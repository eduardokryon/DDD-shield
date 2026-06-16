package com.dddlock.service

import android.telecom.Call
import android.telecom.CallScreeningService
import com.dddlock.DDDLockApplication
import kotlinx.coroutines.runBlocking

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 *
 * Serviço de bloqueio de chamadas baseado em DDDs.
 * Utiliza exclusivamente a API oficial CallScreeningService.
 */
class DDDCallScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = extractPhoneNumber(callDetails)
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

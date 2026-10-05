package com.vitasync

import org.junit.Assert.assertEquals
import org.junit.Test

class ModelsTest {
    @Test
    fun sleepSuggestionsReflectLoggedSleepWithoutMedicalClaims() {
        assertEquals(
            "Registre seu sono para receber uma sugestão mais alinhada ao seu dia.",
            sleepSuggestion(0f),
        )
        assertEquals(
            "Você registrou uma noite mais curta. Se puder, escolha hoje um movimento leve e reserve um momento para desacelerar.",
            sleepSuggestion(5.5f),
        )
        assertEquals(
            "Como seu sono ficou abaixo de 7 horas, um treino moderado e uma pausa tranquila podem combinar bem com hoje.",
            sleepSuggestion(6.5f),
        )
        assertEquals(
            "Seu registro de sono foi de 7 h. Mantenha um ritmo que pareça confortável para você.",
            sleepSuggestion(7f),
        )
    }

    @Test
    fun hourFormattingAvoidsUnnecessaryDecimals() {
        assertEquals("8", 8f.formatHours())
        assertEquals("7,5", 7.5f.formatHours())
    }
}

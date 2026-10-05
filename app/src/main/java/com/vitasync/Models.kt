package com.vitasync

import java.util.Locale

data class DailyLog(
    val sleepHours: Float = 0f,
    val mealsLogged: Int = 0,
    val waterMl: Int = 0,
    val workoutDone: Boolean = false,
    val lessonIndex: Int = 0,
)

data class Lesson(
    val title: String,
    val duration: String,
    val summary: String,
    val takeaway: String,
)

enum class AppTab(val title: String) {
    Today("Hoje"),
    Learn("Aprender"),
    Move("Treinar"),
    Eat("Alimentação"),
    Journal("Diário"),
}

val lessons = listOf(
    Lesson(
        "Pequenos passos, grandes hábitos",
        "3 min",
        "Hábitos duradouros começam com ações simples que cabem no seu dia.",
        "Escolha uma ação pequena e repita-a hoje.",
    ),
    Lesson(
        "Seu prato em cores",
        "4 min",
        "Variar cores no prato é uma forma prática de explorar diferentes alimentos.",
        "Adicione uma fruta ou vegetal que você gosta à próxima refeição.",
    ),
    Lesson(
        "Movimento que combina com você",
        "3 min",
        "Uma caminhada, alongamento ou treino curto também conta como movimento.",
        "Reserve dez minutos para se movimentar no seu ritmo.",
    ),
    Lesson(
        "Uma rotina para desacelerar",
        "4 min",
        "Um ritual tranquilo antes de dormir pode ajudar a criar uma rotina consistente.",
        "Experimente deixar as telas de lado alguns minutos antes de deitar.",
    ),
)

fun sleepSuggestion(hours: Float): String = when {
    hours <= 0f -> "Registre seu sono para receber uma sugestão mais alinhada ao seu dia."
    hours < 6f -> "Você registrou uma noite mais curta. Se puder, escolha hoje um movimento leve e reserve um momento para desacelerar."
    hours < 7f -> "Como seu sono ficou abaixo de 7 horas, um treino moderado e uma pausa tranquila podem combinar bem com hoje."
    else -> "Seu registro de sono foi de ${hours.formatHours()} h. Mantenha um ritmo que pareça confortável para você."
}

fun Float.formatHours(): String =
    if (this % 1f == 0f) toInt().toString() else String.format(Locale.forLanguageTag("pt-BR"), "%.1f", this)

package com.vitasync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.IOException
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { VitaSyncApp() }
    }
}

private val Forest = Color(0xFF176B58)
private val Ink = Color(0xFF18342D)
private val Mint = Color(0xFFE5F2EC)
private val Sand = Color(0xFFF7F5EF)
private val Orange = Color(0xFFE9A96C)
private val Muted = Color(0xFF69766F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VitaSyncApp() {
    val context = LocalContext.current
    val store = remember { LocalStore(context) }
    var log by remember { mutableStateOf(store.read()) }
    var selectedTab by rememberSaveable { mutableStateOf(AppTab.Today.name) }
    var settingsOpen by rememberSaveable { mutableStateOf(false) }
    var deleteDialog by remember { mutableStateOf(false) }
    var exportMessage by remember { mutableStateOf<String?>(null) }
    var workoutPlace by rememberSaveable { mutableStateOf(store.readWorkoutPlace()) }

    fun updateLog(updated: DailyLog) {
        log = updated
        store.write(updated)
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        if (uri == null) {
            exportMessage = "Exportação cancelada."
        } else {
            try {
                val json = """{
  "data": "${LocalDate.now()}",
  "sono_horas": ${log.sleepHours},
  "refeicoes_registradas": ${log.mealsLogged},
  "agua_ml": ${log.waterMl},
  "treino_concluido": ${log.workoutDone},
  "local_preferido_treino": "${store.readWorkoutPlace()}",
  "licoes_concluidas": ${log.lessonIndex}
}"""
                context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(json) }
                    ?: throw IOException("Não foi possível abrir o arquivo escolhido.")
                exportMessage = "Seus dados foram exportados para o local escolhido."
            } catch (exception: IOException) {
                exportMessage = "Não foi possível exportar os dados. Tente novamente."
            }
        }
    }

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Forest,
            onPrimary = Color.White,
            secondary = Orange,
            background = Sand,
            surface = Color.White,
            onSurface = Ink,
            onBackground = Ink,
        ),
        typography = MaterialTheme.typography,
    ) {
        Scaffold(
            containerColor = Sand,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(if (settingsOpen) "Preferências" else "VitaSync", fontWeight = FontWeight.Bold)
                            if (!settingsOpen) {
                                Text("um passo de cada vez", style = MaterialTheme.typography.labelSmall, color = Muted)
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { settingsOpen = !settingsOpen }) {
                            Icon(
                                imageVector = if (settingsOpen) Icons.Rounded.Close else Icons.Rounded.Settings,
                                contentDescription = if (settingsOpen) "Fechar preferências" else "Preferências",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Sand),
                )
            },
            bottomBar = {
                if (!settingsOpen) {
                    NavigationBar(containerColor = Color.White) {
                        AppTab.entries.forEach { tab ->
                            val icon = when (tab) {
                                AppTab.Today -> Icons.Rounded.Favorite
                                AppTab.Learn -> Icons.Rounded.Book
                                AppTab.Move -> Icons.AutoMirrored.Rounded.DirectionsRun
                                AppTab.Eat -> Icons.Rounded.Restaurant
                                AppTab.Journal -> Icons.Rounded.EditNote
                            }
                            NavigationBarItem(
                                selected = selectedTab == tab.name,
                                onClick = { selectedTab = tab.name },
                                icon = { Icon(icon, contentDescription = null) },
                                label = { Text(tab.title, fontSize = 10.sp, maxLines = 1) },
                            )
                        }
                    }
                }
            },
        ) { padding ->
            if (settingsOpen) {
                SettingsScreen(
                    log = log,
                    onExport = { exportLauncher.launch("vitasync-dados.json") },
                    onDelete = { deleteDialog = true },
                    modifier = Modifier.padding(padding),
                )
            } else {
                when (AppTab.valueOf(selectedTab)) {
                    AppTab.Today -> TodayScreen(
                        log = log,
                        onWater = { updateLog(log.copy(waterMl = log.waterMl + 250)) },
                        onNavigate = { selectedTab = it.name },
                        modifier = Modifier.padding(padding),
                    )
                    AppTab.Learn -> LearnScreen(
                        log = log,
                        onComplete = { updateLog(log.copy(lessonIndex = (log.lessonIndex + 1).coerceAtMost(lessons.size))) },
                        modifier = Modifier.padding(padding),
                    )
                    AppTab.Move -> MoveScreen(
                        done = log.workoutDone,
                        place = workoutPlace,
                        onPlaceChange = {
                            workoutPlace = it
                            store.writeWorkoutPlace(it)
                        },
                        onDone = { updateLog(log.copy(workoutDone = !log.workoutDone)) },
                        modifier = Modifier.padding(padding),
                    )
                    AppTab.Eat -> EatScreen(
                        waterMl = log.waterMl,
                        mealsLogged = log.mealsLogged,
                        onWater = { updateLog(log.copy(waterMl = log.waterMl + 250)) },
                        onMeal = { updateLog(log.copy(mealsLogged = log.mealsLogged + 1)) },
                        modifier = Modifier.padding(padding),
                    )
                    AppTab.Journal -> JournalScreen(
                        log = log,
                        onUpdate = ::updateLog,
                        modifier = Modifier.padding(padding),
                    )
                }
            }
        }
    }

    if (deleteDialog) {
        AlertDialog(
            onDismissRequest = { deleteDialog = false },
            title = { Text("Apagar seus dados locais?") },
            text = { Text("Esta ação remove os registros e o progresso salvos neste aparelho. Não é possível desfazer.") },
            confirmButton = {
                Button(onClick = {
                    store.clear()
                    log = DailyLog()
                    deleteDialog = false
                    exportMessage = "Dados locais apagados."
                }) { Text("Apagar dados") }
            },
            dismissButton = {
                OutlinedButton(onClick = { deleteDialog = false }) { Text("Cancelar") }
            },
        )
    }

    exportMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { exportMessage = null },
            text = { Text(message) },
            confirmButton = { Button(onClick = { exportMessage = null }) { Text("Entendi") } },
        )
    }
}

@Composable
private fun Page(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
    )
}

@Composable
private fun TodayScreen(log: DailyLog, onWater: () -> Unit, onNavigate: (AppTab) -> Unit, modifier: Modifier = Modifier) {
    Page(modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Seu dia, no seu ritmo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Pequenas escolhas também fazem diferença.", color = Muted)
        }
        Card(colors = CardDefaults.cardColors(containerColor = Forest), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.NightsStay, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.size(8.dp))
                    Text("COMO FOI SUA NOITE?", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = .8f))
                }
                Text(
                    if (log.sleepHours > 0f) "${log.sleepHours.formatHours()} h registradas" else "Seu bem-estar começa com atenção a você.",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(sleepSuggestion(log.sleepHours), color = Color.White.copy(alpha = .9f), style = MaterialTheme.typography.bodyMedium)
                Text("Sugestão geral, não substitui orientação profissional.", color = Color.White.copy(alpha = .7f), style = MaterialTheme.typography.labelSmall)
            }
        }
        Text("Seus pilares", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PillarCard("Aprender", "${log.lessonIndex}/${lessons.size} lições", Icons.Rounded.Book, Mint, Modifier.weight(1f)) { onNavigate(AppTab.Learn) }
            PillarCard("Movimento", if (log.workoutDone) "Treino feito" else "No seu ritmo", Icons.AutoMirrored.Rounded.DirectionsRun, Color(0xFFFFEEDB), Modifier.weight(1f)) { onNavigate(AppTab.Move) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PillarCard("Alimentação", "${log.mealsLogged} refeições", Icons.Rounded.LocalDining, Color(0xFFFFF2D9), Modifier.weight(1f)) { onNavigate(AppTab.Eat) }
            PillarCard("Descanso", if (log.sleepHours > 0) "${log.sleepHours.formatHours()} h de sono" else "Registrar sono", Icons.Rounded.NightsStay, Color(0xFFECEBFA), Modifier.weight(1f)) { onNavigate(AppTab.Journal) }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Água", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("${log.waterMl} ml registrados hoje", color = Muted)
                }
                Button(onClick = onWater, shape = RoundedCornerShape(14.dp)) {
                    Icon(Icons.Rounded.WaterDrop, contentDescription = null)
                    Spacer(Modifier.size(6.dp))
                    Text("+ 250 ml")
                }
            }
        }
        OutlinedButton(
            onClick = { onNavigate(AppTab.Journal) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(Icons.Rounded.EditNote, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Fazer check-in no diário")
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun PillarCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = tint),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = Forest)
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Muted)
        }
    }
}

@Composable
private fun LearnScreen(log: DailyLog, onComplete: () -> Unit, modifier: Modifier = Modifier) {
    Page(modifier) {
        ScreenHeading("Aprender", "Ideias curtas para levar para o dia a dia.")
        ProgressCard("Sua trilha", log.lessonIndex, lessons.size, "lições concluídas")
        if (log.lessonIndex < lessons.size) {
            val lesson = lessons[log.lessonIndex]
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("LIÇÃO ${log.lessonIndex + 1} · ${lesson.duration.uppercase()}", color = Forest, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text(lesson.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(lesson.summary, color = Muted)
                    Surface(color = Mint, shape = RoundedCornerShape(16.dp)) {
                        Text("Para experimentar: ${lesson.takeaway}", Modifier.padding(14.dp), color = Ink)
                    }
                    Button(onClick = onComplete, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Concluir lição")
                    }
                }
            }
        } else {
            Card(colors = CardDefaults.cardColors(containerColor = Mint), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Trilha concluída!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Você concluiu todas as lições disponíveis. Volte quando quiser rever os aprendizados.", color = Muted)
                }
            }
        }
        Text("PRÓXIMAS LIÇÕES", style = MaterialTheme.typography.labelLarge, color = Muted, fontWeight = FontWeight.Bold)
        lessons.drop(log.lessonIndex + 1).forEachIndexed { index, lesson ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${log.lessonIndex + index + 2}", color = Forest, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.size(14.dp))
                    Column {
                        Text(lesson.title, fontWeight = FontWeight.SemiBold)
                        Text(lesson.duration, color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun MoveScreen(
    done: Boolean,
    place: String,
    onPlaceChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Page(modifier) {
        ScreenHeading("Movimento", "Escolha uma opção que combine com seu espaço e sua energia.")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("Casa", "Academia").forEach { option ->
                val selected = place == option
                if (selected) {
                    Button(onClick = { onPlaceChange(option) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) { Text(option) }
                } else {
                    OutlinedButton(onClick = { onPlaceChange(option) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) { Text(option) }
                }
            }
        }
        Text(if (place == "Casa") "TREINO EM CASA · 15 MIN" else "TREINO NA ACADEMIA · 25 MIN", style = MaterialTheme.typography.labelLarge, color = Forest, fontWeight = FontWeight.Bold)
        val exercises = if (place == "Casa") {
            listOf("Aquecimento leve · 3 min", "Agachamento livre · 3 × 10", "Flexão na parede ou no chão · 3 × 8", "Ponte de glúteos · 3 × 12", "Alongamento confortável · 2 min")
        } else {
            listOf("Aquecimento na esteira · 5 min", "Leg press leve · 3 × 10", "Remada sentada · 3 × 10", "Supino com carga confortável · 3 × 8", "Desaceleração · 3 min")
        }
        exercises.forEachIndexed { index, exercise ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(34.dp).background(Mint, CircleShape), contentAlignment = Alignment.Center) {
                        Text("${index + 1}", color = Forest, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.size(12.dp))
                    Text(exercise, fontWeight = FontWeight.Medium)
                }
            }
        }
        Text("Ajuste ou interrompa o treino se não estiver confortável. Exercícios são sugestões gerais.", color = Muted, style = MaterialTheme.typography.bodySmall)
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (done) Color(0xFF49776B) else Forest),
        ) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text(if (done) "Treino concluído hoje" else "Marcar treino como feito")
        }
    }
}

private data class Recipe(val title: String, val time: String, val items: String)

@Composable
private fun EatScreen(waterMl: Int, mealsLogged: Int, onWater: () -> Unit, onMeal: () -> Unit, modifier: Modifier = Modifier) {
    val recipes = listOf(
        Recipe("Tigela colorida", "15 min", "Arroz, feijão, tomate e folhas que você tiver."),
        Recipe("Omelete simples", "10 min", "Ovos, cebola e legumes picados a gosto."),
        Recipe("Aveia com fruta", "5 min", "Aveia, leite ou bebida vegetal e fruta."),
    )
    Page(modifier) {
        ScreenHeading("Alimentação", "Ideias flexíveis, com ingredientes simples.")
        Card(colors = CardDefaults.cardColors(containerColor = Mint), shape = RoundedCornerShape(20.dp)) {
            Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Água registrada", fontWeight = FontWeight.SemiBold)
                    Text("$waterMl ml · ${mealsLogged} refeições no diário", color = Muted)
                }
                Button(onClick = onWater, shape = RoundedCornerShape(14.dp)) { Text("+ 250 ml") }
            }
        }
        Text("RECEITAS DO DIA A DIA", style = MaterialTheme.typography.labelLarge, color = Forest, fontWeight = FontWeight.Bold)
        recipes.forEach { recipe ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(recipe.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(recipe.time, color = Forest, style = MaterialTheme.typography.labelMedium)
                    }
                    Text(recipe.items, color = Muted)
                }
            }
        }
        OutlinedButton(onClick = onMeal, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Icon(Icons.Rounded.Restaurant, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Registrar uma refeição no diário")
        }
        Text("As sugestões podem ser adaptadas às suas preferências, cultura e ingredientes disponíveis.", color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun JournalScreen(log: DailyLog, onUpdate: (DailyLog) -> Unit, modifier: Modifier = Modifier) {
    Page(modifier) {
        ScreenHeading("Diário", "Um check-in simples, privado e sem julgamentos.")
        LogControl(
            title = "Sono",
            detail = if (log.sleepHours > 0) "${log.sleepHours.formatHours()} horas" else "Ainda não registrado",
            icon = Icons.Rounded.NightsStay,
            onMinus = { onUpdate(log.copy(sleepHours = (log.sleepHours - .5f).coerceAtLeast(0f))) },
            onPlus = { onUpdate(log.copy(sleepHours = (log.sleepHours + .5f).coerceAtMost(24f))) },
            plusLabel = "+ 30 min",
        )
        LogControl(
            title = "Refeições",
            detail = "${log.mealsLogged} registradas hoje",
            icon = Icons.Rounded.Restaurant,
            onMinus = { onUpdate(log.copy(mealsLogged = (log.mealsLogged - 1).coerceAtLeast(0))) },
            onPlus = { onUpdate(log.copy(mealsLogged = log.mealsLogged + 1)) },
            plusLabel = "+ 1",
        )
        LogControl(
            title = "Água",
            detail = "${log.waterMl} ml registrados hoje",
            icon = Icons.Rounded.WaterDrop,
            onMinus = { onUpdate(log.copy(waterMl = (log.waterMl - 250).coerceAtLeast(0))) },
            onPlus = { onUpdate(log.copy(waterMl = log.waterMl + 250)) },
            plusLabel = "+ 250 ml",
        )
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
            Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Atividade física", fontWeight = FontWeight.SemiBold)
                    Text(if (log.workoutDone) "Treino registrado hoje" else "Nenhum treino registrado", color = Muted, style = MaterialTheme.typography.bodySmall)
                }
                Button(onClick = { onUpdate(log.copy(workoutDone = !log.workoutDone)) }, shape = RoundedCornerShape(14.dp)) {
                    Text(if (log.workoutDone) "Desfazer" else "Registrar")
                }
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2D9)), shape = RoundedCornerShape(18.dp)) {
            Text(
                "Seus registros ficam somente neste aparelho. O diário não sincroniza com serviços de saúde.",
                Modifier.padding(16.dp),
                color = Ink,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun LogControl(
    title: String,
    detail: String,
    icon: ImageVector,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    plusLabel: String,
) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Forest)
                Spacer(Modifier.size(10.dp))
                Column {
                    Text(title, fontWeight = FontWeight.SemiBold)
                    Text(detail, color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = onMinus, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("−") }
                Button(onClick = onPlus, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text(plusLabel) }
            }
        }
    }
}

@Composable
private fun SettingsScreen(log: DailyLog, onExport: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Page(modifier) {
        ScreenHeading("Seus dados, suas escolhas", "Preferências e privacidade")
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Rounded.Lock, contentDescription = null, tint = Forest)
                Text("Privacidade em primeiro lugar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Neste MVP, seus registros são armazenados localmente neste aparelho. O VitaSync não envia dados de saúde para servidores.", color = Muted)
                HorizontalDivider(color = Sand)
                Text("Health Connect não está disponível nesta versão. Nenhuma integração ou leitura automática de dados está ativa.", color = Muted)
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Resumo salvo neste aparelho", fontWeight = FontWeight.SemiBold)
                Text("${log.sleepHours.formatHours()} h de sono · ${log.mealsLogged} refeições · ${log.waterMl} ml de água", color = Muted)
                Text("${if (log.workoutDone) "Treino registrado" else "Sem treino registrado"} · ${log.lessonIndex} lições concluídas", color = Muted)
            }
        }
        Button(onClick = onExport, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Text("Exportar meus dados (JSON)")
        }
        OutlinedButton(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF9A3F36)),
        ) {
            Text("Apagar dados deste aparelho")
        }
    }
}

@Composable
private fun ScreenHeading(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Muted)
    }
}

@Composable
private fun ProgressCard(title: String, progress: Int, total: Int, suffix: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Mint), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text("$progress de $total $suffix", color = Forest, style = MaterialTheme.typography.labelMedium)
            }
            LinearProgressIndicator(
                progress = { if (total == 0) 0f else progress.toFloat() / total },
                modifier = Modifier.fillMaxWidth(),
                color = Forest,
                trackColor = Color.White,
            )
        }
    }
}

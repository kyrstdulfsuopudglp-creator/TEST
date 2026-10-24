package com.example.testprogram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {

    companion object {
        // Подключаем наш скомпилированный C-модуль эксплойта
        init {
            System.loadLibrary("dfroot-exploit")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Тема приложения в темном стиле
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF6200EE),
                    surface = Color(0xFF121212),
                    background = Color(0xFF1E1E1E)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var logs by remember { mutableStateOf(listOf("Система готова. Ожидание запуска эксплойта...")) }
    var isRunning by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Шапка приложения
        Column {
            Text(
                text = "DFRoot Custom Client",
                color = Color.White,
                fontSize = 22.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "AOSP Local Root Utility",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Терминал (окошко логов)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            LazyColumn {
                items(logs) { log ->
                    Text(
                        text = log,
                        color = Color(0xFF00FF66), // Зеленый терминальный цвет
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка запуска процесса
        Button(
            onClick = {
                if (!isRunning) {
                    isRunning = true
                    coroutineScope.launch {
                        // Здесь мы будем запускать команды из репозитория
                        runShellCommands(
                            onOutput = { newLine ->
                                logs = logs + newLine
                            },
                            onFinished = {
                                isRunning = false
                                logs = logs + "=== Процесс завершен ==="
                            }
                        )
                    }
                }
            },
            enabled = !isRunning,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text(
                text = if (isRunning) "Выполнение..." else "Запустить DFRoot",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

// Функция для выполнения shell-команд внутри устройства
suspend fun runShellCommands(onOutput: (String) -> Unit, onFinished: () -> Unit) {
    withContext(Dispatchers.IO) {
        try {
            withContext(Dispatchers.Main) { onOutput("Инициализация окружения...") }

            // Пример выполнения базовых проверок системы (архитектура, версия Android)
            // Здесь в будущем мы заменим это на вызов бинарников эксплойта из репозитория
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", "uname -m && getprop ro.build.version.release"))

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line != null) {
                    withContext(Dispatchers.Main) {
                        onOutput("> $line")
                    }
                }
            }
            process.waitFor()

            withContext(Dispatchers.Main) { onOutput("Эксплойт-скрипт готов к интеграции.") }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onOutput("Ошибка: ${e.message}")
            }
        } finally {
            withContext(Dispatchers.Main) {
                onFinished()
            }
        }
    }
}
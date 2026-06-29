package com.rocketseat.tripplanner

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Map
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import androidx.compose.ui.tooling.preview.Preview
import com.rocketseat.tripplanner.ui.theme.TripPlannerTheme
import kotlinx.coroutines.delay

enum class TripScreen {
    Splash,
    Start,
    Distance,
    Consumption,
    Price,
    Result
}

@Composable
fun TripPlannerApp(
    navController: NavHostController = rememberNavController(),
    viewModel: TripViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = TripScreen.Splash.name,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(TripScreen.Splash.name) {
            SplashScreen(onAnimationFinish = {
                navController.navigate(TripScreen.Start.name) {
                    popUpTo(TripScreen.Splash.name) { inclusive = true }
                }
            })
        }
        composable(TripScreen.Start.name) {
            StartScreen(onStartClick = { navController.navigate(TripScreen.Distance.name) })
        }
        composable(TripScreen.Distance.name) {
            InputScreen(
                title = "Distância",
                subtitle = "Qual a distância total da sua viagem?",
                tip = "Use ponto (.) para decimais. Ex: 150.5",
                value = viewModel.distance,
                onValueChange = { viewModel.distance = it },
                onNext = { navController.navigate(TripScreen.Consumption.name) },
                onBack = { navController.popBackStack() },
                icon = Icons.Default.Map
            )
        }
        composable(TripScreen.Consumption.name) {
            InputScreen(
                title = "Consumo",
                subtitle = "Quanto seu veículo consome por litro?",
                tip = "Exemplo: 12.5 km/L",
                value = viewModel.consumption,
                onValueChange = { viewModel.consumption = it },
                onNext = { navController.navigate(TripScreen.Price.name) },
                onBack = { navController.popBackStack() },
                icon = Icons.Default.DirectionsCar
            )
        }
        composable(TripScreen.Price.name) {
            InputScreen(
                title = "Preço",
                subtitle = "Qual o valor do combustível hoje?",
                tip = "Informe o preço por litro. Ex: 5.89",
                value = viewModel.fuelPrice,
                onValueChange = { viewModel.fuelPrice = it },
                onNext = { navController.navigate(TripScreen.Result.name) },
                onBack = { navController.popBackStack() },
                icon = Icons.Default.LocalGasStation
            )
        }
        composable(TripScreen.Result.name) {
            ResultScreen(
                distance = viewModel.distance,
                consumption = viewModel.consumption,
                price = viewModel.fuelPrice,
                totalCost = viewModel.totalCost,
                onReset = {
                    viewModel.reset()
                    navController.navigate(TripScreen.Start.name) {
                        popUpTo(TripScreen.Start.name) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun SplashScreen(onAnimationFinish: () -> Unit) {
    var visible by remember { mutableStateOf(true) }
    
    // Animação de rotação infinita
    val infiniteTransition = rememberInfiniteTransition(label = "planeRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    LaunchedEffect(Unit) {
        delay(3000) // Exibe por 3 segundos
        visible = false
        delay(500) // Tempo para a animação de fade
        onAnimationFinish()
    }

    AnimatedVisibility(
        visible = visible,
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            // Fundo estático (roxo com o círculo)
            Image(
                painter = painterResource(id = R.drawable.plane_background),
                contentDescription = null,
                modifier = Modifier.size(280.dp)
            )
            // Avião que gira
            Image(
                painter = painterResource(id = R.drawable.plane_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(280.dp)
                    .rotate(rotation)
            )
        }
    }
}

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(onStartClick: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity

    // Lógica para alternar os ícones e textos
    val steps = listOf(
        R.drawable.icon_planeje to "PLANEJE.",
        R.drawable.icon_viaje to "VIAJE.",
        R.drawable.icon_economize to "ECONOMIZE."
    )
    var currentStepIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            currentStepIndex = (currentStepIndex + 1) % steps.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Seção Hero
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(140.dp),
                    shape = RoundedCornerShape(40.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Crossfade(
                        targetState = steps[currentStepIndex].first,
                        animationSpec = tween(1000),
                        label = "iconFade"
                    ) { iconRes ->
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            tint = Color.Unspecified, // Mantém as cores originais do vetor
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxSize()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Trip Planner",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-1).sp
                )
                
                AnimatedContent(
                    targetState = steps[currentStepIndex].second,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) + slideInVertically { it } togetherWith
                        fadeOut(animationSpec = tween(500)) + slideOutVertically { -it }
                    },
                    label = "textAnimation"
                ) { text ->
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    )
                }
            }

            // Seção de Ação
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onStartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        "COMEÇAR PLANEJAMENTO",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                TextButton(onClick = { showBottomSheet = true }) {
                    Text("Como funciona?", color = Color.White)
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { activity?.finish() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    Text("Sair do App", color = Color.White)
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Informações sobre o cálculo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "O custo é calculado dividindo a distância total pelo consumo médio do " +
                            "veículo e multiplicando pelo preço do combustível por litro.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Fórmula: (Distância / Consumo) * Preço",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    title: String,
    subtitle: String,
    tip: String,
    value: String,
    onValueChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    icon: ImageVector
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = tip,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    // Filtra para permitir apenas números e um único ponto decimal
                    val filtered = newValue.replace(",", ".").filter { it.isDigit() || it == '.' }
                    if (filtered.count { it == '.' } <= 1) {
                        onValueChange(filtered)
                    }
                },
                label = { Text(title) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                leadingIcon = { Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("0.00") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                enabled = value.isNotBlank(),
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("PRÓXIMO", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun ResultScreen(
    distance: String,
    consumption: String,
    price: String,
    totalCost: Double,
    onReset: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reiniciar") },
            text = { Text("Deseja realmente planejar outra viagem?") },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    onReset()
                }) {
                    Text("Sim", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Não")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "RESUMO DA VIAGEM",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 2.sp
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ResultItem(label = "Distância Total", value = "$distance km", icon = Icons.Default.Map)
                    ResultItem(label = "Consumo Médio", value = "$consumption km/L", icon = Icons.Default.DirectionsCar)
                    ResultItem(label = "Preço por Litro", value = "R$ $price", icon = Icons.Default.LocalGasStation)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "CUSTO TOTAL ESTIMADO",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "R$ ${"%.2f".format(totalCost)}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { showResetDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("PLANEJAR OUTRA VIAGEM", fontWeight = FontWeight.ExtraBold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ResultItem(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    TripPlannerTheme {
        StartScreen(onStartClick = {})
    }
}

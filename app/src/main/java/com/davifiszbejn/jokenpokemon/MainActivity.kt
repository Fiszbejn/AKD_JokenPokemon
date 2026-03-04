package com.davifiszbejn.jokenpokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davifiszbejn.jokenpokemon.ui.theme.JokenPokemonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JokenPokemonTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    JokenPokemonScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

val starters = listOf(
    Pokemon("Bulbassaur", R.drawable.bulbassaur),
    Pokemon("Charmander", R.drawable.charmander),
    Pokemon("Squirtle", R.drawable.squirtle)
)

enum class BattleResult(val text: String) {
    NONE(""),
    WIN("Você venceu!"),
    LOSE("Você perdeu!"),
    DRAW("Empate!")
}

fun battle(user: Pokemon, cpu: Pokemon): BattleResult {
    if (user == cpu) return BattleResult.DRAW

    // Regras:
    // Bulbassaur > Squirtle
    // Squirtle > Charmander
    // Charmander > Bulbassaur
    return when (user.name) {
        "Bulbassaur" -> if (cpu.name == "Squirtle") BattleResult.WIN else BattleResult.LOSE
        "Squirtle" -> if (cpu.name == "Charmander") BattleResult.WIN else BattleResult.LOSE
        "Charmander" -> if (cpu.name == "Bulbassaur") BattleResult.WIN else BattleResult.LOSE
        else -> BattleResult.NONE
    }
}

@Composable
fun JokenPokemonScreen(modifier: Modifier = Modifier) {

    var userPokemon by remember { mutableStateOf<Pokemon?>(null) }
    var cpuPokemon by remember { mutableStateOf<Pokemon?>(null) }
    var result by remember { mutableStateOf(BattleResult.NONE) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        PokemonLogo()

        Spacer(Modifier.weight(1f))

        PokemonBattleArena(
            userPokemon = userPokemon,
            cpuPokemon = cpuPokemon
        )

        Spacer(Modifier.weight(1f))

        if (result != BattleResult.NONE) {
            Text(
                text = result.text,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = when (result) {
                    BattleResult.WIN -> Color(0xFF1B5E20)   // verde
                    BattleResult.LOSE -> Color(0xFFB71C1C)  // vermelho
                    BattleResult.DRAW -> Color(0xFF616161)  // cinza
                    BattleResult.NONE -> Color.Transparent
                }
            )
        }

        PokemonOptionList(pokemons = starters, pokemonSelected = userPokemon) { selected ->

            userPokemon = selected

            cpuPokemon = starters.random()

            result = battle(selected, cpuPokemon!!) }
    }
}

data class Pokemon(
    val name: String,
    val imageRes: Int = R.drawable.pokeball_unselected
)

@Composable
fun PokemonLogo() {
    Image(
        painter = painterResource(R.drawable.logo_pokemon),
        contentDescription = "Logo do Pokemon",
        modifier = Modifier
            .height(150.dp)
            .padding(16.dp)
    )
}

@Composable
fun PokemonBattleArena(
    userPokemon: Pokemon?,
    cpuPokemon: Pokemon?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PokeCard(description = "Você", pokemon = userPokemon)
        PokeCard(description = "Computador", pokemon = cpuPokemon)
    }
}

@Composable
fun PokemonOptionList(
    pokemons: List<Pokemon>,
    pokemonSelected: Pokemon?,
    onSelected: (Pokemon) -> Unit
) {
    Column(
        modifier = Modifier
            .border(width = 1.dp, shape = RectangleShape, color = Color.Gray)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp))
    {
        Text(
            text = "Faça sua jogada de mestre",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            pokemons.forEach {
                PokemonOption(it, selected = it == pokemonSelected, onSelected = onSelected)
            }
        }
    }
}

@Composable
fun PokemonOption(
    pokemon: Pokemon,
    selected: Boolean,
    onSelected: (Pokemon) -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onSelected(pokemon) }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = if (selected) painterResource(R.drawable.pokeball_selected) else painterResource(
                R.drawable.pokeball_unselected
            ),
            contentDescription = pokemon.name,
            modifier = Modifier.size(50.dp),
            colorFilter = if (isSystemInDarkTheme() && !selected) ColorFilter.tint(Color.White) else null
        )

        Text(pokemon.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PokeCard(
    pokemon: Pokemon? = null,
    description: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if(pokemon == null) {
            Image(
                painter = painterResource(R.drawable.pokeball_unselected),
                contentDescription = "Pokebola vazia",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "-",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = description,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )
        }else {
            Image(
                painter = painterResource(pokemon.imageRes),
                contentDescription = "Pokemon selecionado é o ${pokemon.name}",
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = pokemon.name.uppercase(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = description,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun JokenPokemonPreview() {
    JokenPokemonTheme {
        JokenPokemonScreen()
    }
}
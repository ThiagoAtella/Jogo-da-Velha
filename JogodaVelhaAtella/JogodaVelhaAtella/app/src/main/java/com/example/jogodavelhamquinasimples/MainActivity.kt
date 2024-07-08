package com.example.jogodavelhamquinasimples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.jogodavelhamquinasimples.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    // Variável de ligação para acesso às views
    private lateinit var binding: ActivityMainBinding

    // Vetor bidimensional que representará o tabuleiro de jogo
    val tabuleiro = arrayOf(
        arrayOf("", "", ""),
        arrayOf("", "", ""),
        arrayOf("", "", "")
    )

    // Nível de dificuldade: 0 = fácil, 1 = médio, 2 = difícil
    var nivelDificuldade = 0

    // Variável para controlar o turno
    var turnoJogador = true

    // Variável para controlar o modo de jogo: true = single player, false = multiplayer
    var singlePlayerMode = true

    // Método onCreate que é chamado quando a Activity é criada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla o layout usando o binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurações de dificuldade
        binding.buttonFacil.setOnClickListener { nivelDificuldade = 0 }
        binding.buttonMedio.setOnClickListener { nivelDificuldade = 1 }
        binding.buttonDificil.setOnClickListener { nivelDificuldade = 2 }

        // Configurações de modo de jogo
        binding.buttonSinglePlayer.setOnClickListener {
            singlePlayerMode = true
            reiniciarJogo(binding.root)
        }
        binding.buttonMultiplayer.setOnClickListener {
            singlePlayerMode = false
            reiniciarJogo(binding.root)
        }
    }

    // Função que será chamada quando um botão for clicado
    fun buttonClick(view: View) {
        val buttonSelecionado = view as Button

        if (turnoJogador) {
            buttonSelecionado.text = "X"
            atualizarTabuleiro(buttonSelecionado, "X")

            // Verifica se o jogador "X" ganhou
            if (verificarVitoria("X")) {
                desativarTodosBotoes()
                binding.textViewResultado.text = if (singlePlayerMode) "Você ganhou!" else "Jogador X venceu!"
                return
            }

            // Verifica se há empate após a jogada do jogador
            if (verificarEmpate()) {
                desativarTodosBotoes()
                binding.textViewResultado.text = "Empate!"
                return
            }

            // Alterna o turno para o próximo jogador ou computador
            turnoJogador = false
            if (singlePlayerMode) {
                movimentoComputador()
            }
        } else {
            buttonSelecionado.text = "O"
            atualizarTabuleiro(buttonSelecionado, "O")

            // Verifica se o jogador "O" ganhou
            if (verificarVitoria("O")) {
                desativarTodosBotoes()
                binding.textViewResultado.text = if (singlePlayerMode) "Você perdeu!" else "Jogador O venceu!"
                return
            }

            // Verifica se há empate após a jogada do jogador
            if (verificarEmpate()) {
                desativarTodosBotoes()
                binding.textViewResultado.text = "Empate!"
                return
            }

            // Alterna o turno para o próximo jogador
            turnoJogador = true
        }

        buttonSelecionado.isEnabled = false
    }

    // Função para atualizar o tabuleiro com a jogada do jogador
    private fun atualizarTabuleiro(buttonSelecionado: Button, jogador: String) {
        when(buttonSelecionado.id) {
            binding.buttonZero.id -> tabuleiro[0][0] = jogador
            binding.buttonUm.id -> tabuleiro[0][1] = jogador
            binding.buttonDois.id -> tabuleiro[0][2] = jogador
            binding.buttonTres.id -> tabuleiro[1][0] = jogador
            binding.buttonQuatro.id -> tabuleiro[1][1] = jogador
            binding.buttonCinco.id -> tabuleiro[1][2] = jogador
            binding.buttonSeis.id -> tabuleiro[2][0] = jogador
            binding.buttonSete.id -> tabuleiro[2][1] = jogador
            binding.buttonOito.id -> tabuleiro[2][2] = jogador
        }
    }

    // Movimento do computador de acordo com o nível de dificuldade
    private fun movimentoComputador() {
        when (nivelDificuldade) {
            0 -> movimentoComputadorFacil()
            1 -> movimentoComputadorMedio()
            2 -> movimentoComputadorDificil()
        }

        // Verifica se o computador ganhou
        if (verificarVitoria("O")) {
            desativarTodosBotoes()
            binding.textViewResultado.text = "Você perdeu!"
            return
        }

        // Verifica se há empate após a jogada do computador
        if (verificarEmpate()) {
            desativarTodosBotoes()
            binding.textViewResultado.text = "Empate!"
        }

        turnoJogador = true
    }

    // Funções de movimento do computador para cada nível de dificuldade
    private fun movimentoComputadorFacil() {
        var rX: Int
        var rY: Int
        while (true) {
            rX = Random.nextInt(0, 3)
            rY = Random.nextInt(0, 3)
            if (tabuleiro[rX][rY].isEmpty()) {
                tabuleiro[rX][rY] = "O"
                atualizarButton(rX, rY, "O")
                break
            }
        }
    }

    private fun movimentoComputadorMedio() {
        if (!tentarBloquear("X")) {
            movimentoComputadorFacil()
        }
    }

    private fun movimentoComputadorDificil() {
        if (!tentarGanhar("O")) {
            if (!tentarBloquear("X")) {
                movimentoComputadorFacil()
            }
        }
    }

    // Tenta bloquear a vitória do jogador ou ganhar o jogo
    private fun tentarBloquear(jogador: String): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (tabuleiro[i][j].isEmpty()) {
                    tabuleiro[i][j] = jogador
                    if (verificarVitoria(jogador)) {
                        tabuleiro[i][j] = "O"
                        atualizarButton(i, j, "O")
                        return true
                    } else {
                        tabuleiro[i][j] = ""
                    }
                }
            }
        }
        return false
    }

    private fun tentarGanhar(jogador: String): Boolean {
        return tentarBloquear(jogador)
    }

    // Atualiza o texto e o estado do botão correspondente no layout
    private fun atualizarButton(rX: Int, rY: Int, texto: String) {
        val posicao = rX * 3 + rY
        when (posicao) {
            0 -> {
                binding.buttonZero.text = texto
                binding.buttonZero.isEnabled = false
            }
            1 -> {
                binding.buttonUm.text = texto
                binding.buttonUm.isEnabled = false
            }
            2 -> {
                binding.buttonDois.text = texto
                binding.buttonDois.isEnabled = false
            }
            3 -> {
                binding.buttonTres.text = texto
                binding.buttonTres.isEnabled = false
            }
            4 -> {
                binding.buttonQuatro.text = texto
                binding.buttonQuatro.isEnabled = false
            }
            5 -> {
                binding.buttonCinco.text = texto
                binding.buttonCinco.isEnabled = false
            }
            6 -> {
                binding.buttonSeis.text = texto
                binding.buttonSeis.isEnabled = false
            }
            7 -> {
                binding.buttonSete.text = texto
                binding.buttonSete.isEnabled = false
            }
            8 -> {
                binding.buttonOito.text = texto
                binding.buttonOito.isEnabled = false
            }
        }
    }

    // Função para verificar se um jogador ganhou
    private fun verificarVitoria(jogador: String): Boolean {
        for (i in 0..2) {
            if (tabuleiro[i][0] == jogador && tabuleiro[i][1] == jogador && tabuleiro[i][2] == jogador) {
                return true
            }
        }
        for (i in 0..2) {
            if (tabuleiro[0][i] == jogador && tabuleiro[1][i] == jogador && tabuleiro[2][i] == jogador) {
                return true
            }
        }
        if (tabuleiro[0][0] == jogador && tabuleiro[1][1] == jogador && tabuleiro[2][2] == jogador) {
            return true
        }
        if (tabuleiro[0][2] == jogador && tabuleiro[1][1] == jogador && tabuleiro[2][0] == jogador) {
            return true
        }
        return false
    }

    // Função para verificar se há empate
    private fun verificarEmpate(): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (tabuleiro[i][j].isEmpty()) {
                    return false
                }
            }
        }
        return true
    }

    // Função para desativar todos os botões após o término do jogo
    private fun desativarTodosBotoes() {
        binding.buttonZero.isEnabled = false
        binding.buttonUm.isEnabled = false
        binding.buttonDois.isEnabled = false
        binding.buttonTres.isEnabled = false
        binding.buttonQuatro.isEnabled = false
        binding.buttonCinco.isEnabled = false
        binding.buttonSeis.isEnabled = false
        binding.buttonSete.isEnabled = false
        binding.buttonOito.isEnabled = false
    }

    // Função para reiniciar o jogo
    fun reiniciarJogo(view: View) {
        for (i in 0..2) {
            for (j in 0..2) {
                tabuleiro[i][j] = ""
            }
        }

        binding.buttonZero.text = ""
        binding.buttonZero.isEnabled = true
        binding.buttonUm.text = ""
        binding.buttonUm.isEnabled = true
        binding.buttonDois.text = ""
        binding.buttonDois.isEnabled = true
        binding.buttonTres.text = ""
        binding.buttonTres.isEnabled = true
        binding.buttonQuatro.text = ""
        binding.buttonQuatro.isEnabled = true
        binding.buttonCinco.text = ""
        binding.buttonCinco.isEnabled = true
        binding.buttonSeis.text = ""
        binding.buttonSeis.isEnabled = true
        binding.buttonSete.text = ""
        binding.buttonSete.isEnabled = true
        binding.buttonOito.text = ""
        binding.buttonOito.isEnabled = true

        binding.textViewResultado.text = ""
        turnoJogador = true
    }
}

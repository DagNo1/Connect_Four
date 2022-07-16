package connectfour

import kotlin.system.exitProcess

const val DONUT = 'o'
const val STAR = '*'
const val STARTING_SCORE = 0
const val DRAW_SCORE = 1
const val WIN_SCORE = 2

data class Player (val name: String, val symbol: Char, var score: Int)

val players = mutableListOf<Player>()
val board = MutableList(9) { mutableListOf<Char>()}
var row: Int = 6
var col: Int = 7

fun main() {
    val numberOfGames = registerGame()
    makeBoard()
    for (i in 1..numberOfGames) {
        if (i != 1) swapTurns(i)
        if (numberOfGames != 1) println("Game #$i")
        showBoard()
        playGame()
        clearBoard()
    }
    swapTurns()
    println("Game over!")
}
fun registerGame(): Int {
    println("Connect Four")
    println("First player's name: ")
    players.add(Player(readln(), DONUT, STARTING_SCORE))
    println("Second player's name: ")
    players.add(Player(readln(), STAR, STARTING_SCORE))
    var validInput: Boolean
    do {
        println("Set the board dimensions (Rows x Columns)\nPress Enter for default (6 x 7)")
        val input = readln().lowercase()
        validInput = inputValidation(input.replace("board", "").replace("\\s".toRegex(), ""))
    } while (!validInput)
    val gameStartMassage = "${players[0].name} VS ${players[1].name}\n" + "$row X $col board\n"
    while (true) {
        println("Do you want to play single or multiple games?\n" + "For a single game, input 1 or press Enter\n" + "Input a number of games:")
        val noOfGames = readln()
        if (noOfGames.isEmpty() || noOfGames == "1") {
            println(gameStartMassage + "Single game")
            return 1
        }
        val numberOfGames: Int
        try {
            numberOfGames = noOfGames.toInt()
        } catch (e: NumberFormatException) {
            println("Invalid input")
            continue
        }
        if (numberOfGames <= 0) println("Invalid input")
        else {
            println(gameStartMassage + "Total $noOfGames games")
            return numberOfGames
        }
    }
}
fun inputValidation(input: String): Boolean {
    if( input == "") return true
    //println(input)
    val regex = Regex("\\s*\\d+\\s*x\\s*\\d+\\s*")
    if(!regex.matches(input)){
        println("Invalid input")
        return false
    }
    val (x, y) = input.split('x')
    if(x.toInt() > 9 || x.toInt() < 5) {
        println("Board rows should be from 5 to 9")
        return false
    }
    if(y.toInt() > 9 || y.toInt() < 5) {
        println("Board columns should be from 5 to 9")
        return false
    }
    row = x.toInt()
    col = y.toInt()
    return true
}
fun swapTurns(i: Int = 0) {
    val zPlayer = players[0]
    players[0] = players[1]
    players[1] = zPlayer
    if (i % 2 != 0) println("Score\n${players[0].name}: ${players[0].score} ${players[1].name}: ${players[1].score}")
    else println("Score\n${players[1].name}: ${players[1].score} ${players[0].name}: ${players[0].score}")
}
fun makeBoard() {
    for (i in 0 until row){
        for (j in 0..col) {
            board[i].add('║')
            if (j != col) board[i].add(' ')
        }
    }
}
fun showBoard() {
    for(i in 1..col) {
        print(" $i")
    }
    println()
    for (i in 0 until row){
        for (j in 0..(col * 2)) {
            print(board[i][j])
            if (j == col * 2) println()
        }
    }
    print("╚")
    repeat(col - 1){
        print("═╩")
    }
    println("═╝")
}
fun playGame() {
    do {
        for (i in 0..1){
            var move: String
            do {
                println("${players[i].name}'s turn: ")
                move = readln()
                if (move == "end") {
                    println("Game over!")
                    exitProcess(0)
                }
                val validMove = moveValidation(move)
            } while (!validMove)
            makeMove(move.toInt() - 1, i)
            showBoard()
            if (gameWon(i)) {
                println("Player ${players[i].name} won")
                players[i].score += WIN_SCORE
                return
            }
            if (gameDraw()) {
                println("It is a draw")
                players[0].score += DRAW_SCORE
                players[1].score += DRAW_SCORE
                return
            }
        }
    }while (true)
}
fun moveValidation(input: String): Boolean {
    val value: Int
    try {
        value = input.toInt()
    }
    catch (e: NumberFormatException) {
        println("Incorrect column number")
        return false
    }
    if (value !in 1..col) {
        println("The column number is out of range (1 - $col)")
        return false
    }
    if (board[0][((value - 1) * 2) + 1] != ' ') {
        println("Column $value is full")
        return false
    }
    return true
}
fun makeMove(column: Int, player: Int) {
    for (i in row - 1 downTo 0) {
        if(board[i][(column * 2) + 1] == ' ') {
            board[i][(column * 2) + 1] = when (players[player].symbol) {
                DONUT -> DONUT
                STAR -> STAR
                else -> '='
            }
            return
        }
    }
}
fun gameWon(no: Int): Boolean {
    val vd = (col * 2)  // difference between 2 consecutive symbols aligned vertically
    val pdd = (col * 2) + 2 //difference between 2 consecutive symbols aligned along the primary diagonal
    val sdd = (col * 2) - 2 //difference between 2 consecutive symbols aligned along the secondary diagonal
    val horizontal = Regex("xzxzxzx")
    val vertical = Regex("x.{$vd}x.{$vd}x.{$vd}x")
    val pDiagonal = Regex("x.{$pdd}x.{$pdd}x.{$pdd}x")
    val sDiagonal = Regex("x.{$sdd}x.{$sdd}x.{$sdd}x")
    var fBS = "" // full Board String
    for (i in 0 until row) {
        fBS += board[i].joinToString("")
    }
    fBS = fBS.replace('║','z').replace(' ', 'z')
    fBS = if (players[no].symbol == DONUT) fBS.replace('o', 'x') else fBS.replace('*','x')
    return horizontal.containsMatchIn(fBS) || vertical.containsMatchIn(fBS) || pDiagonal.containsMatchIn(fBS) || sDiagonal.containsMatchIn(fBS)
}
fun gameDraw(): Boolean{
    for (i in 0 until col) {
        if(board[0][(i * 2) + 1] == ' ') return false
    }
    return true
}
fun clearBoard() {
    for (i in 0 until row){
        for (j in 0..col * 2) {
            if (j % 2 == 0)board[i][j] = ('║')
            else board[i][j] = (' ')
        }
    }
}
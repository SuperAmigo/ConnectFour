package connectfour

const val MIN_BOARD_SIZE = 5
const val MAX_BOARD_SIZE = 9
const val DEFAULT_BOARD_ROWS = 6
const val DEFAULT_BOARD_COLUMNS = 7
const val CHAR_LINE = '\u2550'  // ═
const val CHAR_STICK = '\u2551'  // ║
const val CHAR_LEFT_CORNER = '\u255A'  // ╚
const val CHAR_RIGHT_CORNER = '\u255D'  // ╩
const val CHAR_DOUBLE_CORNER = '\u2569'  // ╝

class ConnectFour {
    private val namePlayer1: String
    private val namePlayer2: String
    private var rows = DEFAULT_BOARD_ROWS
    private var columns = DEFAULT_BOARD_COLUMNS
    private var gamesNumber = 1
    private var currentGameNumber = 1
    private var isMultipleGame = false
    private var boardList = mutableListOf<MutableList<String>>()
    private var firstPlayerTurn = true
    private var scorePlayer1 = 0
    private var scorePlayer2 = 0

    init {
        println("Connect Four")
        println("First player's name:")
        namePlayer1 = readln()
        println("Second player's name:")
        namePlayer2 = readln()
        printBoardSizeQuestion()
        calculateBoardSize()
        printGamesNumberQuestion()
        calculateGamesNumber()
        println("$namePlayer1 VS $namePlayer2")
        println("$rows X $columns board")
        if (gamesNumber == 1) println("Single game")
        else println("Total $gamesNumber games")
        play()
    }

    private fun printBoardSizeQuestion() {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
    }

    private fun calculateBoardSize() {
        var isBoardOk = false
        while (!isBoardOk)
        {
            val readBoardSize = readln().replace("\\s".toRegex(), "").lowercase()
            if (readBoardSize.isEmpty()) {
                isBoardOk = true
            } else if (readBoardSize.matches(Regex("\\d+x\\d+"))) {
                rows = readBoardSize.split("x")[0].toInt()
                columns = readBoardSize.split("x")[1].toInt()

                when {
                    rows !in MIN_BOARD_SIZE..MAX_BOARD_SIZE -> {
                        println("Board rows should be from 5 to 9")
                        printBoardSizeQuestion()
                    }
                    columns !in MIN_BOARD_SIZE..MAX_BOARD_SIZE -> {
                        println("Board columns should be from 5 to 9")
                        printBoardSizeQuestion()
                    }
                    else -> isBoardOk = true
                }
            } else {
                println("Invalid input")
                printBoardSizeQuestion()
            }
        }
    }

    private fun printGamesNumberQuestion(){
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter")
        println("Input a number of games:")
    }

    private fun calculateGamesNumber(){
        while (true) {
            val readGamesNumber = readln()
            if (readGamesNumber.isEmpty()) {
                break
            } else if (readGamesNumber.matches(Regex("\\d+")) && readGamesNumber.toInt() > 0) {
                gamesNumber = readGamesNumber.toInt()
                isMultipleGame = gamesNumber > 1
                break
            } else {
                println("Invalid input")
                printGamesNumberQuestion()
            }
        }
    }

    private fun play() {
        var playerInput: Int
        boardList = mutableListOf()
        for (i in 0 until rows) boardList.add(MutableList(columns) { " " })
        if (gamesNumber > 1) {
            println("Game #$currentGameNumber")
            firstPlayerTurn = currentGameNumber % 2 == 1
        }
        drawBoard()
        do {
            playerInput = checkPlayerInput()
            if (playerInput >= 0) {
                val countCols = countColumnIsFull(rows, playerInput)
                if (countCols > rows - 1) {
                    println("Column $playerInput is full")
                } else {
                    if (firstPlayerTurn) boardList[rows - 1 - countCols][playerInput - 1] = "o"
                    else boardList[rows - 1 - countCols][playerInput - 1] = "*"
                    drawBoard()
                    playerInput = checkVictory()
                }
            }
        } while (playerInput > 0)
/*
 * -1 - game over
 * 0 - someone has won or draw in multiple game, but the game didn't end
 */
        if (playerInput == -1) {
            println("Game over!")
        }
        else if (playerInput == 0) {
            play()
        }
    }

    private fun drawBoard() {
        for (i in 1..columns) {
            print(" $i")
        }
        for (i in 0 until rows) {
            println()
            for (j in 0 until columns) {
                print("$CHAR_STICK${boardList[i][j]}")
            }
            print(CHAR_STICK)
        }
        print("\n$CHAR_LEFT_CORNER")
        for (i in 1 until columns) {
            print("$CHAR_LINE$CHAR_DOUBLE_CORNER")
        }
        println("$CHAR_LINE$CHAR_RIGHT_CORNER")
    }

    private fun checkPlayerInput(): Int{
        while (true) {
            println(if (firstPlayerTurn) "$namePlayer1's turn:" else "$namePlayer2's turn:")
            val input = readln()
            when {
                input == "end" -> return -1
                input.matches(Regex("\\d+")) -> {
                    if (input.toInt() in 1..columns) return input.toInt()
                    else println("The column number is out of range (1 - $columns)")
                }
                else -> println("Incorrect column number")
            }
        }
    }

    private fun countColumnIsFull (row: Int, turn: Int): Int {
        var countForColumn = 0
        for (i in 0 until row) {
            if (boardList[i][turn - 1] == "*" || boardList[i][turn - 1] == "o") countForColumn++
        }
        return countForColumn
    }

    private fun checkVictory(): Int {
        if (isBoardFull(rows, columns)) {
            println("It is a draw")
            return if (isMultipleGame) {
                scorePlayer1++
                scorePlayer2++
                println("Score")
                println("$namePlayer1: $scorePlayer1 $namePlayer2: $scorePlayer2")
                if (currentGameNumber < gamesNumber) {
                    currentGameNumber++
                    0
                } else -1
            } else -1
        } else if (checkHorizontalWinner() || checkVerticalWinner(columns)
            || checkDiagonalWinner(rows, columns) || checkReverseDiagonalWinner(rows, columns)) {
            if (firstPlayerTurn) {
                println("Player $namePlayer1 won")
                scorePlayer1 += 2
            } else {
                println("Player $namePlayer2 won")
                scorePlayer2 += 2
            }
            return if (isMultipleGame) {
                println("Score")
                println("$namePlayer1: $scorePlayer1 $namePlayer2: $scorePlayer2")
                if (currentGameNumber < gamesNumber) {
                    currentGameNumber++
                    0
                } else -1
            } else -1
        } else {
            firstPlayerTurn = !firstPlayerTurn
        }
        return 1
    }

    private fun checkHorizontalWinner(): Boolean {
        for (i in boardList.size - 1 downTo 0) {
            var count1 = 0
            var count2 = 0
            var currentDiscIsO = true
            for (j in boardList[i]) {
                when (j){
                    " " -> continue
                    "o" -> if (currentDiscIsO) count1++
                    else {
                        count1++
                        count2 = 0
                        currentDiscIsO = true
                    }
                    "*" -> if (!currentDiscIsO) count2++
                    else {
                        count2++
                        count1 = 0
                        currentDiscIsO = false
                    }
                }
                if (count1 == 4 || count2 == 4)
                    return true
            }
        }
        return false
    }

    private fun checkVerticalWinner(columns: Int): Boolean {
        for (i in 0 until columns) {
            var count1 = 0
            var count2 = 0
            var currentDiscIsO = true
            for (j in boardList.size - 1 downTo 0) {
                when (boardList[j][i]){
                    " " -> continue
                    "o" -> if (currentDiscIsO) count1++
                    else {
                        count1++
                        count2 = 0
                        currentDiscIsO = true
                    }
                    "*" -> if (!currentDiscIsO) count2++
                    else {
                        count2++
                        count1 = 0
                        currentDiscIsO = false
                    }
                }
                if (count1 == 4 || count2 == 4)
                    return true
            }
        }
        return false
    }

    private fun checkDiagonalWinner(rows: Int, columns: Int): Boolean {
        val diagonals = rows + columns - 1
        for (i in 0 until diagonals) {
            var count1 = 0
            var count2 = 0
            var currentDiscIsO = true
            if (i < rows) {
                var row = i
                var column = 0
                while (row >= 0) {
                    when (boardList[row--][column++]){
                        " " -> continue
                        "o" -> if (currentDiscIsO) count1++
                        else {
                            count1++
                            count2 = 0
                            currentDiscIsO = true
                        }
                        "*" -> if (!currentDiscIsO) count2++
                        else {
                            count2++
                            count1 = 0
                            currentDiscIsO = false
                        }
                    }
                    if (count1 == 4 || count2 == 4)
                        return true
                }
            } else {
                var row = rows - 1
                var colum = i - rows + 1
                while (colum < columns && row >= 0) {
                    when (boardList[row--][colum++]) {
                        " " -> continue
                        "o" -> if (currentDiscIsO) count1++
                        else {
                            count1++
                            count2 = 0
                            currentDiscIsO = true
                        }
                        "*" -> if (!currentDiscIsO) count2++
                        else {
                            count2++
                            count1 = 0
                            currentDiscIsO = false
                        }
                    }
                    if (count1 == 4 || count2 == 4)
                        return true
                }
            }
        }
        return false
    }

    private fun checkReverseDiagonalWinner(rows: Int, columns: Int): Boolean {
        val diagonals = rows + columns - 1
        for (i in 0 until diagonals) {
            var count1 = 0
            var count2 = 0
            var currentDiscIsO = true
            if (i < rows) {
                var row = i
                var column = columns - 1
                while (row >= 0 && column >= 0) {
                    when (boardList[row--][column--]){
                        " " -> continue
                        "o" -> if (currentDiscIsO) count1++
                        else {
                            count1++
                            count2 = 0
                            currentDiscIsO = true
                        }
                        "*" -> if (!currentDiscIsO) count2++
                        else {
                            count2++
                            count1 = 0
                            currentDiscIsO = false
                        }
                    }
                    if (count1 == 4 || count2 == 4)
                        return true
                }
            } else {
                var row = rows - 1
                var colum = diagonals - i - 1
                while (colum >=0 && rows > 0) {
                    when (boardList[row--][colum--]) {
                        " " -> continue
                        "o" -> if (currentDiscIsO) count1++
                        else {
                            count1++
                            count2 = 0
                            currentDiscIsO = true
                        }
                        "*" -> if (!currentDiscIsO) count2++
                        else {
                            count2++
                            count1 = 0
                            currentDiscIsO = false
                        }
                    }
                    if (count1 == 4 || count2 == 4)
                        return true
                }
            }
        }
        return false
    }

    private fun isBoardFull(rows: Int, columns: Int): Boolean {
        var count = 0
        for (i in boardList.indices) {
            for (j in boardList[i].indices) {
                if (boardList[i][j] == "*" || boardList[i][j] == "o")
                    count++
            }
        }
        return count == rows * columns
    }
}

fun main() {
    ConnectFour()
}
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
var mList = mutableListOf<MutableList<String>>()
var firstPlayerTurn = true

fun main() {
    var rows = DEFAULT_BOARD_ROWS
    var columns = DEFAULT_BOARD_COLUMNS
    val regexForBoardSize = Regex("\\d+x\\d+")
    var isBoardOk = false


    println("Connect Four")
    println("First player's name:")
    val namePlayer1 = readln()
    println("Second player's name:")
    val namePlayer2 = readln()
    printBoardSizeQuestion()

    while (!isBoardOk){
        val readBoardSize = readln().replace("\\s".toRegex(),"").lowercase()
        if (readBoardSize.isEmpty()){
            isBoardOk = true
        }
        else if (readBoardSize.matches(regexForBoardSize)) {
            rows = readBoardSize.split("x")[0].toInt()
            columns = readBoardSize.split("x")[1].toInt()

            if (rows < MIN_BOARD_SIZE || rows > MAX_BOARD_SIZE){
                println("Board rows should be from 5 to 9")
                printBoardSizeQuestion()
            } else if (columns < MIN_BOARD_SIZE || columns > MAX_BOARD_SIZE) {
                println("Board columns should be from 5 to 9")
                printBoardSizeQuestion()
            } else {
                isBoardOk = true
            }
        } else {
            println("Invalid input")
            printBoardSizeQuestion()
        }
    }
    println("$namePlayer1 VS $namePlayer2")
    println("$rows X $columns board")
    for (i in 0 until rows) {
        val innerList = MutableList(columns) { " " }
        mList.add(innerList)
    }
    drawBoard(row = rows, colum = columns)

    do {
        println(if (firstPlayerTurn) "$namePlayer1's turn:" else "$namePlayer2's turn:")
        var read = readln()
        while (!checkPlayerInput(read, rows, columns)){
            println(if (firstPlayerTurn) "$namePlayer1's turn:" else "$namePlayer2's turn:")
            read = readln()
        }

        if (read != "end") {
            val turn = read.toInt()
            val countCols = countColumnIsFull(rows, turn)
            if (countCols >  rows - 1) {
                println("Column $turn is full")
            } else {
                if (firstPlayerTurn) mList[rows - 1 - countCols][turn - 1] = "o"
                else mList[rows - 1 - countCols][turn - 1] = "*"
                firstPlayerTurn = !firstPlayerTurn
                drawBoard(rows, columns)
            }
        }
    } while (read != "end")

    println("Game over!")
}

fun printBoardSizeQuestion() {
    println("Set the board dimensions (Rows x Columns)")
    println("Press Enter for default (6 x 7)")
}

fun drawBoard(row: Int, colum: Int) {
    for (i in 1..colum) {
        print(" $i")
    }
    for (i in 0 until row) {
        println()
        for (j in 0 until colum) {
            print("$CHAR_STICK")
            print(mList[i][j])
        }
        print(CHAR_STICK)
    }
    print("\n$CHAR_LEFT_CORNER")
    for (i in 1 until colum) {
        print("$CHAR_LINE$CHAR_DOUBLE_CORNER")
    }
    println("$CHAR_LINE$CHAR_RIGHT_CORNER")
}

fun checkPlayerInput(input: String, rows: Int, columns: Int): Boolean{
    val turn: Int
    if (input == "end") return true
    if (!Regex("\\d+").matches(input)) {
        println("Incorrect column number")
        return false
    } else turn = input.toInt()
    return if (turn in 1..columns) {
        true
    } else {
        println("The column number is out of range (1 - $columns)")
        false
    }
}

fun countColumnIsFull (row: Int, turn: Int): Int {
    var countForColumn = 0
    for (i in 0 until row) {
        if (mList[i][turn - 1] == "*" || mList[i][turn - 1] == "o") countForColumn++
    }
    return countForColumn
}
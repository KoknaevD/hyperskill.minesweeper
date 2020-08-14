package minesweeper

import java.util.*

fun main() {
    Minefield.setup()
    startGame()
}

fun startGame() {

    game_loop@ while (true) {
        Minefield.printPlayersField()
        //println("mines ${Minefield.realFlagCount} miss ${Minefield.fakeFlagCount}")
        println("Set/unset mine marks (x y mine) or claim a cell as free (x y free):")
        val x = Minefield.scanner.nextInt() - 1
        val y = Minefield.scanner.nextInt() - 1
        val action = Minefield.scanner.next()!!

        if (action != "free" && action != "mine") continue
        if (!(x in 0 until Minefield.col && y in 0 until Minefield.row)) continue

        if (Minefield.firstTry) {
            Minefield.firstTry = false
            Minefield.placeMines(x, y)
            //Minefield.printRealField()
        }

        val realValue = Minefield.field[y][x]
        val playerValue = Minefield.playersField[y][x]

        if (action == "free") {
            when (realValue) {
                "X" -> {    //GAMEOVER
                    Minefield.printRealField()
                    println("You stepped on a mine and failed!")
                    break@game_loop
                }

                in "1".."9" -> {
                    Minefield.playersField[y][x] = realValue
                }

                "/" -> {
                    Minefield.openFreeCells(y, x)
                }
            }
        } else {
            when (playerValue) {
                //Если попал на цифру - ничего не делаем
                in "1".."9" -> {
                    println("There is a number here! $playerValue")
                }

                //Если указал точку - меняет на звездочку
                "." -> {
                    Minefield.playersField[y][x] = "*"
                    if (realValue == "X") {
                        Minefield.realFlagCount++
                    } else {
                        Minefield.fakeFlagCount++
                    }
                }

                "*" -> {
                    Minefield.playersField[y][x] = "."
                    if (realValue == "X") {
                        Minefield.realFlagCount--
                    } else {
                        Minefield.fakeFlagCount--
                    }
                }
            }
        }

        if (Minefield.fakeFlagCount == 0 && Minefield.realFlagCount == Minefield.mineCount) {
            println("Congratulations! You found all the mines!")
            break@game_loop
        }
    }
}

object Minefield {

    const val row = 9
    const val col = 9
    var mineCount = 0
    private var maxMine = 0
    val field: Array<Array<String>> = Array(row) { Array(col) {"/"} }
    var playersField: Array<Array<String>> = Array(row) { Array(col) {"."} }
    var realFlagCount = 0
    var fakeFlagCount = 0
    val scanner = Scanner(System.`in`)
    var firstTry = true

    fun setup () {

        println("How many mines do you want on the field?")
        val scanner = Scanner(System.`in`)
        maxMine = scanner.nextInt()

        //Если укажут больше чем само минное поле - оставляем 1 свободную ячейку, пусть угадывают =)
        if (maxMine > row * col) {
            maxMine =  row * col - 1
        }

    }

    fun placeMines(x: Int, y: Int) {
        //Обходим несколько раз для более равномерного распределения мин по полю
        while (mineCount < maxMine) {
            main_loop@ for (i in field.indices) {
                for (j in field[i].indices) {
                    if (field[i][j] == "X") {
                        continue
                    }

                    if (i == y && j == x) {
                        continue
                    }

                    if ((0..10).random() > 9) {
                        field[i][j] = "X"
                        mineCount++

                        if (mineCount == maxMine) {
                            break@main_loop
                        }
                    }
                }
            }
        }

        placeNumbers()
    }

    fun placeNumbers() {
        for (i in field.indices) {
            for (j in field[i].indices) {
                if (field[i][j] == "X") {
                    continue
                }

                val totalMines = countMines(i, j)
                if (totalMines > 0) {
                    field[i][j] = totalMines.toString()
                }
            }
        }
    }

    private fun countMines(i: Int, j:Int): Int {
        var result = 0

        val top = i != 0
        val left = j != 0
        val right = j != field[i].lastIndex
        val bot = i != field.lastIndex

        //Если не первая строчка - проверяем мину сверху
        if (top) {
            if (field[i - 1][j] == "X") {
                result += 1
            }

            //Если не первая строчка и не первая колонка - проверяем мину сверху слева
            if (left) {
                if (field[i - 1][j - 1] == "X") {
                    result += 1
                }
            }

            //Если не первая строчка и не последняя колонка - проверяем мину сверху справа
            if (right) {
                if (field[i - 1][j + 1] == "X") {
                    result += 1
                }
            }
        }

        //Если не последняя строчка - проверяем мину снизу
        if (bot) {
            if (field[i + 1][j] == "X") {
                result += 1
            }

            //Если не последняя строчка и не первая колонка - проверяем мину снизу слева
            if (left) {
                if (field[i + 1][j - 1] == "X") {
                    result += 1
                }
            }

            //Если не последняя строчка и не последняя колонка - проверяем мину снизу справа
            if (right) {
                if (field[i + 1][j + 1] == "X") {
                    result += 1
                }
            }
        }

        //Если не левая колонка - проверяем мину слева
        if (left) {
            if (field[i][j - 1] == "X") {
                result += 1
            }
        }

        //Если не правая колонка - проверяем мину справа
        if (right) {
            if (field[i][j + 1] == "X") {
                result += 1
            }
        }

        return result
    }

    fun printPlayersField() {
        print(" |")
        for (i in 1..col) {
            print(i)
        }
        println("|")

        print("—|")
        for (i in 1..col) {
            print("—")
        }
        println("|")

        for (i in playersField.indices) {
            print("${i+1}|")
            for (j in playersField[i].indices) {
//                if (playersField[i][j] == "X") {
//                    playersField[i][j] = "."
//                }

                print(playersField[i][j])
            }
            println("|")
        }

        print("—|")
        for (i in 1..col) {
            print("—")
        }
        println("|")
    }

    fun printRealField() {
        print(" |")
        for (i in 1..col) {
            print(i)
        }
        println("|")

        print("—|")
        for (i in 1..col) {
            print("—")
        }
        println("|")

        for (i in field.indices) {
            print("${i+1}|")
            for (j in field[i].indices) {
                print(field[i][j])
            }
            println("|")
        }

        print("—|")
        for (i in 1..col) {
            print("—")
        }
        println("|")
    }

    fun openFreeCells(y: Int, x:Int) {
        if (!(x in 0 until col && y in 0 until row)) return
        if (playersField[y][x] == "/") return

        if (field[y][x] in "1".."9") {
            playersField[y][x] = field[y][x]
        } else if (field[y][x] == "/") {
            playersField[y][x] = field[y][x]

            openFreeCells(y - 1, x - 1)
            openFreeCells(y - 1, x)
            openFreeCells(y - 1, x + 1)

            openFreeCells(y, x - 1)
            openFreeCells(y, x + 1)

            openFreeCells(y + 1, x - 1)
            openFreeCells(y + 1, x)
            openFreeCells(y + 1, x + 1)
        }

    }

}

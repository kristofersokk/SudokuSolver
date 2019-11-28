package sudokusolver.logic

import sudokusolver.*
import sudokusolver.Main.dim
import sudokusolver.Main.dim2
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants
import javax.swing.WindowConstants
import kotlin.math.ceil

object Logic {

    var solutionsAmount = 0
    var gamesAmount = 0
    private val collectionNames = arrayOf("pair", "triplet", "quadruplet", "quintuplet", "sextuplet", "septuplet")
    private val puzzles = ArrayList<Game>()
    private val solutions = ArrayList<Game>()

    private fun createAndShowGUI() {
        val frame = JFrame("Logic")
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        val label = JLabel("A new label")
        label.preferredSize = Dimension(400, 200)
        label.horizontalAlignment = SwingConstants.CENTER
        frame.contentPane.add(label, BorderLayout.CENTER)
        frame.pack()
        frame.isVisible = true
    }

    @Throws(FillingException::class, FinishedException::class)
    private fun continuousFilling(game: Game) {
        while (true) {
            //level 1
            if (hiddenSingle(game)) {
                continue
            } else {
                if (game.checkFilled()) {
                    throw FinishedException(game, true)
                }
            }
            if (nakedSingle(game)) {
                continue
            }

            //Level 2
            if (intersectionRemoval(game)) {
                continue
            }

            //Level 3
            if (filling3(game)) {
                continue
            }
            throw FinishedException(game, false)
        }
    }

    /**
     * @param unsolved the original boxes after reading in the information
     * updates puzzles, solutions, gamesAmount and solutionsAmount
     */
    fun startFilling(unsolved: Game): Game {
        initialLoading(unsolved)
        val solvedGame = unsolved.deepcopy()
        //println(solved.toString());
        try {
            continuousFilling(solvedGame)
        } catch (e: FillingException) {
            println("There was an error while solving, sudoku probably doesn't have a solution")
            e.printStackTrace()
            //            try {
//                Thread.sleep(20);
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//            }
            printSidewaysGrid(unsolved, e.game)
            e.game.messages.forEach(::println)
        } catch (finishedException: FinishedException) {
            puzzles.add(unsolved)
            solutions.add(solvedGame)
            gamesAmount++
            if (gamesAmount % 1000 == 0)
                println(gamesAmount)
            if (finishedException.filled) {
                solutionsAmount++
                solvedGame.isSolved = true
            }
        }
        return solvedGame
    }

    private fun initialLoading(game: Game) {
        game.allCells.forEach { cell ->
            if (cell.value > 0) {
                afterNumber(cell, game, false, null, false)
            }
        }
    }

    private fun filling3(game: Game): Boolean {
        var somethingDone = false
        //naked and hidden pairs, triplets, quarters
        game.level = 3
        //box
        for (box in game.boxes) {
            val lahtridInBoxes: List<Cell> = box.cells
            val emptyLahtrid = ArrayList<Cell>()
            for (lahter in lahtridInBoxes) {
                if (lahter.value == 0) {
                    emptyLahtrid.add(lahter)
                }
            }
            if (emptyLahtrid.size > 3) { //naked
                run {
                    var i = 2
                    while (i <= ceil(emptyLahtrid.size / 2f.toDouble())) {
                        combinations@ for (a in combinations(emptyLahtrid.size, i)) { //you've got the combinations, what now?
                            val chosenOnes = ArrayList<Cell>()
                            for (index in a) {
                                chosenOnes.add(emptyLahtrid[index])
                            }
                            //finding naked pairs, triplets, ...
                            val numbers = HashSet<Int>()
                            for (cell in chosenOnes) {
                                for (index in cell.numbers) {
                                    numbers.add(index)
                                    if (numbers.size > i) { //not a hidden pair
                                        continue@combinations
                                    }
                                }
                            }
                            //remove the numbers from the other lahtrid in the same box
                            lahtrid@ for (cell in emptyLahtrid) {
                                for (lahter1 in chosenOnes) {
                                    if (cell == lahter1) {
                                        continue@lahtrid
                                    }
                                }
                                for (usedNumber in numbers) {
                                    if (cell.numbers.remove(usedNumber)) {
                                        somethingDone = true
                                    }
                                }
                            }
                            //TODO check if in a row or a column
                            if (somethingDone) {
                                game.addMessage("box ${box.index}: naked ${getCollectionName(i)} with numbers $numbers")
                                return true
                            }
                        }
                        i++
                    }
                }
                //hidden
//get unfilled numbers
                val unfillednumbers = numberSet
                for (lahter in box.cells) {
                    unfillednumbers.remove(lahter.value)
                }
                val numberscontainedinlahters: ArrayList<ArrayList<Int>?> = ArrayList()
                for (i in 0 until dim2) {
                    numberscontainedinlahters.add(ArrayList())
                }
                for (index in emptyLahtrid.indices) {
                    val lahter = emptyLahtrid[index]
                    if (lahter.value == 0) {
                        for (numbrid in lahter.numbers) {
                            numberscontainedinlahters[numbrid - 1]!!.add(index)
                        }
                    }
                }
                val numberlist = numberList
                //removes unneeded empty lists (of those numbers that are already filled)
                var b = 0
                while (b < numberscontainedinlahters.size) {
                    if (numberscontainedinlahters[b]!!.isEmpty()) {
                        numberscontainedinlahters.removeAt(b)
                        numberlist.remove(b)
                    } else {
                        b++
                    }
                }
                //taking combinations of fillable numbers and analyzing if total lahters needed by them is more than the amount of numbers being analyzed
                var i = 2
                while (i < Math.ceil(emptyLahtrid.size / 2f.toDouble())) {
                    for (a in combinations(unfillednumbers.size, i)) { //you've got the combinations, what now?
                        val chosenOnesIndexes = ArrayList(a)
                        //collecting lahters needed
                        val chosenLahtersIndexes = HashSet<Int>()
                        for (query in chosenOnesIndexes) chosenLahtersIndexes.addAll(numberscontainedinlahters[query]!!)
                        //not a hidden pair, triplet,...
                        if (chosenLahtersIndexes.size > chosenOnesIndexes.size) continue
                        val chosenLahtrid = ArrayList<Cell>()
                        for (index in chosenLahtersIndexes) chosenLahtrid.add(emptyLahtrid[index])
                        val chosenOnes: HashSet<Int> = HashSet()
                        for (arv in chosenOnesIndexes) chosenOnes.add(numberlist[arv])
                        //remove other numbers from chosen lahters
                        for (lahter in chosenLahtrid) {
                            for (arv in numberlist) if (!chosenOnes.contains(arv)) if (lahter.numbers.remove(arv)) somethingDone = true
                        }
                        //TODO add check if inside a row or a column
//remove the numbers from the other lahtrid in the same row
                        lahtrid@ for (lahter in emptyLahtrid) {
                            for (lahter1 in chosenLahtrid) {
                                if (lahter == lahter1) continue@lahtrid
                            }
                            for (usedNumber in chosenOnes) if (lahter.numbers.remove(usedNumber)) somethingDone = true
                        }
                        if (somethingDone) {
                            game.addMessage("box ${box.index}: hidden ${getCollectionName(i)} with numbers $chosenOnes")
                            return true
                        }
                    }
                    i++
                }
            }
        }
        //TODO add puzzle level to the game, after that use filtering to filter out more complex
//rows
        for (rowIndex in 1..dim2) {
            val lahtridInRow = game row rowIndex
            val emptyLahtrid = ArrayList<Cell>()
            for (lahter in lahtridInRow) {
                if (lahter.value == 0) {
                    emptyLahtrid.add(lahter)
                }
            }
            if (emptyLahtrid.size > 3) { //naked
                run {
                    var i = 2
                    while (i <= Math.ceil(emptyLahtrid.size / 2f.toDouble())) {
                        combinations@ for (a in combinations(emptyLahtrid.size, i)) { //you've got the combinations, what now?
                            val chosenOnes = ArrayList<Cell>()
                            for (index in a) {
                                chosenOnes.add(emptyLahtrid[index])
                            }
                            //finding naked pairs, triplets, ...
                            val numbers = HashSet<Int>()
                            for (lahter in chosenOnes) {
                                for (index in lahter.numbers) {
                                    numbers.add(index)
                                    if (numbers.size > i) { //not a hidden pair
                                        continue@combinations
                                    }
                                }
                            }
                            //remove the numbers from the other lahtrid in the same rowIndex
                            lahtrid@ for (lahter in emptyLahtrid) {
                                for (lahter1 in chosenOnes) {
                                    if (lahter == lahter1) {
                                        continue@lahtrid
                                    }
                                }
                                for (usedNumber in numbers) {
                                    if (lahter.numbers.remove(usedNumber)) {
                                        somethingDone = true
                                    }
                                }
                            }
                            if (somethingDone) {
                                game.addMessage("rowIndex " + rowIndex + ": naked " + getCollectionName(i) + " with numbers " + numbers.toString())
                                return true
                            }
                        }
                        i++
                    }
                }
                //hidden
//get unfilled numbers
                val unfillednumbers = numberSet
                for (lahter in game row rowIndex) {
                    unfillednumbers.remove(lahter.value)
                }
                val numberscontainedinlahters: ArrayList<ArrayList<Int>> = ArrayList()
                for (i in 0 until dim2) {
                    numberscontainedinlahters.add(ArrayList())
                }
                for (index in emptyLahtrid.indices) {
                    val lahter = emptyLahtrid[index]
                    if (lahter.value == 0) {
                        for (numbrid in lahter.numbers) {
                            numberscontainedinlahters[numbrid - 1].add(index)
                        }
                    }
                }
                val numberlist = numberList
                //removes unneeded empty lists (of those numbers that are already filled)
                var b = 0
                while (b < numberscontainedinlahters.size) {
                    if (numberscontainedinlahters[b].isEmpty()) {
                        numberscontainedinlahters.removeAt(b)
                        numberlist.removeAt(b)
                    } else {
                        b++
                    }
                }
                //taking combinations of fillable numbers and analyzing if total lahters needed by them is more than the amount of numbers being analyzed
                var i = 2
                while (i < Math.ceil(emptyLahtrid.size / 2f.toDouble())) {
                    for (a in combinations(unfillednumbers.size, i)) { //you've got the combinations, what now?
                        val chosenOnesIndexes = ArrayList(a as ArrayList<Int>)
                        //collecting lahters needed
                        val chosenLahtersIndexes = HashSet<Int>()
                        for (query in chosenOnesIndexes) chosenLahtersIndexes.addAll(numberscontainedinlahters[query]!!)
                        //not a hidden pair, triplet,...
                        if (chosenLahtersIndexes.size > chosenOnesIndexes.size) continue
                        val chosenLahtrid = ArrayList<Cell>()
                        for (index in chosenLahtersIndexes) chosenLahtrid.add(emptyLahtrid[index])
                        val chosenOnes = HashSet<Int>()
                        for (arv in chosenOnesIndexes) chosenOnes.add(numberlist[arv])
                        //remove other numbers from chosen lahters
                        for (lahter in chosenLahtrid) {
                            for (arv in numberlist) if (!chosenOnes.contains(arv)) if (lahter.numbers.remove(arv)) somethingDone = true
                        }
                        //remove the numbers from the other lahtrid in the same rowIndex
                        lahtrid@ for (lahter in emptyLahtrid) {
                            for (lahter1 in chosenLahtrid) {
                                if (lahter == lahter1) continue@lahtrid
                            }
                            for (usedNumber in chosenOnes) if (lahter.numbers.remove(usedNumber)) somethingDone = true
                        }
                        //TODO check if inside a box
                        if (somethingDone) {
                            game.addMessage("rowIndex " + rowIndex.toString() + ": hidden " + getCollectionName(i) + " with numbers " + chosenOnes.toString())
                            return true
                        }
                    }
                    i++
                }
            }
        }
        //columns
        for (column in 1..dim2) {
            val lahtridInColumn = game column column
            val emptyLahtrid = ArrayList<Cell>()
            for (lahter in lahtridInColumn) {
                if (lahter.value == 0) {
                    emptyLahtrid.add(lahter)
                }
            }
            if (emptyLahtrid.size > 3) { //naked
                run {
                    var i = 2
                    while (i <= Math.ceil(emptyLahtrid.size / 2f.toDouble())) {
                        combinations@ for (a in combinations(emptyLahtrid.size, i)) { //you've got the combinations, what now?
                            val chosenOnes = ArrayList<Cell>()
                            for (index in a) {
                                chosenOnes.add(emptyLahtrid[index])
                            }
                            //finding naked pairs, triplets, ...
                            val numbers = HashSet<Int>()
                            for (cell in chosenOnes) {
                                for (index in cell.numbers) {
                                    numbers.add(index)
                                    if (numbers.size > i) { //not a hidden pair
                                        continue@combinations
                                    }
                                }
                            }
                            //remove the numbers from the other lahtrid in the same row
                            lahtrid@ for (cell in emptyLahtrid) {
                                for (lahter1 in chosenOnes) {
                                    if (cell == lahter1) {
                                        continue@lahtrid
                                    }
                                }
                                for (usedNumber in numbers) {
                                    if (cell.numbers.remove(usedNumber)) {
                                        somethingDone = true
                                    }
                                }
                            }
                            if (somethingDone) {
                                game.addMessage("column " + column + ": naked " + getCollectionName(i) + " with numbers " + numbers.toString())
                                return true
                            }
                        }
                        i++
                    }
                }
                //hidden
//get unfilled numbers
                val unfillednumbers = numberSet
                for (lahter in game column column) {
                    unfillednumbers.remove(lahter.value)
                }
                val numberscontainedinlahters: ArrayList<ArrayList<Int>> = ArrayList()
                for (i in 0 until dim2) {
                    numberscontainedinlahters.add(ArrayList())
                }
                for (index in emptyLahtrid.indices) {
                    val lahter = emptyLahtrid[index]
                    if (lahter.value == 0) {
                        for (numbrid in lahter.numbers) {
                            numberscontainedinlahters[numbrid - 1].add(index)
                        }
                    }
                }
                val numberlist = numberList
                //removes unneeded empty lists (of those numbers that are already filled)
                var b = 0
                while (b < numberscontainedinlahters.size) {
                    if (numberscontainedinlahters[b].isEmpty()) {
                        numberscontainedinlahters.removeAt(b)
                        numberlist.removeAt(b)
                    } else {
                        b++
                    }
                }
                //taking combinations of fillable numbers and analyzing if total lahters needed by them is more than the amount of numbers being analyzed
                var i = 2
                while (i < Math.ceil(emptyLahtrid.size / 2f.toDouble())) {
                    for (a in combinations(unfillednumbers.size, i)) { //you've got the combinations, what now?
                        val chosenOnesIndexes = ArrayList(a)
                        //collecting lahters needed
                        val chosenLahtersIndexes = HashSet<Int>()
                        for (query in chosenOnesIndexes) chosenLahtersIndexes.addAll(numberscontainedinlahters[query]!!)
                        //not a hidden pair, triplet,...
                        if (chosenLahtersIndexes.size > chosenOnesIndexes.size) continue
                        val chosenLahtrid = ArrayList<Cell>()
                        for (index in chosenLahtersIndexes) chosenLahtrid.add(emptyLahtrid[index])
                        val chosenOnes: HashSet<Int> = HashSet()
                        for (arv in chosenOnesIndexes) chosenOnes.add(numberlist[arv])
                        //remove other numbers from chosen lahters
                        for (lahter in chosenLahtrid) for (arv in numberSet) if (!chosenOnes.contains(arv)) if (lahter.numbers.remove(arv)) somethingDone = true
                        //remove the numbers from the other lahtrid in the same row
                        lahtrid@ for (lahter in emptyLahtrid) {
                            for (lahter1 in chosenLahtrid) if (lahter == lahter1) continue@lahtrid
                            for (usedNumber in chosenOnes) if (lahter.numbers.remove(usedNumber)) somethingDone = true
                        }
                        //TODO check if inside a box
                        if (somethingDone) {
                            game.addMessage("column " + column.toString() + ": hidden " + getCollectionName(i) + " with numbers " + chosenOnes.toString())
                            return true
                        }
                    }
                    i++
                }
            }
        }
        return somethingDone
    }

    /**
     * @param cell
     * @param game
     * @param showMessage
     * @param messageSuffix
     * @param findNextOnes look for boxes with only one choice remaining, ca 3x times faster for computer, less intuitive for people, but more intuitive, if trying to fill
     */
    internal fun afterNumber(cell: Cell, game: Game, showMessage: Boolean, messageSuffix: String?, findNextOnes: Boolean) {
        val (x, y) = cell.globalCoords
        if (showMessage) game.addMessage("x:$x y:$y value: ${cell.value}$messageSuffix")
        (cell.box(game).cells + (game row y) + (game column x)).distinct().applyAll {
            numbers.remove(cell.value)
        }

        if (findNextOnes) {
            listOf(cell.box(game).cells, game row y, game column x).forEach {
                val unfilled = it.filter { !it.filled }
                if (unfilled.size <= 3) {
                    unfilled.applyAll {
                        if (value == 0 && numbers.size == 1) {
                            val onlyValue = numbers.first()
                            game.addMessage("x:$x y:$y value: ${cell.value}$messageSuffix naked single")
                            value = onlyValue
                            afterNumber(this, game, showMessage, null, findNextOnes)
                        }
                    }
                }
            }
        }
    }

    private fun getCollectionName(a: Int): String {
        return if (a <= collectionNames.size + 1) collectionNames[a - 2] else "$a-uplet"
    }

    private fun printSidewaysGrid(game1: Game, game2: Game) {
        for (kastY in 1..dim) {
            for (lahterY in 1..dim) {
                for (kastX in 1..dim) {
                    for (lahterX in 1..dim) {
                        val value = game1.getCell(Coords(kastX, kastY), Coords(lahterX, lahterY)).value
                        print(when {
                            value == 0 -> " *"
                            value > 9 -> value
                            else -> " $value"
                        })
                    }
                    print(" ")
                }
                print("        ")
                for (kastX in 1..dim) {
                    for (lahterX in 1..dim) {
                        val value = game2.getCell(Coords(kastX, kastY), Coords(lahterX, lahterY)).value
                        print(when {
                            value == 0 -> " *"
                            value > 9 -> value
                            else -> " $value"
                        })
                    }
                    print(" ")
                }
                println()
            }
            println()
        }
    }

    /**
     * @param printSolutionSteps  --
     * @param printOnlyUnsolvable --
     */
    fun printSolutions(printSolutionSteps: Boolean, printOnlyUnsolvable: Boolean) {
        for (i in 0 until gamesAmount) {
            if (!printOnlyUnsolvable || printOnlyUnsolvable && !solutions[i].isSolved) {
                if (printSolutionSteps) {
                    for (j in solutions[i].messages.indices) {
                        val message = solutions[i].messages[j]
                        if (!message.contains("Logic")) {
                            val index = j.toString()
                            print("${" ".repeat(5 - index.length)}$index: ")
                        }
                        println(message)
                    }
                }
                println()
                printSidewaysGrid(puzzles[i], solutions[i])
                println()
                println("----------------------------------------------------------------")
            }
        }
    }

    val numberSet: HashSet<Int>
        get() = (1..dim2).toHashSet()

    val numberList: MutableList<Int>
        get() = (1..dim2).toMutableList()

    private fun combinations(sample: Int, maxLen: Int): ArrayList<ArrayList<Int>> {

        fun _combsFindNext(last: ArrayList<Int>, lastInt: Int, results: ArrayList<ArrayList<Int>>, sample: Int, givenLevel: Int, maxLevel: Int): ArrayList<ArrayList<Int>> {
            var level = givenLevel
            level++
            if (level < maxLevel) {
                for (i in lastInt + 1 until sample) {
                    val send = copyList(last)
                    send.add(i)
                    _combsFindNext(send, i, results, sample, level, maxLevel)
                }
            } else {
                results.add(last)
            }
            return results
        }

        val results = ArrayList<ArrayList<Int>>()
        return _combsFindNext(ArrayList(), -1, results, sample, -1, maxLen)
    }

}

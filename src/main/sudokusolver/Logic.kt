package sudokusolver

import sudokusolver.Main.dim
import sudokusolver.Main.dim2
import sudokusolver.Main.findNextOnes
import sudokusolver.Main.sameBoxFirst
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
        val frame = JFrame("Loogika")
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        val label = JLabel("A new label")
        label.preferredSize = Dimension(400, 200)
        label.horizontalAlignment = SwingConstants.CENTER
        frame.contentPane.add(label, BorderLayout.CENTER)
        frame.pack()
        frame.isVisible = true
    }

    /**
     * @param game game
     * @throws FillingException FinishedException
     */
    @Throws(FillingException::class, FinishedException::class)
    private fun continuousFilling(game: Game) {
        while (true) {
            if (fillingNormal(game)) {
                continue
            } else {
                if (game.checkFilled()) {
                    throw FinishedException(game, true)
                }
            }
            if (filling1(game)) {
                continue
            }
            if (filling2(game)) {
                continue
            }
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
    fun startFilling(unsolved: Game) {
        val solvedGame = unsolved.copy()
        solvedGame.allCells.forEach { println(it.value) }
        initialLoading(solvedGame)
        println(solvedGame.toPrettyString(true))
        //println(solved.toString());
        try {
            continuousFilling(solvedGame)
        } catch (e: FillingException) {
            e.printStackTrace()
            //            try {
//                Thread.sleep(20);
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//            }
            e.game.allCells.forEach {
                println("value: ${it.value}, numbers: ${it.numbers}")
            }
            printSidewaysGrid(unsolved, e.game)
            for (message in e.game.messages) {
                println(message)
            }
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
            //            println(solved.kastid[0]);
//            println(solved);
        }
    }

    private fun initialLoading(game: Game) {
        game.allCells.forEach { cell ->
            if (cell.value > 0) {
                afterNumber(cell, game, false, null, findNextOnes)
            }
        }
    }

    @Throws(FillingException::class)
    private fun fillingNormal(game: Game): Boolean {
        var somethingDone = false
        //TODO p''ra ringi, et ta otsiks sama numbrit teistest kastidest enne, siis teisi numbreid
        game.level = 1
        //box
        var boxIndex = 1
        while (boxIndex <= dim2) {
            somethingDone = false
            val box = game.getBox(boxIndex)
            box.cells.checkForErrors(game)
            //get the frequency of possibilities of numbers 1-dim2
            val possibilities = box.cells.possibilities()
            //check the different numbers and the number of possibilities
            possibilities.forEach { (number, possibleCells) ->
                if (possibleCells.isNotEmpty() && number in box) {
                    throw FillingException("box $boxIndex already has number $number, but possibilities exist", game)
                }
                if (possibleCells.size == 1) {
                    val cell = possibleCells.first()
                    cell.value = number
                    afterNumber(cell, game, true, " (box $boxIndex)", findNextOnes)
                    somethingDone = true
                    //                    println("box",j+1);
                } else if (possibleCells.isEmpty() && number !in box) {
                    throw FillingException("box $boxIndex doesn't have number $number and no possibilities exist", game)
                }
            }
            if (!somethingDone) {
                boxIndex++
            }
        }
        if (somethingDone) {
            return true
        }
        //rows
        for (row in 1..dim2) {
            val availableSlots = ArrayList<ArrayList<Int>>()
            val lahtridInRow = game row row
            val olemasNumbrid = ArrayList<Int>()
            for (j in 1..dim2) {
                availableSlots.add(ArrayList())
            }
            for (j in 0 until dim2) {
                val cell = lahtridInRow[j]
                val numbers: HashSet<*> = cell.numbers
                if (numbers.size == 0 && cell.value == 0) {
                    val globalCoords: GlobalCoords = cell.globalCoords
                    throw FillingException("lahter at x: ${globalCoords.x}, y: ${globalCoords.y} is empty and without possibilities", game)
                }
            }
            //get the frequency of possibilities of numbers 1-dim2
            for (j in 0 until dim2) {
                val cell = lahtridInRow[j]
                for (a in cell.numbers) {
                    availableSlots[a - 1].add(j + 1)
                }
                olemasNumbrid.add(cell.value)
            }
            //check the different numbers and the number of the possibilities
            for (j in 0 until dim2) {
                val arvud = availableSlots[j]
                if (arvud.size > 0 && j + 1 in olemasNumbrid) {
                    throw FillingException("row " + row + " already has number " + (j + 1) + ", but possibilities exist", game)
                }
                if (arvud.size == 1) {
                    val cell = game.getCell(GlobalCoords(arvud[0], row))
                    cell.value = j + 1
                    afterNumber(cell, game, true, " (row $row)", findNextOnes)
                    return true
                    //                    println("row",j+1);
                } else if (arvud.size == 0 && j + 1 !in olemasNumbrid) {
                    throw FillingException("row " + row + " doesn't have number " + (j + 1) + " and no possibilities exist", game)
                }
            }
        }
        //columns
        for (column in 1..dim2) {
            val availableSlots = ArrayList<ArrayList<Int>>()
            val lahtridInColumn = game column column
            val olemasNumbrid = ArrayList<Int>()
            for (j in 1..dim2) {
                availableSlots.add(ArrayList())
            }
            for (j in 0 until dim2) {
                val cell = lahtridInColumn[j]
                val numbers: HashSet<*> = cell.numbers
                if (numbers.size == 0 && cell.value == 0) {
                    val globalCoords: GlobalCoords = cell.globalCoords
                    throw FillingException("lahter at x: ${globalCoords.x}, y: ${globalCoords.y} is empty and without possibilities", game)
                }
            }
            //get the frequency of possibilities of numbers 1-dim2
            for (j in 0 until dim2) {
                val lahter = lahtridInColumn[j]
                for (a in lahter.numbers) {
                    availableSlots[a - 1].add(j + 1)
                }
                olemasNumbrid.add(lahter.value)
            }
            //check the different numbers and the number of the possibilities
            for (j in 0 until dim2) {
                val arvud = availableSlots[j]
                if (arvud.size > 0 && olemasNumbrid.contains(j + 1)) {
                    throw FillingException("column " + column + " already has number " + (j + 1) + ", but possibilities exist", game)
                }
                if (arvud.size == 1) {
                    val lahter = game.getCell(GlobalCoords(column, arvud[0]))
                    lahter.value = j + 1
                    afterNumber(lahter, game, true, " (column $column)", findNextOnes)
                    return true
                    //                    println("column",j+1);
                } else if (arvud.size == 0 && !olemasNumbrid.contains(j + 1)) {
                    throw FillingException("column " + column + " doesn't have number " + (j + 1) + " and no possibilities exist", game)
                }
            }
        }
        return false
    }

    @Throws(FillingException::class)
    private fun filling1(game: Game): Boolean {
        game.level = 1
        for (box in game.boxes) {
            for (cell in box.cells) {
                val numbers = cell.numbers
                if (numbers.size == 0 && cell.value == 0) {
                    val globalCoords = cell.globalCoords
                    throw FillingException("cell at x: " + globalCoords.x + ", y: " + globalCoords.y + " is empty and without possibilities", game)
                } else if (numbers.size == 1) {
                    cell.value = numbers.toTypedArray()[0]
                    numbers.clear()
                    afterNumber(cell, game, true, " (only choice)", findNextOnes)
                    return true
                }
            }
        }
        return false
    }

    @Throws(FillingException::class)
    private fun filling2(game: Game): Boolean {
        var somethingDone = false
        game.level = 2
        //box
        for (box in game.boxes) {
            val availableSlots = ArrayList<ArrayList<Int>>()
            val lahtridInBoxes: List<Cell> = box.cells
            val olemasNumbrid = ArrayList<Int>()
            for (j in 1..dim2) {
                availableSlots.add(ArrayList())
            }
            for (j in 0 until dim2) {
                val cell = lahtridInBoxes[j]
                val numbers = cell.numbers
                if (numbers.size == 0 && cell.value == 0) {
                    val globalCoords = cell.globalCoords
                    throw FillingException("lahter at x: ${globalCoords.x}, y: ${globalCoords.y} is empty and without possibilities", game)
                }
            }
            //get the frequency of possibilities of numbers 1-dim2
            for (j in 0 until dim2) {
                val lahter = lahtridInBoxes[j]
                for (a in lahter.numbers) {
                    availableSlots[a - 1].add(j + 1)
                }
                olemasNumbrid.add(lahter.value)
            }
            //check the different numbers and the number of the possibilities
            for (j in 0 until dim2) {
                val arvud = availableSlots[j]
                if (arvud.size > 0 && j + 1 in olemasNumbrid) {
                    throw FillingException("box ${box.index} already has number ${j + 1}, but possibilities exist", game)
                }
                if (arvud.size in 2..dim) { //check number of rows
                    val moduleRow = HashSet<Int>()
                    for (arv in arvud) {
                        moduleRow.add(arv.partition)
                    }
                    // candidate line (row) is active
                    if (moduleRow.size == 1) {
                        val moduleList = ArrayList(moduleRow)
                        for (lahter in game.row((box.locY - 1) * dim + moduleList[0])) {
                            if (lahter.box != box) {
                                if (lahter.numbers.remove(j + 1)) {
                                    somethingDone = true
                                    //                                    println("vs3 row", (box.locY-1)*3+moduleList.get(0));
                                }
                            }
                        }
                        if (somethingDone) {
                            game.addMessage("row ${(box.locY - 1) * dim + moduleList[0]}: ${j + 1}")
                            return true
                        }
                    }
                    //check number of columns
                    val moduleColumn = HashSet<Int>()
                    for (arv in arvud) {
                        moduleColumn.add(arv.modulo)
                    }
                    // candidate line (column) is active
                    if (moduleColumn.size == 1) {
                        val moduleList = ArrayList(moduleColumn)
                        for (lahter in game.column((box.locX - 1) * dim + moduleList[0])) {
                            if (lahter.box != box) {
                                if (lahter.numbers.remove(j + 1)) {
                                    somethingDone = true
                                    //                                    println("vs3 column", (box.locX-1)*3+moduleList.get(0));
                                }
                            }
                        }
                        if (somethingDone) {
                            game.addMessage("column " + ((box.locX - 1) * dim + moduleList[0]) + ": " + (j + 1))
                            return true
                        }
                    }
                }
            }
        }
        //rows
        for (row in 1..dim2) {
            val availableSlots = ArrayList<ArrayList<Int>>()
            val lahtridInRow = game row row
            val olemasNumbrid = ArrayList<Int>()
            for (j in 1..dim2) {
                availableSlots.add(ArrayList())
            }
            for (j in 0 until dim2) {
                val cell = lahtridInRow[j]
                val numbers: HashSet<*> = cell.numbers
                if (numbers.size == 0 && cell.value == 0) {
                    val globalCoords = cell.globalCoords
                    throw FillingException("lahter at x: ${globalCoords.x}, y: ${globalCoords.y} is empty and without possibilities", game)
                }
            }
            //get the frequency of possibilities of numbers 1-dim2
            for (j in 0 until dim2) {
                val lahter = lahtridInRow[j]
                for (a in lahter.numbers) {
                    availableSlots[a - 1].add(j + 1)
                }
                olemasNumbrid.add(lahter.value)
            }
            //check the different numbers and the number of the possibilities
            for (j in 0 until dim2) {
                val arvud = availableSlots[j]
                if (arvud.size > 0 && olemasNumbrid.contains(j + 1)) {
                    throw FillingException("row " + row + " already has number " + (j + 1) + ", but possibilities exist", game)
                }
                if (arvud.size >= 2 && arvud.size <= dim) {
                    val module = HashSet<Int>()
                    for (arv in arvud) {
                        module.add(arv.partition)
                    }
                    // multiple lines is active, one number in a row resides in one box
                    if (module.size == 1) {
                        val moduleList = ArrayList(module)
                        for (lahter in game.getBox(moduleList[0], row.partition).cells) {
                            if (lahter.locY != row.modulo) {
                                if (lahter.numbers.remove(j + 1)) {
                                    somethingDone = true
                                    //                                    println("vs4 row", j+1);
                                }
                            }
                        }
                        if (somethingDone) {
                            game.addMessage("row " + row + ": " + (j + 1))
                            return true
                        }
                    }
                }
            }
        }
        //columns
        for (column in 1..dim2) {
            val availableSlots = ArrayList<ArrayList<Int>>()
            val lahtridInColumn = game column column
            val olemasNumbrid = ArrayList<Int>()
            for (j in 1..dim2) {
                availableSlots.add(ArrayList())
            }
            for (j in 0 until dim2) {
                val cell = lahtridInColumn[j]
                val numbers: HashSet<*> = cell.numbers
                if (numbers.size == 0 && cell.value == 0) {
                    val globalCoords = cell.globalCoords
                    throw FillingException("lahter at x: ${globalCoords.x}, y: ${globalCoords.y} is empty and without possibilities", game)
                }
            }
            //get the frequency of possibilities of numbers 1-dim2
            for (j in 0 until dim2) {
                val lahter = lahtridInColumn[j]
                for (a in lahter.numbers) {
                    availableSlots[a - 1].add(j + 1)
                }
                olemasNumbrid.add(lahter.value)
            }
            //check the different numbers and the number of the possibilities
            for (j in 0 until dim2) {
                val arvud = availableSlots[j]
                if (arvud.size > 0 && olemasNumbrid.contains(j + 1)) {
                    throw FillingException("column " + column + " already has number " + (j + 1) + ", but possibilities exist", game)
                }
                if (arvud.size in 2..dim) {
                    val module = HashSet<Int>()
                    for (arv in arvud) {
                        module.add(arv.partition)
                    }
                    //multiple lines is active
                    if (module.size == 1) {
                        val moduleList = ArrayList(module)
                        for (lahter in game.getBox(column.partition, moduleList[0]).cells) {
                            if (lahter.locX != column.modulo) {
                                if (lahter.numbers.remove(j + 1)) {
                                    somethingDone = true
                                    //                                    println("vs4 column", j+1);
                                }
                            }
                        }
                        if (somethingDone) {
                            game.addMessage("column $column: ${j + 1}")
                            return true
                        }
                    }
                }
            }
        }
        return false
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
                            for (lahter in chosenOnes) {
                                for (index in lahter.numbers) {
                                    numbers.add(index)
                                    if (numbers.size > i) { //not a hidden pair
                                        continue@combinations
                                    }
                                }
                            }
                            //remove the numbers from the other lahtrid in the same box
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
                            for (lahter in chosenOnes) {
                                for (index in lahter.numbers) {
                                    numbers.add(index)
                                    if (numbers.size > i) { //not a hidden pair
                                        continue@combinations
                                    }
                                }
                            }
                            //remove the numbers from the other lahtrid in the same row
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
    private fun afterNumber(cell: Cell, game: Game, showMessage: Boolean, messageSuffix: String?, findNextOnes: Boolean) {
        val (x, y) = cell.globalCoords
        if (showMessage) game.addMessage("x:$x y:$y value: ${cell.value}${messageSuffix ?: ""}")
        if (findNextOnes) {
            (cell.box.cells + (game row y) + (game column x))
                .distinct()
                .filter {
                    if (it.value == 0 && it.numbers.size == 1) {
                        val onlyValue = it.numbers.first()
                        it.value = onlyValue
                        afterNumber(it, game, true, null, findNextOnes)
                        return@filter true
                    }
                    false
                }
        }
    }

    private fun getCollectionName(a: Int): String {
        return if (a <= collectionNames.size + 1) collectionNames[a - 2] else "$a-uplet"
    }

    private fun copyList(`in`: ArrayList<Int>): ArrayList<Int> {
        return ArrayList(`in`)
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

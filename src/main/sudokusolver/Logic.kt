package sudokusolver

import sudokusolver.Main.dim2
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingConstants
import javax.swing.WindowConstants
import kotlin.collections.ArrayList

object Logic {

    var solutionsAmount = 0
    var gamesAmount = 0
    private val collectionNames = arrayOf("pair", "triplet", "quadruplet", "quintuplet", "sextuplet", "septuplet")
    private val puzzles = ArrayList<Game>()
    private val solutions = ArrayList<Game>()
    private val allNumbers = numberList

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
                if (checkFilled(game)) {
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
     * @param unsolved the original kastid after reading in the information
     * updates puzzles, solutions, gamesAmount and solutionsAmount
     */
    fun startFilling(unsolved: Game) {
        val solved = copyGame(unsolved)
        initialLoading(solved)
        //println(solved.toString());
        try {
            continuousFilling(solved)
        } catch (e: FillingException) {
            e.printStackTrace()
            //            try {
//                Thread.sleep(20);
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//            }
            printSidewaysGrid(unsolved, e.game)
            for (message in e.game.messages) {
                println(message!!)
            }
        } catch (e: FinishedException) {
            puzzles.add(unsolved)
            solutions.add(solved)
            gamesAmount++
            if (gamesAmount % 1000 == 0) println(gamesAmount)
            prevLevel = -1 //to print the first logic level
            if (e.filled) {
                solutionsAmount++
                solved.solved = true
            }
            //            println(solved.kastid[0]);
//            println(solved);
        }
    }

    private fun initialLoading(game: Game) {
        for (box in game.boxes) {
            for (lahter in box.cells) {
                if (lahter.getValue() !== 0) {
                    afterNumber(lahter, game, false, null, Main.findNextOnes)
                }
            }
        }
    }

    @Throws(FillingException::class)
    private fun fillingNormal(game: Game): Boolean {
        var somethingDone = false
        //TODO p''ra ringi, et ta otsiks sama numbrit teistest kastidest enne, siis teisi numbreid
        setLevel(1)
        //box
        if (Main.sameBoxFirst) {
            var i = 1
            while (i <= dim2) {
                somethingDone = false
                val box = getKast(i, game)
                val availableSlots = ArrayList<ArrayList<Int>>()
                val lahtridInBoxes: ArrayList<Cell> = arrayToArrayList(box.cells)
                val olemasNumbrid = ArrayList<Int>()
                for (j in 1..dim2) {
                    availableSlots.add(ArrayList())
                }
                for (j in 0 until dim2) {
                    val lahter = lahtridInBoxes[j]
                    val numbers: HashSet<*> = lahter.numbers
                    if (numbers.size == 0 && lahter.getValue() === 0) {
                        val XY: IntArray = lahter.getXyOnBoard()
                        throw FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game)
                    }
                }
                //get the frequency of possibilities of numbers 1-dim2
                for (j in 0 until dim2) {
                    val lahter = lahtridInBoxes[j]
                    for (a in lahter.numbers) {
                        availableSlots[a - 1].add(j + 1)
                    }
                    olemasNumbrid.add(lahter.getValue())
                }
                //check the different numbers and the number of possibilities
                for (j in 0 until dim2) {
                    val arvud = availableSlots[j]
                    if (arvud.size > 0 && olemasNumbrid.contains(j + 1)) {
                        throw FillingException("box " + i + " already has number " + (j + 1) + ", but possibilities exist", game)
                    }
                    if (arvud.size == 1) {
                        val lahter = box.cells[arvud[0] - 1]
                        lahter.setValue(j + 1)
                        afterNumber(lahter, game, true, " (box $i)", Main.findNextOnes)
                        somethingDone = true
                        //                    println("box",j+1);
                    } else if (arvud.size == 0 && !olemasNumbrid.contains(j + 1)) {
                        throw FillingException("box " + i + " doesn't have number " + (j + 1) + " and no possibilities exist", game)
                    }
                }
                if (!somethingDone) {
                    i++
                }
            }
        } else {
            for (box in game.boxes) {
                val availableSlots = ArrayList<ArrayList<Int>>()
                val lahtridInBoxes: ArrayList<Cell> = arrayToArrayList(box.cells)
                val olemasNumbrid = ArrayList<Int>()
                for (j in 1..dim2) {
                    availableSlots.add(ArrayList())
                }
                for (j in 0 until dim2) {
                    val lahter = lahtridInBoxes[j]
                    val numbers: HashSet<*> = lahter.numbers
                    if (numbers.size == 0 && lahter.getValue() === 0) {
                        val XY: IntArray = lahter.getXyOnBoard()
                        throw FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game)
                    }
                }
                //get the frequency of possibilities of numbers 1-dim2
                for (j in 0 until dim2) {
                    val lahter = lahtridInBoxes[j]
                    for (a in lahter.numbers) {
                        availableSlots[a - 1].add(j + 1)
                    }
                    olemasNumbrid.add(lahter.getValue())
                }
                //check the different numbers and the number of the possibilities
                for (j in 0 until dim2) {
                    val arvud = availableSlots[j]
                    if (arvud.size > 0 && olemasNumbrid.contains(j + 1)) {
                        throw FillingException("box " + getIntFromLocalLocs(intArrayOf(box.locX, box.locY)) + " already has number " + (j + 1) + ", but possibilities exist", game)
                    }
                    if (arvud.size == 1) {
                        val lahter = box.cells[arvud[0] - 1]
                        lahter.setValue(j + 1)
                        afterNumber(lahter, game, true, " (box " + getIntFromLocalLocs(intArrayOf(box.locX, box.locY)) + ")", Main.findNextOnes)
                        return true
                        //                    println("box",j+1);
                    } else if (arvud.size == 0 && !olemasNumbrid.contains(j + 1)) {
                        throw FillingException("box " + getIntFromLocalLocs(intArrayOf(box.locX, box.locY)) + " doesn't have number " + (j + 1) + " and no possibilities exist", game)
                    }
                }
            }
        }
        if (somethingDone) {
            return true
        }
        //rows
        for (row in 1..dim2) {
            val availableSlots = ArrayList<ArrayList<Int>>()
            val lahtridInRow = getRow(row, game)
            val olemasNumbrid = ArrayList<Int>()
            for (j in 1..dim2) {
                availableSlots.add(ArrayList())
            }
            for (j in 0 until dim2) {
                val lahter = lahtridInRow[j]
                val numbers: HashSet<*> = lahter.numbers
                if (numbers.size == 0 && lahter.getValue() === 0) {
                    val XY: IntArray = lahter.getXyOnBoard()
                    throw FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game)
                }
            }
            //get the frequency of possibilities of numbers 1-dim2
            for (j in 0 until dim2) {
                val lahter = lahtridInRow[j]
                for (a in lahter.numbers) {
                    availableSlots[a - 1].add(j + 1)
                }
                olemasNumbrid.add(lahter.getValue())
            }
            //check the different numbers and the number of the possibilities
            for (j in 0 until dim2) {
                val arvud = availableSlots[j]
                if (arvud.size > 0 && olemasNumbrid.contains(j + 1)) {
                    throw FillingException("row " + row + " already has number " + (j + 1) + ", but possibilities exist", game)
                }
                if (arvud.size == 1) {
                    val lahter = getLahter(arvud[0], row, game)
                    lahter.setValue(j + 1)
                    afterNumber(lahter, game, true, " (row $row)", Main.findNextOnes)
                    return true
                    //                    println("row",j+1);
                } else if (arvud.size == 0 && !olemasNumbrid.contains(j + 1)) {
                    throw FillingException("row " + row + " doesn't have number " + (j + 1) + " and no possibilities exist", game)
                }
            }
        }
        //columns
        for (column in 1..dim2) {
            val availableSlots = ArrayList<ArrayList<Int>>()
            val lahtridInColumn = getColumn(column, game)
            val olemasNumbrid = ArrayList<Int>()
            for (j in 1..dim2) {
                availableSlots.add(ArrayList())
            }
            for (j in 0 until dim2) {
                val lahter = lahtridInColumn[j]
                val numbers: HashSet<*> = lahter.numbers
                if (numbers.size == 0 && lahter.getValue() === 0) {
                    val XY: IntArray = lahter.getXyOnBoard()
                    throw FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game)
                }
            }
            //get the frequency of possibilities of numbers 1-dim2
            for (j in 0 until dim2) {
                val lahter = lahtridInColumn[j]
                for (a in lahter.numbers) {
                    availableSlots[a - 1].add(j + 1)
                }
                olemasNumbrid.add(lahter.getValue())
            }
            //check the different numbers and the number of the possibilities
            for (j in 0 until dim2) {
                val arvud = availableSlots[j]
                if (arvud.size > 0 && olemasNumbrid.contains(j + 1)) {
                    throw FillingException("column " + column + " already has number " + (j + 1) + ", but possibilities exist", game)
                }
                if (arvud.size == 1) {
                    val lahter = getLahter(column, arvud[0], game)
                    lahter.setValue(j + 1)
                    afterNumber(lahter, game, true, " (column $column)", Main.findNextOnes)
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
        setLevel(1)
        for (box in game.boxes) {
            for (lahter in box.cells) {
                val numbers = lahter.numbers
                if (numbers.size == 0 && lahter.getValue() === 0) {
                    val XY: IntArray = lahter.getXyOnBoard()
                    throw FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game)
                } else if (numbers.size == 1) {
                    lahter.setValue(numbers.toTypedArray()[0])
                    numbers.clear()
                    afterNumber(lahter, game, true, " (only choice)", Main.findNextOnes)
                    return true
                }
            }
        }
        return false
    }

    @Throws(FillingException::class)
    private fun filling2(game: Game): Boolean {
        var somethingDone = false
        setLevel(2)
        //box
        for (box in game.boxes) {
            val availableSlots = ArrayList<ArrayList<Int>>()
            val lahtridInBoxes: ArrayList<Cell> = arrayToArrayList(box.cells)
            val olemasNumbrid = ArrayList<Int>()
            for (j in 1..dim2) {
                availableSlots.add(ArrayList())
            }
            for (j in 0 until dim2) {
                val lahter = lahtridInBoxes[j]
                val numbers: HashSet<*> = lahter.numbers
                if (numbers.size == 0 && lahter.getValue() === 0) {
                    val XY: IntArray = lahter.getXyOnBoard()
                    throw FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game)
                }
            }
            //get the frequency of possibilities of numbers 1-dim2
            for (j in 0 until dim2) {
                val lahter = lahtridInBoxes[j]
                for (a in lahter.numbers) {
                    availableSlots[a - 1].add(j + 1)
                }
                olemasNumbrid.add(lahter.getValue())
            }
            //check the different numbers and the number of the possibilities
            for (j in 0 until dim2) {
                val arvud = availableSlots[j]
                if (arvud.size > 0 && olemasNumbrid.contains(j + 1)) {
                    throw FillingException("box " + getIntFromLocalLocs(intArrayOf(box.locX, box.locY)) + " already has number " + (j + 1) + ", but possibilities exist", game)
                }
                if (arvud.size >= 2 && arvud.size <= Main.dim) { //check number of rows
                    val moduleRow = HashSet<Int>()
                    for (arv in arvud) {
                        moduleRow.add(arv.partition)
                    }
                    // candidate line (row) is active
                    if (moduleRow.size == 1) {
                        val moduleList = ArrayList(moduleRow)
                        for (lahter in getRow((box.locY - 1) * Main.dim + moduleList[0], game)) {
                            if (lahter.box != box) {
                                if (lahter.numbers.remove(j + 1)) {
                                    somethingDone = true
                                    //                                    println("vs3 row", (box.locY-1)*3+moduleList.get(0));
                                }
                            }
                        }
                        if (somethingDone) {
                            game.addMessage("row " + ((box.locY - 1) * Main.dim + moduleList[0]) + ": " + (j + 1))
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
                        for (lahter in getColumn((box.locX - 1) * Main.dim + moduleList[0], game)) {
                            if (lahter.box != box) {
                                if (lahter.numbers.remove(j + 1)) {
                                    somethingDone = true
                                    //                                    println("vs3 column", (box.locX-1)*3+moduleList.get(0));
                                }
                            }
                        }
                        if (somethingDone) {
                            game.addMessage("column " + ((box.locX - 1) * Main.dim + moduleList[0]) + ": " + (j + 1))
                            return true
                        }
                    }
                }
            }
        }
        //rows
        for (row in 1..dim2) {
            val availableSlots = ArrayList<ArrayList<Int>>()
            val lahtridInRow = getRow(row, game)
            val olemasNumbrid = ArrayList<Int>()
            for (j in 1..dim2) {
                availableSlots.add(ArrayList())
            }
            for (j in 0 until dim2) {
                val lahter = lahtridInRow[j]
                val numbers: HashSet<*> = lahter.numbers
                if (numbers.size == 0 && lahter.getValue() === 0) {
                    val XY: IntArray = lahter.getXyOnBoard()
                    throw FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game)
                }
            }
            //get the frequency of possibilities of numbers 1-dim2
            for (j in 0 until dim2) {
                val lahter = lahtridInRow[j]
                for (a in lahter.numbers) {
                    availableSlots[a - 1].add(j + 1)
                }
                olemasNumbrid.add(lahter.getValue())
            }
            //check the different numbers and the number of the possibilities
            for (j in 0 until dim2) {
                val arvud = availableSlots[j]
                if (arvud.size > 0 && olemasNumbrid.contains(j + 1)) {
                    throw FillingException("row " + row + " already has number " + (j + 1) + ", but possibilities exist", game)
                }
                if (arvud.size >= 2 && arvud.size <= Main.dim) {
                    val module = HashSet<Int>()
                    for (arv in arvud) {
                        module.add(arv.partition)
                    }
                    // multiple lines is active, one number in a row resides in one box
                    if (module.size == 1) {
                        val moduleList = ArrayList(module)
                        for (lahter in getKast(moduleList[0], row.partition, game).cells) {
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
            val lahtridInColumn = getColumn(column, game)
            val olemasNumbrid = ArrayList<Int>()
            for (j in 1..dim2) {
                availableSlots.add(ArrayList())
            }
            for (j in 0 until dim2) {
                val lahter = lahtridInColumn[j]
                val numbers: HashSet<*> = lahter.numbers
                if (numbers.size == 0 && lahter.getValue() === 0) {
                    val XY: IntArray = lahter.getXyOnBoard()
                    throw FillingException("lahter at x: " + XY[0] + ", y: " + XY[1] + " is empty and without possibilities", game)
                }
            }
            //get the frequency of possibilities of numbers 1-dim2
            for (j in 0 until dim2) {
                val lahter = lahtridInColumn[j]
                for (a in lahter.numbers) {
                    availableSlots[a - 1].add(j + 1)
                }
                olemasNumbrid.add(lahter.getValue())
            }
            //check the different numbers and the number of the possibilities
            for (j in 0 until dim2) {
                val arvud = availableSlots[j]
                if (arvud.size > 0 && olemasNumbrid.contains(j + 1)) {
                    throw FillingException("column " + column + " already has number " + (j + 1) + ", but possibilities exist", game)
                }
                if (arvud.size >= 2 && arvud.size <= Main.dim) {
                    val module = HashSet<Int>()
                    for (arv in arvud) {
                        module.add(arv.partition)
                    }
                    //multiple lines is active
                    if (module.size == 1) {
                        val moduleList = ArrayList(module)
                        for (lahter in getKast(column.partition, moduleList[0], game).cells) {
                            if (lahter.locX != column.modulo) {
                                if (lahter.numbers.remove(j + 1)) {
                                    somethingDone = true
                                    //                                    println("vs4 column", j+1);
                                }
                            }
                        }
                        if (somethingDone) {
                            game.addMessage("column " + column + ": " + (j + 1))
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
        setLevel(3)
        //box
        for (box in game.boxes) {
            val lahtridInBoxes: ArrayList<Cell> = arrayToArrayList(box.cells)
            val emptyLahtrid = ArrayList<Cell>()
            for (lahter in lahtridInBoxes) {
                if (lahter.getValue() === 0) {
                    emptyLahtrid.add(lahter)
                }
            }
            if (emptyLahtrid.size > 3) { //naked
                run {
                    var i = 2
                    while (i <= Math.ceil(emptyLahtrid.size / 2f.toDouble())) {
                        combinations@ for (a in combinations(emptyLahtrid.size, i)) { //you've got the combinations, what now?
                            val chosenOnes = ArrayList<Cell>()
                            for (index in a as ArrayList<Int>) {
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
                                game.addMessage("box " + getIntFromLocalLocs(intArrayOf(box.locX, box.locY)).toString() + ": naked " + getCollectionName(i) + " with numbers " + numbers.toString())
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
                    unfillednumbers.remove(lahter.getValue())
                }
                val numberscontainedinlahters: ArrayList<ArrayList<Int>?> = ArrayList<Any?>()
                for (i in 0 until dim2) {
                    numberscontainedinlahters.add(ArrayList())
                }
                for (index in emptyLahtrid.indices) {
                    val lahter = emptyLahtrid[index]
                    if (lahter.getValue() === 0) {
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
                        val chosenOnes: HashSet<Int?> = HashSet<Any?>()
                        for (arv in chosenOnesIndexes) chosenOnes.add(numberlist[arv])
                        //remove other numbers from chosen lahters
                        for (lahter in chosenLahtrid) {
                            for (arv in allNumbers) if (!chosenOnes.contains(arv)) if (lahter.numbers.remove(arv)) somethingDone = true
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
                            game.addMessage("box " + getIntFromLocalLocs(intArrayOf(box.locX, box.locY)).toString() + ": hidden " + getCollectionName(i) + " with numbers " + chosenOnes.toString())
                            return true
                        }
                    }
                    i++
                }
            }
        }
        //TODO add puzzle level to the game, after that use filtering to filter out more complex
//rows
        for (row in 1..dim2) {
            val lahtridInRow = getRow(row, game)
            val emptyLahtrid = ArrayList<Cell>()
            for (lahter in lahtridInRow) {
                if (lahter.getValue() === 0) {
                    emptyLahtrid.add(lahter)
                }
            }
            if (emptyLahtrid.size > 3) { //naked
                run {
                    var i = 2
                    while (i <= Math.ceil(emptyLahtrid.size / 2f.toDouble())) {
                        combinations@ for (a in combinations(emptyLahtrid.size, i)) { //you've got the combinations, what now?
                            val chosenOnes = ArrayList<Cell>()
                            for (index in a as ArrayList<Int>) {
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
                                game.addMessage("row " + row + ": naked " + getCollectionName(i) + " with numbers " + numbers.toString())
                                return true
                            }
                        }
                        i++
                    }
                }
                //hidden
//get unfilled numbers
                val unfillednumbers = numberSet
                for (lahter in getRow(row, game)) {
                    unfillednumbers.remove(lahter.getValue())
                }
                val numberscontainedinlahters: ArrayList<ArrayList<Int>?> = ArrayList<Any?>()
                for (i in 0 until dim2) {
                    numberscontainedinlahters.add(ArrayList())
                }
                for (index in emptyLahtrid.indices) {
                    val lahter = emptyLahtrid[index]
                    if (lahter.getValue() === 0) {
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
                            for (arv in allNumbers) if (!chosenOnes.contains(arv)) if (lahter.numbers.remove(arv)) somethingDone = true
                        }
                        //remove the numbers from the other lahtrid in the same row
                        lahtrid@ for (lahter in emptyLahtrid) {
                            for (lahter1 in chosenLahtrid) {
                                if (lahter == lahter1) continue@lahtrid
                            }
                            for (usedNumber in chosenOnes) if (lahter.numbers.remove(usedNumber)) somethingDone = true
                        }
                        //TODO check if inside a box
                        if (somethingDone) {
                            game.addMessage("row " + row.toString() + ": hidden " + getCollectionName(i) + " with numbers " + chosenOnes.toString())
                            return true
                        }
                    }
                    i++
                }
            }
        }
        //columns
        for (column in 1..dim2) {
            val lahtridInColumn = getColumn(column, game)
            val emptyLahtrid = ArrayList<Cell>()
            for (lahter in lahtridInColumn) {
                if (lahter.getValue() === 0) {
                    emptyLahtrid.add(lahter)
                }
            }
            if (emptyLahtrid.size > 3) { //naked
                run {
                    var i = 2
                    while (i <= Math.ceil(emptyLahtrid.size / 2f.toDouble())) {
                        combinations@ for (a in combinations(emptyLahtrid.size, i)) { //you've got the combinations, what now?
                            val chosenOnes = ArrayList<Cell>()
                            for (index in a as ArrayList<Int>) {
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
                for (lahter in getColumn(column, game)) {
                    unfillednumbers.remove(lahter.getValue())
                }
                val numberscontainedinlahters: ArrayList<ArrayList<Int>?> = ArrayList<Any?>()
                for (i in 0 until dim2) {
                    numberscontainedinlahters.add(ArrayList())
                }
                for (index in emptyLahtrid.indices) {
                    val lahter = emptyLahtrid[index]
                    if (lahter.getValue() === 0) {
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
                        val chosenOnes: HashSet<Int?> = HashSet<Any?>()
                        for (arv in chosenOnesIndexes) chosenOnes.add(numberlist[arv])
                        //remove other numbers from chosen lahters
                        for (lahter in chosenLahtrid) for (arv in allNumbers) if (!chosenOnes.contains(arv)) if (lahter.numbers.remove(arv)) somethingDone = true
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
     * @param antudCell
     * @param game
     * @param showXY
     * @param message
     * @param findNextOnes look for lahtrid with only one choice remaining, ca 3x times faster for computer, less intuitive for people, but more intuitive, if trying to fill
     */
    private fun afterNumber(antudCell: Cell, game: Game, showXY: Boolean, message: String?, findNextOnes: Boolean) {
        val XY = getXYFromLocs(arrayOf(intArrayOf(antudCell.box.locX, antudCell.box.locY), intArrayOf(antudCell.locX, antudCell.locY)))
        val x = XY[0]
        val y = XY[1]
        val value = antudCell.getValue()
        val nextLahtrid = ArrayList<Cell>()
        if (showXY) game.addMessage("x:$x y:$y value: $value$message")
        //remove possible numbers in the same row, column and the 3x3 box
//the same 3x3 box
        for (lahter in getLahter(x, y, game).box.cells) {
            lahter.numbers.remove(value)
            if (findNextOnes) {
                if (lahter.numbers.size == 1 && lahter.getValue() === 0) {
                    val onlyValue = lahter.numbers.toTypedArray()[0]
                    game.addMessage(" (box $y, only choice $onlyValue)")
                    lahter.setValue(onlyValue)
                    nextLahtrid.add(lahter)
                }
            }
        }
        //same row
        for (lahter in getRow(y, game)) {
            lahter.numbers.remove(value)
            if (findNextOnes) {
                if (lahter.numbers.size == 1 && lahter.getValue() === 0) {
                    val onlyValue = lahter.numbers.toTypedArray()[0]
                    game.addMessage(" (row $y, only choice $onlyValue)")
                    lahter.setValue(onlyValue)
                    nextLahtrid.add(lahter)
                }
            }
        }
        //same column
        for (lahter in getColumn(x, game)) {
            lahter.numbers.remove(value)
            if (findNextOnes) {
                if (lahter.numbers.size == 1 && lahter.getValue() === 0) {
                    val onlyValue = lahter.numbers.toTypedArray()[0]
                    game.addMessage(" (column $x, only choice $onlyValue)")
                    lahter.setValue(onlyValue)
                    nextLahtrid.add(lahter)
                }
            }
        }
        for (lahter in nextLahtrid) {
            afterNumber(lahter, game, true, lahter.message, findNextOnes)
        }
    }

    private fun getCollectionName(a: Int): String {
        return if (a <= collectionNames.size + 1) collectionNames[a - 2] else "$a-uplet"
    }

    private fun setLevel(level: Int) {
        Logic.level = level
    }

    private fun copyGame(game: Game): Game {
        val clone = newBoxes()
        for (i in 0 until dim2) {
            for (j in 0 until dim2) {
                clone[i]!!.cells[j].setValue(game.boxes[i].cells[j].getValue())
                clone[i]!!.cells[j].numbers = copyHashSet(game.boxes[i].cells[j].numbers)
            }
        }
        return Game(clone, game.messages)
    }

    private fun copyList(`in`: ArrayList<Int>): ArrayList<Int> {
        return ArrayList(`in`)
    }

    private fun copyHashSet(set: HashSet<Int>): HashSet<Int?> {
        return HashSet<Any?>(Arrays.asList(*set.toArray(arrayOf<Int>())))
    }

    private fun printSidewaysGrid(game1: Game, game2: Game) {
        for (kastY in 1..Main.dim) {
            for (lahterY in 1..Main.dim) {
                for (kastX in 1..Main.dim) {
                    for (lahterX in 1..Main.dim) {
                        var value: Int
                        value = getKast(kastX, kastY, game1).getLahter(lahterX, lahterY).getValue()
                        if (value == 0) {
                            printSpaceBefore("*")
                        } else if (value > 9) {
                            printNoSpace(value)
                        } else {
                            printSpaceBefore(value)
                        }
                    }
                    print(" ")
                }
                print("        ")
                for (kastX in 1..Main.dim) {
                    for (lahterX in 1..Main.dim) {
                        var value: Int
                        value = getKast(kastX, kastY, game2).getLahter(lahterX, lahterY).getValue()
                        if (value == 0) {
                            printSpaceBefore("*")
                        } else if (value > 9) {
                            printNoSpace(value)
                        } else {
                            printSpaceBefore(value)
                        }
                    }
                    print(" ")
                }
                newLine()
            }
            newLine()
        }
    }

    /**
     * @param printSolutionSteps  --
     * @param printOnlyUnsolvable --
     */
    fun printSolutions(printSolutionSteps: Boolean, printOnlyUnsolvable: Boolean) {
        for (i in 0 until gamesAmount) {
            if (!printOnlyUnsolvable || printOnlyUnsolvable && !solutions[i].solved) {
                if (printSolutionSteps) {
                    for (j in solutions[i].messages.indices) {
                        val message = solutions[i].messages[j]
                        if (!message.contains("Logic")) {
                            val index = Integer.toString(j)
                            print(String(CharArray(5 - index.length)).replace('\u0000', ' ') + index + ": ")
                        }
                        println(message)
                    }
                }
                newLine()
                printSidewaysGrid(puzzles[i], solutions[i])
                newLine()
                println("----------------------------------------------------------------")
            }
        }
    }

    /**
     * @param file for example: /src/level4-10000.txt
     * @return string of contents
     */
    @Throws(FileNotFoundException::class)
    private fun readTextFromFile(file: File): ArrayList<String> {
        return try {
            val scan = Scanner(file)
            val lines = ArrayList<String>()
            while (scan.hasNextLine()) {
                lines.add(scan.nextLine())
            }
            //            println("File read successfully!");
            lines
        } catch (e: FileNotFoundException) {
            throw FileNotFoundException()
        }
    }

    /**
     * @param locX 1-3
     * @param locY 1-3
     * @return Box
     */
    private fun genKast(locX: Int, locY: Int): Box {
        val lahtrid = arrayOfNulls<Cell>(dim2)
        for (i in 0 until dim2) { //dim2 korda tee Lahter
            lahtrid[i] = Cell(i % Main.dim + 1, i / Main.dim + 1, numberSet, 0, null)
        }
        val box = Box(locX, locY, lahtrid, Main.dim)
        for (lahter in lahtrid) {
            lahter!!.box = box
        }
        return box
    }

    val numberSet: HashSet<Int>
        get() = (1..dim2).toHashSet()

    val numberList: List<Int>
        get() = (1..dim2).toList()

    private fun getRow(y: Int, game: Game): ArrayList<Cell> {
        val lahtrid = ArrayList<Cell>()
        for (box in game.boxes) {
            for (lahter in box.cells) {
                if (box.locY == (y - 1) / Main.dim + 1 && lahter.locY == (y - 1) % Main.dim + 1) {
                    lahtrid.add(lahter)
                }
            }
        }
        return lahtrid
    }

    private fun getColumn(x: Int, game: Game): ArrayList<Cell> {
        val lahtrid = ArrayList<Cell>()
        for (box in game.boxes) {
            for (lahter in box.cells) {
                if (box.locX == (x - 1) / Main.dim + 1 && lahter.locX == (x - 1) % Main.dim + 1) {
                    lahtrid.add(lahter)
                }
            }
        }
        return lahtrid
    }

    private fun checkFilled(game: Game): Boolean {
        for (box in game.boxes) {
            for (lahter in box.cells) {
                if (lahter.getValue() === 0) {
                    return false
                }
            }
        }
        return true
    }

    private fun combinations(sample: Int, maxLen: Int): ArrayList<Int> {

        fun _combsFindNext(last: ArrayList<Int>, lastInt: Int, results: ArrayList<Int>, sample: Int, level: Int, maxLevel: Int): ArrayList<Int> {
            var level = level
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

        val results = ArrayList<Int>()
        return _combsFindNext(ArrayList(), -1, results, sample, -1, maxLen)
    }



    /**
     * @param locX 1-3
     * @param locY 1-3
     * @return Box
     */
    private fun getKast(locX: Int, locY: Int, game: Game): Box {
        return game.boxes[(locY - 1) * Main.dim + locX - 1]
    }

    private fun getKast(loc: IntArray, game: Game): Box {
        return game.boxes[(loc[1] - 1) * Main.dim + loc[0] - 1]
    }

    private fun getKast(ind: Int, game: Game): Box {
        return game.boxes[ind - 1]
    }



    /**
     * @param val 1-9
     * @return x = 1-3, y = 1-3
     */
//    fun getXYFromInt(`val`: Int): IntArray {
//        return intArrayOf((`val` - 1) % Main.dim + 1, (`val` - 1) / Main.dim + 1)
//    }

    /**
     *
     * @param locs [x 1-dim, y 1-dim]
     * @return 1-dim2
     */
//    fun getIntFromLocalLocs(locs: IntArray): Int {
//        return (locs[1] - 1) * Main.dim + locs[0]
//    }

    /**
     * @param x 1-9
     * @param y 1-9
     * @return [[kastX 1-3, kastY 1-3],[lahterX 1-3, lahterY 1-3]]
     */
//    private fun getLocsFromXY(x: Int, y: Int): Array<IntArray> {
//        val kastX = (x - 1) / Main.dim + 1
//        val kastY = (y - 1) / Main.dim + 1
//        val lahterX = x - (kastX - 1) * Main.dim
//        val lahterY = y - (kastY - 1) * Main.dim
//        return arrayOf(intArrayOf(kastX, kastY), intArrayOf(lahterX, lahterY))
//    }

//    private fun getXYFromLocs(locs: Array<IntArray>): IntArray {
//        val kastXY = locs[0]
//        val lahterXY = locs[1]
//        return intArrayOf((kastXY[0] - 1) * Main.dim + lahterXY[0], (kastXY[1] - 1) * Main.dim + lahterXY[1])
//    }

}

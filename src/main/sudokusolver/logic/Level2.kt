package sudokusolver.logic

import sudokusolver.*

@Throws(FillingException::class)
internal fun intersectionRemoval(game: Game): Boolean {
    var somethingDone = false
    game.level = 2
    //box
    for (box in game.boxes) {

        //get the frequency of possibilities of numbers 1-dim2
        val possibilities = box.cells.possibilities()
        //check the different numbers and the number of the possibilities
        possibilities.forEach { (number, possibleCells) ->
            if (possibleCells.size > 1) {
                //check number of rows
                val rows = possibleCells.map { it.globalCoords.y }.distinct()
                // candidate line (row) is active
                if (rows.size == 1) {
                    somethingDone = (game row rows.first()).filter { it !in possibleCells }.applyAll {
                        numbers.remove(number)
                    }.allTrue()
                    if (somethingDone) {
                        game.addMessage("row ${(box.locY - 1) * Main.dim + moduleList[0]}: ${j + 1}")
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
                    for (cell in game.column((box.locX - 1) * Main.dim + moduleList[0])) {
                        if (cell.box(game) != box) {
                            if (cell.numbers.remove(j + 1)) {
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
    for (row in 1..Main.dim2) {
        val availableSlots = ArrayList<ArrayList<Int>>()
        val lahtridInRow = game row row
        val olemasNumbrid = ArrayList<Int>()
        for (j in 1..Main.dim2) {
            availableSlots.add(ArrayList())
        }
        for (j in 0 until Main.dim2) {
            val cell = lahtridInRow[j]
            val numbers: HashSet<*> = cell.numbers
            if (numbers.size == 0 && cell.value == 0) {
                val globalCoords = cell.globalCoords
                throw FillingException("lahter at x: ${globalCoords.x}, y: ${globalCoords.y} is empty and without possibilities", game)
            }
        }
        //get the frequency of possibilities of numbers 1-dim2
        for (j in 0 until Main.dim2) {
            val lahter = lahtridInRow[j]
            for (a in lahter.numbers) {
                availableSlots[a - 1].add(j + 1)
            }
            olemasNumbrid.add(lahter.value)
        }
        //check the different numbers and the number of the possibilities
        for (j in 0 until Main.dim2) {
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
                    for (lahter in game.box(moduleList[0], row.partition).cells) {
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
    for (column in 1..Main.dim2) {
        val availableSlots = ArrayList<ArrayList<Int>>()
        val lahtridInColumn = game column column
        val olemasNumbrid = ArrayList<Int>()
        for (j in 1..Main.dim2) {
            availableSlots.add(ArrayList())
        }
        for (j in 0 until Main.dim2) {
            val cell = lahtridInColumn[j]
            val numbers: HashSet<*> = cell.numbers
            if (numbers.size == 0 && cell.value == 0) {
                val globalCoords = cell.globalCoords
                throw FillingException("lahter at x: ${globalCoords.x}, y: ${globalCoords.y} is empty and without possibilities", game)
            }
        }
        //get the frequency of possibilities of numbers 1-dim2
        for (j in 0 until Main.dim2) {
            val lahter = lahtridInColumn[j]
            for (a in lahter.numbers) {
                availableSlots[a - 1].add(j + 1)
            }
            olemasNumbrid.add(lahter.value)
        }
        //check the different numbers and the number of the possibilities
        for (j in 0 until Main.dim2) {
            val arvud = availableSlots[j]
            if (arvud.size > 0 && olemasNumbrid.contains(j + 1)) {
                throw FillingException("column " + column + " already has number " + (j + 1) + ", but possibilities exist", game)
            }
            if (arvud.size in 2..Main.dim) {
                val module = HashSet<Int>()
                for (arv in arvud) {
                    module.add(arv.partition)
                }
                //multiple lines is active
                if (module.size == 1) {
                    val moduleList = ArrayList(module)
                    for (lahter in game.box(column.partition, moduleList[0]).cells) {
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

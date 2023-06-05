import kotlin.random.Random

class BEA(
    private val numOfBac : Int,
    private val numOfGen : Int,
    private val numOfInf : Int,
    private val machineList: Array<String>,
    private val scriptList: Array<Script>
) {

    private fun initializePopulation() = MutableList(numOfBac) {
        Array(scriptList.size) {
            Random.nextInt(machineList.size)
        }
    }

    private fun bestFit(population: List<Array<Int>>) : Int {
        //println("Best fit function:")
        val sumList = List(population.size){ Array(machineList.size) { 0 } }
        for (i in population.indices) {
            for (j in scriptList.indices) {
                sumList[i][population[i][j]] += scriptList[j].duration
            }
            //println(sumList[i].toList())
        }

        //println(sumList.minOf { it.max() })

        return sumList.indexOfFirst { i ->
            i.max() == sumList.minOf { it.max() }
        }
    }

    private fun getSum(bacteria: Array<Int>) : Int {
        val sumList = Array(machineList.size) { 0 }
        for (i in bacteria.indices) {
            sumList[bacteria[i]] += scriptList[i].duration
        }
        return sumList.max()
    }

    private fun desc(bacteria: Array<Int>) {
        val sumList = Array(machineList.size) { 0 }
        for (i in bacteria.indices) {
            sumList[bacteria[i]] += scriptList[i].duration
        }
        println("Bacteria: " + bacteria.toList() + " Durations: " + sumList.toList() + " Best: " + sumList.max())
    }

    private fun bacterialMutation(bacteria: Array<Int>): Array<Int> {
        val cloneList = MutableList((machineList.size)) { bacteria.copyOf() }
        val randomListForBacteria = MutableList(bacteria.size) { i -> i }

        while(randomListForBacteria.isNotEmpty()) {
            val randomBacteria = (0 until randomListForBacteria.size).random()
            val randomListForMutationValue = MutableList(machineList.size) { i -> i }

            for (i in cloneList.indices) {
                val randomMutationValue = (0 until randomListForMutationValue.size).random()
                cloneList[i][randomBacteria] = randomListForMutationValue[randomMutationValue]
                //print("Kivettem ${randomListForMutationValue[randomMutationValue]} -t a mutációs listából, ")
                randomListForMutationValue.removeAt(randomMutationValue)
                //print("maradt:  $randomListForMutationValue\n")
            }

            val indexOfBestClone = bestFit(cloneList)

            for (i in cloneList.indices) {
                cloneList[i][randomBacteria] = cloneList[indexOfBestClone][randomBacteria]
            }
            randomListForBacteria.removeAt(randomBacteria)
            //println("Kivettem $randomBacteria -t a baktérium listából, maradt:  $randomListForBacteria")
        }
        return cloneList[bestFit(cloneList)]
    }

    private fun geneTransfer(population: MutableList<Array<Int>>) {
        //1. Populáció felosztása jó és rossz egyedekre
        val goodBacterias = mutableListOf<Array<Int>>()
        val badBacterias = mutableListOf<Array<Int>>()

        val sumList = List(population.size) { Array(machineList.size) { 0 } }

        for (i in population.indices) {
            for (j in scriptList.indices) {
                sumList[i][population[i][j]] += scriptList[j].duration
            }
        }

        val averageDuration = sumList.map { it.max() }.average()

        for (i in population.indices) {
            if (sumList[i].max() < averageDuration) {
                goodBacterias.add(population[i])
            } else {
                badBacterias.add(population[i])
            }
        }

        if (goodBacterias.isEmpty()) {
            val transferSize = badBacterias.size / 2
            val transferredElements = badBacterias.take(transferSize)
            goodBacterias.addAll(transferredElements)
            badBacterias.removeAll(transferredElements)
        } else if (badBacterias.isEmpty()) {
            val transferSize = goodBacterias.size / 2
            val transferredElements = goodBacterias.take(transferSize)
            badBacterias.addAll(transferredElements)
            goodBacterias.removeAll(transferredElements)
        }

        var inf = 0

        while (inf < numOfInf && badBacterias.isNotEmpty()) {

            //2. Véletlenszerűen kiválasztani egyet a jó és rossz baktériumok közül
            val randomGoodBacteria = (0 until goodBacterias.size).random()
            val randomBadBacteria = (0 until badBacterias.size).random()

            //3. Véletlenszerűen kiválasztani egy részt a baktériumokból, és átadni a jót a rossznak
            val fromIndex = (0 until (scriptList.size - 1)).random()
            val toIndex = (fromIndex until scriptList.size).random()

            println("Random good bacteria before gene transfer: ")
            desc(goodBacterias[randomGoodBacteria])
            println("Random bad bacteria before gene transfer: ")
            desc(badBacterias[randomBadBacteria])
            println("From index: $fromIndex, To index: $toIndex")

            val badBacteriaClone = badBacterias[randomBadBacteria].copyOf()

            for (i in fromIndex..toIndex) {
                badBacteriaClone[i] = goodBacterias[randomGoodBacteria][i]
            }
            println("Bad bacteria clone after gene transfer: ")
            desc(badBacteriaClone)

            println("Random bad bacteria after gene transfer: ")
            if (getSum(badBacteriaClone) < averageDuration) {
                badBacterias.removeAt(randomBadBacteria)
                goodBacterias.add(badBacteriaClone)
                desc(badBacteriaClone)
            } else if (getSum(badBacteriaClone) < getSum(badBacterias[randomBadBacteria])) {
                badBacterias[randomBadBacteria] = badBacteriaClone
                desc(badBacterias[randomBadBacteria])
            } else {
                desc(badBacterias[randomBadBacteria])
            }



            println("Good bacteria: ")
            goodBacterias.forEach {
                desc(it)
            }
            println("Bad bacteria: ")
            badBacterias.forEach {
                desc(it)
            }

            println("Original population: ")
            population.forEach {
                desc(it)
            }
            println("After gene transfer: ")
            population.clear()
            population.addAll(goodBacterias.plus(badBacterias))
            population.forEach {
                desc(it)
            }

            inf++
        }
    }

    fun run() {
        val pop = initializePopulation()
        for (generation in 0 until numOfGen) {
            for (i in pop.indices) {
                pop[i] = bacterialMutation(pop[i])
            }
            /*pop.forEach {
                desc(it)
            }*/
            geneTransfer(pop)
        }
    }
}
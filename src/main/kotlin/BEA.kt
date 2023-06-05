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
    private fun getDurationsOfBacterium(bacterium: Array<Int>) : Array<Int> {
        val sumList = Array(machineList.size) { 0 }
        for (i in bacterium.indices) {
            sumList[bacterium[i]] += scriptList[i].duration
        }
        return sumList
    }
    private fun getIndexOfBestBacterium(population: List<Array<Int>>) : Int {
        val sumList = MutableList(population.size) { 0 }
        for (i in population.indices) {
            sumList[i] += getDurationsOfBacterium(population[i]).max()
        }
        return sumList.indexOfFirst {
            it == sumList.min()
        }
    }
    private fun desc(bacterium: Array<Int>) {
        val durations = getDurationsOfBacterium(bacterium)
        println("Bacterium: " + bacterium.toList() + " Durations: " + durations.toList() + " Best: " + durations.max())
    }
    private fun bacterialMutation(bacterium: Array<Int>): Array<Int> {
        // First step: create clones from the bacterium n times, where n is the number of machines
        val cloneList = MutableList((machineList.size)) { bacterium.copyOf() }
        val randomListForBacteria = MutableList(bacterium.size) { i -> i }
        
        while(randomListForBacteria.isNotEmpty()) {
            val randomBacteria = (0 until randomListForBacteria.size).random()
            val randomListForMutationValue = MutableList(machineList.size) { i -> i }
            for (i in cloneList.indices) {
                val randomMutationValue = (0 until randomListForMutationValue.size).random()
                cloneList[i][randomBacteria] = randomListForMutationValue[randomMutationValue]
                randomListForMutationValue.removeAt(randomMutationValue)
            }

            val indexOfBestClone = getIndexOfBestBacterium(cloneList)

            for (i in cloneList.indices) {
                cloneList[i][randomBacteria] = cloneList[indexOfBestClone][randomBacteria]
            }
            randomListForBacteria.removeAt(randomBacteria)
        }
        return cloneList[getIndexOfBestBacterium(cloneList)]
    }

    private fun geneTransfer(population: MutableList<Array<Int>>) {
        val goodBacteria = mutableListOf<Array<Int>>()
        val badBacteria = mutableListOf<Array<Int>>()

        val sumList = List(population.size) { Array(machineList.size) { 0 } }

        for (i in population.indices) {
            for (j in scriptList.indices) {
                sumList[i][population[i][j]] += scriptList[j].duration
            }
        }

        val averageDuration = sumList.map { it.max() }.average()

        for (i in population.indices) {
            if (sumList[i].max() < averageDuration) {
                goodBacteria.add(population[i])
            } else {
                badBacteria.add(population[i])
            }
        }

        if (goodBacteria.isEmpty()) {
            val transferSize = badBacteria.size / 2
            val transferredElements = badBacteria.take(transferSize)
            goodBacteria.addAll(transferredElements)
            badBacteria.removeAll(transferredElements)
        } else if (badBacteria.isEmpty()) {
            val transferSize = goodBacteria.size / 2
            val transferredElements = goodBacteria.take(transferSize)
            badBacteria.addAll(transferredElements)
            goodBacteria.removeAll(transferredElements)
        }

        var inf = 0

        while (inf < numOfInf && badBacteria.isNotEmpty()) {
            
            val randomGoodBacteria = (0 until goodBacteria.size).random()
            val randomBadBacteria = (0 until badBacteria.size).random()
            
            val fromIndex = (0 until (scriptList.size - 1)).random()
            val toIndex = (fromIndex until scriptList.size).random()

            println("Random good bacterium before gene transfer: ")
            desc(goodBacteria[randomGoodBacteria])
            println("Random bad bacterium before gene transfer: ")
            desc(badBacteria[randomBadBacteria])
            println("From index: $fromIndex, To index: $toIndex")

            val badBacteriaClone = badBacteria[randomBadBacteria].copyOf()

            for (i in fromIndex..toIndex) {
                badBacteriaClone[i] = goodBacteria[randomGoodBacteria][i]
            }
            println("Bad bacterium clone after gene transfer: ")
            desc(badBacteriaClone)

            println("Random bad bacterium after gene transfer: ")
            val bestDurationOfBadBacteriaClone = getDurationsOfBacterium(badBacteriaClone).max()

            if (bestDurationOfBadBacteriaClone < averageDuration) {
                badBacteria.removeAt(randomBadBacteria)
                goodBacteria.add(badBacteriaClone)
                desc(badBacteriaClone)
            } else if (bestDurationOfBadBacteriaClone <  getDurationsOfBacterium(badBacteria[randomBadBacteria]).max()) {
                badBacteria[randomBadBacteria] = badBacteriaClone
                desc(badBacteria[randomBadBacteria])
            } else {
                desc(badBacteria[randomBadBacteria])
            }

            println("Original population: ")
            population.forEach {
                desc(it)
            }
            println("After gene transfer: ")
            
            population.clear()
            population.addAll(goodBacteria.plus(badBacteria))
            population.forEach {
                desc(it)
            }

            inf++
        }
    }
    fun run() {
        val pop = initializePopulation()                // First step: initialize population with random Bacteria
        for (generation in 0 until numOfGen) {    // Second step: run generations
            for (i in pop.indices) {                    // Third step: Bacterial mutation for each bacterium in the population
                pop[i] = bacterialMutation(pop[i])
            }
            geneTransfer(pop)                           // Fourth step: Gene transfer on the population
        }
    }
}
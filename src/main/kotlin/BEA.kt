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
    private fun bacterialMutation(bacterium: Array<Int>): Array<Int> {
        val cloneList = MutableList((machineList.size)) { bacterium.copyOf() }
        val listOfIndexes = MutableList(bacterium.size) { i -> i }
        while(listOfIndexes.isNotEmpty()) {
            val indexOfRandomGene = (0 until listOfIndexes.size).random()
            val randomListForMutationIndexes = MutableList(machineList.size) { i -> i }
            for (i in cloneList.indices) {
                val randomMutationIndex = (0 until randomListForMutationIndexes.size).random()
                cloneList[i][indexOfRandomGene] = randomListForMutationIndexes[randomMutationIndex]
                randomListForMutationIndexes.removeAt(randomMutationIndex)
            }
            val indexOfBestClone = CompanionClass().getIndexOfBestBacterium(cloneList)
            for (i in cloneList.indices) {
                cloneList[i][indexOfRandomGene] = cloneList[indexOfBestClone][indexOfRandomGene]
            }
            listOfIndexes.removeAt(indexOfRandomGene)
        }
        return cloneList[CompanionClass().getIndexOfBestBacterium(cloneList)]
    }
    private fun geneTransfer(population: MutableList<Array<Int>>) {
        val goodBacteria = mutableListOf<Array<Int>>()
        val badBacteria = mutableListOf<Array<Int>>()
        val sumList = CompanionClass().getMaxDurationsOfPopulation(population)
        val averageDuration = sumList.average()
        for (i in population.indices) {
            if (sumList[i] < averageDuration) {
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
            val indexOfRandomGoodBacterium = (0 until goodBacteria.size).random()
            val indexOfRandomBadBacterium = (0 until badBacteria.size).random()
            val fromIndex = (0 until (scriptList.size - 1)).random()
            val toIndex = (fromIndex until scriptList.size).random()
            val badBacterium = badBacteria[indexOfRandomBadBacterium].copyOf()
            for (i in fromIndex..toIndex) {
                badBacterium[i] = goodBacteria[indexOfRandomGoodBacterium][i]
            }
            val maxDurationOfBadBacterium = CompanionClass().getDurationsOfBacterium(badBacterium).max()
            if (maxDurationOfBadBacterium < averageDuration) {
                badBacteria.removeAt(indexOfRandomBadBacterium)
                goodBacteria.add(badBacterium)
            } else if (maxDurationOfBadBacterium <  CompanionClass().getDurationsOfBacterium(badBacteria[indexOfRandomBadBacterium]).max()) {
                badBacteria[indexOfRandomBadBacterium] = badBacterium
            }
            inf++
        }
        population.clear()
        population.addAll(goodBacteria.plus(badBacteria))
    }
    fun run() {
        val pop = initializePopulation()                // First step: initialize population with random Bacteria
        println("Initial population: ")
        pop.forEach {
            CompanionClass().desc(it)
        }
        for (generation in 0 until numOfGen) {    // Second step: run generations
            for (i in pop.indices) {                    // Third step: Bacterial mutation for each bacterium in the population
                pop[i] = bacterialMutation(pop[i])
            }
            geneTransfer(pop)                           // Fourth step: Gene transfer on the population
        }
        println("After BEA: ")
        pop.forEach {
            CompanionClass().desc(it)
        }

        CompanionClass().showResult(pop)
    }
    inner class CompanionClass  {
        fun getDurationsOfBacterium(bacterium: Array<Int>) : Array<Int> {
            val sumList = Array(machineList.size) { 0 }
            for (i in bacterium.indices) {
                sumList[bacterium[i]] += scriptList[i].duration
            }
            return sumList
        }
        fun getMaxDurationsOfPopulation(population: List<Array<Int>>) : MutableList<Int> {
            val sumList = MutableList(population.size) { 0 }
            for (i in population.indices) {
                sumList[i] += getDurationsOfBacterium(population[i]).max()
            }
            return sumList
        }
        fun getIndexOfBestBacterium(population: List<Array<Int>>) : Int {
            val sumList = getMaxDurationsOfPopulation(population)
            return sumList.indexOfFirst {
                it == sumList.min()
            }
        }
        fun desc(bacterium: Array<Int>) {
            val durations = getDurationsOfBacterium(bacterium)
            println("\tBacterium: " + bacterium.toList() + " Durations: " + durations.toList() + " Max: " + durations.max())
        }

        fun showResult(population: List<Array<Int>>) {
            val best = population[getIndexOfBestBacterium(population)]
            println("\nBest configuration: \n")
            machineList.forEach {
                print("$it:\t\t")
            }
            println()
            for(i in best.indices) {
                for (j in 0 until best[i]) {
                    print("\t\t\t\t")
                }
                println(scriptList[i].name)
            }
            println("\nMaximum duration: ${getDurationsOfBacterium(best).max()}")
        }
    }
}
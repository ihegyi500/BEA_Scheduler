import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis


/*
*  Implementing job shop scheduling problem optimization
* using bacterial evolutionary algorithm
*/

data class Script(
    val name: String,
    val duration: Int,
    val startTime: Int,
    val endTime: Int
)

data class Setup (
    val machineList: Array<String>,
    val scriptList: Array<Script>,
    val scriptToMachine: Array<Int>
)

/*
fun showResult_orig(setup: Setup) {
    val sumList = MutableList<Int>(setup.machineList.size) {0}
    setup.machineList.forEach {print("\t\t\t$it")}
    println()
    for (i in setup.scriptList.indices) {
        print(setup.scriptList[i].name + ":\t")
        for (j in setup.machineList.indices) {
            if (setup.scriptToMachine[i] == j) {
                sumList[j] += setup.scriptList[i].duration
                print("${setup.scriptList[i].duration}\t\t\t\t")
            } else print("_\t\t\t\t")
        }
        println()
    }
    print("\t\t\t")
    for (j in setup.machineList.indices) {
        print("${sumList[j]}\t\t\t\t")
    }
    println("\nTotal: ${sumList.max()}")
}
*/
fun main() = runBlocking {
    val time = measureTimeMillis {

        val numOfMachines = 3
        val numOfScripts = 10

        val machineList: Array<String> = Array(numOfMachines) { "Machine_${it + 1}" }
        val scriptList: Array<Script> = Array(numOfScripts) {
            Script("Script_${it + 1}", it + 1, 0, 0)
        }

        /*val scriptToMachine:Array<Int> = Array(numOfScripts) {
        Random.nextInt(numOfMachines)
    }

    /*val setup = Setup(
        machineList,
        scriptList,
        scriptToMachine
    )*/

    val setupList = List(5) {
        Setup(
            machineList,
            scriptList,
            Array(numOfScripts) {
                Random.nextInt(numOfMachines)
            }
        )
    }

    setupList.forEach {
        showResult(it)
    }*/

        val bea = BEA(
            5,
            2,
            5,
            machineList,
            scriptList
        )

        bea.run()
    }

    println("\nTime: $time ms")
}

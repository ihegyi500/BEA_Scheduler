import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

data class Script(
    val name: String,
    val duration: Int,
    val startTime: Int,
    val endTime: Int
)

fun main() = runBlocking {

    val time = measureTimeMillis {

        val numOfMachines = 3
        val numOfScripts = 10

        val machineList: Array<String> = Array(numOfMachines) { "Machine_${it + 1}" }
        val scriptList: Array<Script> = Array(numOfScripts) {
            Script("Script_${it + 1}", it + 1, 0, 0)
        }

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

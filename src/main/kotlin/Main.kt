import kotlin.system.exitProcess

fun main() {
    while (true) {
        print("$ ")
        val line = readln()
        val words = line.split(" ")
        if (words.isNotEmpty()) {
            if (words.first() == "exit") {
                if (words.size > 1) {
                    exitProcess(words[1].toInt())
                } else {
                    exitProcess(0)
                }
            }
        }
        println("${words[0]}: command not found")
    }
}

import kotlin.system.exitProcess

val builtinCommands = listOf("exit", "echo", "type")

fun main() {
    while (true) {
        print("$ ")
        val line = readln()
        val words = line.split(" ")

        var command: String
        try {
            command = words.first()
        } catch (e: NoSuchElementException) {
            continue
        }

        when (command) {
            "exit" -> {
                var statusCode = 0
                if (words.size > 1) {
                    try {
                        statusCode = words[1].toInt()
                    } catch (_: Exception) { }
                }
                exitProcess(statusCode)
            }

            "echo" -> {
                val echoString = words.subList(1, words.size).joinToString(separator = " ")
                println(echoString)
            }

            "type" -> {
                words.subList(1, words.size).forEach {
                    if (it in builtinCommands) {
                        println("$it is a shell builtin")
                    } else {
                        println("$it: not found")
                    }
                }
            }

            else -> {
                println("$command: command not found")
            }
        }

    }
}

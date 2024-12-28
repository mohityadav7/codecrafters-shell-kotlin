import java.io.File
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.system.exitProcess

val builtinCommands = listOf("exit", "echo", "type")

fun main() {
    while (true) {
        print("$ ")
        val line = readln()
        val inputArgs = line.split(" ")

        var command: String
        try {
            command = inputArgs.first()
        } catch (e: NoSuchElementException) {
            continue
        }

        when (command) {
            "exit" -> {
                var statusCode = 0
                if (inputArgs.size > 1) {
                    try {
                        statusCode = inputArgs[1].toInt()
                    } catch (_: Exception) { }
                }
                exitProcess(statusCode)
            }

            "echo" -> {
                val echoString = inputArgs.subList(1, inputArgs.size).joinToString(separator = " ")
                println(echoString)
            }

            "type" -> {
                val pathFiles = System.getenv("PATH")
                    .split(":")
                    .filter { pathString ->
                        Path.of(pathString).exists()
                    }
                    .flatMap { dirPath ->
                        File(dirPath).listFiles()?.toList() ?: listOf()
                    }
                inputArgs.subList(1, inputArgs.size).forEach { inputArg ->
                    pathFiles.firstOrNull { it.name == inputArg }?.let {
                        println("${it.name} is ${it.path}")
                        return@forEach
                    }
                    if (inputArg in builtinCommands) {
                        println("$inputArg is a shell builtin")
                    } else {
                        println("$inputArg: not found")
                    }
                }
            }

            else -> {
                println("$command: command not found")
            }
        }
    }
}

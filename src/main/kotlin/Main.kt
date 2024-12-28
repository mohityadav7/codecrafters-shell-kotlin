import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.system.exitProcess

fun main() {
    while (true) {
        print("$ ")
        val line = readln()
        handleCommand(line)
    }
}

fun handleCommand(inputLine: String) {
    val (cmd, args) = inputLine.trim().split("\\s+".toRegex()).let {
        if (it.isEmpty()) return
        Command.from(it.first()) to it.drop(1)
    }

    when (cmd) {
        is Command.Exit -> {
            var statusCode = 0
            try {
                if (args.isNotEmpty()) statusCode = args[0].toInt()
            } catch (_: Exception) { }
            exitProcess(statusCode)
        }

        is Command.Echo -> {
            println(inputLine.substringAfter("${cmd.value} ", ""))
        }

        is Command.Type -> {
            args.forEach { arg ->
                if (arg in Command.entries.map { it.value }) {
                    println("$arg is a shell builtin")
                } else {
                    findExecutablePath(arg)?.let {
                        println("$arg is $it")
                    } ?: run {
                        println("$arg: not found")
                    }
                }
            }
        }

        is Command.None -> {
            println()
        }

        is Command.Pwd -> {
            println(System.getProperty("user.dir"))
        }

        is Command.Unknown -> {
            val cmdPath = findExecutablePath(cmd.value)
            cmdPath?.let {
                try {
                    val process = ProcessBuilder(cmdPath, *args.toTypedArray())
                        .redirectErrorStream(true)
                        .start()
                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    reader.lines().forEach {
                        println(it)
                    }
                    process.waitFor()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } ?: run {
                println("${cmd.value}: command not found")
            }
        }
    }
}

fun findExecutablePath(executable: String): String? {
    return System.getenv("PATH")
        .split(":")
        .filter { Path.of(it).exists() }
        .flatMap { dirPath -> File(dirPath).listFiles()?.toList() ?: listOf() }
        .firstOrNull { it.name == executable }?.path
}

sealed class Command(val value: String) {
    data object None : Command("")
    data object Echo: Command("echo")
    data object Type: Command("type")
    data object Exit: Command("exit")
    data object Pwd: Command("pwd")
    class Unknown(value: String): Command(value)

    companion object {
        val entries: List<Command> =
            Command::class.sealedSubclasses.mapNotNull { it.objectInstance }

        fun from(value: String): Command =
            entries.firstOrNull { it.value == value } ?: Unknown(value)
    }
}

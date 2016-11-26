package ru.spbau.mit.aush.execute.cmd

import java.io.InputStream
import java.io.OutputStream
import kotlin.system.exitProcess


class ExitExecutor : CmdExecutor() {
    override fun name(): String {
        return "exit"
    }

    override fun exec(args: String, inStream: InputStream, outStream: OutputStream): Int {
        exitProcess(0)
        return 0
    }
}
package com.dkosub.ffxiv.tools.config

class Environment {
    fun variable(name: String): String? = System.getenv(name)
    fun variable(name: String, default: String): String = variable(name) ?: default
}

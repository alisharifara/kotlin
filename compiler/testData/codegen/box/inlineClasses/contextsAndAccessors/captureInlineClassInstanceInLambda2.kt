// !LANGUAGE: +InlineClasses
// IGNORE_BACKEND_FIR: JVM_IR

inline class R(private val r: Long) {
    fun test() = { ok() }()

    fun ok() = "OK"
}

fun box() = R(0).test()
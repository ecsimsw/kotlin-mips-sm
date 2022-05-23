package computer.architecture.component

class Latch<T>(
    initialValue: T
) {
    private var input = initialValue
    private var output = initialValue

    fun fetch(): T = output

    fun store(input: T) {
        this.input = input
    }

    fun flush() {
        this.output = this.input
    }
}

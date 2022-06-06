package computer.architecture.cpu.utils

import computer.architecture.utils.Logger
import org.assertj.core.api.Assertions.assertThat

class Utils {
    companion object {
        fun checkProcessResult(processResult: Int, expected: Int) {
            Logger.printProcessResult(processResult)
            assertThat(processResult).isEqualTo(expected)
        }
    }
}

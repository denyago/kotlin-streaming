package name.denyago.streaming.source.db

import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec

class GeneratedReaderTest : DescribeSpec() {

    init {
        describe("selectUsers") {
            it("returns a sequence of users between provided IDs inclusive") {
                GeneratedReader().selectUsers(1..3).asSequence().toList() shouldBe
                        listOf(GeneratedUser(1), GeneratedUser(2), GeneratedUser(3))
            }
        }
    }
}

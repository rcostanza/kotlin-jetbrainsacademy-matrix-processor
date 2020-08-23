package processor

import java.text.DecimalFormat
import java.util.*
import kotlin.math.pow

val scanner = Scanner(System.`in`)

fun main() {
    do {
        var choice: Int? = prompt() ?: return

        when (choice) {
            1 -> addMatrices()
            2 -> multiplyMatrix()
            3 -> multiplyMatrices()
            4 -> transposeMatrix()
            5 -> calculateDeterminant()
            6 -> invertMatrix()
            0 -> return
            else -> throw IllegalArgumentException("Invalid option")
        }
        println("")
    } while(true)

}

fun prompt(): Int? {
    print("""1. Add matrices
            |2. Multiply matrix to a constant
            |3. Multiply matrices
            |4. Transpose matrix
            |5. Calculate a determinant
            |6. Inverse matrix
            |0. Exit
            |Your choice: """.trimMargin("|"))

    return try {
        val option = scanner.nextInt()
        option
    } catch(e: Exception) {
         null
    }
}

fun addMatrices() {
    val m1 = readMatrix("first")
    val m2 = readMatrix("second")

    println("The addition result is:")
    println(m1 + m2)
    println("")
}

fun multiplyMatrix() {
    val m1 = readMatrix()
    val scalar = scanner.nextDouble()

    println("The multiplication result is:")
    m1 *= scalar
    println(m1.toIntString())
    println("")
}

fun multiplyMatrices() {
    val m1 = readMatrix("first")
    val m2 = readMatrix("second")

    println("The multiplication result is:")

    var m3 = m1 * m2
    if (m1[0, 0] % m1[0, 0].toInt() == 0.0) {
        println(m3.toIntString())
    } else {
        println(m3)
    }

    println("")
}

fun transposeMatrix() {
    print("""1. Main diagonal
                |2. Side diagonal
                |3. Vertical line
                |4. Horizontal line
                |Your choice: """.trimMargin("|"))

    val transposeType = scanner.nextInt()

    val matrix = readMatrix()

    println("The result is:")
    println(matrix.transpose(transposeType).toIntString())
}

fun calculateDeterminant() {
    val matrix = readMatrix()
    println("The result is:")
    println(matrix.determinant)
}

fun invertMatrix() {
    val matrix = readMatrix()
    println("The result is:")
    println(matrix.inverse())

}

fun readMatrix(label: String = "\b"): Matrix {
    print("Enter size of the $label matrix: ")
    val (rows, cols) = IntArray(2) { scanner.nextInt() }

    val matrix = Matrix(rows, cols)

    println("Enter $label matrix: ")
    scanner.nextLine()

    repeat(rows) { row ->
        val line = scanner.nextLine().trim().replace("> ", "").split(" ")
        if (line.size != cols) {
            throw IllegalArgumentException("Invalid matrix size.")
        }
        matrix[row] = line.map { it.toDouble() }.toDoubleArray()
    }

    return matrix
}

class Matrix(
        var rows: Int,
        var cols: Int,
        var initfn: (() -> Double)? = null
    ) {
    private var matrix = Array(rows) { DoubleArray(cols) { initfn?.invoke() ?: 0.0 } }

    val determinant: Double
    get() = this.calculateDeterminant()

    // https://kotlinlang.org/docs/reference/operator-overloading.html
    // this++
    fun unaryPlus() {
        for(r in 0..this.rows-1) {
            for(c in 0..this.cols-1) {
                this[r, c] += 1.0
            }
        }
    }

    // Matrix + Matrix
    operator fun plus(other: Matrix): Matrix {
        var newMatrix = Matrix(rows, cols)
        for(r in 0..rows-1) {
            for(c in 0..cols-1) {
                newMatrix[r, c] = this[r, c] + other[r, c]
            }
        }
        return newMatrix
    }

    // Matrix *= Double
    operator fun timesAssign(scalar: Double) {
        for(r in 0 until rows) {
            for(c in 0 until cols) this[r, c] *= scalar
        }
    }

    // Matrix * Matrix
    operator fun times(other: Matrix): Matrix {
        if (this.cols != other.rows) {
            throw ArithmeticException("Incompatible matrices for dot operation.")
        }

        var newMatrix = Matrix(rows, other.cols)

        for(row in 0 until rows) {
            for(col in 0 until other.cols) {
                repeat(this.cols) {
                    newMatrix[row, col] += (this[row, it] * other[it, col])
                }
            }
        }
        return newMatrix
    }

    // Matrix * Double
    operator fun times(scalar: Double): Matrix {
        val newMatrix = this.copy()
        newMatrix *= scalar
        return newMatrix
    }

    // Matrix /= Double
    operator fun divAssign(scalar: Double) {
        for(r in 0 until rows) {
            for(c in 0 until cols) {
                this[r, c] /= scalar
            }
        }
    }

    // Matrix / Matrix
    operator fun div(other: Matrix): Matrix {
        if (this.cols != other.rows) {
            throw ArithmeticException("Incompatible matrices for dot operation.")
        }

        var newMatrix = Matrix(rows, other.cols)

        for(row in 0 until rows) {
            for(col in 0 until other.cols) {
                repeat(this.cols) {
                    newMatrix[row, col] += (this[row, it] / other[it, col])
                }
            }
        }
        return newMatrix
    }
    // Matrix / Double
    operator fun div(scalar: Double): Matrix {
        return Matrix(rows, cols){ scalar } / this
    }

    operator fun get(row: Int): DoubleArray {
        return matrix[row]
    }

    operator fun get(row: Int, col: Int): Double {
        return matrix[row][col]
    }

    operator fun set(row: Int, col: Int, value: Double) {
        matrix[row][col] = value
    }

    operator fun set(row: Int, value: DoubleArray) {
        if (value.size != cols) {
            throw IllegalArgumentException("Array is larger than the set matrix size.")
        }
        matrix[row] = value
    }

    override fun toString(): String = matrix.joinToString("\n") { it -> it.joinToString(" ") }

    fun toIntString(): String = matrix.joinToString("\n") { it.map{ it.toInt() }.joinToString(" ") }

    fun copy() : Matrix {
        val other = Matrix(rows, cols)
        for(r in 0 until rows) other[r] = this[r]
        return other
    }

    fun transpose(type: Int): Matrix {
        var newMatrix = Matrix(cols, rows)

        when(type) {
            1 -> {
                for (r in 0 until rows) {
                    for (c in 0 until cols) newMatrix[r, c] = this[c, r]
                }
            }
            2 -> {
                for (r in 0 until rows) {
                    for (c in 0 until cols) newMatrix[(rows-1)-r, (cols-1) - c] = this[c, r]
                }
            }
            3 -> {
                for (r in 0 until rows) {
                    for (c in 0 until cols) newMatrix[r, (cols-1) - c] = this[r, c]
                }
            }
            4 -> {
                for (r in 0 until rows) {
                    for (c in 0 until cols) newMatrix[(rows-1)-r, c] = this[r, c]
                }
            }
            else -> throw UnsupportedOperationException()
        }

        return newMatrix
    }

    fun inverse(): Matrix {
        /**
        Step 1: calculating the Matrix of Minors,
        Step 2: then turn that into the Matrix of Cofactors,
        Step 3: then the Adjugate, and
        Step 4: multiply that by 1/Determinant.
         */
        val matrixOfMinors = Matrix(rows, cols)
        val matrixOfCofactors = Matrix(rows, cols)
        for(r in 0 until rows) {
            for(c in 0 until cols) {
                val det = det(this.getMinor(kotlin.Pair(r, c)))
                matrixOfMinors[r, c] = det
                matrixOfCofactors[r, c] = if ((r+c) % 2 == 0) det else -det
            }
        }

        val adjucateMatrix = matrixOfCofactors.transpose(1)

        val det = this.determinant

        return adjucateMatrix * (1/det)
    }

    private fun getMinor(axis: Pair<Int, Int>): Matrix {
        val minor = Matrix(this.rows-1, this.cols-1)
        var rr = 0
        for(r in 0 until this.rows) {
            if (r == axis.first) continue
            minor[rr++] = this[r].filterIndexed { index, _ -> index != axis.second }.toDoubleArray()
        }
        return minor
    }

    private fun cofactor(row: Int, col: Int): Double {
        return this[row, col] * (-1.0).pow(2 + (row + col)) * det(this.getMinor(Pair(row, col)))
    }

    private fun findZero(matrix: Matrix): Pair<Int, Int> {
        for(r in 0 until matrix.rows) {
            for(c in 0 until matrix.cols) {
                if (matrix[r, c] == 0.0) return Pair(r, c)
            }
        }

        return Pair(0, 0)
    }

    private fun det(matrix: Matrix): Double {
        if (matrix.rows == 2) return (matrix[0, 0] * matrix[1, 1]) - (matrix[0, 1] * matrix[1, 0])

        var total = 0.0
        val axis = findZero(matrix)

        for(c in 0 until matrix.cols) {
            // cofactor
            total += matrix.cofactor(axis.first, c)
        }

        return total
    }

    private fun calculateDeterminant(): Double {
        if (rows != cols) {
            throw ArithmeticException("Not a square matrix")
        }

        return det(this)
    }

    companion object {
        fun identity(rows: Int, cols: Int): Matrix {
            val identityMatrix = Matrix(rows, cols)
            repeat(rows) {
                identityMatrix[it, it] = 1.0
            }
            return identityMatrix
        }
    }

}

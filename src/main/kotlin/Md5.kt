import java.math.BigInteger
import java.security.MessageDigest

private const val RADIX = 16
private const val WIDTH = 32

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(RADIX)
    .padStart(WIDTH, '0')

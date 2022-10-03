package com.token

import io.smallrye.jwt.build.Jwt
import org.eclipse.microprofile.jwt.Claims
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import javax.json.JsonObject;


object TokenUtils {
    /**
     * Utility method to generate a JWT string from a JSON resource file that is signed by the privateKey.pem
     * test resource key, possibly with invalid fields.
     *
     * @param jsonResName - name of test resources file
     * @param timeClaims - used to return the exp, iat, auth_time claims
     * @return the JWT string
     * @throws Exception on parse failure
     */
    @Throws(Exception::class)
    fun generateTokenString(jsonResName: JsonObject): String {
        // Use the test private key associated with the test public key for a valid signature
        val pk = readPrivateKey("/privateKey.pem")
        return generateTokenString(pk, jsonResName)
    }

    @Throws(Exception::class)
    fun generateTokenString(
        privateKey: PrivateKey?, jsonResName: JsonObject): String {
        val claims = Jwt.claims(jsonResName)
        return claims.jws().sign(privateKey)
    }

    /**
     * Read a PEM encoded private key from the classpath
     *
     * @param pemResName - key file resource name
     * @return PrivateKey
     * @throws Exception on decode failure
     */
    @Throws(Exception::class)
    fun readPrivateKey(pemResName: String?): PrivateKey {
        TokenUtils::class.java.getResourceAsStream(pemResName).use { contentIS ->
            val tmp = ByteArray(4096)
            val length = contentIS.read(tmp)
            return decodePrivateKey(String(tmp, 0, length, charset("UTF-8")))
        }
    }

    /**
     * Decode a PEM encoded private key string to an RSA PrivateKey
     *
     * @param pemEncoded - PEM string for private key
     * @return PrivateKey
     * @throws Exception on decode failure
     */
    @Throws(Exception::class)
    fun decodePrivateKey(pemEncoded: String): PrivateKey {
        val encodedBytes = toEncodedBytes(pemEncoded)
        val keySpec = PKCS8EncodedKeySpec(encodedBytes)
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePrivate(keySpec)
    }

    private fun toEncodedBytes(pemEncoded: String): ByteArray {
        val normalizedPem = removeBeginEnd(pemEncoded)
        return Base64.getDecoder().decode(normalizedPem)
    }

    private fun removeBeginEnd(pem: String): String {
        var pem = pem
        pem = pem.replace("-----BEGIN (.*)-----".toRegex(), "")
        pem = pem.replace("-----END (.*)----".toRegex(), "")
        pem = pem.replace("\r\n".toRegex(), "")
        pem = pem.replace("\n".toRegex(), "")
        return pem.trim { it <= ' ' }
    }

    /**
     * @return the current time in seconds since epoch
     */
    fun currentTimeInSecs(): Int {
        val currentTimeMS = System.currentTimeMillis()
        return (currentTimeMS / 1000).toInt()
    }
}
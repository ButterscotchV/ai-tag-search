package net.dankrushen.aitagsearch.database

import org.agrona.DirectBuffer
import org.agrona.concurrent.UnsafeBuffer
import org.lmdbjava.DirectBufferProxy
import org.lmdbjava.Env
import java.io.File
import java.nio.ByteBuffer

object DatabaseUtils {
    fun stringToUnsafeBuffer(string: String, size: Int): UnsafeBuffer {
        val byteBuffer = UnsafeBuffer(ByteBuffer.allocateDirect(size))

        byteBuffer.putStringUtf8(0, string)

        return byteBuffer
    }

    fun createEnv(file: File, estimatedSizeBytes: Long, numDb: Int): Env<DirectBuffer> {
        return Env.create(DirectBufferProxy.PROXY_DB)
                // An estimate of how large the database might be
                .setMapSize(estimatedSizeBytes)
                // How many databases will be stored
                .setMaxDbs(numDb)
                .open(file)
    }
}
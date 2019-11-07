package net.dankrushen.aitagsearch.database

import org.agrona.DirectBuffer
import org.lmdbjava.DirectBufferProxy
import org.lmdbjava.Env
import java.io.File

object DatabaseUtils {

    fun createEnv(file: File, estimatedSizeBytes: Long, numDb: Int): Env<DirectBuffer> {
        return Env.create(DirectBufferProxy.PROXY_DB)
                // An estimate of how large the database might be
                .setMapSize(estimatedSizeBytes)
                // How many databases will be stored
                .setMaxDbs(numDb)
                .open(file)
    }
}
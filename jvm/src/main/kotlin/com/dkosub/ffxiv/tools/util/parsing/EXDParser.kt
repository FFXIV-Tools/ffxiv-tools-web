package com.dkosub.ffxiv.tools.util.parsing

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.InputStream
import kotlin.math.max

private val HEADER_REGEX = Regex("^([^{\\[]+)(\\{(\\w+)})?(\\[(\\d+)])?(\\[(\\d+)])?$")

data class EXDKey(
    val key: String,
    val subKey: String?,
)

private data class EXDMetadata(
    var index1Size: Int = 0,
    var index2Size: Int = 0,
)

private data class EXDColumn(
    val key: EXDKey,
    val metadata: EXDMetadata,
    val index1: Int,
    val index2: Int,
) {
    fun assign(r: HashMap<String, Any>, v: String) {
        var row = r
        var rowKey = key.key

        if (key.subKey != null) {
            row = row.getOrPut(key.key) {
                hashMapOf<String, Any>()
            } as HashMap<String, Any>
            rowKey = key.subKey
        }

        if (index1 >= 0) {
            if (index2 >= 0) {
                val array = row.getOrPut(rowKey) {
                    Array(metadata.index2Size) {
                        Array<String?>(metadata.index1Size) { null }
                    }
                } as Array<Array<String?>>
                array[index2][index1] = v
            } else {
                val array = row.getOrPut(rowKey) {
                    Array<String?>(metadata.index1Size) { null }
                } as Array<String?>
                array[index1] = v
            }
        } else {
            row[rowKey] = v
        }
    }
}

class EXDParser(private val stream: InputStream) {
    suspend fun parse(processRow: (HashMap<String, Any>) -> Unit) {
        csvReader().openAsync(stream) {
            // First line only contains the indexes, pretty useless
            readNext()

            val headers = readNext() ?: throw RuntimeException("Failed to read header row")
            val metadataLookup = hashMapOf<EXDKey, EXDMetadata>()

            val columns = headers.map { header ->
                if (header.isEmpty()) return@map null

                val match = HEADER_REGEX.matchEntire(header) ?: throw RuntimeException("Header does not parse: $header")
                val key = match.groups[1]?.value ?: throw RuntimeException("No primary key")
                val subKey = match.groups[3]?.value
                val index1 = match.groups[5]?.value?.toInt()
                val index2 = match.groups[7]?.value?.toInt()
                val dataKey = EXDKey(key, subKey)

                // TODO: This appears to be a valid use case for EXD but for now going to skip these scenarios
                if (subKey != null && metadataLookup[EXDKey(key, null)] != null) return@map null

                val metadata = metadataLookup.getOrPut(dataKey) { EXDMetadata() }
                if (index1 != null) metadata.index1Size = max(metadata.index1Size, index1 + 1)
                if (index2 != null) metadata.index2Size = max(metadata.index2Size, index2 + 1)

                EXDColumn(dataKey, metadata, index1 ?: -1, index2 ?: -1)
            }

            // Skip the type and empty row
            repeat(2) { readNext() }

            // Read the rest of the file, handling each with the provided user function
            readAllAsSequence().forEach { row ->
                val rowData = hashMapOf<String, Any>()
                row.forEachIndexed { index, value -> columns[index]?.assign(rowData, value) }
                processRow(rowData)
            }
        }
    }
}

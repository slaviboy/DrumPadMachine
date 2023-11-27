cmake_minimum_required(VERSION 3.4.1)
project("parselib")

set(parselib_sources
        stream/FileInputStream.cpp
        stream/InputStream.cpp
        stream/MemInputStream.cpp
        wav/AudioEncoding.cpp
        wav/WavChunkHeader.cpp
        wav/WavFmtChunkHeader.cpp
        wav/WavRIFFChunkHeader.cpp
        wav/WavStreamReader.cpp
)
add_library(parselib SHARED ${parselib_sources})

#target_include_directories(parselib
#        PUBLIC stream
#        PUBLIC wav)

set(OBOE_DIR ../../../../oboe)

include_directories(
        ${OBOE_DIR}/src/main/cpp/src
        ${OBOE_DIR}/src/main/cpp/include/oboe
        ${CMAKE_CURRENT_LIST_DIR}
        ../../../../shared)

target_link_libraries(
        parselib
        log)

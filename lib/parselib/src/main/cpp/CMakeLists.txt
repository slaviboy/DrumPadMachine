cmake_minimum_required(VERSION 3.4.1)

set(parselib_sources
        ${CMAKE_CURRENT_LIST_DIR}/stream/FileInputStream.cpp
        ${CMAKE_CURRENT_LIST_DIR}/stream/InputStream.cpp
        ${CMAKE_CURRENT_LIST_DIR}/stream/MemInputStream.cpp
        ${CMAKE_CURRENT_LIST_DIR}/wav/AudioEncoding.cpp
        ${CMAKE_CURRENT_LIST_DIR}/wav/WavChunkHeader.cpp
        ${CMAKE_CURRENT_LIST_DIR}/wav/WavFmtChunkHeader.cpp
        ${CMAKE_CURRENT_LIST_DIR}/wav/WavRIFFChunkHeader.cpp
        ${CMAKE_CURRENT_LIST_DIR}/wav/WavStreamReader.cpp
)
add_library(parselib ${parselib_sources})

include_directories(
        ../../../../oboe/src/main/cpp/src
        ../../../../oboe/src/main/cpp/include/oboe
        ${CMAKE_CURRENT_LIST_DIR}
        ../../../../shared)

target_link_libraries(
        parselib
        log
)
target_include_directories(
        parselib
        PUBLIC stream
        PUBLIC wav
)
target_include_directories(
        parselib
        PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}
)
cmake_minimum_required(VERSION 3.4.1)

set(OBOE_DIR ../../../../lib/oboe)
set(PARSELIB_DIR ../../../../lib/parselib)

set(iolib_sources
        ${CMAKE_CURRENT_LIST_DIR}/player/SampleSource.cpp
        ${CMAKE_CURRENT_LIST_DIR}/player/SampleBuffer.cpp
        ${CMAKE_CURRENT_LIST_DIR}/player/OneShotSampleSource.cpp
        ${CMAKE_CURRENT_LIST_DIR}/player/SimpleMultiPlayer.cpp
)
add_library(iolib STATIC ${iolib_sources})

include_directories(
        iolib
        ${OBOE_DIR}/src/main/cpp/include
        ${OBOE_DIR}/src/main/cpp/src/flowgraph
        ${PARSELIB_DIR}/src/main/cpp
        ${CMAKE_CURRENT_LIST_DIR}
        ../../../../shared)

target_link_libraries(
        iolib
        log
)
target_include_directories(
        iolib
        PUBLIC
        player
)
cmake_minimum_required(VERSION 3.4.1)
project("iolib")

set(OBOE_DIR ../../../../oboe)
set(PARSELIB_DIR ../../../../parselib)

include_directories(
        iolib
        ${PARSELIB_DIR}/src/main/cpp
        ${OBOE_DIR}/src/main/cpp/include
        ${OBOE_DIR}/src/main/cpp/src/flowgraph
        ${CMAKE_CURRENT_LIST_DIR}
        ../../../../shared)

add_library(
        iolib
        SHARED
        ${CMAKE_CURRENT_LIST_DIR}/player/SampleSource.cpp
        ${CMAKE_CURRENT_LIST_DIR}/player/SampleBuffer.cpp
        ${CMAKE_CURRENT_LIST_DIR}/player/OneShotSampleSource.cpp
        ${CMAKE_CURRENT_LIST_DIR}/player/SimpleMultiPlayer.cpp)

target_link_libraries(
        iolib
        log)

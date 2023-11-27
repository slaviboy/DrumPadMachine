cmake_minimum_required(VERSION 3.4.1)
project("iolib")

set(OBOE_DIR ../../../../oboe)
set(PARSELIB_DIR ../../../../parselib)

include_directories(
        iolib
        ${OBOE_DIR}/src/main/cpp/include
        ${OBOE_DIR}/src/main/cpp/src/flowgraph
        ${PARSELIB_DIR}/src/main/cpp
        ${CMAKE_CURRENT_LIST_DIR}
        ../../../../shared)

#target_include_directories(iolib
#        PUBLIC player
#)

add_library(
        iolib
        SHARED
        player/SampleSource.cpp
        player/SampleBuffer.cpp
        player/OneShotSampleSource.cpp
        player/SimpleMultiPlayer.cpp)

target_link_libraries(
        iolib
        log
)

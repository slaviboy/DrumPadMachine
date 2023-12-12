cmake_minimum_required(VERSION 3.4.1)
project("audio")

set(OBOE_DIR ../../../../lib/oboe)
set(PARSELIB_DIR ../../../../lib/parselib)
set(IOLIB_DIR ../../../../lib/iolib)

include(${PARSELIB_DIR}/src/main/cpp/CMakeLists.txt)
include(${IOLIB_DIR}/src/main/cpp/CMakeLists.txt)
include(${OBOE_DIR}/src/main/cpp/CMakeLists.txt)

include_directories(
        audio
        ${OBOE_DIR}/src/main/cpp/include
        ${OBOE_DIR}/src/main/cpp/src
        ${PARSELIB_DIR}/src/main/cpp
        ${IOLIB_DIR}/src/main/cpp
        ${CMAKE_CURRENT_LIST_DIR}
        ../../../../shared
)
add_library(audio
        SHARED
        DrumPadPlayerJNI.cpp
)
target_link_libraries(
        audio
        oboe
        parselib
        iolib
        log
)
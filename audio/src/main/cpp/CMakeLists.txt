cmake_minimum_required(VERSION 3.4.1)
project("audio")

set(OBOE_DIR ../../../../oboe)
set(PARSELIB_DIR ../../../../parselib)
set(IOLIB_DIR ../../../../iolib)

include_directories(
        ${OBOE_DIR}/src/main/cpp/src
        ${OBOE_DIR}/src/main/cpp/include/oboe
        ${PARSELIB_DIR}/src/main/cpp
        ${IOLIB_DIR}/src/main/cpp
        ${CMAKE_CURRENT_LIST_DIR}
        ../../../../shared)

add_library(audio SHARED audio.cpp)
target_link_libraries(audio log)

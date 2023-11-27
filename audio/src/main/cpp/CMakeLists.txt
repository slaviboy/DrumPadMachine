cmake_minimum_required(VERSION 3.4.1)
project("audio")

set(OBOE_DIR ../../../../oboe)
set(PARSELIB_DIR ../../../../parselib)
set(IOLIB_DIR ../../../../iolib)

#include(${PARSELIB_DIR}/src/main/cpp/CMakeLists.txt)
#include(${IOLIB_DIR}/src/main/cpp/CMakeLists.txt)
include_directories(
        audio
        ${OBOE_DIR}/src/main/cpp/include
        ${OBOE_DIR}/src/main/cpp/src/
        ${PARSELIB_DIR}/src/main/cpp
        ${IOLIB_DIR}/src/main/cpp
        ${CMAKE_CURRENT_LIST_DIR}
        ../../../../shared
)
add_library(audio
        SHARED
        audio.cpp
        #DrumPlayerJNI.cpp
)
target_link_libraries(audio
        log
)

#target_compile_options(audio PRIVATE -Wall -Werror "$<$<CONFIG:RELEASE>:-Ofast>")
#
#target_link_libraries( # Specifies the target library.
#        audio
#
#        -Wl,--whole-archive
#        iolib
#        parselib
#        oboe
#        -Wl,--no-whole-archive
#
#
#        # Links the target library to the log library
#        # included in the NDK.
#        log)
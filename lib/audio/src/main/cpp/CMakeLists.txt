cmake_minimum_required(VERSION 3.4.1)
project("audio")

include(../../../../parselib/src/main/cpp/CMakeLists.txt)
include(../../../../iolib/src/main/cpp/CMakeLists.txt)
include(../../../../oboe/src/main/cpp/CMakeLists.txt)

include_directories(
        audio
        ../../../../oboe/src/main/cpp/include
        ../../../../oboe/src/main/cpp/src
        ../../../../parselib/src/main/cpp
        ../../../../iolib/src/main/cpp
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
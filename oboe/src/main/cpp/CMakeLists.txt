cmake_minimum_required(VERSION 3.4.1)

# Set the name of the project and store it in PROJECT_NAME. Also set the following variables:
# PROJECT_SOURCE_DIR (usually the root directory where Oboe has been cloned e.g.)
# PROJECT_BINARY_DIR (usually the containing project's binary directory,
# e.g. ${OBOE_HOME}/samples/RhythmGame/.externalNativeBuild/cmake/ndkExtractorDebug/x86/oboe-bin)
project(oboe)

set(oboe_sources
        ${CMAKE_CURRENT_LIST_DIR}/src/aaudio/AAudioLoader.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/aaudio/AudioStreamAAudio.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/AdpfWrapper.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/AudioSourceCaller.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/AudioStream.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/AudioStreamBuilder.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/DataConversionFlowGraph.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/FilterAudioStream.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/FixedBlockAdapter.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/FixedBlockReader.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/FixedBlockWriter.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/LatencyTuner.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/OboeExtensions.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/SourceFloatCaller.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/SourceI16Caller.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/SourceI24Caller.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/SourceI32Caller.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/Utilities.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/QuirksManager.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/fifo/FifoBuffer.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/fifo/FifoController.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/fifo/FifoControllerBase.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/fifo/FifoControllerIndirect.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/FlowGraphNode.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/ChannelCountConverter.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/ClipToRange.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/Limiter.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/ManyToMultiConverter.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/MonoBlend.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/MonoToMultiConverter.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/MultiToManyConverter.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/MultiToMonoConverter.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/RampLinear.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/SampleRateConverter.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/SinkFloat.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/SinkI16.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/SinkI24.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/SinkI32.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/SourceFloat.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/SourceI16.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/SourceI24.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/SourceI32.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/resampler/IntegerRatio.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/resampler/LinearResampler.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/resampler/MultiChannelResampler.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/resampler/PolyphaseResampler.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/resampler/PolyphaseResamplerMono.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/resampler/PolyphaseResamplerStereo.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/resampler/SincResampler.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/flowgraph/resampler/SincResamplerStereo.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/opensles/AudioInputStreamOpenSLES.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/opensles/AudioOutputStreamOpenSLES.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/opensles/AudioStreamBuffered.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/opensles/AudioStreamOpenSLES.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/opensles/EngineOpenSLES.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/opensles/OpenSLESUtilities.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/opensles/OutputMixerOpenSLES.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/StabilizedCallback.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/Trace.cpp
        ${CMAKE_CURRENT_LIST_DIR}/src/common/Version.cpp
)

add_library(oboe ${oboe_sources})

# Specify directories which the compiler should look for headers
target_include_directories(oboe
        PRIVATE src
        PUBLIC include)

# Compile Flags:
#     Enable -Werror when building debug config
#     Enable -Ofast
target_compile_options(oboe
        PRIVATE
        -std=c++17
        -Wall
        -Wextra-semi
        -Wshadow
        -Wshadow-field
        "$<$<CONFIG:RELEASE>:-Ofast>"
        "$<$<CONFIG:DEBUG>:-O3>"
        "$<$<CONFIG:DEBUG>:-Werror>")

# Enable logging of D,V for debug builds
target_compile_definitions(oboe PUBLIC $<$<CONFIG:DEBUG>:OBOE_ENABLE_LOGGING=1>)

target_link_libraries(oboe PRIVATE log OpenSLES)

# When installing oboe put the libraries in the lib/<ABI> folder e.g. lib/arm64-v8a
install(TARGETS oboe
        LIBRARY DESTINATION lib/${ANDROID_ABI}
        ARCHIVE DESTINATION lib/${ANDROID_ABI})

# Also install the headers
install(DIRECTORY include/oboe DESTINATION include)
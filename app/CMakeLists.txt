cmake_minimum_required(VERSION 3.4.1)

project("channeldetails")

add_library( # Sets the name of the library.
        ${CMAKE_PROJECT_NAME} SHARED
        native-lib.cpp)

target_link_libraries( # Specifies the target library.
        ${CMAKE_PROJECT_NAME}
        android
        log)
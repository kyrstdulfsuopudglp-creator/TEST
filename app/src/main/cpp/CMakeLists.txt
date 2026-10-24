cmake_minimum_required(VERSION 3.22.1)
project("dfroot-exploit")

# Указываем CMake искать заголовочные файлы и ресурсы прямо в папке cpp
include_directories(${CMAKE_CURRENT_SOURCE_DIR})

# Добавляем все исходные файлы эксплойта
add_library(
        dfroot-exploit
        SHARED
        bootstrap.c
        exp.c
        elf_parser.c
        splicehelper.c
)

find_library(
        log-lib
        log
)

target_link_libraries(
        dfroot-exploit
        ${log-lib}
)
cmake_minimum_required(VERSION 3.22.1)
project("dfroot-exploit")

# Добавляем библиотеку
add_library(
        dfroot-exploit
        SHARED
        bootstrap.c
        exp.c
        elf_parser.c
        splicehelper.c
)

# САМОЕ ВАЖНОЕ: говорим ассемблеру (-Wa), где искать файлы для .incbin (папка ko и файлы в cpp)
target_compile_options(dfroot-exploit PRIVATE "-Wa,-I${CMAKE_CURRENT_SOURCE_DIR}")

# Подключаем системную библиотеку логов Android
find_library(
        log-lib
        log
)

target_link_libraries(
        dfroot-exploit
        ${log-lib}
)
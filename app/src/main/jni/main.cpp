/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

#include <jni.h>
#include <string>
#include <memory>

#include <dlfcn.h>

#include <elfio/elfio.hpp>
#include <elfio/elfio_dump.hpp>

using namespace ELFIO;

inline bool ends_with(std::string const & value, std::string const & ending)
{
    if (ending.size() > value.size()) return false;
    return std::equal(ending.rbegin(), ending.rend(), value.rbegin());
}

enum METHOD {
    KEY,
    IV
};

static std::string
symbol_tables( std::ostream& out, const elfio& reader, METHOD method)
{
    Elf_Half n = reader.sections.size();
    for ( Elf_Half i = 0; i < n; ++i ) {    // For all sections
        section* sec = reader.sections[i];
        if ( SHT_SYMTAB == sec->get_type() || SHT_DYNSYM == sec->get_type() ) {
            symbol_section_accessor symbols( reader, sec );

            Elf_Xword     sym_no = symbols.get_symbols_num();
            if ( sym_no > 0 ) {
                for ( Elf_Half i = 0; i < sym_no; ++i ) {
                    std::string   name;
                    Elf64_Addr    value   = 0;
                    Elf_Xword     size    = 0;
                    unsigned char bind    = 0;
                    unsigned char type    = 0;
                    Elf_Half      section = 0;
                    unsigned char other   = 0;
                    symbols.get_symbol( i, name, value, size, bind, type, section, other );
                    if (method == KEY && ends_with(name, "getDecryptionKey")) {
                        return name;
                    } else if (method == IV && ends_with(name, "getIVKey")) {
                        return name;
                    }
                }
            }
        }
    }

    return "";

}

extern "C" {
    JNIEXPORT jobjectArray JNICALL
    Java_com_jereksel_libresubstratum_domain_KeyFinderNative_getKeyAndIV(JNIEnv *env, jclass type,
                                                                         jstring location_);
};


JNIEXPORT jobjectArray JNICALL
Java_com_jereksel_libresubstratum_domain_KeyFinderNative_getKeyAndIV(JNIEnv *env, jclass type,
                                                                     jstring location_) {

    auto location = std::unique_ptr<const char, std::function<void(const char *)>>(env->GetStringUTFChars(location_, 0),
                                                                                   [=](char const* p) { env->ReleaseStringUTFChars(location_, p); });

    elfio reader;

    reader.load(location.get());

    auto keyFun = symbol_tables(std::cout, reader, KEY);
    auto ivFun = symbol_tables(std::cout, reader, IV);

    //dlsym has broken signature (void*, const char*, _Symbol)
    auto mydlsym = (void* (*)(void*, const char*))dlsym;
    auto mydlclose = (void (*)(void*))dlclose;

    auto lib = std::unique_ptr<void, std::function<void(void*)>>(dlopen(location.get(), RTLD_LAZY),
                                                                 [=](void* l) { mydlclose(l); });

    if (lib == nullptr) {
        return nullptr;
    }


    auto getKey = (jbyteArray(*)(JNIEnv*)) mydlsym(lib.get(), keyFun.data());
    auto getIV = (jbyteArray(*)(JNIEnv*)) mydlsym(lib.get(), ivFun.data());

    if (getKey == nullptr || getIV == nullptr) {
        return nullptr;
    }

    auto key = getKey(env);
    auto iv = getIV(env);

    jclass myClassArray = env->FindClass("[B");

    auto arr = env->NewObjectArray(2, myClassArray, NULL);

    env->SetObjectArrayElement(arr, 0, key);
    env->SetObjectArrayElement(arr, 1, iv);

    return arr;

}

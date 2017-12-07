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

package com.jereksel.libresubstratum.domain.overlayService.nougat

import android.os.IBinder
import android.os.IInterface
import android.os.Parcel

interface AidlIInterfacerInterface: IInterface {

    companion object {
        val DESCRIPTOR = "projekt.substratum.IInterfacerInterface";

        fun asInterface(obj: android.os.IBinder?): AidlIInterfacerInterface? {
            if (obj == null) {
                return null
            }
            val iin = obj.queryLocalInterface(DESCRIPTOR)
            return if (iin != null && iin is AidlIInterfacerInterface) {
                iin
            } else {
                Proxy(obj)
            }
        }
        class Proxy(
                private val mRemote: IBinder
        ) : AidlIInterfacerInterface {

            override fun asBinder() = mRemote

            fun getInterfaceDescriptor() = DESCRIPTOR

            override fun installPackages(files: List<String>) {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()

                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeStringList(files)
                    mRemote.transact(IBinder.FIRST_CALL_TRANSACTION + 0, data, reply, 0)
                    reply.readException()
                } finally {
                    reply.recycle()
                    data.recycle()
                }

            }

            override fun removePackages(packageIds: List<String>) {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()

                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeStringList(packageIds)
                    //We don't want to restart
                    data.writeInt(0)
                    mRemote.transact(IBinder.FIRST_CALL_TRANSACTION + 1, data, reply, 0)
                    reply.readException()
                } finally {
                    reply.recycle()
                    data.recycle()
                }

            }

            override fun restartSystemUI() {

                val data = Parcel.obtain()
                val reply = Parcel.obtain()

                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    mRemote.transact(IBinder.FIRST_CALL_TRANSACTION + 2, data, reply, 0)
                    reply.readException()
                } finally {
                    reply.recycle()
                    data.recycle()
                }

            }


            override fun enableOverlays(overlayIds: List<String>) {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()

                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeStringList(overlayIds)
                    //We don't want to restart
                    data.writeInt(0)
                    mRemote.transact(IBinder.FIRST_CALL_TRANSACTION + 7, data, reply, 0)
                    reply.readException()
                } finally {
                    reply.recycle()
                    data.recycle()
                }
            }

            override fun disableOverlays(overlayIds: List<String>) {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()

                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeStringList(overlayIds)
                    //We don't want to restart
                    data.writeInt(0)
                    mRemote.transact(IBinder.FIRST_CALL_TRANSACTION + 8, data, reply, 0)
                    reply.readException()
                } finally {
                    reply.recycle()
                    data.recycle()
                }

            }

            override fun changePriority(overlayIds: List<String>) {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()

                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeStringList(overlayIds)
                    //We don't want to restart
                    data.writeInt(0)
                    mRemote.transact(IBinder.FIRST_CALL_TRANSACTION + 9, data, reply, 0)
                    reply.readException()
                } finally {
                    reply.recycle()
                    data.recycle()
                }

            }
        }
    }

    fun installPackages(files: List<String>)

    fun removePackages(packageIds: List<String>)

    fun restartSystemUI()

    fun enableOverlays(overlayIds: List<String>)

    fun disableOverlays(overlayIds: List<String>)

    fun changePriority(overlayIds: List<String>)

}
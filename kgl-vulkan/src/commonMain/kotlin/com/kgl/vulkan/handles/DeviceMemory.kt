/**
 * Copyright [2019] [Dominic Fischer]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kgl.vulkan.handles

import com.kgl.vulkan.dsls.*
import com.kgl.vulkan.utils.*
import io.ktor.utils.io.bits.*

expect class DeviceMemory : VkHandle {
	val device: Device
	val size: ULong
	val memoryTypeIndex: UInt

	val commitment: ULong

	fun map(offset: ULong, size: ULong): Memory

	fun unmap()

	fun getFdKHR(block: MemoryGetFdInfoKHRBuilder.() -> Unit = {}): Int

	override fun close()
}

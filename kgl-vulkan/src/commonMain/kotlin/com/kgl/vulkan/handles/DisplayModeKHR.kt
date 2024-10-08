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
import com.kgl.vulkan.structs.*
import com.kgl.vulkan.utils.*

expect class DisplayModeKHR : VkHandle {
	val physicalDevice: PhysicalDevice

	val display: DisplayKHR

	fun getDisplayPlaneCapabilities(planeIndex: UInt): DisplayPlaneCapabilitiesKHR

	fun getDisplayPlaneCapabilities2(block: DisplayPlaneInfo2KHRBuilder.() -> Unit = {}): DisplayPlaneCapabilities2KHR

	override fun close()
}

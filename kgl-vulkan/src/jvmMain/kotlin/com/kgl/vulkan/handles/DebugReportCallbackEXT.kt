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

import com.kgl.vulkan.utils.*
import org.lwjgl.system.*
import org.lwjgl.vulkan.*
import org.lwjgl.vulkan.EXTDebugReport.*

actual class DebugReportCallbackEXT(
	override val ptr: Long,
	actual val instance: Instance,
	private val callback: VkDebugReportCallbackEXT
) : VkHandleJVM<Long>(), VkHandle {
	override actual fun close() {
		MemoryStack.stackPush()
		try {
			vkDestroyDebugReportCallbackEXT(instance.toVkType(), ptr, null)
		} finally {
			MemoryStack.stackPop()
			callback.close()
		}
	}
}

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

import com.kgl.core.*
import com.kgl.vulkan.utils.*
import cvulkan.*
import io.ktor.utils.io.bits.*
import kotlinx.cinterop.*

actual class DescriptorSet(
	override val ptr: VkDescriptorSet,
	actual val descriptorPool: DescriptorPool
) : VkHandleNative<VkDescriptorSet>(), VkHandle {
	internal val dispatchTable = descriptorPool.dispatchTable

	override actual fun close() {
		VirtualStack.push()
		try {
			val descriptorSet = VirtualStack.alloc<VkDescriptorSetVar> { value = this@DescriptorSet.ptr }
			dispatchTable.vkFreeDescriptorSets(descriptorPool.device.ptr, descriptorPool.ptr, 1u, descriptorSet.ptr)
		} finally {
			VirtualStack.pop()
		}
	}

	actual fun updateWithTemplate(descriptorUpdateTemplate: DescriptorUpdateTemplate, data: Memory) {
		VirtualStack.push()
		try {
			dispatchTable.vkUpdateDescriptorSetWithTemplate!!(
				descriptorPool.device.toVkType(), toVkType(),
				descriptorUpdateTemplate.toVkType(), data.pointer
			)
		} finally {
			VirtualStack.pop()
		}
	}
}

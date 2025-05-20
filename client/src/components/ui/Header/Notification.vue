<template>
  <div class="relative">
    <!-- Nút thông báo -->
    <a-badge :count="unreadNotifications.length">
      <a-dropdown placement="bottomRight" :trigger="['click']" :visible="showNotifications"
        @visibleChange="toggleNotifications">
        <template #overlay>
          <div class="w-72 bg-white border border-gray-200 rounded-lg shadow-lg overflow-hidden">
            <!-- Header Thông báo -->
            <div class="p-3 border-b flex items-center justify-between">
              <span class="font-semibold text-gray-700">Thông báo</span>
              <div class="flex items-center space-x-2">
                <span class="text-sm text-gray-600">Chỉ hiển thị chưa đọc</span>
                <a-switch v-model:checked="showOnlyUnread" @change="filterNotifications" />
              </div>
            </div>

            <!-- Danh sách thông báo -->
            <div class="py-2 text-gray-700 text-sm max-h-60 overflow-y-auto">
              <a-list v-if="filteredNotifications.length > 0" :data-source="filteredNotifications">
                <template #renderItem="{ item }">
                  <a-list-item class="px-4 py-3 border-b hover:bg-gray-100 transition"
                    @click="handleNotificationClick(item)">
                    <div>
                      <p class="font-medium">{{ item.content }}</p>
                      <p class="text-gray-500 text-xs">{{ getDateFormat(item.createdDate, true) }}</p>
                    </div>
                  </a-list-item>
                </template>
              </a-list>
              <p v-else class="text-center text-gray-500 py-3">Không có thông báo nào</p>
            </div>
          </div>
        </template>

        <a-button
          type="text"
          class="notification-button flex items-center justify-center"
          @click.prevent="toggleNotifications"
        >
          <BellOutlined class="notification-icon" />
        </a-button>
      </a-dropdown>
    </a-badge>

    <!-- Modal hiển thị chi tiết -->
    <TodoDetailModal v-model="showTaskDetailModal" :todoId="selectedTodoId" @close="closeModal" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { BellOutlined } from "@ant-design/icons-vue";
import { getAllNotificationMember, MBMeNotificationMemberResponse, updateStatusNotificationNumber } from "@/services/api/member/projectdetail/notification-member.api";
import { getDateFormat } from "@/utils/commom.helper";
import { localStorageAction } from "@/utils/storage";
import { USER_INFO_STORAGE_KEY } from "@/constants/storagekey";
import { webSocketNotificationMember } from "@/services/service/member/notification-member.socket";
import TodoDetailModal from "@/pages/member/project/projectdetails/projectmodal/TodoDetailModal.vue";
import { TODO_ID_STORAGE_KEY } from "@/constants/key";
import Role from "@/pages/admin/role/Role.vue";
import { ROLES } from "@/constants/roles";

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY);
const route = useRoute();
const router = useRouter();
const showNotifications = ref(false);
const showOnlyUnread = ref(false);
const notificationsMember = ref<MBMeNotificationMemberResponse[]>([]);
const unreadNotifications = computed(() => notificationsMember.value.filter(noti => noti.status === 0));
const filteredNotifications = computed(() => (showOnlyUnread.value ? unreadNotifications.value : notificationsMember.value));

const selectedTodoId = ref<string | null>(null);
const showTaskDetailModal = ref(false);

const fetchNotificationMember = async () => {
  try {
    const res = await getAllNotificationMember({ page: 1, size: 100, idMember: userLogin.userId });
    notificationsMember.value = res.data.data;
  } catch (err) {
    console.error("Lỗi khi lấy thông báo:", err);
  }
};

webSocketNotificationMember.getCreateNotificationMember((data) => {
  if(data == true){
    fetchNotificationMember();
  }
});

const handleNotificationClick = async (notification: MBMeNotificationMemberResponse) => {
  try {
    // Nếu thông báo chưa được đọc, cập nhật trạng thái
    if (notification.status === 0) {
      await updateStatusNotificationNumber(notification.id);
      fetchNotificationMember();
    }
    if (userLogin.roleScreen == ROLES.ADMIN) {
      await router.push("/manage" + notification.url);
    } else if (userLogin.roleScreen == ROLES.MANAGE) {
      await router.push("/manage" + notification.url);
    } else if (userLogin.roleScreen == ROLES.MEMBER) {
      await router.push("/member" + notification.url);
    }

    await nextTick();

  } catch (error) {
    console.error("Lỗi khi xử lý thông báo:", error);
  }
};

// Đóng modal và xóa `todoId` khỏi URL
const closeModal = () => {
  showTaskDetailModal.value = false;
  selectedTodoId.value = null;
  const query = { ...route.query };
  delete query.todoId;
  // Cập nhật URL mà không reload trang
  router.replace({ query });
  localStorageAction.remove(TODO_ID_STORAGE_KEY)
};

const toggleNotifications = (visible?: boolean) => {
  showNotifications.value = visible !== undefined ? visible : !showNotifications.value;
};

onMounted(fetchNotificationMember);
webSocketNotificationMember.getNotificationMember(fetchNotificationMember);
</script>


<style scoped>
.notification-button {
  width: 42px; /* Đảm bảo kích thước nút cố định */
  height: 42px;
  border-radius: 50%;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s;
}

.notification-button:hover {
  background-color: #e0e0e0;
}

.notification-icon {
  font-size: 20px; /* Kích thước icon */
  color: #4a4a4a; /* Màu sắc icon */
}
</style>
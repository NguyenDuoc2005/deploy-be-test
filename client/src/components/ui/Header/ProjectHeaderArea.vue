<template>
  <div>
    <!-- Khi đang mở -->
    <div v-if="isExpanded"
      class="w-full bg-white/40 backdrop-blur-md shadow-md rounded-b-lg p-2 transition-all duration-300">
      <div class="flex flex-wrap justify-between items-center py-1 gap-2 sm:gap-4">
        <div class="flex items-center gap-2 text-sm font-medium text-gray-800 px-2 sm:px-4">
          <!-- Nút thu gọn ở góc phải -->
          <a-button @click="isExpanded = false" class="bg-slate-200 hover:bg-slate-300 border-none font-medium text-xs">
            <MenuFoldOutlined />
          </a-button>
          
          <a-button @click="handleToggleView"
            class="bg-slate-200 hover:bg-slate-300 border-none font-medium flex items-center">
            <span v-if="isTableView" class="hidden sm:flex">
              <AppstoreAddOutlined class="mr-2" />
              Chuyển sang Kanban
            </span>
            <span v-else class="hidden sm:flex">
              <TableOutlined class=" mr-2" />
              Chuyển sang Bảng

            </span>
          </a-button>
        </div>

        <div class="flex flex-wrap items-center gap-2 sm:gap-4">
          <a-button @click="showSortModal = !showSortModal"
            class="bg-slate-200 hover:bg-slate-300 border-none font-medium flex items-center space-x-2">
            <SortAscendingOutlined />
            <p class="hidden sm:block m-0">Sắp xếp</p>
          </a-button>
          <SortModal :isOpen="showSortModal" @select="handleSortSelect" />

          <a-button @click="handleOpenFilter"
            class="bg-slate-200 hover:bg-slate-300 border-none font-medium flex items-center space-x-2">
            <FilterOutlined />
            <p class="hidden sm:block">Bộ lọc</p>
          </a-button>
          <FilterModal :isOpen="showFilterModal" @close="showFilterModal = false" />

          <ThreeDotsMenu />
        </div>
      </div>
    </div>

    <!-- Khi thu gọn: chỉ hiện nút mở rộng -->
    <div v-else
      class="w-fit h-10 bg-white/40 backdrop-blur-md shadow-md rounded-b-lg p-2 flex items-center transition-all duration-300">
      <a-button @click="isExpanded = true" class="bg-slate-200 hover:bg-slate-300 border-none font-medium text-xs">
        <DoubleLeftOutlined style="transform: rotate(180deg);" />
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, computed, ref } from 'vue'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import { USER_TYPE } from '@/constants/userType'
import { useRoute } from 'vue-router'
import ThreeDotsMenu from './ThreeDotsMenu.vue'
import SortModal from '@/pages/member/project/projectdetails/SortModal.vue'
import FilterModal from '@/pages/member/project/projectdetails/FilterModal.vue'
import {
  AppstoreAddOutlined,
  DoubleLeftOutlined,
  FilterOutlined,
  MenuFoldOutlined,
  SortAscendingOutlined,
  TableOutlined
} from '@ant-design/icons-vue'

const route = useRoute()
const showSortModal = ref(false)
const showFilterModal = ref(false)
const isExpanded = ref(true) // Thêm state thu gọn / mở rộng

const handleSortSelect = (value: string) => {
  console.log('Bạn chọn:', value)
  showSortModal.value = false
}

const handleOpenFilter = () => {
  showFilterModal.value = true
}

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)

const props = defineProps<{ isTableView: boolean; idProject: string }>()
const emit = defineEmits(['toggleView', 'showStatistics'])

const projectRoute = computed(() => {
  if (userLogin.roleScreen === USER_TYPE.ADMIN) {
    return { name: ROUTES_CONSTANTS.ADMIN.children.PROJECT.name }
  } else if (userLogin.roleScreen === USER_TYPE.MANAGE) {
    return { name: ROUTES_CONSTANTS.MANAGE.children.PROJECT.name }
  } else {
    return { name: ROUTES_CONSTANTS.MEMBER.children.MYPROJECT.name }
  }
})

const projectPhaseRoute = computed(() => {
  if (userLogin.roleScreen === USER_TYPE.ADMIN || userLogin.roleScreen === USER_TYPE.MANAGE) {
    return {
      name: `${ROUTES_CONSTANTS.MANAGE.children.TODO.name}`,
      params: { id: route.params.id }
    }
  } else {
    return {
      name: `${ROUTES_CONSTANTS.PROJECT.children.VOTE_TODO.name}`,
      params: { id: route.params.id }
    }
  }
})

const projectLabelPhase = computed(() => {
  return userLogin.roleScreen === USER_TYPE.MEMBER
    ? 'Bình chọn công việc'
    : 'Giai đoạn dự án '
})

const projectLabel = computed(() => {
  if (userLogin.roleScreen === USER_TYPE.ADMIN) return 'Quản lý tất cả dự án'
  if (userLogin.roleScreen === USER_TYPE.MANAGE) return 'Dự án tôi quản lý'
  return 'Dự án tham gia'
})

const projectName = computed(() => props.idProject || 'Chưa xác định')

const handleToggleView = () => {
  emit('toggleView')
}
</script>
<template>
  <DivCustom label="Danh sách dự án" customClasses="mt-5">
       <template #icon>
    <ProjectOutlined />
      </template>
    <div class="space-y-4 p-4 min-h-[360px]">
      <a-card
        v-for="item in props.data"
        :key="item.id"
        class="shadow-md rounded-lg border p-1 hover:shadow-xl hover:rounded-xl hover:border transition duration-300"
        hoverable
        :style="{ minHeight: '200px' }"
        @click="handleDetailClick(item.id)"
      >
        <div class="flex items-center justify-between">
          <h3 class="font-semibold text-lg text-gray-800">
            {{ `${item.nameProject} - Bộ môn: ${item.nameDepartment}` }}
          </h3>
          <div>
            <!-- <a-tooltip title="Chi tiết dự án">
              <font-awesome-icon
                :icon="['fas', 'eye']"
                class="cursor-pointer text-gray-500 hover:text-blue-600 transition duration-300 mr-5"
                style="font-size: 18px"
                @click="handleDetailClick(item.id)"
              />
            </a-tooltip> -->

            <!-- <a-tooltip title="Quản lí task theo dự án">
              <a-icon @click="handleNavigateToTodoList(item.id)">
                <font-awesome-icon
                  :icon="['fas', 'circle-info']"
                  class="cursor-pointer text-gray-500 hover:text-blue-600 transition duration-300 pl-4"
                  style="font-size: 18px"
                />
              </a-icon>
            </a-tooltip> -->
          </div>
        </div>
        <hr class="my-2 border-gray-300" />

        <div class="flex justify-between items-start">
          <div class="flex-1 pr-6">
            <div class="text-gray-600 text-sm font-medium">
              <span class="mr-4"
                >Mã dự án:
                <span class="text-gray-900 font-semibold">{{ item.codeProject }}</span></span
              >
              <span
                >Ngày tạo:
                <span class="text-gray-900 font-semibold">{{
                  formatDate(item.createdDateProject)
                }}</span></span
              >
            </div>
            <div class="grid grid-cols-3 gap-4 mt-2">
              <div>
                <p class="text-gray-600 font-medium">Ngày bắt đầu</p>
                <a-tag color="blue">{{ formatDate(item.startTimeProject) }}</a-tag>
              </div>
              <div>
                <p class="text-gray-600 font-medium">Ngày kết thúc</p>
                <a-tag color="red">{{ formatDate(item.endTimeProject) }}</a-tag>
              </div>
              <div>
                <p class="text-gray-600 font-medium">Tình trạng</p>
                <a-tag :color="getStatusColor(item.status)">{{
                  item.status == 'DANG_DIEN_RA'
                    ? 'Đang diễn ra'
                    : item.status == 'CHUA_DIEN_RA'
                    ? 'Chưa diễn ra'
                    : 'Đã kết thúc'
                }}</a-tag>
              </div>
            </div>
          </div>

          <!-- Tiến độ -->
          <div class="w-1/4 flex flex-col items-center">
            <p class="text-gray-600 font-medium mt-7">Tiến độ</p>
            <a-progress :percent="Math.floor(item.progressProject)" show-info class="w-full" />
          </div>
        </div>
      </a-card>

      <!-- Phân trang -->
      <div class="flex justify-center mt-4">
        <a-pagination
          :current="page"
          :page-size="pageSize"
          :total="totalItems"
          show-size-changer
          :page-size-options="['5', '10', '15', '20']"
          @change="handlePageChange"
        />
      </div>
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, computed, ref } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { router } from '@/routes/router'
import {
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight,
  faTrash,
  faTrashAlt,
  faEye
} from '@fortawesome/free-solid-svg-icons'
library.add(
  faPenToSquare,
  faReceipt,
  faCircleInfo,
  faFilter,
  faRotateRight,
  faTrash,
  faTrashAlt,
  faEye
)
import { useRouter } from 'vue-router'
import { AppstoreAddOutlined, BarsOutlined, ClusterOutlined, FileAddOutlined } from '@ant-design/icons-vue'

// Props
const props = defineProps<{
  data: Array<{
    id: string
    codeProject: string
    nameProject: string
    nameDepartment: string
    status: string
    idFacility: string
    backgroundColorProject: string
    bachbackgroundImageProject: string
    progressProject: number
    startTimeProject: number
    endTimeProject: number
    createdDateProject: number
  }>
  totalItems: number
}>()

const emit = defineEmits(['view', 'page-change'])

const page = ref(1)
const pageSize = ref(5)

// const paginatedData = computed(() => {
//   const start = (page.value - 1) * pageSize.value
//   return props.data.slice(start, start + pageSize.value)
// })

const handlePageChange = (current: number, size: number) => {
  page.value = current
  pageSize.value = size
  emit('page-change', { page: current, pageSize: size })
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'CHUA_DIEN_RA':
      return 'red'
    case 'DANG_DIEN_RA':
      return 'green'
    case 'DA_DIEN_RA':
      return 'orange'
    default:
      return 'gray'
  }
}

const getProgressColor = (progress: number) => {
  if (progress < 30) return 'red'
  if (progress < 70) return 'orange'
  return 'green'
}

const formatDate = (timestamp: number) => {
  if (!timestamp) return 'N/A'
  const date = new Date(timestamp)
  return date.toLocaleDateString('vi-VN')
}

const handleViewClick = (id: string, idFacility: string) => {
  emit('view', id, idFacility)
}

const handleDetailClick = (id: string) => {
  router.push({
    name: `${ROUTES_CONSTANTS.PROJECT.children.VOTE_TODO.name}`,
    params: { id }
  })
}

// const router = useRouter()
// const handleNavigateToTodoList = (projectId: string) => {
//   router.push({
//     path: ROUTES_CONSTANTS.MANAGE.children.TODOLIST.path,
//     query: { projectId }
//   })
// }
</script>

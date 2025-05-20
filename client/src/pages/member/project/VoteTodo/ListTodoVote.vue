<template>
  <!-- Voting Section -->
  <section class="bg-white border-gray-200 transition-all duration-300">
    <header
      class="flex justify-between items-center p-5 bg-gradient-to-r text-white rounded-t-2xl cursor-pointer hover:opacity-95 transition"
      style="background-color: #edfffd !important"
      @click="toggleVotingSection"
    >
      <div class="flex items-center gap-2">
        <BarChartOutlined class="text-xl" />
        <h2 class="text-lg font-semibold text-black tracking-wide">Thành viên bình chọn</h2>
      </div>
      <span
        class="text-sm font-medium px-3 py-1 rounded-full"
        style="background-color: #99c6ff !important"
      >
        {{ isVotingSectionOpen ? 'Ẩn đi' : 'Hiển thị' }}
      </span>
    </header>

    <transition name="fade">
      <div v-if="isVotingSectionOpen" class="p-6 bg-gray-50 rounded-b-2xl">
        <StaffStudentVoteTable
          :searchQuery="state.searchQuery"
          :staffstudent="state.staffstudent"
          :paginationParams="state.paginationParams"
          :totalItems="state.totalItems"
          @page-change="handlePageChange"
        />
      </div>
    </transition>
  </section>

  <!-- Filters Section -->
  <DivCustom label="Bộ lọc" customClasses="mb-4 mt-4">
    <template #icon>
      <FilterOutlined />
    </template>
    <div class="flex flex-col md:flex-row gap-4">
      <!-- Mức độ task -->
      <a-select
        placeholder="Mức độ task"
        class="w-full md:w-1/3 rounded-lg"
        v-model:value="valueLevel"
        allowClear
      >
        <a-select-option value="3">Thấp</a-select-option>
        <a-select-option value="2">Trung bình</a-select-option>
        <a-select-option value="1">Cao</a-select-option>
        <a-select-option value="0">Quan trọng</a-select-option>
      </a-select>

      <!-- Nhập tên công việc -->
      <a-input
        v-model:value="paramsTodo.search"
        placeholder="Nhập tên công việc"
        allowClear
        class="w-full md:w-2/3 rounded-lg"
      />
    </div>
  </DivCustom>
  <DivCustom label="Danh sách công việc" customClasses="mb-4 mt-4">
    <template #icon>
      <UnorderedListOutlined />
    </template>
    <!-- check -->
    <div
      class="mb-4 p-4 rounded-lg border-l-4 shadow-sm flex items-start gap-3"
      :class="
        stageVote
          ? 'border-l-green-500 bg-green-50 text-green-900'
          : 'border-l-red-400 bg-red-50 text-red-900'
      "
    >
      <template v-if="!stageVote">
        <svg
          class="w-6 h-6 text-red-500 mt-0.5"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M12 9v2m0 4h.01M5.07 5.07a10 10 0 0113.86 0M5.07 18.93a10 10 0 0013.86 0M12 5v.01M12 19v.01M5 12H5.01M19 12h.01"
          />
        </svg>
      </template>

      <div class="flex-1 text-sm sm:text-base leading-relaxed">
        <template v-if="stageVote">
          <span class="block font-semibold text-green-700 mb-1">🗳️ Giai đoạn bình chọn</span>
          <div class="text-gray-800">
            <span class="font-medium">{{ stageVote.namePhaseProject }}</span>
            <span class="ml-2 text-sm text-gray-600">
              ({{ formatDateTime(stageVote.startTime) }} – {{ formatDateTime(stageVote.endTime) }})
            </span>
            <!-- Badge -->
            <span
              class="ml-3 px-2 py-0.5 rounded-full text-xs font-semibold bg-green-200 text-green-800"
            >
              ĐANG DIỄN RA
            </span>
          </div>
        </template>

        <template v-else>
          <span class="block font-semibold text-red-700 mb-1">⚠️ Chưa có giai đoạn bình chọn</span>
          <span class="text-red-800">
            Hiện tại bạn <strong>chưa thể bình chọn</strong> công việc do chưa có giai đoạn bình
            chọn nào đang diễn ra.
          </span>
          <!-- Badge -->
          <div class="mt-2">
            <span
              class="inline-block px-2 py-0.5 rounded-full text-xs font-semibold bg-red-200 text-red-800"
            >
              CHƯA DIỄN RA
            </span>
          </div>
        </template>
      </div>
    </div>

    <!-- table -->
    <div v-if="todoList.length" class="overflow-x-auto rounded-lg">
      <table class="min-w-full text-xs sm:text-sm text-left">
        <thead class="bg-gray-50 text-gray-700 font-semibold tracking-wide">
          <tr>
            <th class="px-2 sm:px-3 py-2 border-b whitespace-nowrap">STT</th>
            <th class="px-2 sm:px-3 py-2 border-b whitespace-nowrap">Tên Công Việc</th>
            <th class="px-2 sm:px-3 py-2 border-b whitespace-nowrap">Ngày Tạo</th>
            <th class="px-2 sm:px-3 py-2 border-b whitespace-nowrap">Độ Ưu tiên</th>
            <th class="px-2 sm:px-3 py-2 border-b whitespace-nowrap">% Bình chọn</th>
            <th v-if="stageVote" class="px-2 sm:px-3 py-2 border-b text-center whitespace-nowrap">
              Bình chọn
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(task, index) in todoList" :key="task.id" class="hover:bg-gray-50 transition">
            <td class="px-2 sm:px-3 py-2 border-b text-gray-500">{{ index + 1 }}</td>
            <td class="px-2 sm:px-3 py-2 border-b">
              <template v-if="editedTaskId === task.id">
                <input
                  v-model="editedTaskName[task.id]"
                  class="w-full px-2 py-1 border rounded text-xs sm:text-sm"
                />
              </template>
              <template v-else>
                <span class="font-medium text-gray-800">{{ task.name }}</span>
              </template>
            </td>
            <td class="px-2 sm:px-3 py-2 border-b text-gray-500 whitespace-nowrap">
              {{ formatDate(Number(task.createdDate ?? task.date)) }}
            </td>
            <td class="px-2 sm:px-3 py-2 border-b">
              <span
                class="inline-block px-2 py-0.5 rounded-full text-[10px] sm:text-xs font-semibold"
                :class="{
                  'bg-green-100 text-green-700': valuePriorityLevel[task.id] == 3,
                  'bg-yellow-100 text-yellow-700': valuePriorityLevel[task.id] == 2,
                  'bg-orange-100 text-orange-700': valuePriorityLevel[task.id] == 1,
                  'bg-red-100 text-red-700': valuePriorityLevel[task.id] == 0
                }"
              >
                {{
                  valuePriorityLevel[task.id] == 3
                    ? 'Thấp'
                    : valuePriorityLevel[task.id] == 2
                    ? 'Trung bình'
                    : valuePriorityLevel[task.id] == 1
                    ? 'Cao'
                    : 'Quan trọng'
                }}
              </span>
            </td>
            <td class="px-2 sm:px-3 py-2 border-b">
              <a-progress :percent="percentMap[task.id] || 0" size="small" stroke-color="#3B82F6" />
            </td>
            <td v-if="stageVote" class="px-2 sm:px-3 py-2 border-b text-center">
              <a-checkbox
                :checked="isTaskChecked(task.id)"
                @change="handleCheckboxChange(task.id)"
              />
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else class="mt-6">
      <a-empty description="Không có công việc nào" />
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import StaffStudentVoteTable from './StaffStudentVoteTable.vue'
import {
  todoResponse,
  getAllStaffProject,
  getAllUserByTodo,
  getAllTodo
} from '@/services/api/manage/todo/todo.api'
import { nextTick, onMounted, reactive, ref, watch, watchEffect } from 'vue'
import { toast } from 'vue3-toastify'
import draggable from 'vuedraggable'
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
import { library } from '@fortawesome/fontawesome-svg-core'
library.add(
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight,
  faTrash,
  faTrashAlt,
  faEye
)
import { dataCreateTodo, todoWebSocket } from '@/services/api/manage/todo/todo.socket.api'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import { todoVoteWebSocket } from '@/services/api/manage/todo/todovote.socket.api'
import { debounce } from 'lodash'
import { websocketService } from '@/services/configsocket/websocket'
import {
  getStaffStudentVote,
  getTodoVoteByStaffProject,
  GetUpcomingVote,
  getVotingIsOnGoing,
  StaffStudentVoteResponse
} from '@/services/api/member/votetodo/votetodo.api'
import { useRoute } from 'vue-router'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { formatDateTime } from '@/utils/commom.helper'
import { FilterOutlined, UnorderedListOutlined } from '@ant-design/icons-vue'
// Biến để quản lý trạng thái mở/đóng của phần "Thành viên bình chọn"
const isVotingSectionOpen = ref(false)

// Hàm để thay đổi trạng thái mở/đóng
function toggleVotingSection() {
  isVotingSectionOpen.value = !isVotingSectionOpen.value
}
const router = useRoute()

const state = reactive({
  searchQuery: '',
  staffstudentId: null as string | null,
  staffstudent: [] as StaffStudentVoteResponse[],
  paginationParams: { page: 1, size: 10 },
  totalItems: 0
})

const fetchStaffStudentVote = async () => {
  try {
    const response = await getStaffStudentVote(router.params.id as string)
    state.staffstudent = response?.data?.data || []
    state.totalItems = response?.data?.totalElements || 0
    console.log('danh sách: ', response.data)
  } catch (error) {
    console.error('Lỗi khi tải danh sách bộ môn:', error)
  }
}

onMounted(fetchStaffStudentVote)

const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  state.paginationParams.page = page
  if (pageSize) {
    state.paginationParams.size = pageSize
  }
  fetchStaffStudentVote()
}

const props = defineProps<{
  todos: todoResponse[]
  idProject: string
}>()
const emit = defineEmits(['update-todos', 'level', 'search'])
const paramsTodo = reactive({
  page: 1,
  size: 10,
  idProject: '',
  totalItem: '',
  level: '',
  search: ''
})

const valuePriorityLevel = reactive({})
const todoList = ref([...props.todos])
const checkedTasks = ref(new Set<string | undefined>())
const stageVote = ref({})
const phaseStageVote = ref([])
const countStaffProject = ref(0)

watchEffect(() => {
  todoList.value = [...props.todos]
})

// Gửi sự kiện cập nhật Todo

const sortTasksByProgress = debounce(() => {
  props.todos.sort((a, b) => {
    const percentA = percentMap[a.id as string] || 0
    const percentB = percentMap[b.id as string] || 0
    return percentB - percentA // Sắp xếp giảm dần theo progress
  })
}, 1000)

const debouncedUpdatePercentVoted = debounce((id: string) => {
  updatePercentVoted(id)
}, 100)

const dataFecth = async () => {
  try {
    const res = await getAllTodo(paramsTodo)
    todoList.value = res.data as unknown as todoResponse[]
  } catch (error) {
    console.error('❌ Lỗi khi lấy dữ liệu todo:', error)
  }
}

const fecthVotingIsOnGoing = async () => {
  try {
    const payload = {
      idProject: router.params.id
    }
    const res = await getVotingIsOnGoing(payload)
    stageVote.value = res.data
    console.log('getVotingIsOnGoing', res.data)
  } catch (error) {
    console.error('❌ Lỗi khi lấy dữ liệu todo:', error)
  }
}
const fecthGetUpcomingVote = async () => {
  try {
    const payload = {
      idProject: router.params.id
    }
    const res = await GetUpcomingVote(payload)
    phaseStageVote.value = res.data
    console.log('fecthGetUpcomingVote', res.data)
  } catch (error) {
    console.error('❌ Lỗi khi lấy dữ liệu todo:', error)
  }
}
const getTodoIdByStaffProject = async () => {
  const user = localStorageAction.get(USER_INFO_STORAGE_KEY)
  const idUser = user.userId

  const res = await getTodoVoteByStaffProject(props.idProject, idUser)

  console.log(res.data)

  for (const idTodo of res.data) {
    checkedTasks.value.add(idTodo)
  }
}

const handleCheckboxChange = async (id: string) => {
  if (checkedTasks.value.has(id)) {
    checkedTasks.value.delete(id)

    await todoVoteWebSocket.sendMessageDeleteTodoVote({
      idUser: user.userId,
      idTodo: id,
      idProject: props.idProject
    })
    setTimeout(() => {
      fetchStaffStudentVote()
    }, 300)
    console.log('đã vào đây delete vote')

    debouncedUpdatePercentVoted(id)
  } else {
    checkedTasks.value.add(id)
    await todoVoteWebSocket.sendMessageCreateTodoVote({
      idUser: user.userId,
      idTodo: id,
      idProject: props.idProject
    })
    setTimeout(() => {
      fetchStaffStudentVote()
    }, 300)

    console.log('đã vào đây create vote')
    // Cập nhật lại phần trăm bình chọn ngay lập tức
    debouncedUpdatePercentVoted(id)
  }
}

const isTaskChecked = (id: string) => {
  return checkedTasks.value.has(id)
}

const percentMap = reactive<Record<string, number>>({})
const updatePercentVoted = async (id: string) => {
  if (!id || countStaffProject.value === 0) {
    percentMap[id] = 0
    return
  }

  console.log(`📡 Gọi API getAllUserByTodo cho Task ID: ${id}`)

  try {
    const res = await getAllUserByTodo(paramsTodo, id)

    console.log(`✅ Dữ liệu trả về cho Task ID: ${id}:`, res.data)

    if (!res || !res.data || !res.data.content) {
      console.error(`❌ Lỗi: Không có dữ liệu hợp lệ cho Task ID: ${id}`)
      return
    }

    const votedPercent = Math.floor((res.data.content.length / countStaffProject.value) * 100)

    console.log(`📊 Phần trăm cập nhật cho Task ID ${id}: ${votedPercent}%`)

    percentMap[id] = votedPercent as number

    sortTasksByProgress()
  } catch (error) {
    console.error(`❌ Lỗi khi gọi API getAllUserByTodo cho Task ID ${id}:`, error)
  }
}

const formatDate = (timestamp) => {
  const date = new Date(timestamp)
  const day = String(date.getDate()).padStart(2, '0')
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const year = date.getFullYear()
  return `${day}/${month}/${year}`
}

const hoveredTask = ref<number | null>(null)
const hoveredTaskId = ref<number | null>(null)
const editedTaskId = ref<string | null>(null)
// const editedTaskName = ref<string>('')
const editedTaskName = reactive<{ [key: string]: string }>({})

const user = localStorageAction.get(USER_INFO_STORAGE_KEY)
// Xóa task

// Dữ liệu

const fetchDataTodo = debounce(async () => {
  try {
    const resData = await getAllStaffProject(props.idProject)
    // const res = await getAllTodo(paramsTodo)
    countStaffProject.value = resData.data.length as number

    await Promise.all(
      props.todos.map(async (todo) => {
        console.log(`🚀 Gọi API updatePercentVoted cho ID: ${todo.id}`)
        return updatePercentVoted(todo.id as string)
      })
    )

    for (const todo of props.todos) {
      valuePriorityLevel[todo.id as string] =
        todo.priorityLevel === 'CAO'
          ? '1'
          : todo.priorityLevel === 'THAP'
          ? '3'
          : todo.priorityLevel === 'TRUNG_BINH'
          ? '2'
          : todo.priorityLevel === 'QUAN_TRONG'
          ? '0'
          : null

      editedTaskName[todo.id as string] = todo.name
    }

    sortTasksByProgress()
  } catch (error) {
    console.error('❌ Lỗi khi lấy dữ liệu todo:', error)
  }
}, 50)

onMounted(async () => {
  dataFecth()
  fecthVotingIsOnGoing()
  fecthGetUpcomingVote()
  paramsTodo.search = ''
  valueLevel.value = null
  await fetchDataTodo()
  paramsTodo.idProject = props.idProject

  todoVoteWebSocket.subscribeCreateTodoVote((data) => {
    console.log('dữ liệu ', data)
    if (data.body.status == 'NOT_FOUND') {
      toast.error(data.body.message)
    }
    fetchDataTodo()
  })
  //   subscribeCreateTodoVote((data) => {
  //     // Giả sử server trả về dữ liệu là một đối tượng JSON
  //     console.log('Dữ liệu nhận được:', data);

  //     // Kiểm tra dữ liệu có đúng không
  //     if (data && data.id && data.name) {
  //         // Tiến hành xử lý với dữ liệu nhận được
  //         console.log('Đã nhận todo vote với ID:', data.id);
  //         console.log('Tên Todo Vote:', data.name);
  //     } else {
  //         console.error('Dữ liệu không hợp lệ:', data);
  //     }
  // });
  todoVoteWebSocket.subscribeDeleteTodoVote(fetchDataTodo)
  todoWebSocket.subscribeCreateTodo(fetchDataTodo)
  todoWebSocket.subscribeDeleteTodo(fetchDataTodo)
  paramsTodo.idProject = props.idProject
  getTodoIdByStaffProject()
})

const valueLevel = ref(null)

const handlePriorityChange = (id: string, value: string) => {
  todoWebSocket.sendMessageUpdateTodo(id, {
    name: editedTaskName[id], // Nếu chưa chỉnh sửa thì giữ nguyên
    code: editedTaskName[id], // N
    priorityLevel: value
  })

  sortTasksByProgress()
  toast.success('Cập nhật mức độ thành công')
}

// Cập nhật Task

watch(valueLevel, (newValue) => {
  emit('level', newValue)
})

watch(
  () => paramsTodo.search,
  (newValue) => {
    console.log('SDfsdf')

    console.log(newValue)

    emit('search', newValue)
  }
)
</script>

<style scoped>
.cursor-grab {
  cursor: grab;
}
</style>

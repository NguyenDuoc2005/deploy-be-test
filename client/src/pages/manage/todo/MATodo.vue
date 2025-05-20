<template>
  <!-- Thanh header cố định -->
  <div
    class="sticky top-0 w-full bg-white shadow-md h-40 flex justify-between items-center px-4 z-1"
  >
    <div class="w-full mt-10">
      <h1 class="font-semibold">Tên Dự án : {{ valueProject?.name }}</h1>
      <h1 class="font-bold">
        <span
          :class="{
            'text-gray-500': valueProject?.statusProject === 'CHUA_DIEN_RA', // Màu xám cho 'Chưa diễn ra'
            'text-blue-500': valueProject?.statusProject === 'DANG_DIEN_RA', // Màu xanh cho 'Đang diễn ra'
            'text-red-500': valueProject?.statusProject === 'DA_DIEN_RA' // Màu đỏ cho 'Đã diễn ra'
          }"
        >
          {{
            valueProject?.statusProject === 'CHUA_DIEN_RA'
              ? 'Chưa diễn ra'
              : valueProject?.statusProject === 'DANG_DIEN_RA'
              ? 'Đang diễn ra'
              : 'Đã diễn ra'
          }}
        </span>
      </h1>
      <h1 class="text-blue-600 font-medium top-0"></h1>
      <br />
      <div class="flex justify-between w-full items-center">
        <div class="items-center flex justify-normal w-1/2">
          <a-input placeholder="Tìm kiếm" class="w-1/4" v-model:value="search">
            <template #prefix>
              <SearchOutlined class="text-gray-400" />
            </template>
          </a-input>
          <div class="flex items-center ml-5">
            <label class="font-semibold">Thành viên : </label>
            <a-avatar-group
              :max-count="3"
              :max-style="{ color: '#f56a00', backgroundColor: '#fde3cf' }"
            >
              <a-tooltip
                v-for="(user, index) in dataStaffProject"
                :key="index"
                :title="user.staff == null ? user.student.name : user.staff.name"
                placement="top"
              >
                <a-avatar :src="user.staff == null ? user.student.image : user.staff.picture" />
              </a-tooltip>
            </a-avatar-group>
          </div>
        </div>
        <div class="flex justify-around items-center">
          <!-- <DatabaseOutlined class="font-bold mt-1 mr-2" /> -->
          <a-dropdown>
            <a-button
              class="border-none font-medium flex ml-5 mb-2"
              style="
                box-shadow: none !important; /* Không có shadow */
                font-weight: 500;
                background: #e6f4ff !important; /* Nền xanh nhạt khi hover */
                color: #1890ff !important; /* Đảm bảo màu chữ khi hover */
                border-color: #1890ff !important; /* Đảm bảo viền khi hover */
              "
            >
              <LikeOutlined class="font-bold mt-1 mr-1" />
              Bình chọn
            </a-button>
            <template #overlay>
              <a-menu @click="handleDropdownClick">
                <a-menu-item key="1" class="font-semibold">Tạo cuộc bình chọn</a-menu-item>
                <a-menu-item key="2">Chi tiết cuộc bình chọn</a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>

          <!-- <a-button
            @click="goToStatistics"
            class="border-none font-medium flex mb-2 ml-5"
            style="
              box-shadow: none !important; /* Không có shadow */
              font-weight: 500;
              background: #e6f4ff !important; /* Nền xanh nhạt khi hover */
              color: #1890ff !important; /* Đảm bảo màu chữ khi hover */
              border-color: #1890ff !important; /* Đảm bảo viền khi hover */
            "
          >
            <LineChartOutlined class="font-bold mt-1 mr-2" />
            <p>Thống kê</p>
          </a-button> -->
          <a-button
            @click="goToSprint"
            class="border-none font-medium flex ml-5 mb-2"
            style="
              box-shadow: none !important; /* Không có shadow */
              font-weight: 500;
              background: #e6f4ff !important; /* Nền xanh nhạt khi hover */
              color: #1890ff !important; /* Đảm bảo màu chữ khi hover */
              border-color: #1890ff !important; /* Đảm bảo viền khi hover */
            "
          >
            <DatabaseOutlined class="font-bold mt-1 mr-2" />
            <p>sprint hoàn thành</p>
          </a-button>
        </div>
      </div>
    </div>
  </div>
  <div class="h-full">
    <DivCustomOld class="flex min-h-screen">
      <!-- Cột trái -->

      <div :class="showStageVote ? 'w-3/5' : 'w-full'">
        <Sprint
          :search="search"
          :id-project="idProject"
          :id-todo="idTodo"
          :dataTodo="dataSprint.dataSprint"
          :sprints="sprints"
          @update-sprint-todos="updateSprintTodos"
          @remove-task-from-sprint="removeTaskFromSprint"
        />

        <Todo
          :todos="dataTodo.data"
          @update-todos="updateTodos"
          :idProject="idProject"
          @data-staff-project="handleDatastaff"
          @idTodo="idTodoModal"
        />
      </div>
      <!-- Cột phải -->
      <DivCustom v-if="showStageVote" class="w-2/5 ml-4 relative">
        <a-button
          @click="closeStageVote"
          style="
            position: absolute !important;
            top: 0.75rem !important;
            right: 0.75rem !important;
            background: transparent !important;
            border: none !important;
            box-shadow: none !important;
            transition: none !important;
          "
        >
          <CloseOutlined />
        </a-button>

        <StageVote />
      </DivCustom>
    </DivCustomOld>
  </div>

  <TodoDetailModal
    v-model="showTaskDetailModal"
    :todoId="selectedTodoId"
    @close="showTaskDetailModal = false"
  />

  <StageVoteModal
    v-model="stageVoteModal"
    :open="dataStage.openModal"
    :title="'Tạo cuộc bình chọn '"
    @close="closeModal"
    :data-phase="dataSprint.dataSprint"
  />
</template>

<script setup lang="ts">
import { h, onMounted, reactive, ref, watch } from 'vue'
import Todo from './Todo.vue'
import Sprint from './Sprint.vue'
import { getAllTodo, todoResponse } from '@/services/api/manage/todo/todo.api'
import { dataPhase, phaseResponse } from '@/services/api/manage/phase/phase.api'
import dayjs from 'dayjs'
import { phaseWebSocket } from '@/services/api/manage/phase/phase.socket.api'
import { todoVoteWebSocket } from '@/services/api/manage/todo/todovote.socket.api'
import { useRoute, useRouter } from 'vue-router'
import { detailProject } from '@/services/api/manage/project/maproject.api'
import { projectDetailResponse } from '@/services/api/admin/project/project.api'
import {
  AppstoreOutlined,
  CloseOutlined,
  DatabaseOutlined,
  DownOutlined,
  LikeOutlined,
  LineChartOutlined,
  MailOutlined,
  SearchOutlined
} from '@ant-design/icons-vue'
import { useProjectStore } from '@/utils/store'
import TodoDetailModal from '@/pages/member/project/projectdetails/projectmodal/TodoDetailModal.vue'
import StageVoteModal from './StageVoteModal.vue'
import StageVote from './StageVote.vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { MenuProps } from 'ant-design-vue'
import DivCustomOld from '@/components/custom/Div/DivCustomOld.vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'

const dataStage = reactive({
  openModal: false,
  stage: '',
  days: 3,
  endDate: null
})

const showStageVote = ref(false)

// Hàm đóng StageVote
function closeStageVote() {
  showStageVote.value = false
}
const handleDropdownClick = (e: any) => {
  if (e.key === '1') {
    // Tạo cuộc bình chọn
    dataStage.openModal = true
    showStageVote.value = false
  } else if (e.key === '2') {
    // Chi tiết các cuộc bình chọn
    showStageVote.value = true
  }
}

const openModal = () => {
  dataStage.openModal = true
}

const closeModal = () => {
  dataStage.openModal = false
}

const paramsTodo = reactive({
  page: 1,
  size: 1000000,
  totalItem: '',
  idProject: '',
  search: ''
})

const project = useProjectStore()

const dataStaffProject = ref([])

const search = ref('')

const dataSprint = reactive({
  dataSprint: [] as phaseResponse[]
})

const dataTodo = reactive({
  data: [] as todoResponse[]
})

watch(search, (newSearch) => {
  search.value = newSearch
})

const showTaskDetailModal = ref(false)

const selectedTodoId = ref<string | null>(null)

const idTodoModal = (data) => {
  selectedTodoId.value = data
  showTaskDetailModal.value = true
}

const route = useRoute()

const open = ref(false)

const openDrawer = () => {
  open.value = true
}

const handleDatastaff = (data) => {
  dataStaffProject.value = data
}

const idProject = ref('')

const removeTaskFromSprint = (sprintId: string, todo) => {
  if (Array.isArray(todo)) {
    // Xử lý nhiều todo
    todo.forEach((t) => {
      const index = idSprintTodo.value[sprintId]?.findIndex((item) => item.id === t.id)
      if (index !== -1) {
        idSprintTodo.value[sprintId].splice(index, 1)
      }
      phaseWebSocket.sendMessageDeleteTodoByPhase(t.id)

      if (!dataTodo.data.some((item) => item.id === t.id)) {
        dataTodo.data.push(t)
      }
    })
  } else {
    // Xử lý một todo
    const index = idSprintTodo.value[sprintId]?.findIndex((t) => t.id === todo.id)
    if (index !== -1) {
      idSprintTodo.value[sprintId].splice(index, 1)
    }
    phaseWebSocket.sendMessageDeleteTodoByPhase(todo.id)

    if (!dataTodo.data.some((t) => t.id === todo.id)) {
      dataTodo.data.push(todo)
    }
  }
}

const sprints = ref([]) // Danh sách các Sprint

const idSprintTodo = ref<Record<string, any[]>>({}) // Task trong từng Sprint

const idTodo = ref<string>()

// const updateTodos = (newTodos) => {
//   dataTodo.data = newTodos
// }

const valueProject = ref<projectDetailResponse>()

// ✅ Cập nhật danh sách Sprint và loại bỏ Todo khi kéo vào Sprint
const updateSprintTodos = async ({ sprintId, tasks }) => {
  idSprintTodo.value[sprintId] = tasks

  // ✅ Xóa Todo đã được thêm vào Sprint khỏi danh sách chung
  const taskIds = new Set(tasks.map((task) => task.id))
  dataTodo.data = dataTodo.data.filter((todo) => !taskIds.has(todo.id))
}

watch(search, (newSearch) => {
  paramsTodo.search = newSearch
  fetchDataTodo()
})

const fetchDataTodo = async () => {
  try {
    const resDetail = await detailProject(idProject.value)
    valueProject.value = resDetail.data

    const res = await getAllTodo(paramsTodo)
    dataTodo.data = res.data as unknown as todoResponse[]

    console.log('HSHHSHSHHSHSHHSH')

    console.log(dataTodo.data)
  } catch (error) {
    console.error('❌ Lỗi khi lấy dữ liệu todo:', error)
  }
}

const fetchDataPhase = async () => {
  const res = await dataPhase(paramsTodo)
  dataSprint.dataSprint = res.data.content.map((item) => ({
    ...item,
    startTime: item.startTime ? dayjs(Number(item.startTime)) : null,
    endTime: item.endTime ? dayjs(Number(item.endTime)) : null,
    statusPhase: item.statusPhase,
    expanded: true
  })) as unknown as phaseResponse[]

  dataSprint.dataSprint.forEach((sprint) => {
    idSprintTodo.value[sprint.id as string] = []
  })
}

onMounted(() => {
  idProject.value = route.params.id as string
  paramsTodo.idProject = idProject.value as string
  fetchDataTodo()
  fetchDataPhase()
  project.setIdProject(idProject.value)

  console.log('✅ WebSocket đã kết nối thành công!')
  todoVoteWebSocket.subscribeCreateTodoVote(fetchDataTodo)
  todoVoteWebSocket.subscribeDeleteTodoVote(fetchDataTodo)
  phaseWebSocket.subscribeCreatePhase(fetchDataPhase)
  phaseWebSocket.subscribeDeletePhase(fetchDataPhase)
  phaseWebSocket.subscribeUpdatePhase(fetchDataPhase)
  phaseWebSocket.subscribeCreateTodoByPhase(fetchDataPhase)
  // phaseWebSocket.subscribeCreateTodoByPhase(fetchDataPhase)
  phaseWebSocket.subscribeUpdateStatusPhase(fetchDataPhase)

  // phần websocket modal
  // webSocketTodo.getUpdateNameTodo(fetchDataPhase)
  // webSocketTodoChild.getUpdateStatusTodoChecklist(fetchDataPhase)
  // webSocketTodoChild.getDeleteTodoChecklist(fetchDataPhase)
  // webSocketTodoChild.getCreateTodoChildChecklist(fetchDataPhase)
  // webSocketTodoChild.getEditTodoChecklist(fetchDataPhase)
  // webSocketMemberProject.getJoinTodoVote(fetchDataPhase)
  // webSocketMemberProject.getOutTodoVoteMemberProject(fetchDataPhase)
  // webSocketTodo.getDeleteDeadlineTodo(fetchDataPhase)
  // webSocketTodo.getUpdateDeadlineTodo(fetchDataPhase)
  // // webSocketTodo.getupdateCompleteTodo(fetchBoard)
  // webSocketTodo.getUpdatePriorityLevel(fetchDataPhase)
})

const stageVoteModal = ref(false)

const stageVote = () => {}

const router = useRouter()
const goToStatistics = () => {
  router.push({ name: 'statistics', params: { id: idProject.value } }) // Sử dụng name thay vì path
}

const goToSprint = () => {
  router.push({ name: 'phase', params: { id: idProject.value } }) // Sử dụng name thay vì path
}
</script>

<style scoped>
.cursor-grab {
  cursor: grab;
}

.heght-blackbog {
  height: 100%;
}

.ant-btn {
  box-shadow: none !important; /* Không có shadow */
  font-weight: 500;
}

/* Hiệu ứng hover */
.ant-btn:hover {
  background: #e6f4ff !important; /* Nền xanh nhạt khi hover */
  color: #1890ff !important; /* Đảm bảo màu chữ khi hover */
  border-color: #1890ff !important; /* Đảm bảo viền khi hover */
}
</style>

<template>
  <!-- <<<<<<< HEAD -->
  <BreadcrumbDefault :label="'Quản lý giai đoạn'">
    <DivCustom :label="'Bộ lọc '">
      <template #icon>
        <FilterOutlined />
      </template>
      <!-- =======
  <DivCustom :label="'Quản lý giai đoạn'" :class="'min-h-screen'">
    <DivCustom>
>>>>>>> 621aa6a (fix bug) -->
      <FilterPhase @search-name="handleSearchName" @time="handleTime" />
    </DivCustom>

    <PhaseDetail
      :id-project="idProject"
      :id-todo="idTodo"
      :dataTodo="dataSprint.dataSprint"
      :sprints="sprints"
      :idSprintTodo="idSprintTodo"
      @update-sprint-todos="updateSprintTodos"
      @remove-task-from-sprint="removeTaskFromSprint"
    />
  </BreadcrumbDefault>
</template>

<script setup lang="ts">
import PhaseDetail from './PhaseDetail.vue'

import { onMounted, reactive, ref, watch } from 'vue'
import Todo from './Todo.vue'
import Sprint from './Sprint.vue'
import { getAllTodo, todoResponse } from '@/services/api/manage/todo/todo.api'
import { todoWebSocket } from '@/services/api/manage/todo/todo.socket.api'
import { dataPhase, dataSprintProject, phaseResponse } from '@/services/api/manage/phase/phase.api'
import dayjs from 'dayjs'
import { phaseWebSocket } from '@/services/api/manage/phase/phase.socket.api'
import { todoVoteWebSocket } from '@/services/api/manage/todo/todovote.socket.api'
import { useRoute, useRouter } from 'vue-router'
import { detailProject } from '@/services/api/manage/project/maproject.api'
import { projectDetailResponse } from '@/services/api/admin/project/project.api'
import { FilterOutlined, LineChartOutlined, SearchOutlined } from '@ant-design/icons-vue'
import { ROUTES_CONSTANTS } from '@/constants/path'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import FilterPhase from './FilterPhase.vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'

const paramsTodo = reactive({
  page: 1,
  size: 10,
  totalItem: '',
  idProject: '',
  search: '',
  time: ''
})

const search = ref('')

const dataSprint = reactive({
  dataSprint: [] as phaseResponse[]
})

const handleSearchName = (value: string) => {
  search.value = value
  fetchDataTodo()
}

const handleTime = (value: string) => {
  paramsTodo.time = dayjs(value).valueOf() as unknown as string
}
const dataTodo = reactive({
  data: [] as todoResponse[]
})

watch(search, (newSearch) => {
  search.value = newSearch
})

const route = useRoute()

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

  console.log(paramsTodo)

  fetchDataPhase()
})

watch(
  () => paramsTodo.time,
  (newTime) => {
    paramsTodo.time = dayjs(newTime).valueOf()
    fetchDataPhase()
  }
)
const fetchDataTodo = async () => {
  try {
    console.table(paramsTodo)
    const resDetail = await detailProject(idProject.value)
    valueProject.value = resDetail.data
    console.log(valueProject.value.name)

    const res = await getAllTodo(paramsTodo)
    dataTodo.data = res.data as unknown as todoResponse[]
  } catch (error) {
    console.error('❌ Lỗi khi lấy dữ liệu todo:', error)
  }
}

const fetchDataPhase = async () => {
  paramsTodo.search = search.value
  console.log(paramsTodo.search + 'lslls')

  const res = await dataSprintProject(paramsTodo)
  dataSprint.dataSprint = res.data.content.map((item) => ({
    ...item,
    startTime: item.startTime ? dayjs(Number(item.startTime)) : null,
    endTime: item.endTime ? dayjs(Number(item.endTime)) : null,
    expanded: false
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
  console.log('✅ WebSocket đã kết nối thành công!')
  todoWebSocket.subscribeCreateTodo(fetchDataTodo)
  todoWebSocket.subscribeDeleteTodo(fetchDataTodo)
  todoWebSocket.subscribeUpdateTodo(fetchDataTodo)
  todoVoteWebSocket.subscribeCreateTodoVote(fetchDataTodo)
  todoVoteWebSocket.subscribeDeleteTodoVote(fetchDataTodo)
  phaseWebSocket.subscribeCreatePhase(fetchDataPhase)
  phaseWebSocket.subscribeDeleteTodoByPhase(fetchDataPhase)
  phaseWebSocket.subscribeDeletePhase(fetchDataPhase)
  phaseWebSocket.subscribeUpdatePhase(fetchDataPhase)
  phaseWebSocket.subscribeCreateTodoByPhase(fetchDataPhase)
})
const router = useRouter()

const goToSprint = () => {
  router.push({ name: 'phase', params: { id: idProject.value } }) // Sử dụng name thay vì path
}
</script>

<style scoped>
:deep(.ant-btn) {
  background-color: var(--selected-header) !important;
  color: var(--selected-text) !important;
  border-color: var(--selected-header) !important;
}

:deep(.ant-btn:hover),
:deep(.ant-btn:focus) {
  background-color: var(--selected-header-hover) !important;
  border-color: var(--selected-header-hover) !important;
}
</style>


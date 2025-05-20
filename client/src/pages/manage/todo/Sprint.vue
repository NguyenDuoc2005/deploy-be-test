<template>
  <DivCustom class="mb-10">
    <!-- lass="w-full shadow-lg mt-5" -->
    <!-- <h3 class="text-2xl mb-6 font-bold text-black-2">📋 Danh sách gia giai đoạn</h3> -->
    <!-- 🔹 Thêm div có thanh cuộn -->

    <div class="space-y-4">
      <div v-if="!localSprints?.length">
        <div class="border-dashed border-2 border-slate-300 w-full p-5 text-center">
          <p class="font-satoshi text-sm text-slate-700">
            Danh sách công việc đang trống, hãy tạo thêm
          </p>
        </div>
      </div>
      <div
        v-for="(item, sprintIndex) in localSprints"
        :key="item.id"
        class="bg-slate-100 rounded-lg relative"
      >
        <div class="flex flex-col lg:flex-row justify-between gap-3 p-2">
          <!-- Phần bên trái -->
          <div class="flex flex-wrap items-center gap-2">
            <a-checkbox
              :checked="isAllSelected(item.id)"
              :indeterminate="isIndeterminate(item.id)"
              @change="(e) => handleSelectAll(e, item.id)"
              class="mr-2"
            ></a-checkbox>

            <button
              @click="toggleSprintVisibility(sprintIndex)"
              class="ml-2 px-3 rounded font-semibold flex items-center"
            >
              <RightOutlined v-if="item.expanded" class="font-bold" />
              <DownOutlined v-else />
            </button>

            <input
              v-model="editedSprintName[item.id]"
              @keyup.enter="updateTSprint(item.id)"
              @blur="updateTSprint(item.id)"
              class="p-1 border-b-0 focus:border-b focus:border-gray-500 focus:outline-none font-semibold text-black w-40 bg-slate-100"
            />

            <!-- Ngày -->
            <div class="flex items-center">
              <a-date-picker
                v-model:value="startTime[item.id]"
                @change="handleDateChange(item.id, 'start', $event)"
                class="w-24 border-b border-gray-400 font-semibold bg-slate-100 text-sm"
                :suffix-icon="null"
                :allow-clear="false"
              />
              <span class="mx-2 font-semibold">-</span>
              <a-date-picker
                v-model:value="endTime[item.id]"
                @change="handleDateChange(item.id, 'end', $event)"
                class="w-24 border-b border-gray-400 font-semibold bg-slate-100 text-sm"
                :suffix-icon="null"
                :allow-clear="false"
              />
            </div>

            <!-- Số lượng -->
            <span class="bg-slate-100 px-2 text-sm rounded-full font-satoshi">
              ({{ getSprintTaskCount(item.id) }} mục công việc)
            </span>

            <!-- Xóa -->
            <button @click="deletePhase(item.id)" class="hover:text-red-500">
              <DeleteOutlined />
            </button>
          </div>

          <!-- Phần nút bên phải -->
          <div class="flex flex-wrap gap-2 justify-end">
            <a-button
              v-if="sprintStatus[item.id] == 'inProgress'"
              class="font-medium text-sm px-3 py-2 rounded-md text-white"
              @click="detailSprint(item.id)"
              :disabled="sprintStatus[item.id] === 'completed'"
              style="
                background-color: #cce0ff !important;
                color: #44546f !important;
                border: none;
                border-color: #f1f2f4 !important;
                display: flex;
                font-weight: 500 !important;
                align-items: center;
                justify-content: center;
                text-align: center;
              "
            >
              {{
                !sprintStatus[item.id] || sprintStatus[item.id] === 'notStarted'
                  ? hasInProgressSprint
                    ? 'Bắt đầu'
                    : 'Bắt đầu'
                  : sprintStatus[item.id] === 'inProgress'
                  ? 'Hoàn thành'
                  : 'Đã hoàn thành'
              }}
            </a-button>

            <button
              v-else-if="idSprintStarted == ''"
              class="font-medium text-sm px-3 py-2 rounded-md text-white"
              @click="detailSprint(item.id)"
              :disabled="sprintStatus[item.id] === 'completed'"
              style="
                background-color: #cce0ff !important;
                color: #44546f !important;
                border: none;
                border-color: #f1f2f4 !important;
                display: flex;
                font-weight: 500 !important;
                align-items: center;
                justify-content: center;
                text-align: center;
              "
            >
              {{
                !sprintStatus[item.id] || sprintStatus[item.id] === 'notStarted'
                  ? hasInProgressSprint
                    ? 'Bắt đầu'
                    : 'Bắt đầu'
                  : sprintStatus[item.id] === 'inProgress'
                  ? 'Hoàn thành'
                  : 'Đã hoàn thành'
              }}
            </button>

            <a-button
              v-if="sprintStatus[item.id] === 'inProgress'"
              class="font-medium text-sm px-3 py-2 rounded-md text-white"
              @click="viewSprintDetail(item.id)"
              style="
                background-color: #cce0ff !important;
                color: #44546f !important;
                border: none;
                border-color: #f1f2f4 !important;
                display: flex;
                font-weight: 500 !important;
                align-items: center;
                justify-content: center;
                text-align: center;
              "
            >
              Chi tiết
            </a-button>
          </div>
        </div>

        <div v-if="item.expanded" class="space-y-4 w-full px-6 pb-6">
          <div v-if="getSprintTaskCount(item.id) == 0">
            <div class="border-dashed border-2 border-slate-300 w-full p-5 text-center">
              <p Lên class="font-satoshi text-sm text-slate-700">
                Lên kế hoặch các giai đoạn bằng cách kéo các mục công việc
              </p>
            </div>
          </div>

          <div class="overflow-x-auto max-w-full">
            <table class="w-full border border-gray-400 border-collapse mt-5 min-w-[800px]">
              <!-- ... -->
              <draggable
                :model-value="getSprintTasks(item.id)"
                @update:model-value="(newList) => updateSprintTasks(item.id, newList)"
                group="tasks"
                item-key="id"
                @change="handleDraggableChange($event, item.id)"
                tag="tbody"
                class="rounded-lg"
                @start="handleDragStart($event, item.id)"
                @end="handleDragEnd($event, item.id)"
              >
                <template #item="{ element }">
                  <tr
                    class="bg-white text-black border-b border-gray-400 hover:bg-gray-100 cursor-grab"
                    @click="handleTodoClick(element, $event)"
                  >
                    <!-- Ô checkbox -->
                    <td class="px-3 border border-x-0 text-sm w-10 text-center align-middle">
                      <a-checkbox
                        :checked="checkedTodosBySprintId[item.id]?.has(element.id)"
                        @change="(e) => handleCheckboxChange(element.id, item.id, e)"
                        class="mr-2 scale-125"
                        @click.stop
                      />
                    </td>

                    <!-- Tên công việc -->
                    <td class="p-3 border border-x-0 text-sm w-2/6 max-w-[400px] truncate">
                      {{ element.name }}
                    </td>

                    <!-- Mức độ ưu tiên -->
                    <td
                      class="p-3 border border-x-0 font-semibold text-sm w-2/12 align-middle truncate"
                    >
                      <div
                        class="flex items-end h-full"
                        :class="{
                          'text-blue-400': element.priorityLevel === 'THAP',
                          'text-orange-500': element.priorityLevel === 'TRUNG_BINH',
                          'text-red-400': element.priorityLevel === 'CAO',
                          'text-red-600': element.priorityLevel === 'QUAN_TRONG'
                        }"
                      >
                        {{
                          element.priorityLevel == 'THAP'
                            ? 'Thấp'
                            : element.priorityLevel == 'TRUNG_BINH'
                            ? 'Trung Bình'
                            : element.priorityLevel == 'CAO'
                            ? 'Cao'
                            : 'Quan Trọng'
                        }}
                      </div>
                    </td>

                    <!-- Hạn chót -->
                    <td
                      class="p-3 border border-x-0 text-sm w-1/12 text-center align-middle truncate"
                    >
                      {{ element.deadline == null ? '-' : formatDateNVV(Number(element.deadline)) }}
                    </td>

                    <!-- Trạng thái -->
                    <td
                      class="p-4 border border-x-0 text-sm w-2/12 text-center align-bottom truncate"
                    >
                      <a-tag
                        :color="
                          getStatusTodoText(element.statusTodo) === 'Hoàn thành sớm'
                            ? 'success'
                            : getStatusTodoText(element.statusTodo) === 'Hoàn thành muộn'
                            ? 'warning'
                            : getStatusTodoText(element.statusTodo) === 'Đúng hạn'
                            ? 'blue'
                            : 'default'
                        "
                      >
                        {{ getStatusTodoText(element.statusTodo) }}
                      </a-tag>
                    </td>

                    <!-- Tiến độ -->
                    <td class="p-3 border border-x-0 w-3/12 min-w-[200px]">
                      <div class="flex items-center justify-end space-x-2 w-full">
                        <p class="text-sm whitespace-nowrap">Tiến độ:</p>
                        <a-progress
                          class="min-w-[100px]"
                          :percent="Number(element.progress)"
                          stroke-color="#4F46E5"
                          :show-info="true"
                        />
                      </div>
                    </td>
                  </tr>
                </template>
              </draggable>
            </table>
          </div>

          <!-- <table
            class="w-full border border-gray-400 border-collapse mt-5 max-h-171.5 overflow-y-auto pr-2"
          ></table> -->
        </div>
      </div>
    </div>
    <a-button
      @click="showAddSprintInput"
      v-if="isAddingSprint == false"
      class="w-30 mt-2"
      :style="{
        backgroundColor: '#F1F2F4 !important',
        color: '#222222 !important',
        border: 'none !important'
      }"
    >
      + Tạo giai đoạn
    </a-button>

    <div class="p-3 border border-x-0 w-full" v-if="isAddingSprint">
      <input
        v-model="newSprint"
        @keyup.enter="addSprint"
        @blur="addSprint"
        class="w-full p-2 rounded-lg h-11 border-blue-300 hover:!ring-0 hover:!border-blue-300 outline-none shadow-none !border-2 focus:!ring-0 focus:!border-blue-300"
        autofocus
      />
    </div>
  </DivCustom>
  <DrawerPhase
    :open="openDrawer"
    :title="`Hoàn thành Sprint ${currentSprintName}`"
    :todos="currentSprintTodos"
    @close="closeDrawer"
    @success="handleSprintComplete"
  />

  <TodoDetailModal
    v-model="showTaskDetailModal"
    class="custom-modal"
    :todoId="selectedTodoId"
    @close="closeDetailModal"
  />
</template>

<script setup lang="ts">
import { ref, watch, onMounted, reactive, computed } from 'vue'
import draggable from 'vuedraggable'
import {
  DeleteOutlined,
  DownOutlined,
  MoreOutlined,
  RightOutlined,
  RightSquareOutlined,
  UpOutlined
} from '@ant-design/icons-vue'
import { toast } from 'vue3-toastify'
import { getAllTodoByPhase, todoResponse } from '@/services/api/manage/todo/todo.api'

import { phaseWebSocket } from '@/services/api/manage/phase/phase.socket.api'
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
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import dayjs from 'dayjs'
import { List, Modal } from 'ant-design-vue'
import { router } from '@/routes/router'
import { ROUTES_CONSTANTS } from '@/constants/path'
import DrawerPhase from './DrawerPhase.vue'
import TodoDetailModal from '@/pages/member/project/projectdetails/projectmodal/TodoDetailModal.vue'
import { formatDateNVV, getStatusTodoText } from '@/utils/commom.helper'
import { webSocketTodo } from '@/services/service/member/metodo.socket'
import { webSocketTodoChild } from '@/services/service/member/todochild.socket'
import { webSocketMemberProject } from '@/services/service/member/memberproject.socket'
import { useRoute } from 'vue-router'
import { useProjectStore } from '@/utils/store'
import DivCustom from '@/components/custom/Div/DivCustom.vue'

const newSprint = ref('')
const idSprintTodo = ref<Record<string, todoResponse[]>>({})
const props = defineProps<{
  idTodo?: string
  dataTodo: any[]
  sprints: any[]
  idProject: string
  idSprintTodo: Record<string, todoResponse[]>
  search: string
}>()
const emit = defineEmits(['update-sprint-todos', 'count-todo', 'remove-task-from-sprint'])
const editedSprintName = reactive<{ [key: string]: string }>({})
const startTime = reactive<{ [key: string]: string }>({})
const endTime = reactive<{ [key: string]: string }>({})
// ✅ Đồng bộ `localSprints` với props.dataTodo
const localSprints = ref()
const paramsTodo = reactive({
  page: 1,
  size: 100000,
  totalItem: '',
  search: ''
})

const project = useProjectStore()

const route = useRoute()

const isAddingSprint = ref(false)
const newSprintName = ref('')
const showAddSprintInput = () => {
  isAddingSprint.value = true
  newSprintName.value = ''
}

const handleTodoClick = (todo: any, event: Event) => {
  selectedTodoId.value = todo.id
  showTaskDetailModal.value = true
  router.replace({
    path: `${pathTodo.value}/${todo.idPhase}`,
    query: {
      idTodo: selectedTodoId.value
    }
  })
}

const closeDetailModal = () => {
  router.replace({
    path: `${pathTodo.value}`
  })
}
const showTaskDetailModal = ref(false)

const selectedTodoId = ref<string | null>(null)

const handleDraggableChange = async (event, sprintId) => {
  if (event.added) {
    // Kiểm tra xem có nhiều todo được chọn không
    const selectedTodos = event.added.element.selectedTodos || [event.added.element]

    const arrayTodoId = selectedTodos.map((todo) => todo.id)
    // Xử lý tất cả các todo được chọn

    index.value++
    await phaseWebSocket.sendMessageCreateTodoByPhase({
      idTodo: arrayTodoId,
      idPhase: sprintId,
      index: index.value as unknown as string,
      idProject: props.idProject
    })

    // Xóa tất cả các todo đã được thêm khỏi danh sách gốc
    selectedTodos.forEach((todo) => removeTodoFromOriginalList(todo.id))
  }

  if (event.removed) {
    const removedTodo = event.removed.element
    const selectedTodos = checkedTodosBySprintId[sprintId]

    if (selectedTodos && selectedTodos.has(removedTodo.id)) {
      const todosToRemove = Array.from(selectedTodos)
        .map((todoId) => {
          return idSprintTodo.value[sprintId].find((t) => t.id === todoId)
        })
        .filter(Boolean)

      // Đảm bảo removedTodo cũng được thêm vào nếu chưa có
      if (!todosToRemove.find((t) => t.id === removedTodo.id)) {
        todosToRemove.push(removedTodo)
      }

      emit('remove-task-from-sprint', sprintId, todosToRemove)
      checkedTodosBySprintId[sprintId].clear()
    } else {
      emit('remove-task-from-sprint', sprintId, removedTodo)
    }
    fetchDataPhase()
    // Cập nhật lại danh sách todo trong sprint
    updateSprintTasks(sprintId, getSprintTasks(sprintId))
  }
}

const getSprintTaskCount = (sprintId) => {
  return idSprintTodo.value[sprintId]?.length || 0
}

watch(
  () => props.dataTodo,
  (newData) => {
    localSprints.value = [...newData]
    // phaseWebSocket.subscribeCreatePhase(fetchDataPhase)
    // phaseWebSocket.subscribeDeletePhase(fetchDataPhase)
    // phaseWebSocket.subscribeUpdatePhase(fetchDataPhase)
    fetchDataPhase()
  }
)

watch(idSprintTodo, (newvalue) => {
  fetchDataPhase()

  phaseWebSocket.subscribeDeleteTodoByPhase(fetchDataPhase)
  phaseWebSocket.subscribeCreateTodoByPhase(fetchDataPhase)

  webSocketTodo.getUpdateNameTodo(fetchDataPhase)
  webSocketTodoChild.getUpdateStatusTodoChecklist(fetchDataPhase)
  webSocketTodoChild.getDeleteTodoChecklist(fetchDataPhase)
  webSocketTodoChild.getCreateTodoChildChecklist(fetchDataPhase)
  webSocketTodoChild.getEditTodoChecklist(fetchDataPhase)
  webSocketMemberProject.getJoinTodoVote(fetchDataPhase)
  webSocketMemberProject.getOutTodoVoteMemberProject(fetchDataPhase)
  webSocketTodo.getDeleteDeadlineTodo(fetchDataPhase)
  webSocketTodo.getUpdateDeadlineTodo(fetchDataPhase)
})

// ✅ Lấy danh sách công việc theo Sprint ID
const getSprintTasks = (id: string) => {
  return idSprintTodo.value[id] ?? []
}

// ✅ Cập nhật công việc khi kéo/thả
const updateSprintTasks = (sprintId: string, newList: todoResponse[]) => {
  idSprintTodo.value[sprintId] = [...newList]
  emit('update-sprint-todos', { sprintId, tasks: idSprintTodo.value[sprintId] || [] })
}

const handleDateChange = (id: string, type: 'start' | 'end', value: dayjs.Dayjs | null) => {
  if (type === 'start') {
    startTime[id] = value
  } else {
    endTime[id] = value
  }

  // ✅ GỌI HÀM CẬP NHẬT NGAY LẬP TỨC
  updateTSprint(id)
}

// ✅ Xử lý hiển thị/ẩn Sprint
const toggleSprintVisibility = (index: number) => {
  // Tạo một bản sao để đảm bảo Vue cập nhật phản ứng
  localSprints.value = localSprints.value.map((sprint, i) => {
    if (i === index) {
      return { ...sprint, expanded: !sprint.expanded }
    }
    return sprint
  })

  console.log(`🚀 Sprint ${index} expanded:`, localSprints.value[index].expanded)
}
const index = ref(0)
// ✅ Lấy dữ liệu danh sách Sprint từ API
const fetchDataPhase = async () => {
  if (!props.dataTodo || props.dataTodo.length === 0) return

  idSprintStarted.value = ''

  for (const sprint of props.dataTodo) {
    if (!sprint.id) continue

    const dataTodoByPhase = await getAllTodoByPhase(paramsTodo, sprint.id)

    emit('count-todo', dataTodoByPhase.data.length)
    index.value = dataTodoByPhase.data.length
    // idSprintTodo.value[sprint.id] = dataTodoByPhase.data || []
    const todosWithPhaseId = dataTodoByPhase.data.map((todo) => ({
      ...todo,
      idPhase: sprint.id
    }))

    idSprintTodo.value[sprint.id] = todosWithPhaseId
    // Cập nhật trạng thái nút dựa vào status từ server
    switch (sprint.statusPhase) {
      case 'CHUA_HOAN_THANH':
        sprintStatus.value[sprint.id] = 'notStarted'
        break
      case 'DANG_LAM':
        sprintStatus.value[sprint.id] = 'inProgress'
        if (idSprintStarted.value == '') {
          idSprintStarted.value = sprint.id
        }
        break
      case 'DA_HOAN_THANH':
      case 'QUA_HAN':
        sprintStatus.value[sprint.id] = 'completed'
        break
      default:
        sprintStatus.value[sprint.id] = 'notStarted'
    }

    for (const item of props.dataTodo) {
      editedSprintName[item.id as string] = item.name
      startTime[item.id as string] = item.startTime
      endTime[item.id as string] = item.endTime
    }

    // Cập nhật trạng thái mở rộng vào `localSprints`
    // const sprintIndex = localSprints.value.findIndex((s) => s.id === sprint.id)
    // if (sprintIndex !== -1) {
    // localSprints.value[sprintIndex].expanded = dataTodoByPhase.data.length > 0
    // }
  }
}

// ✅ Thêm mới Sprint
const addSprint = async () => {
  if (!newSprint.value.trim()) {
    isAddingSprint.value = !isAddingSprint.value
    return
  } else {
    const payload = { name: newSprint.value, code: newSprint.value, idProject: props.idProject }
    await phaseWebSocket.sendMessageCreatePhase(payload)
    newSprint.value = ''
    isAddingSprint.value = !isAddingSprint.value
    fetchDataPhase()
    toast.success('Thêm sprint thành công!')
  }
}

// ✅ Xóa Sprint

const deletePhase = (id: string) => {
  Modal.confirm({
    title: 'Bạn có chắc muốn xóa?',
    okText: 'Xóa',
    okType: 'danger',
    cancelText: 'Hủy',
    class: 'custom-modal', // ✅ Thêm class tùy chỉnh
    getContainer: () => document.body, // ✅ Đảm bảo có thể tùy chỉnh CSS
    centered: true, // ✅ Hiển thị modal ở giữa
    onOk() {
      phaseWebSocket.sendMessageDeletePhase(id)
      toast.success('Xóa thành công giai đoạn')
    },
    onCancel() {}
  })
}

const updateTSprint = async (id: string) => {
  if (!editedSprintName[id] || !startTime[id] || !endTime[id]) {
    toast.error('Vui lòng nhập đầy đủ thông tin trước khi cập nhật!')
    return
  }

  await phaseWebSocket.sendMessageUpdatePhase(id, {
    name: editedSprintName[id],
    code: editedSprintName[id],
    startTime: dayjs(startTime[id]).valueOf(), // Chuyển sang timestamp (milliseconds)
    endTime: dayjs(endTime[id]).valueOf() // Chuyển sang timestamp
  })

  toast.success('Sprint cập nhật thành công!')
  fetchDataPhase()
}

watch(
  () => props.search,
  (newSearch) => {
    paramsTodo.search = newSearch
    fetchDataPhase()
  }
)

const removeTodoFromOriginalList = (todoId) => {
  for (const sprint of props.dataTodo) {
    const index = idSprintTodo.value[sprint.id]?.findIndex((todo) => todo.id === todoId)

    if (index !== -1 && index !== undefined) {
      idSprintTodo.value[sprint.id].splice(index, 1) // ✅ Xóa khỏi danh sách gốc
      // break
      console.log(`🗑 Đã xóa Todo ${todoId} khỏi Sprint ${sprint.id}`)
    }
  }
}
const pathTodo = ref('')

onMounted(() => {
  pathTodo.value = route.path
  localSprints.value = props.dataTodo
  fetchDataPhase()

  // phaseWebSocket.subscribeCreatePhase(fetchDataPhase)
  // phaseWebSocket.subscribeDeletePhase(fetchDataPhase)
  // phaseWebSocket.subscribeUpdatePhase(fetchDataPhase)

  phaseWebSocket.subscribeDeleteTodoByPhase(fetchDataPhase)
  phaseWebSocket.subscribeCreateTodoByPhase(fetchDataPhase)

  webSocketTodo.getUpdateNameTodo(fetchDataPhase)
  webSocketTodoChild.getUpdateStatusTodoChecklist(fetchDataPhase)
  webSocketTodoChild.getDeleteTodoChecklist(fetchDataPhase)
  webSocketTodoChild.getCreateTodoChildChecklist(fetchDataPhase)
  webSocketTodoChild.getEditTodoChecklist(fetchDataPhase)
  webSocketMemberProject.getJoinTodoVote(fetchDataPhase)
  webSocketMemberProject.getOutTodoVoteMemberProject(fetchDataPhase)
  webSocketTodo.getDeleteDeadlineTodo(fetchDataPhase)
  webSocketTodo.getUpdateDeadlineTodo(fetchDataPhase)
  // webSocketTodo.getupdateCompleteTodo(fetchBoard)
  webSocketTodo.getUpdatePriorityLevel(fetchDataPhase)
})
// chi tiết giai đoạn dự án
const currentSprintTodos = ref([])

const detailSprint = (idPhase: string) => {
  const sprintTasks = getSprintTasks(idPhase) || []
  if (!sprintStatus.value[idPhase] || sprintStatus.value[idPhase] === 'notStarted') {
    // Khi nhấn "Bắt đầu"

    if (sprintTasks.length == 0) {
      toast.error('Hãy thêm công việc vào giai đoạn')
    } else {
      phaseWebSocket.sendMessageUpdateStatusPhase(idPhase, '1')
      sprintStatus.value[idPhase] = 'inProgress'
      project.setIdProject(props.idProject, idPhase)
      localStorage.setItem('currentPhaseId', idPhase)
      localStorage.setItem('currentProjectId', props.idProject)

      // Chuyển trang
      router.push({
        name: `${ROUTES_CONSTANTS.MANAGE.children.PROJECT_DETAIL.name}`,
        params: {
          idProject: props.idProject,
          idPhase: idPhase
        }
      })
    }
  } else if (sprintStatus.value[idPhase] === 'inProgress') {
    // Khi nhấn "Hoàn thành" - mở drawer
    const sprintTasks = getSprintTasks(idPhase) || []

    currentSprintTodos.value = sprintTasks
      .map((task) => {
        if (!task) return null
        return {
          id: task.id,
          name: task.name || '',
          status: task.statusTodo || 'CHUA_HOAN_THANH',
          progress: Number(task.progress || 0),
          type: task.type || ''
        }
      })
      .filter((task) => task !== null)

    currentSprintName.value = editedSprintName[idPhase] || ''
    openDrawer.value = true
  }
}

// Thêm các ref mới
const openDrawer = ref(false)
const currentSprintName = ref('')

// Thêm hàm đóng drawer
const closeDrawer = () => {
  openDrawer.value = false
}

// Thêm các computed properties và methods cho checkbox
const isAllSelected = (sprintId: string) => {
  const sprintTodos = getSprintTasks(sprintId)
  const checkedTodos = checkedTodosBySprintId[sprintId] || new Set()
  return sprintTodos.length > 0 && sprintTodos.every((todo) => checkedTodos.has(todo.id))
}

const isIndeterminate = (sprintId: string) => {
  const sprintTodos = getSprintTasks(sprintId)
  const checkedTodos = checkedTodosBySprintId[sprintId] || new Set()
  const checkedCount = sprintTodos.filter((todo) => checkedTodos.has(todo.id)).length
  return checkedCount > 0 && checkedCount < sprintTodos.length
}

const handleSelectAll = (e: any, sprintId: string) => {
  const checked = e.target.checked
  const sprintTodos = getSprintTasks(sprintId)

  if (!checkedTodosBySprintId[sprintId]) {
    checkedTodosBySprintId[sprintId] = new Set()
  }

  if (checked) {
    // Chọn tất cả
    sprintTodos.forEach((todo) => {
      checkedTodosBySprintId[sprintId].add(todo.id)
    })
  } else {
    // Bỏ chọn tất cả
    checkedTodosBySprintId[sprintId].clear()
  }

  console.log(`Sprint ${sprintId} - All todos ${checked ? 'checked' : 'unchecked'}`)
}

const handleCheckboxChange = (todoId: string, sprintId: string, e: any) => {
  if (!checkedTodosBySprintId[sprintId]) {
    checkedTodosBySprintId[sprintId] = new Set()
  }

  if (e.target.checked) {
    checkedTodosBySprintId[sprintId].add(todoId)
  } else {
    checkedTodosBySprintId[sprintId].delete(todoId)
  }

  console.log(`Sprint ${sprintId} - Checked todos:`, Array.from(checkedTodosBySprintId[sprintId]))
}

// Thêm reactive state để lưu trữ trạng thái checkbox
const checkedTodosBySprintId = reactive<{ [sprintId: string]: Set<string> }>({})

// Thêm state để theo dõi trạng thái của từng sprint
const sprintStatus = ref<{ [key: string]: 'notStarted' | 'inProgress' | 'completed' }>({})

// Chỉ có 1 sprint đưọc bắt đầu
const idSprintStarted = ref('')

// Thêm computed property mới
const hasInProgressSprint = computed(() => {
  return Object.values(sprintStatus.value).some((status) => status === 'inProgress')
})

const handleSprintComplete = async ({ note }) => {
  try {
    const currentSprintId = Object.keys(sprintStatus.value).find(
      (id) => sprintStatus.value[id] === 'inProgress'
    )

    if (currentSprintId) {
      await phaseWebSocket.sendMessageUpdateStatusPhase(currentSprintId, '2')
      sprintStatus.value[currentSprintId] = 'completed'
      openDrawer.value = false
      // Gọi lại API để lấy dữ liệu mới
      await fetchDataPhase() // Đảm bảo gọi lại fetchDataPhase

      console.log(localStorage.getItem('currentPhaseId'))
      console.log(localStorage.getItem('currentProjectId'))
      localStorage.removeItem('currentPhaseId')
      localStorage.removeItem('currentProjectId')

      // Cập nhật lại danh sách công việc của sprint
      const updatedTasks = await getAllTodoByPhase(paramsTodo, currentSprintId)
      idSprintTodo.value[currentSprintId] = updatedTasks.data.content || []

      // Emit sự kiện để cập nhật UI
      emit('update-sprint-todos', {
        sprintId: currentSprintId,
        tasks: idSprintTodo.value[currentSprintId] || []
      })

      toast.success('Hoàn thành Sprint thành công!')
    }
  } catch (error) {
    console.error('Lỗi khi hoàn thành sprint:', error)
    toast.error('Có lỗi xảy ra khi hoàn thành sprint!')
  }
}

const viewSprintDetail = (idPhase: string) => {
  router.push({
    name: `${ROUTES_CONSTANTS.MANAGE.children.PROJECT_DETAIL.name}`,
    params: {
      idProject: props.idProject,
      idPhase: idPhase
    }
  })
}

const handleDragStart = (event, sprintId) => {
  const selectedIds = Array.from(checkedTodosBySprintId[sprintId] || [])

  // Nếu không có gì được chọn hoặc chỉ có 1 item thì bỏ qua
  if (selectedIds.length <= 1) return

  const selectedTodos = selectedIds
    .map((id) => idSprintTodo.value[sprintId].find((todo) => todo.id === id))
    .filter(Boolean)

  // Gắn vào element đang kéo
  event.item.__draggable_context.element.selectedTodos = selectedTodos
}

const handleDragEnd = (event, sprintId) => {
  // 👉 Nếu muốn xóa chọn sau khi kéo xong
  if (checkedTodosBySprintId[sprintId]) {
    checkedTodosBySprintId[sprintId].clear()
  }
}
</script>

<style scoped>
.border-dashed {
  border-style: dashed;
}

.ant-picker {
  border: none !important;
  box-shadow: none !important;
}

.ant-picker-focused {
  border: none !important;
  box-shadow: none !important;
}

.cursor-grab {
  cursor: grab;
}

.custom-row-height td {
  height: 10px !important; /* Giảm chiều cao hàng */
}

.cursor-grab {
  cursor: grab;
}

.custom-row-height td {
  height: 10px !important;
}

.selected-todo {
  background-color: rgba(59, 130, 246, 0.1) !important;
}

/* Thêm style cho modal */
:deep(.ant-modal-mask) {
  background-color: rgba(0, 0, 0, 0.45) !important;
  backdrop-filter: blur(4px);
  z-index: 1000;
}

:deep(.ant-modal-wrap) {
  z-index: 1001;
}

:deep(.ant-modal) {
  top: 50%;
  transform: translateY(-50%);
}

:deep(.ant-modal-content) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-radius: 8px;
}

/* Animation cho modal */
:deep(.ant-modal-enter),
:deep(.ant-modal-appear) {
  transform: scale(0.8);
  opacity: 0;
}

:deep(.ant-modal-enter-active),
:deep(.ant-modal-appear-active) {
  transform: scale(1);
  opacity: 1;
  transition: all 0.3s cubic-bezier(0.08, 0.82, 0.17, 1);
}

/* ✅ Định dạng lại nền, viền, chữ cho Modal */

/* Thêm style cho nút Chi tiết */
.bg-blue-500 {
  background-color: #3b82f6;
}

.bg-blue-500:hover {
  background-color: #2563eb;
}
</style>

<template>
  <div class="page-wrapper flex flex-col min-h-16">
    <div
      class="content-wrapper bg-white dark:bg-boxdark shadow-lg border border-gray-200 dark:border-strokedark space-y-4"
    >
      <div class="flex h-12 items-center">
        <div class="flex flex-row ml-5">
          <a-checkbox
            :checked="isAllSelected"
            :indeterminate="isIndeterminate"
            @change="handleSelectAll"
            class="ml-3"
            @click.stop
          >
          </a-checkbox>
          <!-- ... rest of header ... -->
        </div>
        <button
          @click="toggleTaskVisibility()"
          class="ml-2 px-3 rounded font-semibold flex text-zinc-900 items-center"
        >
          <DownOutlined v-if="openTask" class="font-bold" />
          <RightOutlined v-else class="font-bold" />
        </button>
        <p class="font-semibold text-zinc-900">Danh sách công việc</p>
        <div class="font-semibold text-b rounded-lg text-sm ml-2">
          (
          {{ countTask }} công việc )
        </div>
      </div>
      <div v-if="openTask == false" class="overflow-y-auto space-y-4 w-full p-5">
        <div v-if="todoList.length == 0">
          <div class="border-dashed border-2 border-slate-300 w-full p-5 text-center">
            <p Lên class="font-satoshi text-sm text-slate-700">
              Danh sách công việc đang trống hãy tạo thêm
            </p>
          </div>
        </div>
        <table
          class="w-full border border-gray-400 border-collapse max-h-171.5 overflow-y-auto pr-2 space-y-4"
        >
          <draggable
            v-model="todoList"
            group="tasks"
            item-key="id"
            animation="300"
            tag="tbody"
            class="rounded-lg"
            @change="handleDraggableChange"
            @start="handleDragStart"
            @end="handleTodoDrop"
          >
            <template #item="{ element }">
              <tr
                class="bg-white text-black border-b border-gray-400 hover:bg-gray-100 cursor-grab leading-none text-sm"
                :class="{ 'selected-todo': checkedTodo.has(element.id) }"
                :data-id="element.id"
                @click="handleTodoClick(element.id, $event)"
              >
                <!-- Ngày tạo -->

                <td class="pl-3 border border-x-0 font-satoshi text-sm w-5">
                  <a-checkbox
                    :checked="isChecked(element.id)"
                    @change="(e) => handleCheckboxChange(element.id, e)"
                    @click.stop
                  >
                  </a-checkbox>
                </td>

                <td class="p-3 border border-x-0 w-2/6 font-satoshi text-sm hover:text-blue-500">
                  <template v-if="editedTaskId === element.id">
                    <input
                      v-model="editedTaskName[element.id]"
                      @keyup.enter="updateTask(element.id)"
                      @blur="updateTask(element.id)"
                      class="p-1 border-b border-gray-500 focus:outline-none w-full"
                      autofocus
                      @click.stop
                    />
                  </template>
                  <template v-else>
                    <p
                      @click="enableEdit(element)"
                      class="cursor-pointer max-w-[400px] truncate block"
                      @click.stop
                    >
                      {{ element.name }}
                    </p>
                  </template>
                </td>

                <td class="p-3 border border-x-0 font-satoshi text-sm">
                  {{
                    formatDate(
                      Number(element.createdDate == null ? element.date : element.createdDate)
                    )
                  }}

                  <a-dropdown :trigger="['click']" @click.stop>
                    <a class="cursor-pointer text-sm text-yellow-600 font-bold ml-2">
                      <span v-if="selectedText[element.id]">{{ selectedText[element.id] }}</span>
                      <template v-else>
                        <font-awesome-icon :icon="['fas', 'bars']" />
                        <UnorderedListOutlined />
                      </template>
                    </a>
                    <template #overlay>
                      <a-menu
                        :selectedKeys="selectedKey[element.id]"
                        @click="(e) => handleSelect(e, element.id)"
                        @change="(e) => handlePriorityChange(element.id, e.key)"
                      >
                        <a-menu-item key="3" class="!text-green-500">Thấp</a-menu-item>
                        <a-menu-item key="2" class="!text-orange-500">Trung bình</a-menu-item>
                        <a-menu-item key="1" class="!text-red-400">Cao</a-menu-item>
                        <a-menu-item key="0" class="!text-red-600">Quan trọng</a-menu-item>
                      </a-menu>
                    </template>
                  </a-dropdown>
                </td>

                <!-- Tên công việc -->

                <!-- % Bình chọn -->

                <td class="p-3 border border-x-0">
                  <div class="flex justify-around text-end">
                    <p class="text-sm items-center text-center w-1/3">Bình chọn :</p>
                    <a-progress
                      class="w-3/4"
                      :percent="percentMap[element.id] || 0"
                      stroke-color="#4F46E5"
                      :show-info="true"
                    />
                  </div>
                </td>

                <!-- Người bình chọn -->
                <!-- <td class="p-3 border border-x-0">
                  <a-avatar-group
                    :max-count="3"
                    :max-style="{ color: '#f56a00', backgroundColor: '#fde3cf' }"
                  >
                    <a-tooltip
                      v-for="(user, index) in listUserVote"
                      :key="index"
                      :title="user.nameStaff == null ? user.nameStudent : user.nameStaff"
                      placement="top"
                    >
                      <a-avatar
                        :src="user.imageStaff == null ? user.imageStudent : user.imageStaff"
                      />
                    </a-tooltip>
                  </a-avatar-group>
                </td> -->
                <td class="p-3 border border-x-0 text-end w-1/12">
                  <a-button
                    class="text-slate-500 hover:text-red duration-300"
                    @click="removeTask(element.id)"
                    @click.stop
                    style="
                      background-color: #cce0ff !important;
                      color: #44546f !important;
                      border: none;
                      border-color: rgba(144, 144, 147, 0.95) !important;
                      display: flex;
                      align-items: center;
                      justify-content: center;
                      text-align: center;
                      border-radius: 20%;
                    "
                  >
                    <font-awesome-icon :icon="['fas', 'trash']" style="color: #44546f" />
                  </a-button>
                </td>
              </tr>
            </template>
          </draggable>
        </table>
        <a-button
          @click="showAddTaskInput"
          v-if="isAddingTask == false"
          style="background-color: #f1f2f4 !important; border: none !important"
          >+ Tạo công việc
        </a-button>

        <div class="border border-spacing-1 w-full flex items-center space-x-2" v-if="isAddingTask">
          <!-- Icon bookmark màu xanh -->
          <div class="text-blue-500">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-6 w-6"
              fill="currentColor"
              viewBox="0 0 24 24"
              stroke="none"
            >
              <path d="M6 2a2 2 0 0 0-2 2v16l7-5 7 5V4a2 2 0 0 0-2-2H6z" />
            </svg>
          </div>

          <!-- Dropdown chọn mức độ ưu tiên -->
          <a-select
            v-model:value="typeValue"
            class="w-1/12"
            :bordered="false"
            placeholder="Chọn loại"
          >
            <a-select-option v-for="item in listTypeTodo" :key="item.id" :value="item.id">
              {{ item.type }}
            </a-select-option>
          </a-select>

          <!-- Input nhập task -->
          <input
            v-model="newTask"
            @keyup.enter="addTask"
            @blur="addTask"
            style="border: none"
            placeholder="Tạo công việc mới?"
            class="flex-1 p-2 rounded-lg h-11 border border-blue-300 outline-none focus:border-blue-500"
            autofocus
          />
        </div>
      </div>
    </div>
  </div>

  <!-- <TodoDetailModal v-model="showTaskDetailModal" :todoId="selectedTodoId" class="custom-modal" /> -->
</template>

<script setup lang="ts">
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import {
  getAllStaffProject,
  getAllTodo,
  getAllUserByTodo,
  todoResponse
} from '@/services/api/manage/todo/todo.api'
import { dataCreateTodo, todoWebSocket } from '@/services/api/manage/todo/todo.socket.api'
import { todoVoteWebSocket } from '@/services/api/manage/todo/todovote.socket.api'
import { getListTypeTodo } from '@/services/api/manage/todo/typetodo.ap'
import { TypeTodoResponse } from '@/services/api/member/projectdetail/typetodo.api'
import { localStorageAction } from '@/utils/storage'
import { DownOutlined, RightOutlined, UnorderedListOutlined } from '@ant-design/icons-vue'
import { library } from '@fortawesome/fontawesome-svg-core'
import {
  faCircleInfo,
  faEye,
  faFilter,
  faPenToSquare,
  faReceipt,
  faRotateRight,
  faTrash,
  faTrashAlt
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { debounce } from 'lodash'
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { toast } from 'vue3-toastify'
import draggable from 'vuedraggable'
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

const props = defineProps<{
  todos: todoResponse[]
  idProject: string
}>()
const emit = defineEmits(['update-todos', 'level', 'data-staff-project'])
const paramsTodo = reactive({
  page: 1,
  size: 10000,
  idProject: '',
  totalItem: '',
  level: ''
})

const isAllSelected = computed(() => {
  return todoList.value.length > 0 && todoList.value.every((todo) => checkedTodo.value.has(todo.id))
})

const isIndeterminate = computed(() => {
  const checkedCount = todoList.value.filter((todo) => checkedTodo.value.has(todo.id)).length
  return checkedCount > 0 && checkedCount < todoList.value.length
})

// Thêm method để xử lý chọn tất cả
const handleSelectAll = (e: any) => {
  const checked = e.target.checked
  console.log('Select all clicked:', checked)

  // Tạo một Set mới để lưu trữ các ID đã chọn
  const newCheckedTodo = new Set<string>()

  if (checked) {
    // Chọn tất cả
    todoList.value.forEach((todo) => {
      newCheckedTodo.add(todo.id)
    })
  }

  // Gán lại giá trị mới cho checkedTodo
  checkedTodo.value = newCheckedTodo

  console.log('Updated checked todos:', Array.from(checkedTodo.value))
}

// type todo
const listTypeTodo = ref<TypeTodoResponse[]>()
const typeValue = ref('')

const fetchDataTypeTodo = async () => {
  const res = await getListTypeTodo()
  listTypeTodo.value = res.data
}

const selectedText = reactive<{ [key: string]: string }>({})
const selectedKey = reactive<{ [key: string]: string[] }>({})

const handleSelect = async (event, id) => {
  try {
    const levelMap = {
      '3': 'Thấp',
      '2': 'Trung bình',
      '1': 'Cao',
      '0': 'Quan trọng'
    }
    const key = event.key
    console.log('🚀 Bắt đầu cập nhật - Selected key:', key, 'for ID:', id)

    // Cập nhật UI trước
    selectedKey[id] = [key]
    selectedText[id] = levelMap[key]

    const priorityLevelMap = {
      '3': 'THAP',
      '2': 'TRUNG_BINH',
      '1': 'CAO',
      '0': 'QUAN_TRONG'
    }

    // Tạo payload để gửi
    const payload = {
      name: editedTaskName[id],
      code: editedTaskName[id],
      priorityLevel: key
    }
    console.log('📤 Payload sẽ gửi đi:', payload)

    // Gọi API cập nhật
    try {
      await todoWebSocket.sendMessageUpdateTodo(id, payload)
      console.log('✅ API đã được gọi thành công')

      // Gọi fetchDataTodo để cập nhật lại dữ liệu
      // await fetchDataTodo()

      toast.success('Cập nhật mức độ thành công')
    } catch (error) {
      throw error // Ném lỗi để catch bên ngoài xử lý
    }
  } catch (error) {
    toast.error('Có lỗi xảy ra khi cập nhật mức độ')
  }
}

const handleTodoClick = (idTodo: string, event: Event) => {
  selectedTodoId.value = idTodo
  showTaskDetailModal.value = true
}

const showTaskDetailModal = ref(false)

const selectedTodoId = ref<string | null>(null)

const openTask = ref(false)
const toggleTaskVisibility = () => {
  openTask.value = !openTask.value
}

const isAddingTask = ref(false)

const newTaskName = ref('')

const showAddTaskInput = () => {
  isAddingTask.value = true
  newTaskName.value = ''
}

const valuePriorityLevel = reactive({})

const checkedTodo = ref(new Set<string>())

const countStaffProject = ref(0)

const todoList = ref([...props.todos])

const listUserVote = ref([])
// Khi dữ liệu thay đổi, cập nhật danh sách Todo
watch(
  () => props.todos,
  (newTodos) => {
    idProject.value = route.params.id as string
    dataTodoProject()
    // todoList.value = [...newTodos]
  },
  { deep: true }
)

const route = useRoute()
const idProject = ref('')
const dataTodoProject = debounce(async () => {
  const res = await getAllTodo({
    idProject: idProject.value,
    page: 1,
    size: 1000
  })

  console.log('ppppppppppppppppppp')
  console.table(res.data)

  todoList.value = res.data as unknown as todoResponse[]

  for (const todo of todoList.value) {
    const key =
      todo.priorityLevel === 'CAO'
        ? '1'
        : todo.priorityLevel === 'THAP'
        ? '3'
        : todo.priorityLevel === 'TRUNG_BINH'
        ? '2'
        : todo.priorityLevel === 'QUAN_TRONG'
        ? '0'
        : null

    selectedKey[todo.id as string] = [key]
    selectedText[todo.id as string] = {
      '3': 'Thấp',
      '2': 'Trung bình',
      '1': 'Cao',
      '0': 'Quan trọng'
    }[key]

    valuePriorityLevel[todo.id as string] = key

    editedTaskName[todo.id as string] = todo.name
  }
}, 100)

// watchEffect(() => {
//   todoList.value = [...props.todos]
// })

watch(
  todoList,
  (newValue) => {
    console.log('🔥 Danh sách Todo sau khi kéo thả:', newValue)
    countTask.value = newValue.length
  },
  { deep: true }
)

const isTodoChecked = (id: string) => {
  return checkedTodo.value.has(id)
}

// Gửi sự kiện cập nhật Todo

const handleDraggableChange = (event) => {
  if (event.removed) {
    const removedTodo = event.removed.element

    // Kiểm tra nếu todo bị xóa có trong danh sách đã chọn
    if (checkedTodo.value.has(removedTodo.id)) {
      // Lấy tất cả các todo đã chọn
      const selectedTodos = props.todos.filter((todo) => checkedTodo.value.has(todo.id))
      // Log các todo đã chọn

      // Xóa tất cả các todo đã chọn
      selectedTodos.forEach((todo) => {
        const index = props.todos.findIndex((t) => t.id === todo.id)
        if (index !== -1) {
          props.todos.splice(index, 1)
        }
      })

      // Xóa danh sách đã chọn
      checkedTodo.value.clear()
    } else {
      // Xử lý xóa một todo đơn lẻ
      const index = props.todos.findIndex((todo) => todo.id === removedTodo.id)
      if (index !== -1) {
        props.todos.splice(index, 1)
      }
    }
    // emit('update-todos', props.todos)
  }
}

// const sortTasksByProgress = debounce(() => {
//   props.todos.sort((a, b) => {
//     const percentA = percentMap[a.id as string] || 0
//     const percentB = percentMap[b.id as string] || 0
//     return percentB - percentA // Sắp xếp giảm dần theo progress
//   })
// }, 1000)

// const debouncedUpdatePercentVoted = debounce((id: string) => {
//   updatePercentVoted(id)
// }, 100)

const dataFecth = async () => {
  try {
    const res = await getAllTodo(paramsTodo)
    todoList.value = res.data as unknown as todoResponse[]

    countTask.value = todoList.value.length
  } catch (error) {
    console.error('❌ Lỗi khi lấy dữ liệu todo:', error)
  }
}

const handleCheckboxChange = (id: string, e: any) => {
  if (e.target.checked) {
    checkedTodo.value.add(id)
  } else {
    checkedTodo.value.delete(id)
  }
}

const isChecked = (id: string) => {
  return checkedTodo.value.has(id)
}

const percentMap = reactive<Record<string, number>>({})
const userVoteMap = reactive<Record<string, string>>({})
const updatePercentVoted = async (id: string) => {
  console.log('Đã chạy vào cập nhật task')

  if (!id || countStaffProject.value === 0) {
    percentMap[id] = 0
    return
  }

  console.log(`📡 Gọi API getAllUserByTodo cho Task ID: ${id}`)

  try {
    const res = await getAllUserByTodo(paramsTodo, id)
    listUserVote.value = res.data.content as []
    console.log(`✅ Dữ liệu trả về cho Task ID: ${id}:`, res.data)

    if (!res || !res.data || !res.data.content) {
      console.error(`❌ Lỗi: Không có dữ liệu hợp lệ cho Task ID: ${id}`)
      return
    }

    // for (const item of res.data.content) {
    //   userVoteMap[item.id as string] =
    //     item.imageStudent == null ? item.imageStaff : item.imageStudent
    // }

    const votedPercent = Math.floor((res.data.content.length / countStaffProject.value) * 100)
    console.log(`📊 Phần trăm cập nhật cho Task ID ${id}: ${votedPercent}%`)

    percentMap[id] = votedPercent

    // sortTasksByProgress()
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

const countTask = ref(0)
const editedTaskId = ref<string | null>(null)
// const editedTaskName = ref<string>('')
const editedTaskName = reactive<{ [key: string]: string }>({})

const user = localStorageAction.get(USER_INFO_STORAGE_KEY)
// Xóa task
const removeTask = (id: string) => {
  // Xóa Todo khỏi danh sách
  const index = todoList.value.findIndex((todo) => todo.id === id)
  if (index !== -1) {
    todoList.value.splice(index, 1)
  }

  // Gửi API xóa (nếu cần)
  todoWebSocket.sendMessageDeleteTodo(id)

  console.log(`🗑 Đã xóa Task ID: ${id}`)
}

const newTask = ref('')

// Dữ liệu

const fetchDataTodo = debounce(async () => {
  try {
    const resData = await getAllStaffProject(idProject.value)
    countStaffProject.value = resData.data.length as number

    console.log('số lượng thành viên ', countStaffProject.value)

    emit('data-staff-project', resData.data)

    console.log('Danh sách todooooo', todoList.value)

    const res = await getAllTodo({ idProject: idProject.value, page: 1, size: 1000 })
    todoList.value = res.data as unknown as todoResponse[]
    console.log('Danh sách todooooo sao khi gọi', todoList.value)
    await Promise.all(
      todoList.value.map(async (todo) => {
        console.log(`🚀 Gọi API updatePercentVoted cho ID: ${todo.id}`)
        await updatePercentVoted(todo.id as string)
      })
    )

    for (const todo of props.todos) {
      const key =
        todo.priorityLevel === 'CAO'
          ? '1'
          : todo.priorityLevel === 'THAP'
          ? '3'
          : todo.priorityLevel === 'TRUNG_BINH'
          ? '2'
          : todo.priorityLevel === 'QUAN_TRONG'
          ? '0'
          : null

      selectedKey[todo.id as string] = [key]
      selectedText[todo.id as string] = {
        '3': 'Thấp',
        '2': 'Trung bình',
        '1': 'Cao',
        '0': 'Quan trọng'
      }[key]

      valuePriorityLevel[todo.id as string] = key

      editedTaskName[todo.id as string] = todo.name
    }
    // sortTasksByProgress()
  } catch (error) {
    console.error('❌ Lỗi khi lấy dữ liệu todo:', error)
  }
}, 100)

onMounted(async () => {
  idProject.value = route.params.id as string
  dataFecth()

  fetchDataTypeTodo()
  await fetchDataTodo()
  todoWebSocket.subscribeCreateTodo(dataTodoProject)
  todoWebSocket.subscribeDeleteTodo(dataTodoProject)
  todoWebSocket.subscribeUpdateTodo(dataTodoProject)
  todoVoteWebSocket.subscribeCreateTodoVote(fetchDataTodo)
  todoVoteWebSocket.subscribeDeleteTodoVote(fetchDataTodo)
  valueLevel.value = null
})

// Thêm Task mới
const addTask = async () => {
  if (newTask.value == null || newTask.value == '' || typeValue.value == null) {
    isAddingTask.value = !isAddingTask.value
    toast.error('Vui lòng nhập đủ thông tin')
    return
  } else {
    const payload: dataCreateTodo = {
      name: newTask.value,
      code: newTask.value,
      idProject: props.idProject as string,
      idType: typeValue.value
    }

    isAddingTask.value = !isAddingTask.value
    console.log('📤 Payload gửi đi: ', payload)
    todoWebSocket.sendMessageCreateTodo(payload)
    newTask.value = ''
    dataTodoProject()
  }
}

// Bật chế độ chỉnh sửa
const enableEdit = (task: todoResponse) => {
  editedTaskId.value = task.id as string
  editedTaskName[task.id as string] = task.name
}

const valueLevel = ref(null)

const handlePriorityChange = (id: string, value: string) => {
  console.log(value + ':kskks')

  todoWebSocket.sendMessageUpdateTodo(id, {
    name: editedTaskName[id], // Nếu chưa chỉnh sửa thì giữ nguyên
    code: editedTaskName[id], // N
    priorityLevel: value,
    idType: typeValue.value
  })

  // sortTasksByProgress()

  toast.success('Cập nhật mức độ thành công')
}

// Cập nhật Task

const updateTask = async (id: string) => {
  if (!editedTaskId.value) return

  const currentPriority = valuePriorityLevel[id] // Giữ nguyên priority đã có

  await todoWebSocket.sendMessageUpdateTodo(id, {
    name: editedTaskName[id], // ✅ Lấy từ danh sách chỉnh sửa
    code: editedTaskName[id],
    priorityLevel: currentPriority as string,
    idType: typeValue.value
  })

  toast.success('Task cập nhật thành công!')
  // fetchDataTodo()
  editedTaskId.value = null
}

watch(valueLevel, (newValue) => {
  emit('level', newValue)
})

// Thêm method mới
const handleDragStart = (evt) => {
  const draggedTodoId = evt.item.dataset.id

  // Nếu todo được kéo nằm trong danh sách đã chọn
  if (checkedTodo.value.has(draggedTodoId)) {
    // Lấy tất cả các todo đã chọn
    const selectedTodos = todoList.value.filter((todo) => checkedTodo.value.has(todo.id))
    // Gán thông tin về các todo đã chọn vào element được kéo
    evt.item._underlying_vm_.selectedTodos = selectedTodos
  }
}

const handleTodoDrop = (evt) => {
  if (!evt.item.dataset.selectedTodos) return

  const selectedTodos = JSON.parse(evt.item.dataset.selectedTodos)
  // Xóa tất cả các todo đã chọn khỏi danh sách
  todoList.value = todoList.value.filter((todo) => !selectedTodos.includes(todo.id))
  // emit('update-todos', todoList.value)
}
</script>

<style scoped>
/* Đặt style bên ngoài scoped để đảm bảo có thể override styles của Ant Design */
.ant-modal-mask {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  background-color: rgba(0, 0, 0, 0.65) !important;
  backdrop-filter: blur(4px) !important;
  z-index: 1000 !important;
}

.custom-modal {
  width: 300% !important;
}

.ant-modal-wrap {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  z-index: 1001 !important;
  overflow: auto !important;
  outline: 0 !important;
  -webkit-overflow-scrolling: touch !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.ant-modal-body {
  padding: 24px !important;
  max-height: 80vh !important;
  overflow-y: auto !important;
}

.ant-modal {
  position: relative !important;
  width: 800px !important;
  max-width: 90vw !important;
  margin: 0 auto !important;
  top: 0 !important;
  padding-bottom: 24px !important;
}

.ant-modal-content {
  position: relative !important;
  background-color: #fff !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
  width: 100% !important;
}

/* Animation cho modal */
.ant-modal-enter-active,
.ant-modal-leave-active {
  transition: opacity 0.3s ease !important;
}

.ant-modal-enter-from,
.ant-modal-leave-to {
  opacity: 0 !important;
}

.ant-modal-enter-to,
.ant-modal-leave-from {
  opacity: 1 !important;
}
</style>

<style scoped>
.cursor-grab {
  cursor: grab;
}

.custom-row-height td {
  height: 10px !important; /* Giảm chiều cao hàng */
}

.selected-todo {
  background-color: rgba(59, 130, 246, 0.1) !important;
}

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

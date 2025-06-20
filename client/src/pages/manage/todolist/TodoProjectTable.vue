<template>
  <DivCustom label="Danh sách công việc theo dự án" customClasses="mt-5">
    <template #icon>
      <OrderedListOutlined />
    </template>
    <template #extra>
      <div class="flex w-full justify-end">
        <a-tooltip title="Thêm công việc mới">
          <a-button
            type="primary"
            @click="openTodoModal"
            class="flex items-center justify-center px-4 mr-2"
          >
            Thêm
          </a-button>
        </a-tooltip>

        <a-tooltip title="Import task">
          <a-button
            type="primary"
            @click="emit('openModalImportTodoList')"
            class="flex items-center justify-center px-4 mr-2"
          >
            Import task
          </a-button>
        </a-tooltip>

        <a-tooltip title="Download template">
          <a-button
            type="primary"
            @click="emit('downloadTemplateImport')"
            class="flex items-center justify-center px-4 mr-2"
          >
            Download template
          </a-button>
        </a-tooltip>

        <!-- <a-tooltip title="Thêm mới task">
          <a-button type="primary" @click="handleAddClick" class="flex items-center justify-center px-4 mr-2">
            Thêm Task
          </a-button>
        </a-tooltip> -->

        <a-tooltip title="Import Log">
          <a-button
            type="primary"
            @click="emit('importLog')"
            class="flex items-center justify-center px-4 mr-2"
          >
            Lịch sử
          </a-button>
        </a-tooltip>

        <a-tooltip title="Download file import lỗi">
          <a-button
            type="primary"
            @click="emit('downloadImportLog')"
            class="flex items-center justify-center px-4"
          >
            Download import lỗi
          </a-button>
        </a-tooltip>
      </div>
    </template>

    <div class="min-h-[360px]">
      <a-table
        :columns="columns"
        :data-source="todo"
        :rowKey="(record) => record.idTodo"
        :pagination="{
          current: paginationParams.page,
          pageSize: paginationParams.size,
          total: totalItems,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '30', '40', '50']
        }"
        :scroll="{ x: 1000, y: 300 }"
        @change="handlePageChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'nameTodo'">
            <div
              v-if="editingTodoId !== record.idTodo"
              @click="startEditingTitle(record)"
              class="cursor-pointer hover:bg-gray-100 p-1 rounded"
            >
              {{ record.nameTodo || 'Không có tiêu đề' }}
            </div>

            <a-input
              v-else
              v-model:value="editedTitle"
              size="large"
              @pressEnter="saveTitle(record)"
              @blur="saveTitle(record)"
              class="w-full"
              style="min-width: 120px; border: none; outline: none"
              placeholder="Nhập tiêu đề thẻ"
            />
          </template>

          <template v-if="column.key === 'assignee'">
            <div class="flex items-center flex-wrap gap-2" @click="openMemberModal(record)">
              <a-tooltip
                v-for="member in [...record.students, ...record.staff]"
                :key="member.id"
                :title="member.email"
              >
                <a-avatar :src="member.image" :alt="member.name" />
              </a-tooltip>
              <!-- <a-button shape="circle" icon="+"  /> -->
              <div
                v-if="
                  (!record.students || record.students.length === 0) &&
                  (!record.staff || record.staff.length === 0)
                "
                class="text-gray-500 cursor-pointer"
              >
                Chọn thành viên
              </div>
            </div>
          </template>

          <template v-if="column.key === 'namePhaseProject'">
            <a-select
              v-model:value="record.namePhaseProject"
              style="min-width: 120px"
              @change="(idPhase) => handlePhaseChange(record, idPhase)"
            >
              <!-- Dropdown options lấy từ API fetchPhases -->
              <a-select-option v-for="phase in phases" :key="phase.idPhase" :value="phase.idPhase">
                {{ phase.namePhaseProject }}
              </a-select-option>
            </a-select>
          </template>

          <template v-if="column.key === 'priorityLevel'">
            <a-select
              v-model:value="record.priorityLevel"
              style="min-width: 120px"
              @change="(value) => selectPriority(record, value)"
            >
              <a-select-option
                v-for="(label, key) in statusPriorityLevelOptions"
                :key="key"
                :value="key"
              >
                <a-tag :color="statusPriorityLevelColors[key]">{{ label }}</a-tag>
              </a-select-option>
            </a-select>
          </template>

          <template v-if="column.key === 'deadline'">
            <div
              class="cursor-pointer hover:bg-gray-100 p-1 rounded"
              @click="handClickDeadline(record.idTodo, +record.deadline)"
            >
              {{
                record.deadline
                  ? dayjs(Number(record.deadline)).format('DD/MM/YYYY HH:mm')
                  : 'Chưa có ngày hạn'
              }}
            </div>
          </template>

          <template v-if="column.key === 'operation'">
            <a-popconfirm
              title="Bạn chắc chắn muốn xóa công việc"
              @confirm="deleteTodo(record.idTodo as string)"
            >
              <a-tooltip title="Xóa công việc" placement="left">
                <a-button
                  class="items-center justify-center text-center"
                  style="
                    background-color: #fee2e2 !important;
                    color: #dc2626 !important;
                    border: none !important;
                  "
                >
                  <DeleteOutlined />
                </a-button>
              </a-tooltip>
            </a-popconfirm>
          </template>
        </template>
      </a-table>

      <MaTodoMemberModal
        v-model="showAddMemberModal"
        @close="showAddMemberModal = false"
        :todoId="selectedTodoId"
      />

      <TodoDeadlineModal
        :model-value="isDeadlineModal"
        :current-deadline="stateDeadline.currentTime"
        :current-reminder="''"
        @update:model-value="handleUpdateDeadlineModal"
        @update-success-deadline="updateSuccessDeadline"
      />
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import {
  getAllPhaseMa,
  TodoProjectResponse,
  updateTodoPhaseProject
} from '@/services/api/manage/todolist/todolist.api'
import { webSocketMemberProject } from '@/services/service/member/memberproject.socket'
import { MBMeUpdateTodoRequest, webSocketTodo } from '@/services/service/member/metodo.socket'
import { DeleteFilled, DeleteOutlined, OrderedListOutlined } from '@ant-design/icons-vue'
import { Tag } from 'ant-design-vue'
import dayjs from 'dayjs'
import { defineEmits, defineProps, h, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { toast } from 'vue3-toastify'
import MaTodoMemberModal from './MaTodoMemberModal.vue'
import { todoWebSocket } from '@/services/api/manage/todo/todo.socket.api'
import TodoDeadlineModal from '@/pages/member/project/projectdetails/projectmodal/TodoDeadlineModal.vue'
import { localStorageAction } from '@/utils/storage'
import { TODO_ID_STORAGE_KEY } from '@/constants/key'

const props = defineProps<{
  todoId: string | null
  modelValue: boolean
  searchQuery: string
  todo: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
  currentPriority?: number
}>()
const meAllDetailTodo = ref<TodoProjectResponse | null>(null)
const emit = defineEmits([
  'page-change',
  'refresh-todo',
  'updatePriority',
  'downloadImportLog',
  'importLog',
  'downloadTemplateImport',
  'openModalImportTodoList',
  'openTodoModal'
])
const isEditingTitle = ref(false)
const showAddMemberModal = ref(false)
const selectedTodoId = ref<string | null>(null)

const openMemberModal = (record) => {
  selectedTodoId.value = record.idTodo
  showAddMemberModal.value = true
}
const editingTodoId = ref<string | null>(null)
const editedTitle = ref('')

const startEditingTitle = (record) => {
  editingTodoId.value = record.idTodo
  editedTitle.value = record.nameTodo || ''
}
const route = useRoute()

const phases = ref<any[]>([])
const loading = ref(true)

const deadlineTodo = ref(null)

const saveTitle = (record) => {
  if (editedTitle.value.trim() !== record.nameTodo) {
    const payload = {
      idTodo: record.idTodo,
      nameTodo: editedTitle.value.trim()
    }
    webSocketTodo.sendUpdateNameTodo(payload)
    record.nameTodo = editedTitle.value.trim()
  }
  editingTodoId.value = null
}
const handlePhaseChange = async (record: any, idPhase: string) => {
  try {
    const response = await updateTodoPhaseProject(idPhase, record.idTodo, record.projectId)

    toast.success('Cập nhật Sprint thành công!')
    emit('refresh-todo')
  } catch (err) {
    toast.error('Cập nhật Sprint thất bại!')
  }
}

const columns = [
  { title: 'STT', key: 'orderNumber', dataIndex: 'orderNumber', width: 60, align: 'center' },
  { title: 'Tên', key: 'nameTodo', dataIndex: 'nameTodo', width: 300, align: 'left' },
  {
    title: 'Trạng thái',
    key: 'statusTodo',
    dataIndex: 'statusTodo',
    width: 150,
    align: 'center',
    customRender: ({ text }) => {
      const statusMap = {
        CHUA_HOAN_THANH: { color: 'blue', label: 'Chưa hoàn thành' },
        HOAN_THANH_SOM: { color: 'orange', label: 'Hoàn thành sớm' },
        DA_HOAN_THANH: { color: 'green', label: 'Đã hoàn thành' },
        QUA_HAN: { color: 'red', label: 'Quá hạn' }
      }
      return h(
        Tag,
        { color: statusMap[text]?.color || 'default' },
        () => statusMap[text]?.label || text
      )
    }
  },
  {
    title: 'Sprint',
    key: 'namePhaseProject',
    dataIndex: 'namePhaseProject',
    width: 150,
    align: 'center'
  },

  {
    title: 'Người được giao task',
    key: 'assignee',
    dataIndex: 'email',
    width: 300,
    align: 'center'
  },

  {
    title: 'Sự ưu tiên',
    key: 'priorityLevel',
    dataIndex: 'priorityLevel',
    width: 150,
    align: 'center'
  },
  {
    title: 'Ngày tạo',
    key: 'createdDate',
    dataIndex: 'createdDate',
    width: 150,
    align: 'center',
    customRender: ({ text }) =>
      text ? h(Tag, {}, () => dayjs(Number(text)).format('DD/MM/YYYY HH:mm')) : ''
  },
  {
    title: 'Ngày Cập nhật',
    key: 'lastModifiedDate',
    dataIndex: 'lastModifiedDate',
    width: 150,
    align: 'center',
    customRender: ({ text }) =>
      text ? h(Tag, {}, () => dayjs(Number(text)).format('DD/MM/YYYY HH:mm')) : ''
  },
  {
    title: 'Ngày Hạn',
    key: 'deadline',
    dataIndex: 'deadline',
    width: 150,
    align: 'center'
  },
  {
    title: 'Hành động',
    key: 'operation',
    dataIndex: 'operation',
    width: 150,
    fixed: 'right',
    align: 'center'
  }
]

const handlePageChange = (pagination: any) => {
  emit('page-change', { page: pagination.current, pageSize: pagination.pageSize })
}

const statusPriorityLevelOptions = {
  QUAN_TRONG: 'Quan trọng',
  CAO: 'Cao',
  TRUNG_BINH: 'Trung bình',
  THAP: 'Thấp'
}

const statusPriorityLevelColors = {
  THAP: 'blue',
  CAO: 'orange',
  TRUNG_BINH: 'green',
  QUAN_TRONG: 'red'
}

const fetchPhases = async (id: string) => {
  try {
    const response = await getAllPhaseMa(id) // Gọi API `getAllPhaseMa`
    phases.value = response.data // Lưu dữ liệu Phase vào phases
    console.log('ssss: ', response.data)
  } catch (error) {
    console.error('Lỗi khi tải danh sách Phase:', error)
  }
}
onMounted(async () => {
  await fetchPhases(route.params.id as string)
  console.log('Phases đã được tải:', phases.value)
})

const visible = ref(props.modelValue)
const selectedPriority = ref<number | null>(props.currentPriority ?? null)

watch(
  () => props.modelValue,
  (val) => {
    visible.value = val
    if (val) {
      selectedPriority.value = props.currentPriority ?? null
    }
  }
)

const selectPriority = (record: any, priority: string) => {
  console.log('Selected Priority:', priority)

  const priorityLevelMap = {
    QUAN_TRONG: 0,
    CAO: 1,
    TRUNG_BINH: 2,
    THAP: 3
  }

  record.priorityLevel = priorityLevelMap[priority] || 0

  const payload: MBMeUpdateTodoRequest = {
    idTodo: record.idTodo,
    idTodoChange: record.idTodo,
    priorityLevel: record.priorityLevel
  }

  console.log('Sending payload to WebSocket:', payload)

  webSocketTodo.sendUpdatePriorityLevel(payload)

  emit('updatePriority', { idTodo: record.idTodo, priorityLevel: record.priorityLevel })
}

const openTodoModal = () => {
  emit('openTodoModal')
}

const deleteTodo = (idTodo: string): void => {
  try {
    todoWebSocket.sendMessageDeleteTodo(idTodo)
    toast.success('Xóa thành công')
  } catch (error) {
    console.log(error)
  }
}

// deadline
watch(selectedTodoId, (newVal) => {
  localStorageAction.set(TODO_ID_STORAGE_KEY, newVal)
})

const isDeadlineModal = ref<boolean>(false)
const stateDeadline = reactive({
  currentTime: null as number | null
})

const handClickDeadline = (id: string, currentTime: null | number): void => {
  isDeadlineModal.value = true
  selectedTodoId.value = id
  stateDeadline.currentTime = currentTime
}

const handleUpdateDeadlineModal = (isClose: boolean) => {
  isDeadlineModal.value = isClose
}

const updateSuccessDeadline = () => {
  emit('refresh-todo')
}

webSocketTodo.getUpdateNameTodo((updatedTodo) => {
  const todoItem = props.todo.find((item) => item.id === updatedTodo.idTodo)
  if (todoItem) {
    todoItem.nameTodo = updatedTodo.nameTodo
  }
  emit('refresh-todo')
})

webSocketMemberProject.getJoinTodoVote((record) => {
  if (props.todoId == record.idTodo) {
    emit('refresh-todo')
  }
})
webSocketMemberProject.getOutTodoVoteMemberProject((record) => {
  if (props.todoId == record.idTodo) {
    emit('refresh-todo')
  }
})

webSocketTodo.getUpdatePriorityLevel((record: MBMeUpdateTodoRequest) => {
  if (props.todoId === record.idTodo) {
    emit('refresh-todo')
  }
})
</script>

<style scoped>
:deep(.ant-select-selector) {
  border: none !important;
}

:deep(.ant-select-open .ant-select-selector) {
  border: none !important;
  box-shadow: none !important;
}

:deep(.ant-select-selector:focus),
:deep(.ant-select-selector:hover) {
  border: none !important;
  box-shadow: none !important;
}
</style>

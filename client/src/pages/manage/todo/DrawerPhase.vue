<template>
  <a-drawer
    :title="title"
    :width="'30%'"
    :open="open"
    @close="onClose"
    class="phase-completion-drawer"
  >
    <template #extra>
      <a-button style="margin-right: 8px" @click="onClose">Hủy</a-button>
      <a-button type="primary" @click="handleComplete">Xác nhận hoàn thành</a-button>
    </template>

    <div class="completion-form">
      <p class="font-semibold text-lg mb-4">Danh sách công việc trong Sprint</p>

      <!-- Thêm kiểm tra null cho dataSource -->
      <template v-if="formattedTodos && formattedTodos.length > 0">
        <a-table :dataSource="formattedTodos" :columns="columns" :pagination="false" class="mb-6">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record?.status || '')">
                {{ getStatusText(record?.status || '') }}
              </a-tag>
            </template>
            <template v-if="column.key === 'progress'">
              <a-progress
                :percent="Number(record?.progress || 0)"
                size="small"
                :stroke-color="getProgressColor(Number(record?.progress || 0))"
              />
            </template>
          </template>
        </a-table>
      </template>
      <template v-else>
        <p>Không có công việc nào trong Sprint này</p>
      </template>

      <div class="mt-6">
        <p class="font-semibold mb-2">Ghi chú kết thúc Sprint:</p>
        <a-textarea
          v-model:value="completionNote"
          placeholder="Ghi chú về Sprint (không bắt buộc)"
          :rows="4"
        />
      </div>
    </div>
  </a-drawer>
</template>

<script lang="ts" setup>
import { ref, computed, reactive } from 'vue'
import { toast } from 'vue3-toastify'

interface Todo {
  id?: string
  name?: string
  status?: string
  progress?: number
  type?: string
  // thêm các trường khác nếu cần
}

const props = defineProps<{
  open: boolean
  title: string
  todos?: Todo[]
}>()

const dataStage = reactive({
  openModal: false,
  stage: '',
  days: 3,
  endDate: null
})

const openModal = () => {
  dataStage.openModal = true
}

const emit = defineEmits(['close', 'success'])
const completionNote = ref('')

const check = ref(true)

// Sửa lại computed property
const formattedTodos = computed(() => {
  if (!Array.isArray(props.todos)) return []
  if (props.todos.filter((item) => item.status === 'CHUA_HOAN_THANH').length > 0) {
    check.value = false
  } else {
    check.value = true
  }

  return props.todos
    .map((todo) => {
      console.log(todo.status)

      // Tạo object mới với các giá trị mặc định
      return {
        key: todo?.id || Math.random().toString(),
        name: todo?.name || 'Không có tên',
        status: todo?.status || 'CHUA_HOAN_THANH',
        progress: Number(todo?.progress || 0),
        type: todo?.type || '',
        ...todo // spread các thuộc tính khác
      }
    })
    .filter((todo) => todo !== null) // Lọc bỏ các giá trị null
})

// Định nghĩa cột với kiểm tra null
const columns = [
  {
    title: 'Tên công việc',
    dataIndex: 'name',
    key: 'name',
    width: '40%',
    render: (text: string) => text || 'Không có tên'
  },
  {
    title: 'Trạng thái',
    dataIndex: 'status',
    key: 'status',
    width: '25%'
  },
  {
    title: 'Tiến độ',
    dataIndex: 'progress',
    key: 'progress',
    width: '35%'
  }
]

// Hàm lấy màu cho status
const getStatusColor = (status: string) => {
  switch (status) {
    case 'CHUA_HOAN_THANH':
      return 'blue'
    case 'DANG_LAM':
      return 'orange'
    case 'DA_HOAN_THANH':
      return 'green'
    case 'QUA_HAN':
      return 'red'
    case 'HOAN_THANH_SOM':
      return 'orange'

    default:
      return 'default'
  }
}

// Hàm chuyển đổi text status
const getStatusText = (status: string) => {
  if (!status) return 'Chưa hoàn thành'

  switch (status) {
    case 'CHUA_HOAN_THANH':
      return 'Chưa hoàn thành'
    case 'DANG_LAM':
      return 'Đang làm'
    case 'DA_HOAN_THANH':
      return 'Đã hoàn thành'
    case 'QUA_HAN':
      return 'Quá hạn'
    case 'HOAN_THANH_SOM':
      return 'Hoàn thành sớm'
    default:
      return status
  }
}

// Hàm lấy màu cho progress bar
const getProgressColor = (progress: number) => {
  const safeProgress = Number(progress) || 0
  if (safeProgress >= 100) return '#52c41a'
  if (safeProgress >= 70) return '#1890ff'
  if (safeProgress >= 30) return '#faad14'
  return '#ff4d4f'
}

const onClose = () => {
  completionNote.value = ''
  check.value = true
  console.log(check.value)
  console.log(formattedTodos)

  emit('close')
}

const handleComplete = () => {
  // Xử lý logic hoàn thành sprint

  if (check.value) {
    emit('success', {
      note: completionNote.value
    })

    onClose()
    check.value = true
    console.log(check.value)
  } else {
    console.log(check.value)

    toast.error('Còn công việc chưa được hoàn thành')
  }
}
</script>

<style scoped>
.completion-form {
  padding: 24px;
}

:deep(.ant-table-thead > tr > th) {
  background: #f0f2f5;
  font-weight: 600;
}

:deep(.ant-table-tbody > tr > td) {
  padding: 8px 16px;
}
</style>

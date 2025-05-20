<template>
  <ModalCustom
    :open="props.open"
    @close="handleCloseModal"
    :title="props.title"
    @submit="handleUpload"
  >
    <div class="clearfix text-center">
      <a-upload
        :file-list="fileList"
        :before-upload="beforeUpload"
        @remove="handleRemove"
        accept=".xls,.xlsx"
      >
        <a-button>
          <upload-outlined></upload-outlined>
          Select File
        </a-button>
      </a-upload>
    </div>
  </ModalCustom>
</template>

<script setup lang="ts">
import ModalCustom from '@/components/custom/Modal/ModalCustom.vue'
import { reactive, ref } from 'vue'
import { UploadOutlined } from '@ant-design/icons-vue'
import type { UploadProps } from 'ant-design-vue'
import { toast } from 'vue3-toastify'
import { importStudent } from '@/services/api/admin/project/project.api'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import {
  fillAllStudent,
  getStudent,
  importLog,
  ParamsGetStudent
} from '@/services/api/admin/student.api'

const props = defineProps<{ open: boolean; title: string }>()
const emit = defineEmits(['close', 'fetchAll', 'updateLog'])

const handleCloseModal = () => {
  fileList.value = []
  emit('close')
}

const fileList = ref<UploadProps['fileList']>([])

const handleRemove: UploadProps['onRemove'] = (file) => {
  const index = fileList?.value?.indexOf(file)
  const newFileList = fileList?.value?.slice()
  newFileList?.splice(index, 1)
  fileList.value = newFileList
}

const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  fileList.value = [file]
  return false
}

// Thêm dòng mới vào logImport
const formattedDate = new Date(Date.now()).toLocaleString('vi-VN', {
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
  second: '2-digit'
})

const getFormattedDate = () => {
  return new Date().toLocaleString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const params: ParamsGetStudent = {
  search: null,
  searchDF: null,
  page: 5,
  size: 5
}

const user = localStorageAction.get(USER_INFO_STORAGE_KEY)
const dataImportLog = async () => {
  const res = await importLog()
}
const handleUpload = async () => {
  const formData = new FormData()
  fileList?.value?.forEach((file: UploadProps['fileList'][number]) => {
    formData.append('filePath', file as any)
  })

  try {
    const initialData = await fillAllStudent()

    const response = await importStudent(formData)

    const importTime = getFormattedDate()

    // 🟢 Lấy log cũ từ localStorage (nếu có)
    const oldLogs = JSON.parse(localStorage.getItem('IMPORT_LOGS') || '[]')

    const updatedData = await fillAllStudent()

    if (initialData.data.length == updatedData.data.length) {
      toast.error('Import thất bại')
      const newLogEntry = {
        email: user?.email || 'Chưa xác định',
        date: importTime,
        rolesCodes: user?.rolesNames ? user.rolesNames.join(', ') : 'Chưa xác định'
      }

      // 🟢 Cập nhật localStorage với log mới
      const updatedLogs = [newLogEntry, ...oldLogs]
      localStorage.setItem('IMPORT_LOGS', JSON.stringify(updatedLogs))
      emit('updateLog', newLogEntry)
      handleCloseModal()
    } else {
      toast.success('import thành công')
    }
    // 🟢 Tạo bản ghi mới

    // 🟢 Gửi log mới sang modal
    await dataImportLog()
    handleCloseModal()

    emit('fetchAll')
  } catch (error) {
    console.error('❌ Lỗi khi Import:', error)
  }
}
</script>
<style scoped>
.ant-btn {
  background-color: var(--selected-header) !important;
  color: var(--selected-text) !important;
  border-color: var(--selected-header) !important;
}

.ant-btn:hover,
.ant-btn:focus {
  background-color: var(--selected-header-hover) !important;
  border-color: var(--selected-header-hover) !important;
}
</style>
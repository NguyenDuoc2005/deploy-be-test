<template>
  <ModalCustom
    :open="props.open"
    @close="handleCloseModal"
    :title="props.title"
    @submit="handleUpload"
  >
    <div class="clearfix">
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
import { ref } from 'vue'
import { UploadOutlined } from '@ant-design/icons-vue'
import type { UploadProps } from 'ant-design-vue'
import { toast } from 'vue3-toastify'
import { getAllStaff, importStaff, paramGetStaff } from '@/services/api/admin/staff.api'

const props = defineProps<{ open: boolean; title: string }>()
const emit = defineEmits(['close', 'fetchAll'])

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

const handleUpload = async () => {
  const formData = new FormData()
  fileList?.value?.forEach((file: UploadProps['fileList'][number]) => {
    formData.append('file', file as any)
  })
  try {
    const params: paramGetStaff = {
      page: 1,
      size: 50,
      search: '',
      searchStatus: '',
      searchFacility: ''
    }

    const initialData = await getAllStaff(params)

    const response = await importStaff(formData)

    handleCloseModal()
    await new Promise((resolve) => setTimeout(resolve, 2000))

    const updatedData = await getAllStaff(params)

    console.table(initialData)

    console.table(updatedData)
    console.log("dữ liệu Trước khi import: ",initialData)
    const isDataChanged = initialData.data.length !== updatedData.data.length
    console.log("dữ liệu sau khi import: ",isDataChanged)

    if (isDataChanged) {
      toast.success('Import thành công')
      handleCloseModal()
      setTimeout(() => emit('fetchAll'), 1000)
    } else {
      toast.error('Import thất bại')
      handleCloseModal()
      setTimeout(() => emit('fetchAll'), 1000)
    }
  } catch (error) {
    toast.error('Import thất bại')
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
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
  import { localStorageAction } from '@/utils/storage'
  import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'

import { getAllTodoList, getAllTodoProject, importLogTodoList, importTodoList, ParamsGetTodoList } from '@/services/api/manage/todolist/todolist.api'
import { useRoute } from 'vue-router'
  
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
  
  const params: ParamsGetTodoList = {
    search: "",
    page: 1,
    size: 100
  }

  const route = useRoute()
  const projectId = ref<string | null>(route.query.projectId as string || null)
  
  const user = localStorageAction.get(USER_INFO_STORAGE_KEY)
  const dataImportLog = async () => {
    const res = await importLogTodoList()
  }


  const handleUpload = async () => {
  console.log('🔄 handleUpload() được gọi!')

  if (fileList.value.length === 0) {
    console.log('⚠️ Không có file để upload');
    return;
  }

  const formData = new FormData();
  fileList?.value?.forEach((file: UploadProps['fileList'][number]) => {
    formData.append('filePath', file as any);
  });

  try {
    const initialData = await getAllTodoProject(params, route.params.id as string);

    // Xác nhận không gọi lại API nhiều lần
    console.log('📡 Gửi request importTodoList...');
    const response = await importTodoList(formData, route.params.id as string);

    const updatedData = await getAllTodoProject(params, route.params.id as string);

    if (updatedData.data.data.length == initialData.data.data.length) {
      toast.error('Import thất bại');
    } else {
      toast.success('Import thành công');
    }

    await dataImportLog();
    handleCloseModal(); // Đảm bảo modal đóng và file list reset
    emit('fetchAll');
  } catch (error) {
    console.error('❌ Lỗi khi Import:', error);
  }
}


  </script>
  
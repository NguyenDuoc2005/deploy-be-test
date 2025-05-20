<template>
  <a-modal :open="open" :title="props.title" @cancel="closeModal" :width="'50%'">
    <template #footer>
      <a-popconfirm
        title="Bạn có chắc chắn muốn lưu thay đổi?"
        @confirm="handleSubmit"
        ok-text="Đồng ý"
        cancel-text="Huỷ"
      >
        <a-button type="primary">Xác nhận</a-button>
      </a-popconfirm>
      <a-button @click="closeModal">Huỷ</a-button>
    </template>

    <a-form :model="dataCreateProject" ref="formRef" name="formRef" autocomplete="off">
      <a-form-item label="Mã dự án" name="codeProject" :rules="rules.codeProject">
        <a-input v-model:value="dataCreateProject.codeProject" />
      </a-form-item>

      <a-form-item label="Tên dự án" name="nameProject" :rules="rules.nameProject">
        <a-input v-model:value="dataCreateProject.nameProject" />
      </a-form-item>

      <div class="grid grid-cols-3 gap-3">
        <a-form-item label="Cơ sở" name="idFacility" :label-col="{ span: 24 }">
          <a-select v-model:value="idFacility" placeholder="Chọn cơ sở" name="major">
            <a-select-option
              v-for="dataCate in dataFacility"
              :key="dataCate.id"
              :value="dataCate.id"
            >
              {{ dataCate.name }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="* Chuyên ngành cơ sở" :label-col="{ span: 24 }" name="id" :rules="id">
          <a-select v-model:value="id" placeholder="Chọn bộ môn cơ sở" name="id" class="w-full">
            <a-select-option
              v-for="df in dataDepartmentFacility"
              class="w-full"
              :key="df.id"
              :value="df.id"
            >
              {{ df.nameMajor + ' - ' + df.nameDepartment }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="* Loại thành viên" :label-col="{ span: 24 }" name="id">
          <a-select
            ref="select"
            v-model:value="valueSelectMember"
            placeholder="Chọn hiện thị"
            class="w-full"
          >
            <a-select-option value="1">Giảng viên</a-select-option>
            <a-select-option value="2">Sinh viên</a-select-option>
            <a-select-option value="3">GV - SV</a-select-option>
          </a-select>
        </a-form-item>
      </div>

      <div>
        <a-form-item name="members" class="w-full">
          <!-- Bảng hiển thị thành viên hiện tại của dự án -->
          <!-- <template v-if="dataTableProject.length > 0">
          <h3 class="text-lg font-semibold">Thành viên hiện có trong dự án</h3>
          <a-table :columns="columns" :data-source="dataTableProject" rowKey="email" bordered>
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'image'">
                <img :src="record.image" class="w-8 h-8 rounded-full" />
              </template>
              <template v-if="column.dataIndex === 'role'">
                <a-tag>{{ record.role }}</a-tag>
              </template>
            </template>
          </a-table>
        </template> -->

          <label>* Danh sách thành viên</label>
          <a-select
            v-model:value="selectedEmails"
            mode="multiple"
            show-search
            placeholder="Chọn thành viên"
            :filter-option="filterOption"
            class="w-full"
          >
            <template v-for="item in filteredSelectOptions" :key="item.email">
              <a-select-option :value="item.email" :label="item.name">
                <div class="flex items-center space-x-2">
                  <span class="font-semibold">{{ item.name }}</span>
                  <span>-</span>
                  <span class="font-semibold">{{ item.email }}</span>
                </div>
              </a-select-option>
            </template>
          </a-select>
        </a-form-item>

        <!-- Bảng hiển thị danh sách thành viên mới chọn -->
        <template v-if="tableData.length > 0">
          <h3 class="text-lg font-semibold">Danh sách thành viên được chọn</h3>
          <a-table :columns="columns" :data-source="tableData" rowKey="email" bordered>
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'image'">
                <img :src="record.image" class="w-8 h-8 rounded-full" />
              </template>
              <template v-if="column.dataIndex === 'role'">
                <a-select
                  class="w-full"
                  v-model:value="record.role"
                  @change="(value) => updateRole(record.email, value)"
                >
                  <template v-if="isFPTorFE(record.email)">
                    <a-select-option value="QUAN_Li">Quản lý</a-select-option>
                    <a-select-option value="DEV">DEV</a-select-option>
                    <a-select-option value="TESTER">TESTER</a-select-option>
                  </template>
                  <template v-else>
                    <a-select-option value="DEV">DEV</a-select-option>
                    <a-select-option value="TESTER">TESTER</a-select-option>
                  </template>
                </a-select>
              </template>
            </template>
          </a-table>
        </template>
      </div>

      <a-form-item label="Mô tả" name="description">
        <a-textarea class="w-full" rows="4" v-model:value="dataCreateProject.descriptions" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits, onMounted, reactive, computed } from 'vue'
import { toast } from 'vue3-toastify'
import axios from 'axios'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import {
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight
} from '@fortawesome/free-solid-svg-icons'

library.add(faReceipt, faPenToSquare, faCircleInfo, faFilter, faRotateRight)
import {
  createProjectRequest,
  detailProject,
  facilityResponse,
  getAllFacilityProject,
  getAllPJDepartFacility,
  pjDepartmentFacilityResponse,
  updateProject
} from '@/services/api/manage/project/maproject.api'
import {
  getAllProjectStaff,
  getAllProjectStudent,
  getAllStaffByProject,
  getAllStaffByStudent,
  projectStudentResponse
} from '@/services/api/manage/project/maprojectstudent.api'
import dayjs from 'dayjs'
import {
  CategoryResponse,
  getAllCategory,
  ParamsGetCategory
} from '@/services/api/admin/category/category.api'
const selectedMembers = ref<{ email: string; role: string }[]>([])

const dataTableStaff = ref<projectStudentResponse[]>([])
const dataDepartmentFacility = ref<pjDepartmentFacilityResponse[]>([])
const valueSelectMember = ref<string | null>(null)
const id = ref()
const dataTableProject = ref<projectStudentResponse[]>([])
const idFacility = ref()
const dataCreateProject = reactive({
  nameProject: '',
  codeProject: '',
  startTime: '',
  endTime: '',
  idMajorFacility: '',
  descriptions: '',
  idCategory: ''
})

const isFPTorFE = (email) => {
  return email?.endsWith('@fe.edu.vn') || email?.endsWith('@fpt.edu.vn')
}

const listMember = [
  {
    name: '',
    code: '',
    email: '',
    image: '',
    role: ''
  }
]

const dataFacility = ref<facilityResponse>()

const fetchDataFacility = async () => {
  const res = await getAllFacilityProject()
  dataFacility.value = res.data
}

const selectedEmails = computed({
  get: () => selectedMembers.value.map((member) => member.email),
  set: (emails) => {
    selectedMembers.value = emails.map((email) => {
      const existingMember = selectedMembers.value.find((m) => m.email === email)
      return existingMember || { email, role: '' }
    })
  }
})

const updateRole = (email, newRole) => {
  selectedMembers.value = selectedMembers.value.map((member) =>
    member.email === email ? { ...member, role: newRole } : member
  )

  const listMemberIndex = listMember.findIndex((m) => m.email === email)
  if (listMemberIndex !== -1) {
    listMember[listMemberIndex].role = newRole
  }
  console.log('selectedMembers sau cập nhật:', selectedMembers.value)
}

const filterOption = (input, option) => {
  return (
    option?.value?.toLowerCase().includes(input.toLowerCase()) ||
    option?.label?.toLowerCase().includes(input.toLowerCase())
  )
}

watch(idFacility, async (newvalue) => {
  const res = await getAllPJDepartFacility(newvalue as unknown as string)

  dataDepartmentFacility.value = res.data as unknown as []
})

const tableData = computed(() => {
  return selectedMembers.value.map((member) => {
    const staff = dataTableStaff.value.find((m) => m.email === member.email)
    return {
      name: staff?.name,
      email: staff?.email,
      avatar: staff?.image,
      role: member.role
    }
  })
})

watch(selectedMembers, () => {
    console.log('Trước updateListMember:', selectedMembers.value)
  updateListMember()
    console.log('Sau updateListMember:', listMember)

})

const filteredSelectOptions = computed(() => {
  const existingEmails = new Set(tableData.value.map((m) => m.email))

  if (valueSelectMember.value === '1') {
    return dataTableStaff.value.filter(
      (m) =>
        (m.email.endsWith('@fe.edu.vn') || m.email.endsWith('@fpt.edu.vn')) &&
        !existingEmails.has(m.email)
    )
  } else if (valueSelectMember.value === '2') {
    return dataTableStaff.value.filter(
      (m) => m.email.endsWith('@gmail.com') && !existingEmails.has(m.email)
    )
  }

  // Loại bỏ những người đã có role khỏi danh sách chung
  return dataTableStaff.value.filter((m) => !existingEmails.has(m.email))
})

const updateListMember = () => {
  const updatedList = [...listMember]
  console.log("updateList: ", updatedList)
  if (selectedMembers.value.length === 0) {
        console.log('Không có thay đổi, giữ nguyên listMember:', listMember)
    return
  }

  selectedMembers.value.forEach((select) => {
    const member = dataTableStaff.value.find((m) => m.email === select.email)
    if (member) {
      const existingIndex = updatedList.findIndex((m) => m.email === member.email)

      if (existingIndex !== -1) {
        if (updatedList[existingIndex].role !== select.role) {
          updatedList[existingIndex].role = select.role
        }
      } else {
        updatedList.push({
          name: member.name,
          code: member.code || '',
          email: member.email,
          image: member.image || 'default.png',
          role: select.role
        })
      }
    }
  })

  const finalList = updatedList.filter((m) =>
    selectedMembers.value.some((s) => s.email === m.email)
  )

  if (finalList.length === 0) {
    console.log(
      'Không có thay đổi hoặc toàn bộ thành viên bị xóa, giữ nguyên listMember:',
      listMember
    )
    return
  }

  listMember.splice(0, listMember.length, ...finalList)
}

watch(valueSelectMember, async (newValue) => {
  try {
    let newData: projectStudentResponse[] = []

    if (newValue === '1') {
      const res = await getAllProjectStaff(id.value as string)
      newData = res.data || []
    } else if (newValue === '2') {
      const res = await getAllProjectStudent(id.value as string)
      newData = res.data || []
    } else {
      const [resStaff, resStudent] = await Promise.all([
        getAllProjectStaff(id.value),
        getAllProjectStudent(id.value as string)
      ])
      newData = [...(resStaff.data || []), ...(resStudent.data || [])]
    }

    const existingEmails = new Set(dataTableStaff.value.map((m) => m.email))
    const filteredNewData = newData.filter((m) => !existingEmails.has(m.email))

    dataTableStaff.value = [...dataTableStaff.value, ...filteredNewData]
  } catch (error) {
    console.error('Lỗi khi lấy dữ liệu:', error)
  }
})

onMounted(() => {
  fecthCategory()
  fetchDataFacility()
})

const props = defineProps<{
  open: boolean
  projectId: string | null
  title: string
  idFacilityProject: string
}>()
const emit = defineEmits(['close', 'success'])

const rules = {
  codeProject: [
    { required: true, message: 'Mã không được để trống!', trigger: 'blur' },
    { max: 50, message: 'Mã không được vượt quá 50 ký tự!', trigger: 'blur' }
  ],
  // role: [{ required: true, message: 'Role không được để trống!', trigger: 'change' }],
  nameProject: [
    { required: true, message: 'Tên không được để trống!', trigger: 'blur' },
    { max: 100, message: 'Tên không được vượt quá 100 ký tự!', trigger: 'blur' }
  ],
  startTime: [
  { required: true, message: 'Thời gian bắt đầu không được để trống!', trigger: 'blur' }
],
endTime: [
  { required: true, message: 'Thời gian kết thúc không được để trống!', trigger: 'blur' },
  {
    validator: (rule, value, callback, source) => {
      const start = source.startTime;
      const end = value;

      if (!start || !end) {
        return callback(); // Đợi cả 2 trường có giá trị
      }

      const startDate = new Date(start);
      const endDate = new Date(end);

      if (endDate < startDate) {
        return callback(new Error('Thời gian kết thúc không thể trước thời gian bắt đầu!'));
      }

      // Tính khoảng cách thời gian (theo tháng)
      const months =
        (endDate.getFullYear() - startDate.getFullYear()) * 12 +
        (endDate.getMonth() - startDate.getMonth());

      if (months < 5) {
        return callback(new Error('Dự án phải kéo dài ít nhất 5 tháng!'));
      }

      return callback();
    },
    trigger: 'blur'
  }
],

  idCategory: [{ required: true, message: 'Thể loại không được để trống!', trigger: 'blur' }],
  id: [{ required: true, message: 'Chuyên ngành bộ môn không được để trống!', trigger: 'blur' }]
}
const formRef = ref()

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    updateListMember()
    console.log("listMemberrrr:", [...listMember])
    const newProject: createProjectRequest = {
      nameProject: dataCreateProject.nameProject,
      codeProject: dataCreateProject.codeProject,
      idMajorFacility: id.value,
      descriptions: dataCreateProject.descriptions,
      startTime: new Date(dataCreateProject.startTime).getTime() as unknown as string,
      endTime: new Date(dataCreateProject.endTime).getTime() as unknown as string,
      idCategory: dataCreateProject.idCategory,
      listMembers: [...listMember]
    }

    if (props.projectId != null) {
      console.log("listMembersss: ",newProject.listMembers)

      const res = await updateProject(props.projectId, newProject)
      toast.success(res.message)
    }
    closeModal()
    emit('success')
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}
const state = reactive({
  searchQuery: '',
  categoryName: null as string | null,
  categoryStatus: null as number | null,
  isModalOpen: false,
  id: null as string | null,
  category: [] as CategoryResponse[],
  paginationParams: { page: 1, size: 10 },
  totalItems: 0
})

const fecthCategory = async () => {
  const params: ParamsGetCategory = {
    page: state.paginationParams.page,
    size: state.paginationParams.size,
    categoryName: state.searchQuery || '',
    categoryStatus: state.categoryStatus as unknown as string
  }
  const response = await getAllCategory(params)
  state.category = response.data.data
}

const closeModal = () => {
  dataTableStaff.value = []
  dataTableProject.value = []
  selectedMembers.value = []
  idFacility.value = null
  Object.assign(dataCreateProject, {
    nameProject: '',
    codeProject: '',
    startTime: '',
    endTime: '',
    idMajorFacility: '',
    descriptions: '',
    idCategory: ''
  })
  // listMember.length = 0
  listMember.push({
    name: '',
    code: '',
    email: '',
    image: '',
    role: ''
  })

  id.value = null
  valueSelectMember.value = null
  emit('close')
}

// detail project
const fetchProjectDetails = async (idProject: string) => {
  try {
    const response = await detailProject(idProject)
    const resStaff = await getAllStaffByProject(idProject)
    const resStudent = await getAllStaffByStudent(idProject)

    dataTableProject.value = [...resStaff.data, ...resStudent.data]

    dataCreateProject.codeProject = response.data.code
    dataCreateProject.nameProject = response.data.name
    dataCreateProject.descriptions = response.data.descriptions
    dataCreateProject.endTime = dayjs(response.data.endTime)
    dataCreateProject.startTime = dayjs(response.data.startTime)
    dataCreateProject.idCategory = response.data.category.id
    id.value = response.data.majorFacility.id
    valueSelectMember.value = ''

    selectedMembers.value = dataTableProject.value.map((member) => ({
      email: member.email,
      role: member.role ?? ''
    }))

    updateListMember()
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}

watch(
  () => [props.projectId, props.open],
  async ([newId, isOpen]) => {
    if (isOpen) {
      if (formRef.value) {
        formRef.value.resetFields()
      }
      if (newId) {
        idFacility.value = props.idFacilityProject
        await fetchProjectDetails(newId as string)
      } else {
        dataCreateProject.codeProject = ''
        dataCreateProject.descriptions = ''
        dataCreateProject.endTime = ''
        dataCreateProject.idCategory = ''
        dataCreateProject.idMajorFacility = ''
        dataCreateProject.startTime = ''
        dataCreateProject.nameProject = ''
      }
    }
  },
  { immediate: true }
)

const columns = [
  { title: 'Avatar', dataIndex: 'image', key: 'image', width: 30, ellipsis: true },
  { title: 'Tên thành viên', dataIndex: 'name', key: 'name', width: 100, ellipsis: true },
  { title: 'Email', dataIndex: 'email', key: 'email', width: 100, ellipsis: true },
  { title: 'Vai trò', dataIndex: 'role', key: 'role', width: 60, ellipsis: true }
  // { title: 'Hành động', key: 'action', width: 80 }
]

watch(
  selectedMembers,
  (newValue) => {
    console.log('🔍 Dữ liệu selectedMembers khi mở detail:', JSON.stringify(newValue, null, 2))
  },
  { deep: true, immediate: true }
)

watch(
  tableData,
  (newValue) => {
    console.log('📋 Dữ liệu tableData:', JSON.stringify(newValue, null, 2))
  },
  { deep: true, immediate: true }
)
</script>

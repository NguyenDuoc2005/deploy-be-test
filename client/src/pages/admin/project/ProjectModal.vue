<template>
  <a-modal
    :open="open"
    :title="props.title"
    @cancel="closeModal"
    :width="'45%'"
    :bodyStyle="{ maxHeight: '70vh', overflowY: 'auto' }"
    centered
  >
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

      <div class="grid grid-cols-3 gap-4">
        <a-form-item
          label="Thời gian bắt đầu"
          name="startTime"
          :label-col="{ span: 24 }"
          :rules="rules.startTime"
        >
          <a-date-picker class="w-full" v-model:value="dataCreateProject.startTime" />
        </a-form-item>

        <a-form-item
          label="Thời gian kết thúc"
          name="endTime"
          :label-col="{ span: 24 }"
          :rules="rules.endTime"
        >
          <a-date-picker class="w-full" v-model:value="dataCreateProject.endTime" />
        </a-form-item>
        <a-form-item
          label="Thể loại"
          name="idCategory"
          :label-col="{ span: 24 }"
          :rules="rules.idCategory"
        >
          <a-select
            v-model:value="dataCreateProject.idCategory"
            placeholder="Chọn chuyên ngành"
            name="major"
          >
            <a-select-option
              v-for="dataCate in state.category"
              :key="dataCate.id"
              :value="dataCate.id"
            >
              {{ dataCate.categoryName }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </div>

      <div class="grid grid-cols-3 gap-4">
        <a-form-item label="* Cơ sở" name="idFacility" :label-col="{ span: 24 }">
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
      <div class="">
        <a-form-item name="members" class="w-full" label="">
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
                  <img :src="item.image" class="w-8 h-8 rounded-full" />
                  <span class="font-semibold">{{ item.name }}</span>
                  <span>-</span>
                  <span class="font-semibold">{{ item.email }}</span>
                </div>
              </a-select-option>
            </template>
          </a-select>
        </a-form-item>

        <!-- Bảng hiển thị thành viên -->
        <template v-if="tableData.length > 0">
          <a-table :columns="columns" :data-source="tableData" rowKey="email" bordered>
            <template #bodyCell="{ column, record }">
              <!-- Hiển thị hình ảnh -->
              <template v-if="column.dataIndex === 'image'">
                <img :src="record.image" class="w-8 h-8 rounded-full" />
              </template>

              <!-- Hiển thị dropdown chọn role -->
              <template v-if="column.dataIndex === 'role'">
                <a-select
                  class="w-full"
                  v-model:value="record.role"
                  @change="(value) => updateRole(record.email, value)"
                >
                  <!-- Nếu email là fe.edu.vn hoặc fpt.edu.vn thì hiển thị tất cả các vai trò -->
                  <template v-if="isFPTorFE(record.email)">
                    <a-select-option value="QUAN_Li">Quản lý</a-select-option>
                    <a-select-option value="DEV">Dev</a-select-option>
                    <a-select-option value="TESTER">Tester</a-select-option>
                  </template>

                  <!-- Nếu là email khác chỉ hiển thị Dev và Tester -->
                  <template v-else>
                    <a-select-option value="DEV">Dev</a-select-option>
                    <a-select-option value="TESTER">Tester</a-select-option>
                  </template>
                </a-select>
              </template>
            </template>
          </a-table>
        </template>
      </div>

      <a-form-item label="Mô tả" name="description" :label-col="{ span: 24 }">
        <a-textarea class="w-full" rows="4" v-model:value="dataCreateProject.descriptions" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits, onMounted, reactive, computed } from 'vue'
import { toast } from 'vue3-toastify'
import axios from 'axios'
import {
  createProject,
  createProjectRequest,
  detailProject,
  facilityResponse,
  getAllFacilityProject,
  getAllPJDepartFacility,
  pjDepartmentFacilityResponse,
  updateProject
} from '@/services/api/admin/project/project.api'
import {
  getAllProjectStaff,
  getAllProjectStudent,
  getAllStaffByProject,
  getAllStaffByStudent,
  projectStudentResponse
} from '@/services/api/admin/project/project.student.api'
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

  // Cập nhật listMember để đồng bộ
  const listMemberIndex = listMember.findIndex((m) => m.email === email)
  if (listMemberIndex !== -1) {
    listMember[listMemberIndex].role = newRole
  }

  console.log('Danh sách thành viên sau khi cập nhật role:', selectedMembers.value)
}

const filterOption = (input, option) => {
  return (
    option?.value?.toLowerCase().includes(input.toLowerCase()) ||
    option?.label?.toLowerCase().includes(input.toLowerCase())
  )
}

var idMajorFacilityDefault: string = ''

watch(idFacility, async (newvalue) => {
  const res = await getAllPJDepartFacility(newvalue as unknown as string)

  dataDepartmentFacility.value = res.data as unknown as []

  // clear chuyên ngành cơ sở

  if (props.idFacilityProject && props.idFacilityProject == newvalue) {
    id.value = dataDepartmentFacility.value.find(
      (department) => department.id == idMajorFacilityDefault
    )?.id
  } else {
    clearMajorFacilityOption()
    clearMemberOption()
  }
})

const tableData = computed(() => {
  return selectedMembers.value.map((member) => {
    const staff = dataTableStaff.value.find((m) => m.email === member.email)
    return {
      label: staff?.name,
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
  if (valueSelectMember.value === '1') {
    return dataTableStaff.value.filter(
      (m) => m.email.endsWith('@fe.edu.vn') || m.email.endsWith('@fpt.edu.vn')
    )
  } else if (valueSelectMember.value === '2') {
    return dataTableStaff.value.filter((m) => m.email.endsWith('@gmail.com'))
  }
  return dataTableStaff.value
})

const updateListMember = () => {
  // Tạo bản sao của listMember để giữ lại dữ liệu cũ
  const updatedList = [...listMember]
  console.log('oowiiwiwiwiw')

  console.table(updatedList)

  // Nếu selectedMembers rỗng, giữ nguyên dữ liệu cũ
  if (selectedMembers.value.length === 0) {
    console.log('Không có thay đổi, giữ nguyên listMember:', listMember)
    return
  }

  // Cập nhật hoặc thêm thành viên mới từ selectedMembers
  selectedMembers.value.forEach((select) => {
    const member = dataTableStaff.value.find((m) => m.email === select.email)
    if (member) {
      const existingIndex = updatedList.findIndex((m) => m.email === member.email)

      if (existingIndex !== -1) {
        // Nếu đã tồn tại, cập nhật role nếu thay đổi
        if (updatedList[existingIndex].role !== select.role) {
          updatedList[existingIndex].role = select.role
        }
      } else {
        // Nếu chưa tồn tại, thêm mới
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

  // Xóa thành viên không còn trong selectedMembers
  const finalList = updatedList.filter((m) =>
    selectedMembers.value.some((s) => s.email === m.email)
  )

  // Nếu không có thành viên mới hoặc xóa toàn bộ, giữ lại dữ liệu cũ
  if (finalList.length === 0) {
    console.log(
      'Không có thay đổi hoặc toàn bộ thành viên bị xóa, giữ nguyên listMember:',
      listMember
    )
    return
  }

  // Cập nhật lại listMember mà không ảnh hưởng các project khác
  listMember.splice(0, listMember.length, ...finalList)

  console.log('Danh sách thành viên sau cập nhật:', listMember)
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

    // ✅ Không xóa dữ liệu cũ, chỉ thêm mới nếu chưa có
    const existingEmails = new Set(dataTableStaff.value.map((m) => m.email))
    const filteredNewData = newData.filter((m) => !existingEmails.has(m.email))

    // 🛠 Thêm thành viên mới vào danh sách nhưng giữ nguyên thành viên cũ
    dataTableStaff.value = [...dataTableStaff.value, ...filteredNewData]

    console.log('Dữ liệu bảng cập nhật:', dataTableStaff.value)
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
  nameProject: [
    { required: true, message: 'Tên không được để trống!', trigger: 'blur' },
    { max: 100, message: 'Tên không được vượt quá 100 ký tự!', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: 'Thời gian bắt đầu không được để trống!', trigger: 'blur' }
    // {
    //   validator: (_, value, callback) => {
    //     const today = new Date()
    //     today.setHours(0, 0, 0, 0)
    //     const selectedStart = new Date(value)
    //     selectedStart.setHours(0, 0, 0, 0)
    //     if (selectedStart < today) {
    //       callback(new Error('Thời gian bắt đầu phải từ hôm nay trở đi!'))
    //     } else {
    //       callback()
    //     }
    //   },
    //   trigger: 'change'
    // }
  ],
  endTime: [
    { required: true, message: 'Thời gian kết thúc không được để trống!', trigger: 'blur' },
    {
      validator: (_, value, callback) => {
        const start = new Date(dataCreateProject.startTime)
        const end = new Date(value)
        if (end < start) {
          callback(new Error('Thời gian kết thúc không được nhỏ hơn thời gian bắt đầu!'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  idCategory: [{ required: true, message: 'Thể loại không được để trống!', trigger: 'blur' }],
  id: [{ required: true, message: 'Chuyên ngành bộ môn không được để trống!', trigger: 'blur' }]
}

const formRef = ref()

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
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

    if (props.projectId == null) {
      const res = await createProject(newProject)
      toast.success(res.message)
    } else {
      console.log(newProject.listMembers + 'LLLLAA')

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

  console.log(response.data)
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
    console.log(dataTableProject.value + 'LLLL')

    dataCreateProject.codeProject = response.data.code
    dataCreateProject.nameProject = response.data.name
    dataCreateProject.descriptions = response.data.descriptions
    dataCreateProject.endTime = dayjs(response.data.endTime)
    dataCreateProject.startTime = dayjs(response.data.startTime)
    dataCreateProject.idCategory = response.data.category.id
    id.value = response.data.majorFacility.id
    valueSelectMember.value = ''

    idMajorFacilityDefault = response.data.majorFacility.id

    // selectedMembers.value = dataTableProject.value.map((member) => member.email)
    selectedMembers.value = dataTableProject.value.map((member) => ({
      email: member.email,
      role: member.role ?? '' // Đảm bảo có role
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
  { title: 'Avatar', dataIndex: 'image', key: 'image', width: 60, ellipsis: true },
  { title: 'Tên thành viên', dataIndex: 'label', key: 'label', width: 100, ellipsis: true },
  { title: 'Email', dataIndex: 'email', key: 'email', width: 100, ellipsis: true },
  { title: 'Vai trò', dataIndex: 'role', key: 'role', width: 60, ellipsis: true }
]

const clearMajorFacilityOption = () => {
  id.value = ''
}

const clearMemberOption = () => {
  valueSelectMember.value = ''
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

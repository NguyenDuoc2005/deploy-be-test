<template>
  <div class="calendar-wrapper">
    <div class="calendar-container">
      <FullCalendar :options="calendarOptions" />
    </div>

    <a-modal
      v-model:visible="isModalVisible"
      :title="reportId ? `Chỉnh sửa báo cáo ngày: ${selectedDate}` : `Báo cáo ngày: ${selectedDate}`"
      @cancel="closeModal"
      @ok="submitReport"
      class="custom-modal"
    >
      <div class="modal-content">
        <a-form layout="vertical" ref="reportForm" :model="report" :rules="rules">
          <a-card class="report-card">
            <a-form-item label="Hôm nay bạn đã làm gì?" name="workDoneToday">
              <a-textarea v-model:value="report.workDoneToday" rows="3" placeholder="Nhập nội dung..." />
            </a-form-item>
          </a-card>

          <a-card class="report-card">
            <a-form-item label="Bạn gặp khó khăn gì?" name="obstacles">
              <a-textarea v-model:value="report.obstacles" rows="3" placeholder="Nhập nội dung..." />
            </a-form-item>
          </a-card>

          <a-card class="report-card">
            <a-form-item label="Kế hoạch ngày mai?" name="workPlanTomorrow">
              <a-textarea v-model:value="report.workPlanTomorrow" rows="3" placeholder="Nhập nội dung..." />
            </a-form-item>
          </a-card>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>
<script setup lang="ts">
import { ref, reactive } from "vue";
import FullCalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import { addReport, updateReport, detailReport, getReportIdByDate, getAllReport } from "@/services/api/member/report/report.api.ts";
import { toast } from "vue3-toastify";
import { Modal } from "ant-design-vue"; 
import { useRoute } from "vue-router";

const isModalVisible = ref(false);
const selectedDate = ref(null);
const reportId = ref(null);
const report = reactive({ workDoneToday: "", obstacles: "", workPlanTomorrow: "" });
const reportForm = ref();
const route = useRoute()
const projectId = ref<string | null>(route.params.id ? String(route.params.id) : null)

const rules = reactive({
  workDoneToday: [{ required: true, message: "Nội dung không được để trống!" }],
  obstaclesReport: [{ required: true, message: "Nội dung không được để trống!" }],
  workPlanTomorrow: [{ required: true, message: "Nội dung không được để trống!" }]
});

const calendarOptions = ref({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: "dayGridWeek",
  locale: "vi",
  buttonText: { today: "Hôm nay", month: "Tháng", week: "Tuần", day: "Ngày", list: "Danh sách" },
  headerToolbar: { left: "prev,next today", center: "title", right: "dayGridWeek,dayGridMonth" },
  selectable: true,
  editable: false,


  dateClick: async (info) => {
    const clickedDate = new Date(info.dateStr);
    clickedDate.setHours(0, 0, 0, 0); 

    const today = new Date();
    today.setHours(0, 0, 0, 0); 

    if (clickedDate < today) {
      toast.error("Không thể báo cáo ngày đã qua!");
      return; 
    } 
    
    if(clickedDate > today){
      toast.error("Chưa đến thời gian báo cáo!");
      return;
    }

  selectedDate.value = info.dateStr;

  try {
    const timestamp = new Date(selectedDate.value);
    timestamp.setHours(0, 0, 0, 0);
    const startOfDayTimestamp = timestamp.getTime();

    const response = await getReportIdByDate(startOfDayTimestamp, route.params.id as string);

    if (response?.data) {
      reportId.value = response.data;
      console.log("✅ Lấy ID báo cáo thành công:", reportId.value);
      await fetchReportDetails(reportId.value);
    } else {
      // console.warn("⚠ Không tìm thấy báo cáo ngày:", selectedDate.value);
      reportId.value = null;
      report.workDoneToday = "";
      report.obstacles = "";
      report.workPlanTomorrow = "";
    }
  } catch (error) {
    console.error("❌ Lỗi khi lấy ID báo cáo:", error);
  }

  isModalVisible.value = true;
},

});

const fetchReportDetails = async (idReport) => {
  try {
    const response = await detailReport(idReport, route.params.id as string);
    if (response?.data) {
      report.workDoneToday = response.data.wordDoneTodayReport;
      report.obstacles = response.data.obstaclesReport;
      report.workPlanTomorrow = response.data.wordPlanTomorrowReport;
      
        reportId.value = response.data.idReport || idReport;
      
      // console.log("✅ Lấy báo cáo thành công, ID:", reportId.value);
    } else {
      resetReport();
      reportId.value = null;
      console.warn("⚠ Không tìm thấy báo cáo!");
    }
  } catch (error) {
    console.error("❌ Lỗi khi lấy báo cáo:", error);
  }
};


const resetReport = () => {
  report.workDoneToday = "";
  report.obstacles = "";
  report.workPlanTomorrow = "";
};

const closeModal = () => {
  isModalVisible.value = false;
  resetReport();
};


const emit = defineEmits(["update-report-list"]);

const fetchReports = async () => {
  try {
    const response = await getAllReport();
    if (response?.data) {
      emit("update-report-list", response.data);
    }
  } catch (error) {
    console.error("Lỗi khi tải danh sách báo cáo:", error);
  }
};

const submitReport = async () => {
  const action = reportId.value ? "cập nhật" : "thêm mới";

  Modal.confirm({
    title: `Bạn muốn ${action} báo cáo không?`,
    content: `Hành động này sẽ ${action} báo cáo cho ngày ${selectedDate.value}.`,
    okText: "Xác nhận",
    cancelText: "Hủy",
    onOk: async () => {
      try {
        await reportForm.value?.validateFields();
        const formData = {
          workDoneToday: report.workDoneToday,
          obstacles: report.obstacles,
          workPlanTomorrow: report.workPlanTomorrow,
          statusReport: "DA_BAO_CAO",
          reportTime: new Date(selectedDate.value).getTime(),
        };

        if (reportId.value != null) {
          await updateReport(formData, reportId.value);
          toast.success("Cập nhật báo cáo thành công!");
          
        } else {
          await addReport(formData, route.params.id);
          toast.success("Thêm báo cáo thành công!");
        }

        closeModal();
        fetchReports();
      } catch (error) {
        console.log("pro: ", route.params.id)
        console.error("❌ Lỗi khi gửi báo cáo:", error);
        toast.error("Thất bại!");
      }
    },
  });
};


</script>

<style scoped>
.calendar-container {
  max-width: 1200px;
  margin: auto;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
}

.calendar-container .fc {
  max-height: 500px;
}

</style>

<template>
  <div>
    <el-page-header @back="goBack" content="申请详情"></el-page-header>
    
    <el-card style="margin-top: 20px" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>{{ application?.title }}</span>
          <el-tag :type="getStatusType(application?.status)">
            {{ getStatusText(application?.status) }}
          </el-tag>
        </div>
      </template>

      <el-descriptions :column="2" border v-if="application">
        <el-descriptions-item label="申请编号">{{ application.applicationNo }}</el-descriptions-item>
        <el-descriptions-item label="申请类型">
          {{ application.applicationType === 'LEAVE' ? '请假' : '报销' }}
        </el-descriptions-item>
        <el-descriptions-item label="申请人">{{ application.applicant?.fullName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(application.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ formatDateTime(application.submittedAt) }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ formatDateTime(application.completedAt) }}</el-descriptions-item>
        
        <template v-if="application.applicationType === 'LEAVE'">
          <el-descriptions-item label="请假类型">{{ getLeaveTypeText(application.leaveType) }}</el-descriptions-item>
          <el-descriptions-item label="请假天数">{{ application.days }} 天</el-descriptions-item>
          <el-descriptions-item label="开始日期">{{ application.startDate }}</el-descriptions-item>
          <el-descriptions-item label="结束日期">{{ application.endDate }}</el-descriptions-item>
          <el-descriptions-item label="请假原因" :span="2">{{ application.reason }}</el-descriptions-item>
        </template>
        
        <template v-if="application.applicationType === 'REIMBURSEMENT'">
          <el-descriptions-item label="报销类型">{{ getReimbursementTypeText(application.reimbursementType) }}</el-descriptions-item>
          <el-descriptions-item label="报销金额">¥{{ application.amount }}</el-descriptions-item>
          <el-descriptions-item label="费用日期">{{ application.expenseDate }}</el-descriptions-item>
          <el-descriptions-item label="报销用途" :span="2">{{ application.purpose }}</el-descriptions-item>
        </template>

        <el-descriptions-item label="拒绝原因" :span="2" v-if="application.status === 'REJECTED'">
          {{ application.rejectionReason }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>审批流程</span>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="item in history"
          :key="item.id"
          :timestamp="formatDateTime(item.createdAt)"
          placement="top">
          <el-card>
            <h4>{{ item.user?.fullName }} - {{ getActionText(item.action) }}</h4>
            <p v-if="item.stepName">步骤: {{ item.stepName }}</p>
            <p v-if="item.comment">意见: {{ item.comment }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import api from '../services/api';

export default {
  name: 'ApplicationDetail',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const application = ref(null);
    const history = ref([]);
    const loading = ref(false);

    const loadApplication = async () => {
      loading.value = true;
      try {
        const id = route.params.id;
        const [appResponse, historyResponse] = await Promise.all([
          api.getApplicationById(id),
          api.getApplicationHistory(id)
        ]);
        application.value = appResponse.data.data;
        history.value = historyResponse.data.data;
      } catch (error) {
        ElMessage.error('加载申请详情失败');
      } finally {
        loading.value = false;
      }
    };

    const goBack = () => {
      router.back();
    };

    const getStatusType = (status) => {
      const typeMap = {
        'DRAFT': 'info',
        'PENDING': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger',
        'WITHDRAWN': 'info'
      };
      return typeMap[status] || 'info';
    };

    const getStatusText = (status) => {
      const textMap = {
        'DRAFT': '草稿',
        'PENDING': '审批中',
        'APPROVED': '已通过',
        'REJECTED': '已拒绝',
        'WITHDRAWN': '已撤回'
      };
      return textMap[status] || status;
    };

    const getLeaveTypeText = (type) => {
      const textMap = {
        'SICK_LEAVE': '病假',
        'ANNUAL_LEAVE': '年假',
        'PERSONAL_LEAVE': '事假',
        'MATERNITY_LEAVE': '产假',
        'PATERNITY_LEAVE': '陪产假',
        'OTHER': '其他'
      };
      return textMap[type] || type;
    };

    const getReimbursementTypeText = (type) => {
      const textMap = {
        'TRAVEL': '差旅',
        'ENTERTAINMENT': '招待',
        'OFFICE_SUPPLIES': '办公用品',
        'TRAINING': '培训',
        'COMMUNICATION': '通讯',
        'OTHER': '其他'
      };
      return textMap[type] || type;
    };

    const getActionText = (action) => {
      const textMap = {
        'CREATED': '创建申请',
        'SUBMITTED': '提交申请',
        'APPROVED': '同意',
        'REJECTED': '拒绝',
        'WITHDRAWN': '撤回',
        'MODIFIED': '修改'
      };
      return textMap[action] || action;
    };

    const formatDateTime = (dateTime) => {
      if (!dateTime) return '-';
      return new Date(dateTime).toLocaleString('zh-CN');
    };

    onMounted(() => {
      loadApplication();
    });

    return {
      application,
      history,
      loading,
      goBack,
      getStatusType,
      getStatusText,
      getLeaveTypeText,
      getReimbursementTypeText,
      getActionText,
      formatDateTime
    };
  }
};
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

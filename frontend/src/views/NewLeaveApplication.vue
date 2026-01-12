<template>
  <div>
    <h2>新建请假申请</h2>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" style="max-width: 600px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入申请标题"></el-input>
      </el-form-item>
      <el-form-item label="请假类型" prop="leaveType">
        <el-select v-model="form.leaveType" placeholder="请选择请假类型">
          <el-option label="病假" value="SICK_LEAVE"></el-option>
          <el-option label="年假" value="ANNUAL_LEAVE"></el-option>
          <el-option label="事假" value="PERSONAL_LEAVE"></el-option>
          <el-option label="产假" value="MATERNITY_LEAVE"></el-option>
          <el-option label="陪产假" value="PATERNITY_LEAVE"></el-option>
          <el-option label="其他" value="OTHER"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="开始日期" prop="startDate">
        <el-date-picker v-model="form.startDate" type="date" placeholder="选择开始日期"></el-date-picker>
      </el-form-item>
      <el-form-item label="结束日期" prop="endDate">
        <el-date-picker v-model="form.endDate" type="date" placeholder="选择结束日期"></el-date-picker>
      </el-form-item>
      <el-form-item label="请假天数" prop="days">
        <el-input-number v-model="form.days" :min="1" :max="365"></el-input-number>
      </el-form-item>
      <el-form-item label="请假原因" prop="reason">
        <el-input v-model="form.reason" type="textarea" :rows="4" placeholder="请输入请假原因"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="loading">保存并提交</el-button>
        <el-button @click="handleSaveDraft" :loading="loading">保存草稿</el-button>
        <el-button @click="handleCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import api from '../services/api';

export default {
  name: 'NewLeaveApplication',
  setup() {
    const router = useRouter();
    const formRef = ref(null);
    const loading = ref(false);

    const form = reactive({
      title: '',
      leaveType: '',
      startDate: '',
      endDate: '',
      days: 1,
      reason: ''
    });

    const rules = {
      title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
      leaveType: [{ required: true, message: '请选择请假类型', trigger: 'change' }],
      startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
      endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
      days: [{ required: true, message: '请输入请假天数', trigger: 'blur' }]
    };

    const handleSaveDraft = async () => {
      if (!formRef.value) return;
      
      await formRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true;
          try {
            const data = {
              ...form,
              startDate: form.startDate ? new Date(form.startDate).toISOString().split('T')[0] : null,
              endDate: form.endDate ? new Date(form.endDate).toISOString().split('T')[0] : null
            };
            await api.createLeaveApplication(data);
            ElMessage.success('草稿保存成功');
            router.push('/my-applications');
          } catch (error) {
            ElMessage.error(error.response?.data?.message || '保存失败');
          } finally {
            loading.value = false;
          }
        }
      });
    };

    const handleSubmit = async () => {
      if (!formRef.value) return;
      
      await formRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true;
          try {
            const data = {
              ...form,
              startDate: form.startDate ? new Date(form.startDate).toISOString().split('T')[0] : null,
              endDate: form.endDate ? new Date(form.endDate).toISOString().split('T')[0] : null
            };
            const response = await api.createLeaveApplication(data);
            await api.submitApplication(response.data.data.id);
            ElMessage.success('申请提交成功');
            router.push('/my-applications');
          } catch (error) {
            ElMessage.error(error.response?.data?.message || '提交失败');
          } finally {
            loading.value = false;
          }
        }
      });
    };

    const handleCancel = () => {
      router.push('/my-applications');
    };

    return {
      form,
      formRef,
      rules,
      loading,
      handleSubmit,
      handleSaveDraft,
      handleCancel
    };
  }
};
</script>

<style scoped>
h2 {
  margin-bottom: 20px;
}
</style>

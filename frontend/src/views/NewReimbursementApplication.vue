<template>
  <div>
    <h2>新建报销申请</h2>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" style="max-width: 600px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入申请标题"></el-input>
      </el-form-item>
      <el-form-item label="报销类型" prop="reimbursementType">
        <el-select v-model="form.reimbursementType" placeholder="请选择报销类型">
          <el-option label="差旅" value="TRAVEL"></el-option>
          <el-option label="招待" value="ENTERTAINMENT"></el-option>
          <el-option label="办公用品" value="OFFICE_SUPPLIES"></el-option>
          <el-option label="培训" value="TRAINING"></el-option>
          <el-option label="通讯" value="COMMUNICATION"></el-option>
          <el-option label="其他" value="OTHER"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="报销金额" prop="amount">
        <el-input-number v-model="form.amount" :precision="2" :min="0" :max="1000000"></el-input-number>
      </el-form-item>
      <el-form-item label="费用日期" prop="expenseDate">
        <el-date-picker v-model="form.expenseDate" type="date" placeholder="选择费用日期"></el-date-picker>
      </el-form-item>
      <el-form-item label="报销用途" prop="purpose">
        <el-input v-model="form.purpose" type="textarea" :rows="4" placeholder="请输入报销用途"></el-input>
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
  name: 'NewReimbursementApplication',
  setup() {
    const router = useRouter();
    const formRef = ref(null);
    const loading = ref(false);

    const form = reactive({
      title: '',
      reimbursementType: '',
      amount: 0,
      expenseDate: '',
      purpose: ''
    });

    const rules = {
      title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
      reimbursementType: [{ required: true, message: '请选择报销类型', trigger: 'change' }],
      amount: [{ required: true, message: '请输入报销金额', trigger: 'blur' }],
      expenseDate: [{ required: true, message: '请选择费用日期', trigger: 'change' }]
    };

    const handleSaveDraft = async () => {
      if (!formRef.value) return;
      
      await formRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true;
          try {
            const data = {
              ...form,
              expenseDate: form.expenseDate ? new Date(form.expenseDate).toISOString().split('T')[0] : null
            };
            await api.createReimbursementApplication(data);
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
              expenseDate: form.expenseDate ? new Date(form.expenseDate).toISOString().split('T')[0] : null
            };
            const response = await api.createReimbursementApplication(data);
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

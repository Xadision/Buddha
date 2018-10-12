<template>
  <div class="fingerDialog">
    <!--添加-->
    <el-dialog
      title="添加设备端"
      :visible.sync="addDialogVisible"
      width="400px">
      <el-form>
        <el-form-item label="中转端名">
          <el-input v-model="armName" size="large" disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="设备端名">
          <el-input v-model="fingerName" size="large"></el-input>
        </el-form-item>
        <el-form-item label="软件包名">
          <el-select v-model="beadId" placeholder="请选择软件包" style="width:100%" filterable>
            <el-option
              v-for="item in beadList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="add">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import {axiosPost} from '@/utils/fetchData'
  import {
    addFingerUrl
  } from '@/config/globalUrl'
  import Bus from '@/utils/bus'
  import {judgeCodeLen} from "@/utils/handle";

  export default {
    name: "FingerDialog",
    data() {
      return {
        // 添加模态框
        addDialogVisible: false,
        // 中转端名
        armName: "",
        // 设备端名
        fingerName: "",
        // 软件包Id
        beadId: "",
        // 软件包信息
        beadList: []
      }
    },
    beforeDestroy() {
      // 取消监听
      Bus.$off('fingerAdd');
    },
    mounted() {
      // 监听设备端添加
      Bus.$on('fingerAdd', (armName, beadList) => {
        this.armName = armName;
        this.beadList = beadList;
        this.beadId = "";
        this.fingerName = "";
        this.addDialogVisible = true;
      })
    },
    methods: {
      add: function () {
        if (this.armName !== "" && this.beadId !== "" && this.fingerName !== "") {
          if (judgeCodeLen(this.fingerName)) {
            let options = {
              url: addFingerUrl,
              data: {
                armName: this.armName,
                fingerName: this.fingerName,
                beadId: this.beadId
              }
            };
            axiosPost(options).then(response => {
              if (response.data.result === 200) {
                Bus.$emit("fingerRefresh", this.armName, "add");
                this.$alertSuccess("添加成功");
              } else {
                this.$alertWarning(response.data.data);
              }
              this.addDialogVisible = false;
            })
              .catch(err => {
                this.addDialogVisible = false;
                this.$alertError('请求超时，请刷新重试');
              })
          } else {
            this.$alertWarning("设备端名不能超过64个字符");
          }
        } else {
          this.$alertWarning("内容不能为空");
        }
      }
    }
  }
</script>

<style scoped>

</style>

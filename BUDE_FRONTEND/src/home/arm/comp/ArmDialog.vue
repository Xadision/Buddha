<template>
  <div class="armDialog">
    <!--添加中转端-->
    <el-dialog
      title="添加中转端"
      :visible.sync="addDialogVisible"
      width="400px">
      <el-form>
        <el-form-item label="中转端名">
          <el-input v-model="armInfo.armName" size="large"></el-input>
        </el-form-item>
        <el-form-item label="服务器监听端口">
          <el-input v-model="armInfo.serverPort" size="large"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="add">确 定</el-button>
      </span>
    </el-dialog>
    <!--更换中转组-->
    <el-dialog
      title="更换中转组"
      :visible.sync="editBodyDialogVisible"
      width="400px">
      <el-form>
        <el-form-item label="中转端名">
          <el-input v-model="editBodyInfo.armName" size="large" disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="中转组名">
          <el-select v-model="editBodyInfo.bodyId" placeholder="请选择" style="width:100%">
            <el-option value="" label="无"></el-option>
            <el-option
              v-for="item in bodyInfo"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editBodyDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="editBody">确 定</el-button>
      </span>
    </el-dialog>
    <!--查看设备端信息-->
    <el-dialog title="设备端信息" :visible.sync="fingerDialogTableVisible">
      <el-form :inline="true">
        <el-form-item label="中转端名">
          <el-tag>{{fingerForm.armName}}</el-tag>
        </el-form-item>
        <el-form-item label="状态">
          <el-tag :type="fingerForm.color">{{fingerForm.status}}</el-tag>
        </el-form-item>
      </el-form>
      <el-table :data="fingerData" stripe border max-height="300">
        <el-table-column property="name" label="设备端名" align="center"></el-table-column>
        <el-table-column property="beadName" label="软件包" align="center"></el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
  import {axiosPost} from '@/utils/fetchData'
  import {addArmUrl, editBodyUrl} from '@/config/globalUrl'
  import Bus from '@/utils/bus'
  import {judgeCodeLen} from "@/utils/handle"
  export default {
    name: "ArmDialog",
    data() {
      return {
        // 添加模态框
        addDialogVisible: false,
        // 移入中转组模态框
        editBodyDialogVisible: false,
        // 设备端信息显示模态框
        fingerDialogTableVisible: false,
        // 中转端信息
        armInfo: {
          armName: "",
          serverPort: ""
        },
        // 移入信息
        editBodyInfo: {
          armName: "",
          bodyId: ""
        },
        // 中转组信息
        bodyInfo: [],
        // 设备端信息
        fingerData: [],
        fingerForm: {
          armName: "",
          status: "",
          color: ""
        }
      }
    },
    beforeDestroy() {
      // 取消监听
      Bus.$off('armAdd');
      Bus.$off('editBody');
      Bus.$off('showFingerInfo');
    },
    mounted() {
      // 监听中转端添加按钮点击
      Bus.$on('armAdd', (msg) => {
        if (msg === true) {
          this.armInfo = {
            armName: "",
            serverPort: ""
          };
          this.addDialogVisible = true;
        }
      })

      // 监听中转组更改按钮点击
      Bus.$on('editBody', (armName, bodyInfo,bodyId) => {
        this.bodyInfo = bodyInfo;
        this.editBodyInfo.bodyId = bodyId;
        this.editBodyInfo.armName = armName;
        this.$nextTick(function () {
          this.editBodyDialogVisible = true;
        })
      })

      // 监听设备信息展示
      Bus.$on('showFingerInfo', (type, fingerData) => {
        this.init(type, fingerData);
      })
    },
    methods: {
      // 添加
      add: function () {
        if (this.armInfo.armName !== "" && this.armInfo.serverPort !== "") {
          if (judgeCodeLen(this.armInfo.armName)) {
            if (this.judgeNumber(this.armInfo.serverPort)) {
              let options = {
                url: addArmUrl,
                data: {
                  armName: this.armInfo.armName,
                  serverPort: this.armInfo.serverPort
                }
              };
              axiosPost(options).then(response => {
                if (response.data.result === 200) {
                  this.$alertSuccess("添加成功");
                  this.addDialogVisible = false;
                  Bus.$emit("armRefresh","add");
                } else {
                  this.$alertWarning(response.data.data);
                }
              })
                .catch(err => {
                  this.$alertError('请求超时，请刷新重试');
                  this.addDialogVisible = false;
                })
            }
          } else {
            this.$alertWarning("中转端名不能超过64个字符");
          }
        } else {
          this.$alertWarning("内容不能为空");
        }
      },
      // 判断正整数
      judgeNumber: function (val) {
        if (!(/(^[1-9]\d*$)/.test(val))) {
          this.$alertWarning("服务端口格式不对");
          return false;
        } else if (val > 65535) {
          this.$alertWarning("服务端口范围为0-65535");
          return false;
        }else{
          return true;
        }
      },
      // 更改中转组
      editBody: function () {
        let options = {
          url: editBodyUrl,
          data: {
            armName: this.editBodyInfo.armName,
            bodyId: this.editBodyInfo.bodyId
          }
        };
        axiosPost(options).then(response => {
          if (response.data.result === 200) {
            this.$alertSuccess("更改成功");
            this.editBodyDialogVisible = false;
            Bus.$emit("armRefresh","edit");
          } else {
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.$alertError('请求超时，请刷新重试');
            this.editBodyDialogVisible = false;
          })
      },
      // 分类初始化
      init: function (type, fingerData) {
        if (type !== "" && fingerData !== {}) {
          this.fingerForm.armName = fingerData.armName;
          switch (type) {
            case "all":
              this.fingerForm.status = "不限";
              this.fingerForm.color = "";
              this.fingerData = fingerData.totalData.list;
              break;
            case "online":
              this.fingerForm.status = "在线";
              this.fingerForm.color = "success";
              this.fingerData = fingerData.onlineData.list;
              break;
            case "dropline":
              this.fingerForm.status = "掉线";
              this.fingerForm.color = "danger";
              this.fingerData = fingerData.droplineData.list;
              break;
            default:
              break;
          }
          this.fingerDialogTableVisible = true;
        }
      },
    }
  }
</script>

<style scoped>
</style>

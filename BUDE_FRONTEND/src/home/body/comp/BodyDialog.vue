<template>
  <div class="bodyDialog">
    <!--添加中转组-->
    <el-dialog
      title="添加中转组"
      :visible.sync="addDialogVisible"
      width="400px">
      <el-form>
        <el-form-item label="中转组名">
          <el-input v-model="bodyName" size="large"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="add">确 定</el-button>
      </span>
    </el-dialog>

    <!--修改中转组-->
    <el-dialog
      title="修改中转组"
      :visible.sync="editDialogVisible"
      width="400px">
      <el-form>
        <el-form-item label="当前中转组名">
          <el-input v-model="editInfo.oldBodyName" size="large" disabled></el-input>
        </el-form-item>
        <el-form-item label="新中转组名">
          <el-input v-model="editInfo.bodyName" size="large"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="edit">确 定</el-button>
      </span>
    </el-dialog>

    <!--查看设备端信息-->
    <el-dialog title="中转端信息" :visible.sync="armDialogTableVisible">
      <el-form :inline="true">
        <el-form-item label="中转组名">
          <el-tag>{{armInfo.name}}</el-tag>
        </el-form-item>
        <el-form-item label="状态">
          <el-tag :type="armInfo.color">{{armInfo.status}}</el-tag>
        </el-form-item>
      </el-form>
      <el-table :data="armData" stripe border max-height="300">
        <el-table-column property="armName" label="中转端名" align="center"></el-table-column>
        <el-table-column property="serverPort" label="监听端口" align="center"></el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
  import {axiosPost} from '@/utils/fetchData'
  import {addBodyUrl, updateBodyUrl} from '@/config/globalUrl'
  import Bus from '@/utils/bus'
  import {judgeCodeLen} from "@/utils/handle";

  export default {
    name: "BodyDialog",
    data() {
      return {
        // 添加中转组模态框
        addDialogVisible: false,
        // 修改中转组模态框
        editDialogVisible: false,
        // 设备端信息显示模态框
        armDialogTableVisible: false,
        // 中转组名
        bodyName: "",
        // 修改信息
        editInfo: {
          oldBodyId: "",
          oldBodyName: "",
          bodyName: ""
        },
        // 查看中转端信息
        armInfo: {
          name: "",
          status: "",
          color: ""
        },
        // 中转端信息
        armData: []
      }
    },
    beforeDestroy(){
      //取消监听
      Bus.$off('bodyAdd');
      Bus.$off('bodyEdit');
      Bus.$off('showArmInfo')
    },
    mounted(){
      // 监听中转组添加按钮点击
      Bus.$on('bodyAdd', (msg) => {
        if (msg === true) {
          this.bodyName = "";
          this.addDialogVisible = true;
        }
      })
      // 监听中转组修改按钮点击
      Bus.$on('bodyEdit',(bodyId,bodyName) => {
        this.editInfo.oldBodyId = bodyId;
        this.editInfo.oldBodyName = bodyName;
        this.editInfo.bodyName = "";
        this.editDialogVisible = true;
      })
      // 监听中转端信息展示
      Bus.$on('showArmInfo', (type,armData,bodyName) => {
        this.init(type,armData,bodyName);
      })
    },
    methods: {
      // 添加
      add: function () {
        if (this.bodyName !== "") {
          if(judgeCodeLen(this.bodyName)){
            let options = {
              url: addBodyUrl,
              data: {
                bodyName: this.bodyName
              }
            };
            axiosPost(options).then(response => {
              if (response.data.result === 200) {
                this.$alertSuccess("添加成功");
                this.addDialogVisible = false;
                Bus.$emit("bodyRefresh","add");
              } else {
                this.addDialogVisible = false;
                this.$alertWarning(response.data.data);
              }
            })
              .catch(err => {
                this.$alertError('请求超时，请刷新重试');
                this.addDialogVisible = false;
              })
          }else{
            this.$alertWarning("中转组名不能超过64个字符")
          }
        } else {
          this.$alertWarning("内容不能为空");
        }
      },
      // 修改
      edit: function () {
        if (this.editInfo.bodyName !== "") {
          if(judgeCodeLen(this.editInfo.bodyName)){
            let options = {
              url:updateBodyUrl,
              data: {
                bodyId: this.editInfo.oldBodyId,
                bodyName: this.editInfo.bodyName
              }
            };
            axiosPost(options).then(response => {
              if (response.data.result === 200) {
                this.$alertSuccess("修改成功");
                this.editDialogVisible = false;
                Bus.$emit("bodyRefresh","edit");
              } else {
                this.editDialogVisible = false;
                this.$alertWarning(response.data.data);
              }
            })
              .catch(err => {
                this.$alertError('请求超时，请刷新重试');
                this.editDialogVisible = false;
              })
          }else{
            this.$alertWarning("中转组名不能超过64个字符")
          }
        } else {
          this.$alertWarning("内容不能为空");
        }
      },
      // 分类初始化
      init: function (type, armData,bodyName) {
        if (type !== "" && armData !== {}) {
          this.armInfo.name = bodyName;
          this.armData = armData.list;
          switch (type) {
            case "all":
              this.armInfo.status = "不限";
              this.armInfo.color = "";
              break;
            case "online":
              this.armInfo.status = "在线";
              this.armInfo.color = "success";
              break;
            case "dropline":
              this.armInfo.status = "掉线";
              this.armInfo.color = "danger";
              break;
            default:
              break;
          }
          this.armDialogTableVisible = true;
        }
      }
    }
  }
</script>

<style scoped>

</style>

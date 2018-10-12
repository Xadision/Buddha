<template>
  <div class="beadDialog">
    <!--上传软件包-->
    <el-dialog
      title="上传软件包"
      :visible.sync="uploadDialogVisible"
      width="400px">
      <el-form>
        <el-form-item label="软件包">
          <input type="file" style="display:none;" id="fileUpload" @change="handleFileChange"/>
          <el-input id="uploadFile" size="large" @click.native="handleUpload" v-model="fileName"
                    placeholder="请选择软件包"></el-input>
        </el-form-item>
        <el-form-item label="别名">
          <el-input v-model="uploadBead.alias" size="large"></el-input>
        </el-form-item>
        <el-form-item label="更新描述">
          <el-input v-model="uploadBead.updateDescribe" size="large" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="uploadDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="btnUpload">确 定</el-button>
      </span>
    </el-dialog>

    <!--修改软件包-->
    <el-dialog
      title="修改软件包信息"
      :visible.sync="editDialogVisible"
      width="400px">
      <el-form>
        <el-form-item label="软件包ID">
          <el-input v-model="updateBead.id" size="large" disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="软件包别名">
          <el-input v-model="updateBead.alias" size="large"></el-input>
        </el-form-item>
        <el-form-item label="软件包更新描述">
          <el-input v-model="updateBead.updateDescribe" size="large"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="edit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import {axiosPost} from '@/utils/fetchData'
  import {updateBeadUrl, uploadBeadUrl} from '@/config/globalUrl'
  import axios from 'axios'
  import Bus from '@/utils/bus'
  import SparkMD5 from 'spark-md5'
  import store from '@/store'
  export default {
    name: "beadDialog",
    data() {
      return {
        // 上传模态框
        uploadDialogVisible: false,
        // 修改模态框
        editDialogVisible: false,
        // 修改软件包内容
        updateBead: {
          id: "",
          alias: "",
          updateDescribe: ""
        },
        // 上传软件包内容
        uploadBead: {
          file: "",
          alias: "",
          updateDescribe: "",
          md5: ""
        },
        // 文件名
        fileName: ""
      }
    },
    beforeDestroy() {
      // 取消监听
      Bus.$off('uploadBead');
      Bus.$off('beadEdit');
    },
    mounted() {
      // 监听上传软件包按钮点击
      Bus.$on('uploadBead', (msg) => {
        this.initUploadInfo();
        this.uploadDialogVisible = true;
      });
      // 监听编辑按钮点击
      Bus.$on('beadEdit', (msg) => {
        this.initUpdateInfo(msg);
        this.editDialogVisible = true;
      });
    },
    methods: {
      // 初始化数据
      initUpdateInfo: function (msg) {
        if (msg.id) {
          this.updateBead.id = msg.id;
          this.updateBead.alias = msg.alias;
          this.updateBead.updateDescribe = msg.updateDescribe;
        }
      },
      initUploadInfo: function () {
        this.uploadBead = {
          file: "",
          alias: "",
          updateDescribe: "",
          md5: ""
        };
        this.fileName = "";
      },
      // 修改
      edit: function () {
        if (this.updateBead.id !== "" && this.updateBead.alias !== "" && this.updateBead.updateDescribe !== "") {
          let options = {
            url: updateBeadUrl,
            data: {
              beadId: this.updateBead.id,
              alias: this.updateBead.alias,
              updateDescribe: this.updateBead.updateDescribe
            }
          };
          axiosPost(options).then(response => {
            if (response.data.result === 200) {
              this.$alertSuccess('修改成功');
              Bus.$emit('isbeadRefresh',"edit");
              this.editDialogVisible = false;
            } else {
              this.$alertWarning(response.data.data);
            }
          })
            .catch(err => {
              this.$alertError('请求超时，请刷新重试');
              this.editDialogVisible = false;
            })
        } else {
          this.$alertWarning("内容不能为空");
        }
      },
      // 点击上传
      btnUpload: function () {
        if (this.uploadBead.file !== "" && this.uploadBead.alias !== "" && this.uploadBead.updateDescribe !== "") {
          this.handlerFile();
        } else {
          this.$alertWarning("内容不能为空");
        }
      },
      // 上传
      upload: function (that) {
        that.uploadDialogVisible = false;
        let param = new FormData();
        param.append('file', that.uploadBead.file);
        param.append('alias', that.uploadBead.alias);
        param.append('updateDescribe', that.uploadBead.updateDescribe);
        param.append('md5', that.uploadBead.md5);
        param.append('#TOKEN#', store.state.token);
        let config = {
          headers: {'Content-Type': 'multipart/form-data'}
        };
        axios.post(uploadBeadUrl, param, config).then(response => {
          if (response.data.result === 200) {
            that.$alertSuccess('上传成功');
            that.uploadDialogVisible = false;
            Bus.$emit('isbeadRefresh',"add");
          } else {
            that.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            that.$alertError('请求超时，请刷新重试');
            that.uploadDialogVisible = false;
          });
      },
      // 获取文件MD5
      handlerFile: function () {
        let files = document.getElementById('fileUpload');
        let file = files.files[0];
        let reader = new FileReader();
        let that = this;
        reader.onload = function (e) {
          that.$nextTick(function () {
            let md5 = SparkMD5.hashBinary(e.target.result).toUpperCase();
            that.uploadBead.md5 = md5;
            that.upload(that);
          })
        };
        reader.readAsBinaryString(file);
      },
      // 文件input事件
      handleUpload: function () {
        let file = document.getElementById('fileUpload');
        file.value = null;
        file.click();
      },
      handleFileChange: function () {
        let files = document.getElementById('fileUpload');
        let file = files.files[0];
        this.uploadBead.file = file;
        this.fileName = file.name;
      }
    },
  }
</script>

<style scoped>
</style>

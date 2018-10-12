<template>
  <div class="headDetails">
    <!--标题-->
    <div class="header">
      <span>项目</span>
    </div>
    <!--列表展示-->
    <div class="details">
      <el-scrollbar style="height:100%;">
        <el-row :gutter="20">
          <el-col v-for="(item,index) in headList" :xs="24" :sm="12" :md="8" :lg="6" :xl="4" :key="index">
            <div class="item" :style="{height:itemHeight}">
              <div class="content">
                <span class="name">{{item.name}}</span>
              </div>
              <div class="btn-group">
                <el-button type="info" icon="el-icon-edit" @click="btn_edit(item)" circle></el-button>
                <el-button type="info" icon="el-icon-delete" @click="btn_remove(item.id)" circle></el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-scrollbar>
    </div>
    <!--分页-->
    <el-pagination
      :current-page.sync="currentPage"
      @current-change="handleCurrentChange"
      :page-size="pageSize"
      :page-sizes="[4,6,8,12]"
      @size-change="handleSizeChange"
      layout="total,sizes,prev,pager,next, jumper"
      ref="pagination"
      :total="totallyData">
    </el-pagination>
    <el-button type="primary" icon="el-icon-plus" @click="btn_add" class="add" circle></el-button>
  </div>
</template>

<script>
  import Bus from '@/utils/bus'
  import {axiosPost} from '@/utils/fetchData'
  import {deleteHeadUrl, selectHeadUrl} from '@/config/globalUrl'
  import {refreshHandle} from "@/utils/handle";
  import {mapActions} from 'vuex';
  export default {
    name: "headDetails",
    data() {
      return {
        // 展示项目列表
        headList: [],
        // 当前页面
        currentPage: 1,
        // 每页项数
        pageSize: 8,
        // 总项数
        totallyData: 0,
        // 方框高度
        itemHeight: "",
        // 操作按钮样式
        btnDisplay: 'flex',
        // 查询操作
        formInline: {
          headName: ""
        }
      }
    },
    methods: {
      ...mapActions(['setLoading']),
      /*点击添加按钮*/
      btn_add: function () {
        Bus.$emit('headAdd', true);
      },
      /*点击删除按钮*/
      btn_remove: function (id) {
        this.$confirm('是否删除该项目?', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          center: false
        }).then((action) => {
          if (action === "confirm") {
            this.deleteItem(id);
          }
        }).catch(() => {
          this.$alertInfo("已取消删除");
        });
      },
      /*点击修改按钮*/
      btn_edit: function (item) {
        Bus.$emit('headEdit', item);
      },
      /*数据刷新*/
      refresh: function (msg) {
        refreshHandle(msg,this);
        this.selectAll();
      },
      /*查询*/
      selectAll: function () {
        this.setLoading(true);
        let options = {
          url: selectHeadUrl,
          data: {
            currentPage: this.currentPage,
            pageSize: this.pageSize
          }
        };
        axiosPost(options).then(response => {
          this.setLoading(false);
          if (response.data.result === 200) {
            let data = response.data.data;
            this.totallyData = data.totallyData;
            this.headList = data.list;
            this.$nextTick(function () {
              this.itemHeight = document.getElementsByClassName('item')[0].clientWidth + 'px';
            });
            this.currentPage = data.currentPage;
          } else {
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.setLoading(false);
            this.$alertError('请求超时，请刷新重试');
          })
      },
      /*删除*/
      deleteItem: function (id) {
        console.log("delete", this.currentPage, this.totallyData);
        let options = {
          url: deleteHeadUrl,
          data: {
            headId: id
          }
        }
        axiosPost(options).then(response => {
          if (response.data.result === 200) {
            this.$alertSuccess('删除成功');
            this.refresh("delete");
          } else {
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.$alertError('请求超时，请刷新重试');
          })
      },
      /*currentPage变化*/
      handleCurrentChange: function (currentPage) {
        this.currentPage = currentPage;
        this.selectAll();
      },
      /*pageSize变化*/
      handleSizeChange: function (pageSize) {
        this.pageSize = pageSize;
        this.selectAll();
      },
      /*设置方框高度*/
      setItemHeight: function (that) {
        if (document.getElementsByClassName('item')[0]) {
          let item = document.getElementsByClassName('item')[0];
          that.$nextTick(function () {
            that.itemHeight = item.clientWidth + 'px';
          });
        }
      }
    },
    beforeDestroy() {
      Bus.$off('isheadRefresh');
    },
    mounted() {
      this.selectAll();

      this.setItemHeight(this);

      /*设置方框高度*/
      let that = this;
      window.onresize = function temp() {
        that.setItemHeight(that);
      };

      /*监听列表刷新*/
      Bus.$on('isheadRefresh', (msg) => {
        this.refresh(msg);
      });
    }
  }
</script>

<style scoped>
  .headDetails {
    width: 100%;
    height: 100%;
  }
  /*标题*/
  .header {
    height: 40px;
    line-height: 40px;
  }
  .header span {
    padding-left: 20px;
    border-left: 3px solid rgb(0, 176, 240);
    font-size: 20px;
    font-weight: bold;
  }
  /*列表样式*/
  .details {
    box-sizing: border-box;
    width: 100%;
    height: calc(100% - 80px);
    padding: 20px 0px;
  }
  .el-col {
    margin-bottom: 25px;
  }
  .item {
    position: relative;
    background-color: #ddd;
    border-radius: 5px;
    font-size: 18px;
    font-weight: 100;
    color: #333;
    cursor: pointer;
  }
  .item .content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    flex-wrap: wrap;
    position: absolute;
    width: 100%;
    height: 100%;
  }
  .content .name {
    width: 90%;
    font-size: 2em;
    overflow-wrap: break-word;
    font-weight: bold;
    text-align: center;
  }
  .item .btn-group {
    display: flex;
    align-items: center;
    justify-content: center;
    position: absolute;
    bottom: 10px;
    width: 100%;
    text-align: center;
  }
  .btn-group .el-button {
    width: 40px;
    height: 40px;
    transition: all 2s;
    opacity: 0;
  }
  /*item-hover*/
  .item:hover .name {
    font-size: 1.2em;
  }
  .item:hover .el-button {
    opacity: 1;
  }
  /*add按钮*/
  .add {
    position: absolute;
    right: 20px;
    bottom: 20px;
    width: 60px;
    height: 60px;
    font-size: 20px;
  }
  /*屏幕尺寸变化*/
  @media screen and (max-width: 350px) {
    .content .name {
      font-size: 1.5em;
    }
    .item:hover .name {
      font-size: 1.2em;
    }
  }
</style>

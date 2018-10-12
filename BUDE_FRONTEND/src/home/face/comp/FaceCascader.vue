<template>
  <el-cascader
    placeholder="请选择模块"
    :options="options"
    filterable
    @change="find"
    v-model="selectValue"
    :props="props"
  ></el-cascader>
</template>

<script>
  import Bus from '@/utils/bus'
  import {axiosPost} from '@/utils/fetchData'
  import {selectFaceUrl, selectHeadUrl} from '@/config/globalUrl'
  import {mapActions} from 'vuex';
  import {getFaceInfo, getBeadInfo} from "@/utils/getInfo"

  export default {
    name: "faceCascader",
    data() {
      return {
        options: [],
        props: {},
        headList: [],
        faceList: [],
        selectValue: [],
        // 修改，添加或者删除的某一项
        operationValue: []
      }
    },
    beforeDestroy() {
      // 取消监听
      Bus.$off('isCascaderRefresh');
    },
    mounted() {
      this.options = getFaceInfo();
      // 监听刷新
      Bus.$on('isCascaderRefresh', (type, obj, faceName) => {
        this.options = [];
        this.options = getFaceInfo();
        if (type === "edit") {
          let item = {
            headId: obj.headId,
            headName: obj.headName,
            faceId: obj.faceId,
            faceName: faceName
          };
          this.$emit('isFind', item);
        } else if (type === "delete") {
          this.selectValue = [];
        }
      })
    },
    methods: {
      ...mapActions(['setLoading']),
      find: function (row) {
        let headId = row[0];
        let faceId = row[1];
        let headName;
        let faceName;
        for (let i = 0; i < this.options.length; i++) {
          let option = this.options[i];
          if (headId === option.value) {
            headName = option.label;
            for (let j = 0; j < option.children.length; j++) {
              let child = option.children[j];
              if (child.value === faceId) {
                faceName = child.label;
                break;
              }
            }
          }
        }
        let obj = {
          headId: headId,
          headName: headName,
          faceId: faceId,
          faceName: faceName
        };
        console.log(obj);
        this.$emit('isFind', obj);
      }
    }
  }
</script>

<style scoped>
</style>	

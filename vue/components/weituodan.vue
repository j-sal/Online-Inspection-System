<template>
  <div class="weituodan"
    v-loading="loading2"
    element-loading-text="Loading"
    element-loading-spinner="el-icon-loading"
    element-loading-background="rgba(0, 0, 0, 0.8)">
    <div class="weituodan-title">
      <div class="title_title">Order</div>
      <div class="title_tisi">
        <div class="shuaxin" @click="getweituodan">
          <i class="el-icon-refresh"></i>
          Refresh the list
        </div>
        <div @click="new_queding" class="jie">

            <div class="shuaxin" type="text" @click="centerDialogVisible2 = true">
              <i class="el-icon-circle-plus-outline"></i>
              &nbsp;add&nbsp;&nbsp;&nbsp;add&nbsp;&nbsp;&nbsp;

          </div>
        </div>


        <div class="shuaxin" type="text" @click="centerDialogVisible3 = true">
              <i class="el-icon-circle-plus-outline"></i>
              Add batches

          </div>
      </div>
    </div>

    <!-- Element shows the start of the order content -->
    <div class="content_content">

      <div class="list_header">
        <div class="li">Style batch</div>
        <div class="li">Commission number</div>
        <div class="li two">Delete</div>
      </div>



      <div class="ul" v-for="(item,index) in tableData3" :key="index">
        <div class="li">{{item.sampleBatch}}</div>
        <div class="li" @click="three(item.sampleBatch,item.entrustmentNumber,index)">
          <input type="text" v-model="item.entrustmentNumber" class="inputaa" @blur="queding1(item.sampleBatch,item.entrustmentNumber,index)" onkeyup="this.value=this.value.toUpperCase()" >
        </div>
        <div @click="two(item.sampleBatch,item.entrustmentNumber)">
           <div class="li three" @click="centerDialogVisible = true">Delete</div>
        </div>

      </div>

    </div>

    <!-- Delete dialog box starts -->
    <!-- <button class="wt_el_button" type="text" @click="centerDialogVisible = true"></button> -->
    <el-dialog
      title="Prompt"
      class="wt_el_dialog"
      :visible.sync="centerDialogVisible"
      width="600px"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      center
    >
      <span class="wt_P">Deleting data may result in data loss！</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="centerDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="shanchu">Confirm</el-button>
      </span>
    </el-dialog>

    <!-- Delete dialog box ends -->
    <!-- Modify dialog box to start -->
    <!-- <button class="wt_el_button two" type="text" @click="centerDialogVisible1 = true"></button> -->
    <el-dialog
      title="Prompt"
      :visible.sync="centerDialogVisible1"
      width="600px"
      class="wt_el_dialog"
      :modal-append-to-body="false"
      center
    >
      <!-- First level starts style batch -->
      <div class="yspca">
        <div class="yspca_text">Batch number</div>
        <div class="yspca_input">
          <input v-model="xgpch" placeholder="Please enter the type" type="button" class="yspca_input_input" disabled>
          <input>
        </div>
      </div>

      <!-- Second level commission number -->
      <div class="yspca">
        <input placeholder="Please enter the order number you want to register" v-model="xgwtd" clearable class="xgwtd" @keyup.enter="b($listeners)"  ref="con">
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="centerDialogVisible1 = false">Cancel</el-button>
        <el-button type="primary" @click="queding1()">Confirm</el-button>
      </span>
    </el-dialog>

    <!-- End of the modification dialog -->
    <!-- **********************************Add batch number popup******************************************* -->
    <el-dialog
      title="Add batch number"
      :visible.sync="centerDialogVisible2"
      width="600px"
      class="wt_el_dialog"
      center
    >
      <span class="wt_P">Are you sure to add the batch number?？</span>

      <span slot="footer" class="dialog-footer">
        <el-button @click="centerDialogVisible2 = new_quxiao()">Cancel</el-button>
        <el-button type="primary" @click="centerDialogVisible2 = new_queding1()">Confirm</el-button>
      </span>
    </el-dialog>

  <!-- *****************Add a commission number in bulk***************** -->
    <el-dialog
      title="Add a commission number in bulk"
      :visible.sync="centerDialogVisible3"
      width="600px"
      class="wt_el_dialog"
      center>
      <div class="pchgs">
        <div class="left">
          Batch number
        </div>

        <div class="right">
          <el-input
            placeholder="Please enter the number of the batch number"
            v-model="batchNum"
            class="pl_right_input"
            clearable>
          </el-input>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="centerDialogVisible3 = p_lquxiao()">Cancel</el-button>
        <el-button type="primary" @click="p_lqeding()">Confirm</el-button>
      </span>
    </el-dialog>

    <div class="folltor">

    <!-- ********************** Subpage ************************** -->
    <div class="block">
      <el-pagination
        class="sy_Pagination"
        layout="prev, pager, next"
        :page-size="15"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page.sync="page"
      >
      </el-pagination>
    </div>
    </div>
  </div>
</template>
<script>
import axios from "axios";
export default {
  directives:{
    focus:{
      inserted (el,binding){
        el.focus();
      }
    }
  },
  data() {
    return {
      a: "",
      name: "weituodan",
      woshipi: false,
      pch: "",
      wth: "",
      iconList: [],
      xgpch: "",
      xgwtd: "",
      bbb: "",
      tableData3: [],
      centerDialogVisible: false,
      centerDialogVisible1: false,
      centerDialogVisible2: false,
      centerDialogVisible3: false,
      input10: "",
      change_weituodan: "",
      judge_weituodan: "",
      new_Wtd: "", //New order
      dedaopicihao: "",
      //Restore current page count
      page:1,
      total:0,
      loading2:true,
      batchNum:'',
      old:'',
      oldstate:true,
      fengyefuzhu:1,
    };
  },
  methods: {

   b(){
     var that = this
     console.log("Enter")
     that.queding1();
   },

centerState(){
  console.log("1111");
  var that = this
  that.centerDialogVisible1 = true
  document.getElementById("d").focus();
},




     /* Batch modification cancel warning */
    p_lquxiao(){
      var that = this
      that.batchNum = ""
    },
    p_lqeding(){
      console.log("Start adding in bulk------------>")
      var that = this
      if(that.batchNum == null || that.batchNum ==""){
            that.$message({
              message: "Please enter the number of batch numbers to be added",
              type: "warning",
              duration: "2000",
              center: true

          });

      }else{
        that.axios
          .post("/createEntrustment/saveManyBatch",{
            batchNum:that.batchNum
          })
          .then((res)=>{
            console.log(res)
            that.batchNum = ""
            if(res.data.message == "ok"){
                that.$message({
                  message: "Batch added successfully",
                  type: "success",
                  duration: "1000",
                  center: true
                });
              that.batchNum == ""
              that.getweituodan();
              that.centerDialogVisible3 = false
            }else{
            that.$message({
              message: res.data.message,
              type: "warning",
              duration: "2000",
              center: true

          });
            that.batchNum == ""
            }
          })
          .catch((error)=>{
            console.log(error)
          })
      }

    },

    //Get all the data for this row
    wtts(row) {
      // console.log(row)

      this.xgpch = row.sampleBatch;
      this.xgwtd = row.entrustmentNumber;
      this.change_weituodan = row.entrustmentNumber;
      var that = this
      that.centerDialogVisible1 = true
      console.log(this.$refs.con)
      /* Save changes button method
       * determine which method should be executed to modify the small button
       * by judging whether this.xgwtd is empty
       */
    },

    cilck111(e) {
      console.log(e);
      console.log(e.path[3].children[0].textContent);
      // tr.el-table__row.cells[0]..innerHTML
    },
    xiugaitishi() {
      console.log("you clicked the edit button");
    },
    nihao(e) {
      console.log(e);
      console.log("...");
      this.bbb = e;
    },

    open7() {
      this.$notify({
        title: "Modify order information",
        message: "You canceled the modify order operation"
      });
    },
    dedaodongxi(row) {
      console.log(row);
    },

      three(e,a,c){
        var that = this

        if(that.oldstate == true){

        that.old = a;
        console.log("&&&&&&&&&&&&&&&")
        console.log(that.old)
        console.log("&&&&&&&&&&&&&&&")

        console.log(that.tableData3)
        console.log(that.tableData3[c].entrustmentNumber)
        that.xgpch = e
        if(a == null){
           that.xgwtd = that.tableData3[c].entrustmentNumber
           that.change_weituodan = null
        }else{
          that.xgwtd = a
          that.change_weituodan = that.old
        }
        console.log(that.xgpch)
        console.log(that.xgwtd)
        console.log(that.change_weituodan)
        }

      },
    //Start to modify the commission number
    queding1(samplebatch,entNumber,index) {

      var that = this;
      that.xgwtd=that.tableData3[index].entrustmentNumber
      that.xgpch = samplebatch
      //  that.old=entNumber
      console.log(that.xgwtd)
      console.log(that.change_weituodan + "--------->This is the order number");
      console.log("Start of modification");
      console.log(that.xgwtd);
      console.log(that.xgpch);
      that.axios
        .post("/entrustment/updateEntrustment", {
          sampleBatch: that.xgpch,
          newEntNum: that.xgwtd,
          oldEntNum: that.old
        })
        .then(res => {
          console.log(res);

          // **********************Judge*********************************

          //  that.getweituodan();
          if(res.data.message == "OK"){
             console.log("Successfully entered")
            that.$message({
              message: "Successfully modified",
              type: "success",
              duration: "1000",
              center: true
            });
            that.handleCurrentChange(that.fengyefuzhu)
            that.oldstate = true

          }else{
            that.$message({
              message: res.data.message,
              type: "warning",
              duration: "2000",
              center: true

          });
          that.oldstate = false
          // that.centerDialogVisible1 = true
          }
          that.handleCurrentChange(that.fengyefuzhu)
        })
        .catch(error => {
          console.log(error);
        });
    },

    //   Delete order button Interface
    shanchu() {
      var that = this;

      that.axios
        .post("/entrustment/deleteEntrustment", {
          sampleBatch: that.xgpch,
          entrustmentNumber: that.change_weituodan
        })
        .then(res => {
          console.log(res);

          if (res.data.status == 200) {
            that.$message({
              message: "successfully deleted",
              type: "success",
              duration: "1000",
              center: true
            });
            // that.getweituodan();
            that.handleCurrentChange(that.fengyefuzhu)
            that.centerDialogVisible = false;

          } else {
            that.$message({
              message: res.data.message,
              duration: "2000",
              type: "warning",
              center: true
            });
            that.handleCurrentChange(that.fengyefuzhu)
          }
        })
        .catch(error => {
          console.log(error);
        });
    },

    // Lifecycle renders the style batch and delegate number when the page is loaded
    getweituodan() {
      var that = this;
      that.new_queding;
      that.page = 1
      //By judging whether the link is directly jumped over
      if (
        localStorage.id == "" ||
        localStorage.username == "" ||
        localStorage.userRank == "" ||
        localStorage.userClass == ""
      ) {
        console.log("localStorage is empty, blocking jumps");
        this.$message({
          message: "This jump method is dangerous and temporarily not supported.",
          type: "warning",
          duration: "2000",
          center: true
        });
        this.$router.push("/");
      } else {
        that.axios
          .post("/createEntrustment/getEntrustmentList", {
            offset:1,
            limit:15
          })
          .then(res => {
            console.log(res);
            if (res.data.status == 200) {
              that.total = res.data.data.total
              that.tableData3 = res.data.data.datalist;
              that.xgpch = that.value;
              if (res.data.data.datalist.length == "0") {
                that.a = false;
              } else {
                that.a = true;
              }
            } else {
              this.$message({
                message: res.data.message,
                type: "warning",
                duration: "2000",
                center: true
              });
            }
            that.loading2 = false

          })

          .catch(error => {
            that.$message.error("The network link failed. Please check if the network is normal.");
          });
      }
    },

    /* Add cancel button */
    new_quxiao() {
      var that = this;
      console.log("点击了取消，清空数据");
      that.new_Wtd = "";
    },
    /* Add OK button */
    new_queding() {
      console.log("OK button start");
      var that = this;
      console.log(that.new_Wtd);

      // console.log("The delegate number is not empty, request interface")

      var that = this;
      that.axios
        .post("/createEntrustment/createNewBatch", {})
        .then(res => {
          console.log(res);
          that.dedaopicihao = res.data.data;
          that.xgpch = res.data.data;
        })
        .catch(error => {
          console.log(error);
        });
    },
    /* Modify the batch number to store to the database */
    new_queding1() {
      console.log("Store inverted database");
      var that = this;
      // that.centerDialogVisible1 = false
      that.axios
        .post("/createEntrustment/saveEntrustment", {
          sampleBatch: that.dedaopicihao
        })
        .then(res => {
          console.log(res);
          that.getweituodan();
        })
        .catch(error => {
          console.log(error);
        });
    },

    /* Get the default order number */
    dedaoweituodanhao() {
      console.log("Get the default order number");
      var that = this;
      console.log(that.dedaopicihao);
      that.axios
        .get(
          "/entrustmentNum/showEntrustmentDefaultNum?batchNum=" +
            that.dedaopicihao,
          {}
        )
        .then(res => {
          console.log(res);
          /* Assign the generated default order number */
          that.new_Wtd = res.data.data;
        })
        .catch(error => {
          console.log(error);
        });
    },


       // When the page changes
     handleSizeChange(val) {
      console.log(`Per page ${val} condition`);
    },
    handleCurrentChange(val) {
      var that = this
      console.log(`current page: ${val}`);
      console.log("The current page number has changed.")

      that.fengyefuzhu = val

       this.axios
        .post("/createEntrustment/getEntrustmentList",{
            offset:that.fengyefuzhu,
            limit:15
        })
        .then((res=>{
          console.log(res)

          if(res.data.status == 200){

           this.tableData3 = res.data.data.datalist;
          }else{
            that.$message({
              message: res.data.message,
              duration:"2000",
              type: "warning",
              center: true,
            });
          }
        }))
        .catch((error)=>{
          console.log(error)
          that.$message.error('The network link failed. Please check if the network is normal.');
        })
    },
two(e,a){
  console.log(e)
  console.log(a)
  var that = this
  that.xgpch = e;
  that.change_weituodan = a;
}

  },
  /* Define the life cycle to render the style batch and the delegate number
   * when the page is loaded
   */
  mounted() {
    this.getweituodan();
    var that = this


  },


};
</script>
<style>
.el-table .warning-row {
  background: oldlace;
}

.el-table .success-row {
  background: #f0f9eb;
}
.el_table1 {
  border-radius: 8px;
  margin-top: 10px;
  position: absolute;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
}
.el_table1 tr td {
  height: 30px;
  padding: 0px;
  text-align: center;
}
.el-dialog--center {
  box-shadow: 0 5px 30px rgba(0, 0, 0, 0.05);
  border: 1px solid #e0e0e0;
}
.wt_el_button {
  line-height: 18px;
  text-align: center;
  background-size: 100% 100%;
  font-size: 14px;
  color: #e02d35;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.01);
  cursor: pointer;
}
.wt_el_button_two {
  line-height: 18px;
  text-align: center;
  background-size: 100% 100%;
  font-size: 14px;
  color: #c28f6c;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.01);
  cursor: pointer;
}
.PC_ss_input input {
  border: none;
  height: 20px;
}
/* Pagination */
.sy_Pagination {
  height: 38px;
  width: 100%;
  padding: 0px;
  line-height: 38px;
  margin-top: 10px;
}
.sy_Pagination ul{
  height: 38px;
  line-height: 38px;
}
.sy_Pagination ul li{
  height: 38px;
  line-height: 38px;
  width: 38px;
  margin-right: 5px;
  border-radius: 50%;
  font-size: 13px;
  font-weight: bold;
}
.active{
  background-color: #fff;
}
.sy_Pagination button{
  height:38px;
  width: 38px;
  border-radius: 50%;
  line-height: 38px;
  margin-top: 20000px;
  font-size: 13px;
  font-weight: bold;
}
.sy_Pagination  el-icon{
  line-height: 45px;
}
.el-pager li.btn-quicknext, .el-pager li.btn-quickprev{
  line-height: 45px;
}
.el-pager li.active{
  border: 1px solid #f1be2f
}
.el-pagination .btn-next{
  width: 38px;
  height: 38px;
}
.el-pagination .btn-prev {
  width: 38px;
  height: 38px;
  margin-right: 5px;
}
.right11_input input{
  width: 100%;
  height: 30px;
  border:none;
}
.pl_right_input input{
  padding: 0px;
  border: none;
  font-size: 15px;
  color: #888888;
  text-indent: 4px;
}
</style>



<style lang="less" scoped>
.weituodan {
  width: 100%;
  height: 100%;
  min-width: 1160px;
  background-color: #f2f2f2;
}
.weituodan-title {
  min-width: 1160px;
  height: 60px;
  font-size: 30px;
  line-height: 60px;
  padding-left: 40px;
  color: #888888;
  font-weight: bold;
  position: relative;
}
.content_content {
  width: 96%;
  height: 80%;
  margin: auto;
  position: relative;
  overflow: hidden;
  // box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
}
.weituodan-centent {
  width: 1000px;
  height: calc(100% - 100px);
  margin: 0 auto;
  border-radius: 9px;
  background-color: #fcfcfc;
}
.dialog-footer {
  margin-left: 330px;
}

//Modify popup
.yspca {
  width: 60%;
  height: 38px;
  margin: auto;
  margin-top: 23px;
}
.wt_el_dialog {
  box-shadow: none;
  border: none;
  margin-top: 6.5%;
}
//Header refresh
.title_title {
  width: 60%;
  height: 60px;
  font-size: 30px;
  line-height: 60px;
  color: #888888;
  font-weight: bold;
  float: left;
}
.title_tisi {
  width: 30%;
  height: 60px;
  float: right;
  margin-right: 1%;
}

.shuaxin {
  min-width: 30px;
  height: 30px;
  line-height: 30px;
  float: right;
  overflow: hidden;
  margin-top: 15px;
  background: #c28f6c;
  color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  font-size: 14px;
  font-weight: 400;
  padding-left: 5px;
  padding-right: 5px;
  margin-right: 30px;
  cursor: pointer;
  &:active {
    box-shadow: 0 0 0 rgba(0, 0, 0, 0.2);
  }
}

.yspca_text {
  width: 50px;
  height: 35px;
  color: #888888;
  float: left;
  font-size: 15px;
  line-height: 35px;
  font-weight: bold;
}
.yspca_input {
  width: 100px;
  height: 35px;
  float: left;
  line-height: 35px;
  margin-left: 5px;
  border: none;
}
.yspca_input_input {
  border: none;
  font-size: 14px;
  color: #888888;
  background-color: none;
  font-weight: bold;
}
.wt_P {
  width: 200px;
  text-align: center;
  margin-left: calc((100% - 200px) / 2);
  font-size: 15px;
  color: #888888;
}
.PC_ss {
  width: 96%;
  height: 38px;
  margin: auto;
  border-radius: 8px;
  -webkit-box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
  background-color: #fff;
  line-height: 35px;
  margin-left: 2%;
  position: relative;
  .PC_ss_div {
    width: 240px;
    height: 26px;
    line-height: 26px;
    margin-top: 5px;
    border-radius: 4px;
    border: 1px solid #989898;
    margin-left: 15px;
    float: left;
    .left {
      width: 70px;
      height: 26px;
      line-height: 26px;
      font-size: 13px;
      color: #696767;
      text-align: center;
      background-color: #e9e6e6;
      border-top-left-radius: 4px;
      border-bottom-left-radius: 4px;
      float: left;
    }
    .right {
      width: 170px;
      height: 26px;
      font-size: 13px;
      color: #888888;
      line-height: 26px;
      border-top-left-radius: 4px;
      border-bottom-left-radius: 4px;
      float: left;
      border-top-right-radius: 4px;
      border-bottom-right-radius: 4px;
      text-align: left;
    }
  }

  button {
    width: 85.66px;
    height: 30px;
    margin-top: 5px;
    padding: 0;
    float: left;
    margin-left: 55px;
    font-size: 14px;
    color: #fff;
    background-color: #c28f6c;
    border-color: #c28f6c;
    border-radius: 8px;
  }
}
.add_Wtd {
  border: 1px solid #cccccc;
  border-radius: 9px;
  .text {
    width: 85px;
    height: 38px;
    font-size: 15px;
    line-height: 38px;
    text-align: center;
    font-weight: bold;
    border-top-left-radius: 8px;
    border-bottom-left-radius: 8px;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    float: left;
    background-color: #e9e6e6;
    color: #696767;
    padding: 0px;
  }
  .input {
    width: 220px;
  }
}

.folltor {
  width: 100%;
  height: 50px;
  // background-color: red;
  // margin-top: 49%;
  margin-top: 15px;
}

.block {
  width: 100%;
  height: 38px;
  padding: 0px;
  margin-top: 10px;
  position: relative;
  margin: auto;
  line-height: 38px;
  text-align: center;
}

/* Batch modification starts */
.pchgs{
  width: 60%;
  margin: auto;
  margin-top: 10px;
  height: 42px;
  line-height: 40px;
  border-radius: 8px;
  border: 1px solid #ccc;
  .left{
        width: 90px;
        height: 42px;
        font-size: 15px;
        line-height: 40px;
        text-align: center;
        font-weight: bold;
        border-top-left-radius: 8px;
        border-bottom-left-radius: 8px;
        -webkit-box-sizing: border-box;
        box-sizing: border-box;
        float: left;
        background-color: #e9e6e6;
        color: #696767;
        padding: 0px;
  }
  .right{
        float: left;
        width: 225px;
        height: 42px;
        line-height: 42px;
        font-size: 18px;
        font-weight: bold;
  }
}

.xgwtd{
  width: 350px;
  height: 40px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  font-size: 13px;
  color: #888888;
  text-indent: 10px;

}
.list_header{
  width: 96%;
  height: 43px;
  line-height: 43px;
  background-color: #fff;
  border: 1px solid #e0e0e0;
  margin: auto;
  margin-top: 10px;
  border-top-left-radius: 6px;
  border-top-right-radius: 6px;
  box-sizing: border-box;
  .li{
    width: calc(100% / 3);
    height: 43px;
    float: left;
    text-align: center;
    font-size: 13px;
    color: #888888;
    border-right: 1px solid #e0e0e0;
    box-sizing: border-box;

  }
  .two{
    border-right: none;
  }
}
.ul{
  width: 96%;
  height: 28px;
  background-color: #fff;
  margin: auto;
  border: 1px solid #e0e0e0;
  box-sizing: border-box;
  border-top: none;
  // border-right: none;
  line-height: 28px;
  box-sizing: border-box;

  .li{
    width: calc(100% / 3);
    height: 28px;
    line-height: 28px;
    text-align: center;
    border-right: 1px solid #e0e0e0;
    float: left;
    box-sizing: border-box;
    font-size: 13px;
    color: #888888;
    box-sizing: border-box;
    cursor: pointer;
  }
  .three{
    border-right: none;
    color: #c28f6c
  }
}
.inputaa{
  font-size: 13px;
  color: #888888;
}
</style>

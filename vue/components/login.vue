<template>

  <el-container  class="index" >

    <el-header class="login_header" > 
      <div class="text" >
        <p class="login_p">Juli Group wire rope inspection background data monitoring system</p>
        <p class="small_text">Juli group steel wire rope inspection background data monitoring system</p>        
      </div>

      <div class="user_img">
        <div class="user_img_body"></div>
      </div>

    </el-header>

    <el-main class="main">

      <div class="user_info" >
        <input 
          class="el1"
          v-model="userName"
          placeholder="Please enter the user name: "
          @keyup.enter="a($listeners)"
        />
        <input
          class="el1"
          v-model="userPassword"
          placeholder="Please enter the password: "
          type="password"
          @keyup.enter="a($listeners)"
       />

        <el-button
        type="primary"
        id="login_botton"
        @click="userLogin"
        
        >
        Login&nbsp;Register&nbsp;
        </el-button>

      </div>
    </el-main>
    
    <el-footer class="footer">
      <div>Hebei Software Vocational and Technical College</div>
    </el-footer>
  </el-container>

</template>

<script>
import axios from 'axios'

export default {
  name: "Login",
  data() {
    return {
      loading:false,
      userName: "",
      userPassword: "",
      userPhone: ""
    };
  },
  methods : {
 
    userLogin () {
      var that = this 
      console.log(that.userName)
      console.log(that.userPassword)
      that.loading = true
      console.log("大坏蛋")
      //Request for login interface to start
      // First check whether the username and password are empty or not, then call the interface
      // Determine if localStorage is empty

    


      if((that.userName =="" || that.userName ==null) || (that.userPassword == "" || that.userPassword ==null) ){
       if(that.userName =="" || that.userName==null){
           that.$message({
            message: "Please enter the user name",
            type: 'warning',
            duration:'2000'
          });
        }else if(that.userPassword == "" || that.userPassword == null){
           that.$message({
            message: "Please enter the password",
            type: 'warning',
            duration:'2000'
          });
        
        }
      }else{
          localStorage.id = "";
          localStorage.id = "";
          localStorage.username = "";
          localStorage.userRank = "";
          localStorage.userClass = "";
          that.axios
      .post("/user/login",{
        username:that.userName,
        password:that.userPassword
      })
      //成功
      .then((res)=>{
        console.log(res)

        if(res.data.status == 200){
          console.log("haha ")
          //If successful, it will be 1, User ID 2, User Name 3, User Level 4, User Class Saved to localStorage
          localStorage.id = res.data.data.id;
          localStorage.id = res.data.data.id;
          localStorage.username = res.data.data.username;
          localStorage.userRank = res.data.data.userRank;
          localStorage.userClass = res.data.data.userClass;
          console.log("Store data locally")
          console.log(localStorage.id);
          console.log(localStorage.username);
          console.log(localStorage.userRank);
          console.log(localStorage.userClass)
          that.$message({
            message: 'Login is successful, welcome to the system',
            type: 'success',
            duration:'1000'
          });
          that.$router.push('/home')            
        }else{
          that.$message({
            message: res.data.message,
            type: 'warning',
            duration:'2000'
          });
        }
      })
      //失败
      .catch((error)=>{
        console.log(error)
        that.$message.error('The network link failed. Please check if the network is normal.');
      })
      }

      

    },


    // ***********Enter login event***************
   a(){
     var that = this
     console.log("Enter")
     that.userLogin();
   }

   
  },

  mounted(){

  }
};
</script>


<style>
.el input{
     margin-bottom:  20px;
     height: 33px;
     line-height: 33px;
     border-radius: 8px;
     border: 1px solid #979797;
     background: #fafafa;
     margin-top: 13px;
}
</style>

<style lang="less" scoped>
.index {
  min-width: 1270px;
  min-height: 500px;
  height: 100%;
  background: #fff;

  .login_header{
    position: relative;
    min-height: 40%;
    height: 40% !important;
    // background-image: url("http://img2.imgtn.bdimg.com/it/u=2153266548,2708969706&fm=26&gp=0.jpg");
    background: #c28f6c;
    background-size: 100% 100%;
    color: #fff;

    .user_img{
      position: absolute;
      width: 60px;
      height: 60px;
      border-radius: 60px;
      z-index: 2;
      bottom: -20px;
      left: calc(50% - 30px);
      overflow: hidden;
      .user_img_body{
        width: 50px;
        height: 50px;
        margin: auto;
        margin-top: 5px;;
        border-radius: 50px;
        background-image: url('../assets/img/timg.jpeg');
        background-size: 100% 100%;
        border: 1px solid #dcdfe6;
        box-shadow: 0px 0px 2px rgba(0, 0, 0, .3);
      }

    }

    .text{
      position: absolute;
      width: 90%;
      text-align: content;
      bottom: 16%;
      margin: auto;
      line-height: 50px;
      font-size: 30px;
      margin-left: 5%;
      .login_p{
         text-align: center;
      }
      .small_text{
        font-size: 18px;
        line-height: 30px;
        text-align: center;
      }
    }

  }
  .main{
    position: relative;
    height: 60%;
    width: 300px;
    margin: auto;
    margin-top: 20px;


    #login_botton{
      padding: 0;
      border-radius: 8px;
      width: 260px;
      border: none;
      height: 35px;
      line-height: 35px;
      font-size: 18px;
      color: #fff;
      margin-top: 10px;
      // box-shadow: 0px 3px 3px rgba(0, 0, 0, .3);
      &:active{
        color: #eee;
        box-shadow: 0px 0px 0px rgba(0, 0, 0, .3);
      }
    }
  }
  .footer{
    font-size: 15px;
    color: #999;
  }

}
.el1{
     margin-bottom:  20px;
     height: 33px;
     line-height: 33px;
     border-radius: 8px;
     border: 1px solid #979797;
     background: #fafafa;
     width: 255px;
     text-indent: 15px;
     color: #848285;
     font-size: 13px;

}
</style>

const app = getApp()
import Notify from '../../dist/notify/notify';

Page({
  data: {
    username: "",
    password1: "",
    password2: "",
    errMsg: "",
    disabled: true
  },
  onLoad: function() {

  },

  onChange: function(e) {
    if (e.target.id == "username") {
      this.setData({
        "username": e.detail
      });
    } else if (e.target.id == "password1") {
      this.setData({
        "password1": e.detail
      });
      if (this.data.password1 != this.data.password2) {
        this.setData({
          "errMsg": "两次输入密码不相同"
        });
      } else {
        this.setData({
          "errMsg": ""
        });
      }
    } else {
      this.setData({
        "password2": e.detail
      });
      if (this.data.password1 != this.data.password2) {
        this.setData({
          "errMsg": "两次输入密码不相同"
        });
      } else {
        this.setData({
          "errMsg": ""
        });
      }
    }

    if (this.data.username.length != 0 && this.data.password1.length != 0 && this.data.password2.length != 0 && this.data.password1 == this.data.password2) {
      this.setData({
        disabled: false
      });
    } else {
      this.setData({
        disabled: true
      });
    }
  },

  doRegist: function() {
    console.log(this.data.username);
    console.log(this.data.password1);
    console.log(this.data.password2);
    if (this.data.username.length == 0 || this.data.password1.length == 0 || this.data.password2.length == 0) {
      //Toast.fail('用户名和密码不能为空');
      Notify({
        text: "用户名和密码不能为空",
        duration: 3000,
        backgroundColor: "#FF4444"
      });
    } else if (this.data.password1 != this.data.password2) {
      Notify({
        text: "两次输入密码不相同",
        duration: 3000,
        backgroundColor: "#FF4444"
      });
    } else {
      var serverUrl = app.serverUrl;
      wx.showLoading({
        title: '注册中',
      });
      wx.request({
        url: serverUrl + '/register',
        method: "POST",
        data: {
          username: this.data.username,
          password: this.data.password1
        },
        header: {
          'content-type': 'application/json'
        },
        success: function(res) {
          wx.hideLoading();
          console.log(res.data);
          var status = res.data.status;
          if (status == 200) {
            Notify({
              text: "注册成功",
              duration: 3000,
              backgroundColor: "#1E90FF"
            });
            app.userInfo = res.data.data;
            wx.redirectTo({
              url: "/pages/mine/mine",
            })
          } else if (status == 500) {
            wx.hideLoading();
            Notify({
              text: res.data.msg,
              duration: 3000,
              backgroundColor: "#FF4444"
            });
          }
        }
      });
    }
  },

  backToLogin: function () {
    wx.redirectTo({
      url: '/pages/userLogin/login',
    })
  }

})
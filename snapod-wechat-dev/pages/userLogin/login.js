const app = getApp()
import Notify from '../../dist/notify/notify';


Page({
  data: {
    username: "",
    password: "",
    disabled: true
  },

  onChange: function(e) {
    if (e.target.id == "username") {
      this.setData({
        "username": e.detail
      });
    } else {
      this.setData({
        "password": e.detail
      });
    }


    if (this.data.username.length != 0 && this.data.password.length != 0) {
      this.setData({
        disabled: false
      });
    } else {
      this.setData({
        disabled: true
      });
    }
  },

  doLogin: function(e) {
    var username = this.data.username;
    var password = this.data.password;

    var serverUrl = app.serverUrl;
    wx.showLoading({
      title: '登录中',
    });
    wx.request({
      url: serverUrl + '/login',
      method: 'POST',
      data: {
        username: username,
        password: password
      },
      header: {
        'content-type': 'application/json'
      },
      success: function (res) {
        wx.hideLoading();
        console.log(res.data);
        var status = res.data.status;
        if (status == 200) {
          Notify({
            text: "用户登录成功",
            duration: 3000,
            backgroundColor: "#1E90FF"
          });
          app.userInfo = res.data.data;
          // TODO: 跳转
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
  },
  backToRegist: function() {
    wx.redirectTo({
      url: '/pages/userRegist/regist',
    })
  }
})
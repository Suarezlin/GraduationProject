const app = getApp()
import Notify from '../../dist/notify/notify';


Page({
  data: {
    username: "",
    password: "",
    disabled: true,
    url: "",
  },

  onLoad: function(params) {
    let url = params.redirectUrl;
    if (url == null || url == undefined) {
      url = "";
    }
    url = url.replace(/#/g, "?");
    url = url.replace(/@/g, "=");
    this.setData({
      url: url
    });
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
    const that = this;
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
          app.setGlobalUserInfo(res.data.data)
          //app.userInfo = res.data.data;
          // TODO: 跳转

          if (that.data.url != "") {
            wx.redirectTo({
              url: that.data.url,
            })
          } else {
            wx.redirectTo({
              url: "/pages/index/index",
            })
          }

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
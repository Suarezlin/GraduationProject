//app.js
App({

  serverUrl: "http://192.168.1.100:8080",

  userInfo: null,

  onLaunch: function () {
    const userInfo = this.getGlobalUserInfo();
    if (userInfo == null || userInfo == undefined || userInfo == "") {
      wx.redirectTo({
        url: '/pages/userLogin/login?redirectUrl=' + "/pages/index/index",
      });
    }
    wx.request({
      url: `${this.serverUrl}/user/isLogin?userId=${userInfo.id}&userToken=${userInfo.userToken}`,
      method: "GET",
      success: function(res) {
        let status = res.data.data;
        if (!status) {
          wx.redirectTo({
            url: '/pages/userLogin/login?redirectUrl=' + "/pages/index/index",
          });
        }
      }
    })
  },
  globalData: {
    userInfo: null
  },

  setGlobalUserInfo: function(userInfo) {
    wx.setStorageSync("userInfo", userInfo);
  },

  getGlobalUserInfo: function() {
    return wx.getStorageSync("userInfo");
  }
})
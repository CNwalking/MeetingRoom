//所有页面通用js

//检查本地token有效期
if (localStorage.token) {
    var start = +localStorage.tokenTime
    var now = +new Date()
    if (now - start > 7 * 24 * 3600 * 1000) {
        //如果token有效期超过7天
        //清除本地token重回登录页面
        localStorage.removeItem('tokenTime')
        localStorage.removeItem('token')
        window.location.replace('./index.html')
    }
} else {
    //如果没有token，则没有登录，去登录页面
    window.location.replace('./index.html')
}
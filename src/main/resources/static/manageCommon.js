//检查本地     管理端token有效期
if (localStorage.manageTokenTime) {
    var start = +localStorage.manageTokenTime
    var now = +new Date()
    if (now - start > 30 * 60 * 1000) {
        //如果token有效期超过30分钟
        //清除本地token重回登录页面
        localStorage.removeItem('manageTokenTime')
        localStorage.removeItem('manageToken')
        window.location.replace('./index.html')
    }
} else {
    //如果没有token，则没有登录，去登录页面
    window.location.replace('./index.html')
}

function logout() {
    alert('success logout')
    localStorage.removeItem('manageTokenTime')
    localStorage.removeItem('manageToken')
    window.location.replace('./index.html')
}
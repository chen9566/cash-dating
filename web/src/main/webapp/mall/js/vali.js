$.validator.setDefaults({
    submitHandler: function () {
        alert("注册成功!");
    }
});

$().ready(function () {
// 在键盘按下并释放及提交后验证提交表单
    $("#signupForm").validate({
        rules: {
            username: {
                required: true,
                minlength: 2
            },
            password: {
                required: true,
                minlength: 5
            },
            confirm_password: {
                required: true,
                minlength: 5,
                equalTo: "#password"
            },
            agree: "required"
        },
        messages: {
            username: {
                required: "请输入用户名",
                minlength: "必需由两个字母"
            },
            password: {
                required: "请输入密码",
                minlength: "不能小于 5 位"
            },
            confirm_password: {
                required: "请输入密码",
                minlength: "不能小于 5 位",
                equalTo: "两次密码不一致"
            },
            agree: "请接受我们的声明",
        }
    });
});


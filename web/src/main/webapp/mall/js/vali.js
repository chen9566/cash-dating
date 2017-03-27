// $.validator.setDefaults({
//     submitHandler: function () {
//         alert("注册成功!");
//     }
// });

$().ready(function () {
// 在键盘按下并释放及提交后验证提交表单
    $("#signupForm").validate({
        rules: {
            mobile: {
                required: true,
                minlength: 11
            },
            verificationCode: {
                required: true,
                minlength: 4,
                maxlength: 4,
            },
            password: {
                required: true,
                minlength: 6
            },
            confirm_password: {
                required: true,
                minlength: 6,
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
                minlength: "不能小于 6 位"
            },
            confirm_password: {
                required: "请输入密码",
                minlength: "不能小于 6 位",
                equalTo: "两次密码不一致"
            },
            agree: "请接受我们的声明",
        }
    });
});


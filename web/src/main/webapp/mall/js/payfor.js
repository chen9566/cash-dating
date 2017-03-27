//加减效果
var num = 100;
$(function () {
    $(".add").click(function () {
        var t = $(this).parent().find('input[class*=text_box]');
        t.val(parseInt(t.val()) + 1)
        setTotal();
    })
    $(".min").click(function () {
        var t = $(this).parent().find('input[class*=text_box]');
        t.val(parseInt(t.val()) - 1)
        if (parseInt(t.val()) < 0) {
            t.val(0);
        }
        setTotal();
    })
    $(".color").click(function () {
        $(".price").html(num);
        setTotal()
    })
    function setTotal() {
        var s = 0;
        $(".goods-warp").each(function () {
            s += parseInt($(this).find('input[class*=text_box]').val()) * parseFloat($(this).find('span[class*=price]').text());
        });
        $(".total").html(s.toFixed(1));
    }

    setTotal();

})
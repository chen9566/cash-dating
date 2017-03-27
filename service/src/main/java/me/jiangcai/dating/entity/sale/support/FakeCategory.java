package me.jiangcai.dating.entity.sale.support;

/**
 * 伪类的类目
 *
 * @author CJ
 */
public enum FakeCategory {
    s1("女装", 100), s2("男装", 99), s3("鞋子", 98), s4("箱包", 97), s5("母婴", 96), s6("美妆", 95), s7("内衣", 94), s8("美食", 93), s9("家具", 92), s10("文具", 91);
    private final String name;
    private final int weight;

    FakeCategory(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

}

package me.jiangcai.dating;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.luffy.libs.libseext.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author CJ
 */
public class XML2Test {

    @Data
    private class Info {
        private String name;
        private String code;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    private class Province extends Info {
        private List<City> cities;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    private class City extends Info {
        private List<County> counties;
    }

    /**
     * 县
     */
    private class County extends Info {
    }

    private <T> List<T> converter(NodeList list, Function<Node, T> function) {
        ArrayList<T> list1 = new ArrayList<>(list.getLength());
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            list1.add(function.apply(node));
        }
        return list1;
    }

    //    @Test
    public void xml2json() throws IOException, ParserConfigurationException, SAXException {

        Document document = XMLUtils.xml2doc(new FileInputStream(new File("src/main/webapp/dist/local.xml")));

        Element root = (Element) document.getElementsByTagName("Location").item(0);
        System.out.println(root);

        List<Province> provinceList = converter(root.getElementsByTagName("CountryRegion"), node -> {
            Province province = new Province();
            info(province, node);
            Element element = (Element) node;
            province.cities = converter(element.getElementsByTagName("State"), node1 -> {
                City city = new City();
                info(city, node1);
                Element element1 = (Element) node1;
                city.counties = converter(element1.getElementsByTagName("City"), node11 -> {
                    County county = new County();
                    info(county, node11);
                    return county;
                });
                return city;
            });
            return province;
        });

        System.out.println(provinceList);

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.writeValue(new File("target/abc.json"), provinceList);

    }

    private void info(Info info, Node node) {
//        info.setName(node.geta);
        info.setName(node.getAttributes().getNamedItem("Name").getNodeValue());
        info.setCode(node.getAttributes().getNamedItem("Code").getNodeValue());
    }

}

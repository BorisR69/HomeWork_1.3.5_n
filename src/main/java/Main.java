import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCSV = "data.csv";
        String jsonCSV = "data.json";
        String fileNameXML = "data.xml";
        String jsonXML = "data2.json";
// Обработка файла CSV
        List<Employee> listCSV = parseCSV(columnMapping, fileNameCSV);
        String json = listToJson(listCSV);
        writeString(json, jsonCSV);
// Обработка файла XML
        List<Employee> listXML = parseXML(fileNameXML);
        String json2 = listToJson(listXML);
        writeString(json2, jsonXML);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) { // Парсинг CSV
        List<Employee> csvList = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            csvList = csv.parse();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return csvList;
    }

    public static String listToJson(List<Employee> list) { // Преобразование списка в JSON
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String jsonName) { // Запись файла JSON
        try (FileWriter file = new FileWriter(jsonName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Employee> parseXML(String fileXml) throws ParserConfigurationException, IOException,
            SAXException { // Парсинг XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileXml));
        Node root = doc.getDocumentElement();
        List<Employee> employeeList = new ArrayList<>();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                NodeList nodeList1 = node.getChildNodes();
                Employee employee = new Employee();
                for (int j = 0; j < nodeList1.getLength(); j++) {
                    Node node1 = nodeList1.item(j);
                    if (Node.ELEMENT_NODE == node1.getNodeType()) {
                        switch (node1.getNodeName()) {
                            case "id": {
                                employee.setId(Long.parseLong(node1.getTextContent()));
                                break;
                            }
                            case "firstName": {
                                employee.setFirstName(node1.getTextContent());
                                break;
                            }
                            case "lastName": {
                                employee.setLastName(node1.getTextContent());
                                break;
                            }
                            case "country": {
                                employee.setCountry(node1.getTextContent());
                                break;
                            }
                            case "age": {
                                employee.setAge(Integer.parseInt(node1.getTextContent()));
                                break;
                            }
                        }
                    }
                }
                employeeList.add(employee);
            }
        }
        return employeeList;
    }
}
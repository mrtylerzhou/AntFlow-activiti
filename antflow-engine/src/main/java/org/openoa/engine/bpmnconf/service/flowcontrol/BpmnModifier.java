package org.openoa.engine.bpmnconf.service.flowcontrol;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class BpmnModifier {

    public static String changeSequential(
            String xml,
            String taskId,
            boolean sequential) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(
                new ByteArrayInputStream(xml.getBytes("UTF-8"))
        );

        NodeList userTasks = document.getElementsByTagName("userTask");

        for (int i = 0; i < userTasks.getLength(); i++) {

            Element userTask = (Element) userTasks.item(i);

            if (taskId.equals(userTask.getAttribute("id"))) {

                NodeList children = userTask.getElementsByTagName(
                        "multiInstanceLoopCharacteristics"
                );

                if (children.getLength() > 0) {

                    Element multi = (Element) children.item(0);

                    multi.setAttribute(
                            "isSequential",
                            String.valueOf(sequential)
                    );
                }
            }
        }

        Transformer transformer =
                TransformerFactory.newInstance().newTransformer();

        StringWriter writer = new StringWriter();

        transformer.transform(
                new DOMSource(document),
                new StreamResult(writer)
        );

        return writer.toString();
    }
}